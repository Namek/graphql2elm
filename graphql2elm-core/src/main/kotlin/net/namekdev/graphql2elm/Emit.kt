package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parsers.GraphQLParser

fun emitElmQuery(op: OperationDef, emitCfg: CodeEmitterConfig): String {
    val emit = CodeEmitter(emitCfg)
    val hasInput = op.inputType != null
    val funcName = "someRequest"
    val (returnSelection, isReturnSelectionNullable) = inferReturnType(op)
    val (generatedTypes, allTypes) = traverseForNewTypes(op, returnSelection, emitCfg)

    emit.lineEmit("import GraphQL.Request.Builder exposing (..)")

    allTypes
            .groupBy { it.name }
            .map { it.value.get(0) }
            .mapNotNull { emitCfg.getType(it) }
            .filter { it.importPackageName != null }
            .groupBy { it.importPackageName }
            .forEach { (importPackageName, types) ->
                emit.lineBegin("import ", importPackageName!!, " exposing (")
                emit.lineEnd(types.joinToString { it.name }, ")")
            }

    emit.lineEmpty()
    emit.lineEmpty()

    // add a comment showing original query defined in GraphQL
    emit.lineEmit("{-| Decoder for query:")
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

    val returnTypeAsStr = {
        val sb = StringBuilder()
        val returnTypeAsStr = returnSelection.stringifyType(generatedTypes, emit.cfg)
        val isCompound = returnTypeAsStr.contains(' ')

        if (isCompound || isReturnSelectionNullable)
            sb.append("(")

        if (isReturnSelectionNullable) {
            sb.append("Maybe ")
        }

        if (isCompound)
            sb.append("(")

        sb.append(returnTypeAsStr)

        if (isCompound)
            sb.append(")")

        if (isCompound || isReturnSelectionNullable)
            sb.append(")")

        sb.toString()
    }

    emit.lineEnd("Request ", op.opType.name, " ", returnTypeAsStr())
    emit.lineBegin(funcName)

    if (hasInput) {
        emit.lineContinue(" input")
    }

    emit.lineEnd(" =")


    fun appendDecoder(fields: List<AField>, chainWithForObject: Boolean = false) {
        if (!chainWithForObject)
            emit.lineEnd("extract")

        emit.indentForward()

        var isFirstField = true
        for (field in fields) {
            val fieldType = field.type
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


            // field arguments
            if (fieldType is TObject) {
                emit.indentForward()
                emit.lineBegin("[")
            }
            else {
                emit.lineContinue(" [")
            }

            if (field.arguments.isNotEmpty()) {
                emit.lineContinue(" ")
                var argIndex = 0
                for (arg in field.arguments) {
                    emit.lineContinue("( \"${arg.name}\", Arg.")

                    if (arg.valueOrVariable!!.isVariable()) {
                        val varName = arg.valueOrVariable.varName()
                        emit.lineContinue("variable ", varName)

                        throw UnsupportedOperationException("TODO: variables are not yet implemented")
                    }
                    else {
                        // const value
                        if (arg.type is TScalar) {
                            if (isStandardScalarElmType(arg.type.name)) {
                                emit.lineContinue(arg.type.name.toLowerCase(), " ")
                                val value = arg.valueOrVariable.asValue()
                                val valueAsStr = when (value) {
                                    is GraphQLParser.BooleanValue ->
                                            value.boolean.toString()

                                    is GraphQLParser.NumberValue ->
                                        value.valueAsString

                                    is GraphQLParser.StringValue ->
                                            '"' + value.string + '"'

                                    else -> throw Exception("this is not happening")
                                }
                                emit.lineContinue(valueAsStr)
                            }
                            else {
                                TODO("custom scalars are not supported, yet")
                            }
                        }
                        else if (arg.type is TEnum) {
                            val value = (arg.valueOrVariable.asValue() as GraphQLParser.EnumValue)
                            emit.lineContinue("enum", value.value)
                        }
                        else if (arg.type is TObject) {
                            emit.lineContinue("object")

                             TODO("emit object arg")
                        }
                        else {
                            throw Exception("not sure hmm")
                        }

                        emit.lineContinue(" ")
                        emitElmValue(arg.asValue())
                    }

                    emit.lineContinue(")")

                    if (argIndex < field.arguments.size-1) {
                        emit.lineContinue(", ")
                    }

                    argIndex++
                }
                emit.lineContinue(" ")
            }

            emit.lineContinue("]")

            if (fieldType !is TObject) {
                emit.lineContinue(" ")
            }

            // field decoder
            if (emitCfg.emitMaybeForNullableFields && field.isNullable) {
                if (fieldType is TObject) {
                    emit.lineBegin("( ")
                    emit.indentForward()
                }
                else {
                    emit.lineContinue("(")
                }

                emit.lineContinue("nullable <| ")
            }

            if (fieldType is TObject) {
                emit.lineBegin("(")

                if (fieldType.fields.size > 1) {
                    emit.lineEnd(" object ", field.stringifyType(generatedTypes, emit.cfg))
                    appendDecoder(fieldType.fields, true)
                }
                else {
                    appendDecoder(fieldType.fields, false)
                }

                emit.lineEnd(")")
                emit.indentBackward()
            }
            else if (fieldType is TList) {
                val subType = fieldType.innerType
                val isSubTypeScalar = subType.isScalarType

                if (isSubTypeScalar) {
                    emit.lineContinue("(list ")
                }
                else {
                    emit.indentForward()
                    emit.lineBegin("(list")
                    emit.indentForward()
                    emit.lineBegin("(")
                }

                if (subType is TObject) {
                    emit.lineContinue(" object ", subType.stringifyType(generatedTypes, emit.cfg))
                    appendDecoder(subType.fields, true)
                }
                // TODO: does it even exist?
//                else if (subType is QListField) {
//                    appendDecoder(field.selectedFields, false)
//                }
                else if (isSubTypeScalar) {
                    emit.lineContinue(emitCfg.backendTypeToFrontendDecoder(subType as TScalar))
                }

                if (isSubTypeScalar) {
                    emit.lineContinue(")")
                }
                else {
                    emit.lineBegin(")")
                    emit.indentBackward()
                    emit.lineEmit(")")
                    emit.indentBackward()
                }
            }
            else if (fieldType is TScalar) {
                emit.lineContinue(emitCfg.backendTypeToFrontendDecoder(fieldType))
            }
            else if (fieldType is TEnum) {
                emit.lineContinue(emitCfg.backendTypeToFrontendDecoder(fieldType))
            }

            if (emitCfg.emitMaybeForNullableFields && field.isNullable) {
                if (fieldType is TObject) {
                    emit.lineEnd(")")
                    emit.indentBackward()
                }
                else {
                    emit.lineContinue(")")
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


    fun appendTypeDefinition(type: AType) {
        if (type is TObject) {
            emit.lineEmit("type alias ", emit.cfg.typePrefix, type.name, " = ")
            emit.indentForward()

            var isFirstField = true
            for (field in type.fields) {
                emit.lineBegin(if (isFirstField) "{" else ",")
                emit.lineEnd(" ${field.name} : ${field.stringifyType(generatedTypes, emit.cfg)}")

                isFirstField = false
            }
            emit.lineEmit("}")
        }
        else if (type is TEnum) {
            emit.lineEmit("type ", emit.cfg.typePrefix, type.name)
            emit.indentForward()

            var isFirstValue = true
            for (value in type.enumValues) {
                emit.lineBegin(if (isFirstValue) "=" else "|")
                emit.lineEnd(" $value")
                isFirstValue = false
            }
        }
        else if (type is TScalar) {
            if (emit.cfg.isKnownType(type)) {
                throw IllegalStateException("Any known or standard Elm type should not be here")
            }

            emit.lineEmit("decode", type.name.capitalize(), " = 0")
        }

        emit.indentBackward()
    }

    generatedTypes
            .forEach {
                emit.lineEmpty()
                emit.lineEmpty()
                appendTypeDefinition(it)
            }

    return emit.toString()
}

fun emitGraphQL(op: OperationDef, emit: CodeEmitter) {
    emit.lineEmit(op.opType.name.toLowerCase(), " {")

    fun rec(fields: List<AField>) {
        emit.indentForward(2)
        for (field in fields) {
            emit.lineBegin(field.name)

            if (emit.cfg.representNullableInEmittedGraphQLComment && field.isNullable)
                emit.lineContinue("?")

            val fieldType = field.type

            if (fieldType is TObject) {
                emit.lineEnd(" {")
                rec(fieldType.fields)
                emit.lineEnd("}")
            }
            else if (fieldType is TList) {
                // list of scalars or enums won't go deeper
                if (fieldType.innerType is TObject) {
                    emit.lineEnd(" {")
                    rec(fieldType.innerType.fields)
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
