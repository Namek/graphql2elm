package net.namekdev.graphql2elm.parsers

import net.namekdev.graphql2elm.*

class SelectedFieldOutput(val schema: Schema, val operations: List<OperationDef>)

fun parseSchemaJson(schemaJson: String): Schema {
    val doc = JsonParser(schemaJson).parse()

    val jsonRoot = doc.asObject()["data"].asObject()["__schema"].asObject()

    val jsonTypes = jsonRoot["types"].asArray()
    val queryTypeName = jsonRoot["queryType"].asObject().getString("name")
    val mutationTypeName = jsonRoot["mutationType"].asObject().getString("name")

    val schema = Schema(queryTypeName, mutationTypeName)

    for (i in 0 until jsonTypes.size()) {
        val jsonType = jsonTypes[i].asObject()
        val name = jsonType.getString("name")

        if (name.startsWith("__"))
            continue

        val kind = Kind.valueOf(jsonType.getString("kind"))

        val newType = when (kind) {
            Kind.LIST ->
                throw IllegalStateException("no type should be defined as list")

            Kind.SCALAR ->
                QScalarType(name)

            Kind.OBJECT -> {
                QObjectType(name, arrayListOf(), null)
            }

            Kind.ENUM -> {
                val enumValues = jsonType["enumValues"].asArray().values
                        .map {
                            it.asObject().getString("name")
                        }

                QEnumType(name, enumValues)
            }
        }
        schema.types[name] = newType
    }

    val fieldsToFillWithTypes = arrayListOf<QField>()

    for (i in 0 until jsonTypes.size()) {
        val jsonType = jsonTypes[i].asObject()
        val name = jsonType.getString("name")

        if (name.startsWith("__"))
            continue

        val type = schema[name] as? QObjectType ?: continue
        val jsonFields = jsonType["fields"].asArray()

        for (j in 0 until jsonFields.size()) {
            val jsonField = jsonFields[j].asObject()
            val fieldName = jsonField.getString("name")
            val jsonFieldType = jsonField["type"].asObject()
            val isNonNull = jsonFieldType.getString("kind") == "NON_NULL"

            fun rec(jsonFieldType: JsonParser.ValueObject, isNonNull: Boolean): QField {
                val typeName: String? =
                        if (jsonFieldType.get("name").isNull())
                            null
                        else
                            jsonFieldType.getString("name")

                val typeKind = jsonFieldType.getString("kind")

                // TODO read jsonField['args']

                return when (typeKind) {
                    "OBJECT" -> {
                        val objectType = schema[typeName!!] as QObjectType
                        val fields = arrayListOf<QField>()

                        // If `objectType` is same as `type` then we may not have `objectType.selectedFields` filled yet.
                        // So, schedule it.

                        val objectField = QObjectField(fieldName, fields, objectType, !isNonNull)
                        fieldsToFillWithTypes.add(objectField)
                        objectField
                    }

                    "SCALAR" -> {
                        val scalarType = schema[typeName!!] as QScalarType
                        QScalarField(fieldName, scalarType, !isNonNull)
                    }

                    "ENUM" -> {
                        val enumType = schema[typeName!!] as QEnumType
                        QEnumField(fieldName, enumType, !isNonNull)
                    }

                    "LIST" -> {
                        val jsonFieldTypeOfType = jsonFieldType["ofType"].asObject()
                        val fieldSubTypeName = jsonFieldTypeOfType.getString("name")
                        val ofType = schema[fieldSubTypeName]

                        val selectedFields =
                                if (ofType is QObjectType)
                                    arrayListOf<QField>()
                                else
                                    null

                        val listField = QListField(fieldName, ofType, selectedFields, !isNonNull)

                        if (selectedFields != null)
                            fieldsToFillWithTypes.add(listField)

                        listField
                    }

                    "NON_NULL" -> {
                        val jsonFieldTypeOfType = jsonFieldType["ofType"].asObject()
                        rec(jsonFieldTypeOfType, true)
                    }

                    else -> {
                        throw IllegalStateException()
                    }
                }
            }

            val newField: QField =
                    rec(jsonFieldType, isNonNull)

            type.fields.add(newField)
        }
    }

    for (field in fieldsToFillWithTypes) {
        if (field is QObjectField) {
            field.selectedFields.addAll(field.fullType.fields)
        }
        else if (field is QListField) {
            if (field.ofType !is QObjectType)
                throw IllegalStateException()

            field.selectedFields!!.addAll(field.ofType.fields)
        }
    }

    return schema
}

/**
 * Filter entire possible field graph to a small subset defined by the query.
 */
fun mergeQueryWithSchema(doc: GraphQLParser.Document, schema: Schema): SelectedFieldOutput {
    fun traverseFields(selectionSet: GraphQLParser.SelectionSet, opType: OpType, path: List<String>): ArrayList<QField> {
        val selectedFields = arrayListOf<QField>()

        for (fieldCtx in selectionSet.fields()) {
            val fieldName = fieldCtx.fieldName.name
            val field = schema.findFieldByPath(opType, path + fieldName)
            val selectedField: QField =
                    if (field is QObjectField) {
                        val subFields = traverseFields(fieldCtx.selectionSet!!, opType, path + fieldName)
                        QObjectField(field.name, subFields, field.fullType, field.isNullable)
                    }
                    else if (field is QListField) {
                        val selectedSubFields =
                                if (fieldCtx.selectionSet != null)
                                    traverseFields(fieldCtx.selectionSet, opType, path + fieldName)
                                else null

                        QListField(field.name, field.ofType, selectedSubFields, field.isNullable)
                    }
                    else {
                        field
                    }

            selectedFields.add(selectedField)
        }

        return selectedFields
    }

    val ops = doc.operations()
            .map { opDef ->
                val opType = OpType.guess(opDef.operationType)

                val opFields = traverseFields(opDef.selectionSet, opType, listOf<String>())
                OperationDef(opType, opDef.name, opFields)
            }
            .toList()

    return SelectedFieldOutput(schema, ops)
}
