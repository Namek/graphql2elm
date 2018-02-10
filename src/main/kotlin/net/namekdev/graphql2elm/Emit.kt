package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.AType.Kind.*
import kotlin.math.roundToInt

fun emitElmQuery(op: OperationDef, generatedTypesPrefix: String = "Q"): String {
    val emit = CodeEmitter(generatedTypesPrefix)
    val hasInput = op.inputType != null
    val funcName = "someRequest" + (Math.random()*100).roundToInt()
    val returnType = inferReturnType(op)
    val generatedTypes = identifyNewTypes(op)

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
    val returnTypeAsStr = "(" + returnType.type.stringify(generatedTypes, emit.typePrefix) + ")"
    emit.lineEnd("Request ", op.opType.name, " ", returnTypeAsStr)
    emit.lineBegin(funcName)

    if (hasInput) {
        emit.lineContinue(" input")
    }

    emit.lineEnd(" =")


    fun appendDecoder(fields: List<AField>, chainWithForObject: Boolean = false) {
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

            when (field.type.kind) {
                OBJECT -> {
                    emit.indentForward()
                    emit.lineBegin("[]")
                    emit.lineBegin("(")

                    if (field.type.fields.size > 1) {
                        emit.lineEnd(" object ", emit.typePrefix, field.type.name!!)
                        appendDecoder(field.type.fields, true)
                    }
                    else {
                        appendDecoder(field.type.fields, false)
                    }

                    emit.lineEnd(")")
                    emit.indentBackward()
                }

                LIST -> {
                    emit.lineContinue(" []")

                    val subType = field.type.ofType!!
                    val isSubTypeFlat = subType.kind == SCALAR || subType.kind == ENUM

                    if (isSubTypeFlat) {
                        emit.lineContinue(" (list ")
                    }
                    else {
                        emit.indentForward()
                        emit.lineBegin("(list")
                        emit.indentForward()
                        emit.lineBegin("(")
                    }

                    when (subType.kind) {
                        OBJECT -> {
                            emit.lineContinue(" object ", emit.typePrefix, subType.name!!)
                            appendDecoder(field.type.fields, true)
                        }
                        LIST -> {
                            throw Exception("not sure what to do here")
                            appendDecoder(field.type.fields, false)
                        }
                        SCALAR, ENUM -> {
                            emit.lineContinue(subType.name!!)
                        }
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

                SCALAR -> {
                    emit.lineContinue(" [] ")
                    emit.lineContinue(backendTypeToFrontendDecoder(field.type.name!!))
                }

                ENUM -> {
//                    throw Exception("TODO")
                }
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

    fun appendRecordAlias(type: AType) {
        emit.lineBegin("type ")
        if (type.kind == OBJECT) {
            emit.lineEnd("alias ", emit.typePrefix, type.name!!, " = ")
            emit.indentForward()

            var isFirstField = true
            for (field in type.fields) {
                emit.lineBegin(if (isFirstField) "{" else ",")
                emit.lineEnd(" ${field.name} : ${field.type.stringify(generatedTypes, emit.typePrefix)}")

                isFirstField = false
            }
            emit.lineEmit("}")
        }
        else if (type.kind == ENUM) {
            emit.lineEnd(emit.typePrefix, type.name!!)
            emit.indentForward()

            var isFirstValue = true
            for (value in type.enumValues!!) {
                emit.lineBegin(if (isFirstValue) "=" else "|")
                emit.lineEnd(" $value")
                isFirstValue = false
            }
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

    fun rec(fields: List<AField>) {
        emit.indentForward(2)
        for (field in fields) {
            emit.lineBegin(field.name)

            if (field.type.fields.size > 0) {
                emit.lineEnd(" {")
                rec(field.type.fields)
                emit.lineEmit("}")
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

fun inferReturnType(op: OperationDef): AField {
    var fields = op.fields
    var cur: AField? = null
    var depth = 0

    loop@ while (fields.size == 1) {
        cur = fields[0]
        when (cur.type.kind) {
            OBJECT -> {
                fields = cur.type.fields
                cur = fields[0]
                depth += 1
            }

            else -> {
                // TODO normal
                break@loop
            }
        }
    }

    if (cur == null) {
        val newType = AType("rootValue", OBJECT, null)
        newType.fields.addAll(fields)
        cur = AField("rootValue", newType)
    }

    return cur
}

fun identifyNewTypes(op: OperationDef): List<AType> {
    val foundTypes = mutableSetOf<AType>()
    val visitedFields = mutableSetOf<AField>()

    fun rec(fields: List<AField>) {
        for (field in fields) {
            if (visitedFields.contains(field) || foundTypes.contains(field.type))
                continue

            visitedFields.add(field)

            when (field.type.kind) {
                OBJECT -> {
                    foundTypes.add(field.type)
                    rec(field.type.fields)
                }

                LIST -> {
                    val subType = field.type.ofType!!

                    when (subType.kind) {
                        OBJECT, ENUM -> {
                            //  remember the filtered list of fields, not the original one!
                            val foundOne = subType.copy()
                            foundOne.fields = field.type.fields
                            foundTypes.add(foundOne)

                            rec(field.type.fields)
                        }
                        LIST -> {
                            throw Exception("TODO: not really sure what to do here")
                            rec(subType.ofType!!.fields)
                        }
                    }
                }

                ENUM -> {
                    foundTypes.add(field.type)
                }
            }
        }
    }
    rec(op.fields)

    assert(foundTypes.all { !it.isFullInfo })

    val allTypes = foundTypes
        .toList()
        .sortedBy { "${it.kind}_${it.name}" }

    // now let's rename types which names are duplicated
    return allTypes
        .groupBy { it.name!! }
        .map {
            val types = it.value

            if (types.size > 1) {
                var id = 1
                types.map { type  ->
                    type.name += "${id++}"
                    type
                }
            } else {
                types
            }
        }
        .flatten()
}
