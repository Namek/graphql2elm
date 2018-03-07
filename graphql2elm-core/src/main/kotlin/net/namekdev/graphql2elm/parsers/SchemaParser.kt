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

        val kind = jsonType.getString("kind")
        val newType = when (kind) {
            "SCALAR" ->
                TScalar(name)

            "OBJECT" ->
                TObject(name, arrayListOf())

            "ENUM" -> {
                val enumValues = jsonType["enumValues"].asArray().values
                        .map {
                            it.asObject().getString("name")
                        }

                TEnum(name, enumValues)
            }

            "LIST" ->
                throw IllegalStateException("no type should be defined as list")

            else ->
                throw Exception("unknown kind: $kind")
        }
        schema.types[name] = newType
    }

    val fieldsToFillWithTypes = mutableSetOf<AField>()

    for (i in 0 until jsonTypes.size()) {
        val jsonType = jsonTypes[i].asObject()
        val name = jsonType.getString("name")

        if (name.startsWith("__"))
            continue

        val type = schema[name] as? TObject ?: continue
        val jsonFields = jsonType["fields"].asArray()

        for (j in 0 until jsonFields.size()) {
            val jsonField = jsonFields[j].asObject()
            val jsonFieldType = jsonField["type"].asObject()
            val isNonNull = jsonFieldType.getString("kind") == "NON_NULL"
            val newField: AField = parseField(jsonField, schema, isNonNull)

            type.fields.add(newField)

            if (newField.type is TObject) {
                fieldsToFillWithTypes.add(newField)
            }
            else if (newField.type is TList && newField.type.hasObject()) {
                var innerType = newField.type.innerType
                while (innerType is TList) {
                    innerType = innerType.innerType
                }

                fieldsToFillWithTypes.add(newField)
            }
        }
    }

    for (field in fieldsToFillWithTypes) {
        val fieldType = field.type
        if (fieldType is TObject) {
            fieldType.fields.addAll((fieldType.originalType as TObject).fields)
        }
        else if (fieldType is TList) {
            if (fieldType.innerType !is TObject)
                throw IllegalStateException()

            fieldType.innerType.fields.addAll(fieldType.innerType.fields)
        }
    }

    return schema
}

private fun parseField(jsonField: JsonParser.ValueObject, schema: Schema, isNonNull: Boolean): AField {
    val fieldName = jsonField.getString("name")
    val jsonFieldType = jsonField["type"].asObject()

    val arguments = jsonField.getArray("args")
            .map { it.asObject() }
            .map {
                val argName = it.getString("name")
                val (argType, isNullable) = parseType(it.getObject("type"), schema)

                AArgument(argName, argType, isNullable)
            }

    val (fieldType, isNullable) = parseType(jsonFieldType, schema, !isNonNull)

    return AField(fieldName, fieldType, isNullable, arguments)
}

private fun parseType(jsonFieldType: JsonParser.ValueObject, schema: Schema, isNullable: Boolean = true): Pair<AType, Boolean> {
    val typeKind = jsonFieldType.getString("kind")
    val typeName: String? =
            if (jsonFieldType.get("name").isNull())
                null
            else
                jsonFieldType.getString("name")

    return when (typeKind) {
        "NON_NULL" -> {
            val (subType, _) = parseType(jsonFieldType.getObject("ofType"), schema, false)
            Pair(subType, false)
        }

        "LIST" -> {
            val (innerType, _) = parseType(jsonFieldType.getObject("ofType"), schema, isNullable)
            Pair(innerType, isNullable)
        }

        "OBJECT" -> {
            var type = schema[typeName!!] as TObject
            Pair(type, isNullable)
        }

        "SCALAR" -> {
            var type = schema[typeName!!] as TScalar
            Pair(type, isNullable)
        }

        "ENUM" -> {
            var type = schema[typeName!!] as TEnum
            Pair(type, isNullable)
        }

        else -> {
            throw IllegalStateException()
        }
    }
}

/**
 * Filter entire possible field graph to a small subset defined by the query.
 */
fun mergeSchemaIntoQuery(doc: GraphQLParser.Document, schema: Schema): SelectedFieldOutput {
    fun traverseFields(selectionSet: GraphQLParser.SelectionSet, opType: OpType, path: List<String>): ArrayList<AField> {
        val selectedFields = arrayListOf<AField>()

        for (fieldCtx in selectionSet.fields()) {
            val fieldName = fieldCtx.fieldName.name
            val field = schema.findFieldByPath(opType, path + fieldName)
            val fieldType = field.type
            val args = fieldCtx.arguments?.map { f ->
                val fullField = field.arguments.find { it.name == f.name }!!
                AArgument(f.name, fullField.type, fullField.isNullable, f.valueOrVariable)
            } ?: listOf()

            val specializedField =
                    when (fieldType) {
                        is TObject -> {
                            val subFields = traverseFields(fieldCtx.selectionSet!!, opType, path + fieldName)
                            val selectedFieldsType = TObject(fieldType.name, subFields)
                            selectedFieldsType.originalType = fieldType
                            AField(field.name, selectedFieldsType, field.isNullable, args)
                        }
                        is TList -> {
                            val selectedSubFieldsType =
                                    if (fieldCtx.selectionSet != null) {
                                        if (fieldType.innerType is TObject) {
                                            val subfields = traverseFields(fieldCtx.selectionSet, opType, path + fieldName)

                                            val objType = fieldType.innerType as TObject
                                            val newObjType = objType.specialize(subfields)

                                            TList(newObjType, fieldType.allowNulls)
                                        }
                                        else {
                                            // TODO usually innerType is TObject but may be a TList of TList of ... TObject
                                            TODO("support list of lists, please?")
                                        }
                                    }
                                    else {
                                        (fieldType as TList).innerType
                                    }

                            AField(field.name, selectedSubFieldsType, field.isNullable, args)
                        }
                        else -> {
                            // TODO should be cloned? not sure.
                            field
                        }
                    }

            selectedFields.add(specializedField)
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
