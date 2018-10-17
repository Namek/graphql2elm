package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parsers.GraphQLParser

fun emitElmCode(op: OperationDef, emitCfg: CodeEmitterConfig): String {
    val emit = CodeEmitter(emitCfg)
    val inputType = inferInputType(op)
    val (returnSelection, nullableCount) = inferReturnType(op)
    val (generatedTypes, allTypes) = traverseForNewTypes(op, returnSelection, emitCfg)

    emit.lineEmit("import GraphQL.Request.Builder exposing (..)")

    if (op.hasAnyUsageOfArguments())
        emit.lineEmit("import GraphQL.Request.Builder.Arg as Arg")

    if (inputType != null)
        emit.lineEmit("import GraphQL.Request.Builder.Var as Var")

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

    emit.lineBegin(op.functionName(), " : ")

    val returnTypeAsStr = {
        val sb = StringBuilder()
        val returnTypeAsStr = returnSelection.stringifyType(generatedTypes, emit.cfg)
        val isCompound = returnTypeAsStr.contains(' ')
        val hasAlreadyEmittedMaybe = emit.cfg.emitMaybeForNullableFields && returnSelection.isNullable
        val shouldEmitMaybe = !hasAlreadyEmittedMaybe && emit.cfg.emitMaybeForNullableFields
        var braceLevels = if (isCompound) 1 else 0

        if (shouldEmitMaybe) {
            sb.append("(Maybe ")
            braceLevels += 1
        }

        if (isCompound)
            sb.append("(")

        sb.append(returnTypeAsStr)

        while (braceLevels-- > 0)
            sb.append(")")

        sb.toString()
    }()

    if (inputType != null) {
        emit.lineContinue(op.inputTypeName())
        if (!inputType.isScalarType)
            emit.lineContinue(" a")
        emit.lineContinue(" -> ")
    }

    emit.lineContinue("Request ", op.opType.name, " ", returnTypeAsStr)

    emit.lineBegin(op.functionName())
    if (inputType != null) {
        emit.lineContinue(" input")
    }
    emit.lineEnd(" =")

    // define variables
    if (op.variables.isNotEmpty()) {
        emit.lineEmit("let")
        emit.indentForward()

        op.variables.forEachIndexed { i, v ->
            emit.lineEmit("var", v.name.capitalize(), " =")
            emit.indentForward()

            emit.lineBegin("Var.", if (v.isNullable) "optional " else "required ")
            emit.lineContinue("\"${v.name}\" .${v.name} Var.")

            val funcName = when (v.type) {
                is TScalar -> {
                    when (v.type.name) {
                        "String" -> "string"
                        "Int" -> "int"
                        "Float" -> "float"
                        "Boolean" -> "bool"
                        else -> throw Exception("well")
                    }
                }
                else -> throw Exception("variable of unsupported type?")
            }
            emit.lineEnd(funcName)

            emit.indentBackward()

            if (i < op.variables.size-1) {
                emit.lineEmpty()
            }
        }

        emit.indentBackward()
        emit.lineEmit("in")
    }


    fun appendDecoder(fields: List<AField>, leftNullablesToReduce: Int, chainWithForObject: Boolean = false) {
        if (!chainWithForObject)
            emit.lineEnd("extract")

        emit.indentForward()

        var isFirstField = true
        for (field in fields) {
            val fieldType = field.type
            val emitThisFieldAsNullable = emitCfg.emitMaybeForNullableFields
                && field.isNullable && leftNullablesToReduce <= 0

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
                        emit.lineContinue("variable var", varName.capitalize())
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
            if (emitThisFieldAsNullable) {
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

                val deepLeftNullablesToReduce =
                    if (field.isNullable)
                        leftNullablesToReduce - 1
                    else
                        leftNullablesToReduce

                if (fieldType.fields.size > 1) {
                    emit.lineEnd(" object ", field.stringifyType(generatedTypes, emit.cfg))
                    appendDecoder(fieldType.fields, deepLeftNullablesToReduce, true)
                }
                else {
                    appendDecoder(fieldType.fields, deepLeftNullablesToReduce, false)
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
                    appendDecoder(subType.fields, leftNullablesToReduce - 1)
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

            if (emitThisFieldAsNullable) {
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
    appendDecoder(op.fields, nullableCount - 1, false)

    emit.indentForward()
    emit.lineBegin("|> ")
    emit.lineContinue(if (op.opType == OpType.Mutation) "mutation" else "query")
    emit.lineEnd("Document")
    emit.lineBegin("|> request ")
    if (inputType == null)
        emit.lineContinue("()")
    else
        emit.lineContinue("input")
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

    // type alias for input (variables)
    if (inputType != null) {
        emit.lineEmpty()
        emit.lineEmpty()
        emit.lineEmit("type alias ", op.inputTypeName(), " vars = ")
        emit.indentForward()
        emit.lineEmit("{ vars")
        emit.indentForward()

        var isFirstField = true
        for (field in inputType.fields) {
            emit.lineBegin(if (isFirstField) "|" else ",")
            emit.lineEnd(" ${field.name} : ${field.stringifyType(generatedTypes, emit.cfg)}")

            isFirstField = false
        }
        emit.indentBackward()
        emit.lineEmit("}")
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
    emit.lineBegin(op.opType.name.toLowerCase())

    if (op.variables.isNotEmpty()) {
        emit.lineContinue("(")
        op.variables.forEachIndexed { i, v ->
            emit.lineContinue("\$${v.name}: ${v.type.name}")

            if (!v.isNullable) {
                emit.lineContinue("!")
            }

            if (v.defaultValue != null) {
                emit.lineContinue(" = ", emitElmValue(v.defaultValue))
            }

            if (i < op.variables.size-1) {
                emit.lineContinue(", ")
            }
        }

        emit.lineContinue(")")
    }

    emit.lineEnd(" {")

    fun rec(fields: List<AField>) {
        emit.indentForward(2)
        for (field in fields) {
            emit.lineBegin(field.name)

            if (emit.cfg.representNullableInEmittedGraphQLComment && field.isNullable)
                emit.lineContinue("?")

            if (field.arguments.isNotEmpty()) {
                emit.lineContinue("(")
                field.arguments.forEachIndexed { i, arg ->
                    emit.lineContinue("${arg.name}: ")
                    when (arg.valueOrVariable) {
                        is GraphQLParser.Value -> {
                            emitElmValue(arg.valueOrVariable)
                        }
                        is GraphQLParser.Variable -> {
                            emit.lineContinue("\$${arg.valueOrVariable.name}")
                        }
                    }

                    if (i < field.arguments.size-1) {
                        emit.lineContinue(", ")
                    }
                }
                emit.lineContinue(")")
            }

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
