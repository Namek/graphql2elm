package net.namekdev.graphql2elm

import kotlin.math.roundToInt

fun emitElmQuery(op: OperationDef, generatedTypesPrefix: String = "Q"): String {
    val emit = CodeEmitter(generatedTypesPrefix)
    val hasInput = op.inputType != null
    val funcName = "someRequest" + (Math.random()*100).roundToInt()
    val returnSelection = inferReturnType(op)
    val generatedTypes = identifyNewTypes(op, returnSelection)

    emit.lineEmit("import GraphQL.Request.Builder exposing (..)")
    emit.lineEmpty()

    // add a comment showing original query defined in GraphQL
    emit.lineEmit("{-| The exact call is:")
    emit.lineEmpty()
    emit.indentForward()
    emitGraphQL(op, emit)
    emit.indentBackward()
    emit.lineEmpty()
    emit.lineEmit("-}")

    emit.lineBegin(funcName, " : ")

    if (hasInput) {
        // TODO: inputType "-> "
    }
    val returnTypeAsStr = "(" + returnSelection.stringifyType(generatedTypes, emit.typePrefix) + ")"
    emit.lineEnd("Request ", op.opType.name, " ", returnTypeAsStr)
    emit.lineBegin(funcName)

    if (hasInput) {
        emit.lineContinue(" input")
    }

    emit.lineEnd(" =")


    fun appendDecoder(fields: List<QField>, chainWithForObject: Boolean = false) {
        assert(fields.size > 0)

        if (!chainWithForObject)
            emit.lineEnd("extract")

        emit.indentForward()

        var isFirstField = true
        for (field in fields) {
            emit.lineBegin()

            if (chainWithForObject) {
                emit.lineContinue("|> with (")
            }
            else {
                if (fields.size > 1) {
                    if (isFirstField) {
                        emit.lineContinue("[")
                        isFirstField = false
                    }
                    else {
                        emit.lineContinue(",")
                    }
                }
                else {
                    emit.lineContinue("(")
                }

                emit.lineContinue(" ")
            }


            emit.lineContinue("field \"", field.name, "\"")

            if (field is QObjectField) {
                emit.indentForward()
                emit.lineBegin("[]")
                emit.lineBegin("(")

                if (field.selectedFields.size > 1) {
                    emit.lineEnd(" object ", field.stringifyType(generatedTypes, emit.typePrefix))
                    appendDecoder(field.selectedFields, true)
                }
                else {
                    appendDecoder(field.selectedFields, false)
                }

                emit.lineEnd(")")
                emit.indentBackward()
            }
            else if (field is QListField) {
                emit.lineContinue(" []")

                val subType = field.ofType
                val isSubTypeFlat = subType.isFlatType

                if (isSubTypeFlat) {
                    emit.lineContinue(" (list ")
                }
                else {
                    emit.indentForward()
                    emit.lineBegin("(list")
                    emit.indentForward()
                    emit.lineBegin("(")
                }

                if (subType is QObjectType) {
                    emit.lineContinue(" object ", subType.stringifyType(generatedTypes, emit.typePrefix, field.selectedFields))
                    appendDecoder(field.selectedFields!!, true)
                }
                // TODO: does it even exist?
//                else if (subType is QListField) {
//                    appendDecoder(field.selectedFields, false)
//                }
                else if (subType.isFlatType) {
                    emit.lineContinue(subType.name)
                }

                if (isSubTypeFlat) {
                    emit.lineContinue(")")
                }
                else {
                    emit.lineBegin(")")
                    emit.indentBackward()
                    emit.lineEmit(")")
                    emit.indentBackward()
                }
            }
            else if (field is QScalarField) {
                emit.lineContinue(" [] ")
                emit.lineContinue(backendTypeToFrontendDecoder(field.type.name))
            }
            else if (field is QEnumField) {
//                    throw Exception("TODO")
            }


            if (chainWithForObject) {
                emit.lineEnd(")")
            }
            else {
                if (fields.size > 1) {
                    emit.lineContinue("]")
                }
                else {
                    emit.lineEmit(")")
                }
            }
        }

        emit.indentBackward()
    }

    emit.indentForward()
    appendDecoder(op.fields, false)

    emit.indentForward()
    emit.lineEmit("|> queryDocument")
    emit.lineBegin("|> request ")

    if (hasInput)
        emit.lineContinue("input")
    else
        emit.lineContinue("{}")

    emit.lineEnd()
    emit.indentReset()

    // Return type
    emit.lineEmpty()

    fun appendRecordAlias(type: QType) {
        if (type is QObjectType) {
            emit.lineEmit("type alias ", emit.typePrefix, type.name, " = ")
            emit.indentForward()

            var isFirstField = true
            for (field in type.fields) {
                emit.lineBegin(if (isFirstField) "{" else ",")
                emit.lineEnd(" ${field.name} : ${field.stringifyType(generatedTypes, emit.typePrefix)}")

                isFirstField = false
            }
            emit.lineEmit("}")
        }
        else if (type is QEnumType) {
            emit.lineEmit("type ", emit.typePrefix, type.name)
            emit.indentForward()

            var isFirstValue = true
            for (value in type.enumValues) {
                emit.lineBegin(if (isFirstValue) "=" else "|")
                emit.lineEnd(" $value")
                isFirstValue = false
            }
        }
        else if (type is QScalarType) {
            if (type.isStandardElmType()) {
                throw IllegalStateException("standard Elm type should not be here")
            }

            // TODO emit decoder?
            emit.lineEmit("decode", type.name.capitalize(), " = 0")
        }

        emit.indentBackward()
        emit.lineEmpty()
    }

    generatedTypes
        .forEach {
            appendRecordAlias(it)
        }

    return emit.toString()
}

fun emitGraphQL(op: OperationDef, emit: CodeEmitter) {
    emit.lineEmit(op.opType.name.toLowerCase(), " {")

    fun rec(fields: List<QField>) {
        emit.indentForward(2)
        for (field in fields) {
            emit.lineBegin(field.name)

            if (field is QObjectField) {
                emit.lineEnd(" {")
                rec(field.selectedFields)
                emit.lineEnd("}")
            }
            else if (field is QListField) {
                // list of scalars or enums won't go deeper
                if (field.selectedFields != null) {
                    emit.lineEnd(" {")
                    rec(field.selectedFields)
                    emit.lineEnd("}")
                }
            }
            else {
                emit.lineEnd()
            }
        }
        emit.indentBackward(2)
    }
    rec(op.fields)

    emit.lineEmit("}")
}

fun backendTypeToFrontendDecoder(str: String): String {
    return when (str) {
        "Boolean" -> "bool"
        else -> str.toLowerCase()
    }
}

fun backendTypeToFrontendType(str: String): String {
    return when (str) {
        "Boolean" -> "Bool"
        else -> str
    }
}

/**
 * Infers the shortest type it's needed.
 * For instance, if there is selection like: 1 field -> 1 field -> 3 fields
 * then it's reduced by the first two levels of that selection or
 * just a first one if the second is a list.
 * TODO check if that's all true (needs tests)
 */
fun inferReturnType(op: OperationDef): QField {
    var fields = op.fields
    var cur: QField? = null
    var depth = 0

    loop@ while (fields.size == 1) {
        cur = fields[0]
        if (cur is QObjectField) {
            fields = cur.selectedFields
            cur = fields[0]
            depth += 1
        }
        else {
            // TODO normal
            break@loop
        }
    }

    return cur!!
}

fun identifyNewTypes(op: OperationDef, root: QField): List<QType> {
    val foundTypes = mutableSetOf<QType>()
    val visitedFields = mutableSetOf<QField>()
    var hasVisitedRoot = false

    fun rec(fields: List<QField>) {
        for (field in fields) {
            if (field == root) {
                // the root (return type of whole query) reduces some types
                hasVisitedRoot = true
                foundTypes.clear()
            }

            if (visitedFields.contains(field))
                continue

            visitedFields.add(field)

            when (field) {
                is QObjectField -> {
                    foundTypes.add(field.toType())
                    rec(field.selectedFields)
                }
                is QListField -> {
                    if (!field.ofType.isFlatType) {
                        foundTypes.add(field.getObjectType())
                        field.selectedFields?.let { rec(it) }
                    }
                }
                is QEnumField -> {
                    foundTypes.add(field.type)
                }
                is QScalarField -> {
                    if (!field.type.isStandardElmType()) {
                        foundTypes.add(field.type)
                    }
                }
            }
        }
    }
    rec(op.fields)

    val allTypes = foundTypes
        .toList()
        .sortedBy { "${it.javaClass.simpleName}_${it.name}" }

    // now let's rename types which names are duplicated
    return allTypes
        .groupBy { it.name }
        .map {
            val types = it.value

            if (types.size > 1) {
                var id = 1
                types.map { type ->
                    type.getRenamed("${type.name}${id++}")
                }
            }
            else {
                types
            }
        }
        .flatten()
}
