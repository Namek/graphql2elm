package net.namekdev.graphql2elm

import kotlin.math.roundToInt

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
                emit.lineEnd(types.map { it.name }.joinToString(), ")")
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


    fun appendDecoder(fields: List<QField>, chainWithForObject: Boolean = false) {
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


            // TODO: field arguments, right now we ignore them
            if (field is QObjectField) {
                emit.indentForward()
                emit.lineBegin("[]")
            }
            else {
                emit.lineContinue(" [] ")
            }

            // field decoder
            if (emitCfg.emitMaybeForNullableFields && field.isNullable) {
                if (field is QObjectField) {
                    emit.lineBegin("( ")
                    emit.indentForward()
                }
                else {
                    emit.lineContinue("(")
                }

                emit.lineContinue("nullable <| ")
            }

            if (field is QObjectField) {
                emit.lineBegin("(")

                if (field.selectedFields.size > 1) {
                    emit.lineEnd(" object ", field.stringifyType(generatedTypes, emit.cfg))
                    appendDecoder(field.selectedFields, true)
                }
                else {
                    appendDecoder(field.selectedFields, false)
                }

                emit.lineEnd(")")
                emit.indentBackward()
            }
            else if (field is QListField) {
                val subType = field.ofType
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

                if (subType is QObjectType) {
                    emit.lineContinue(" object ", subType.stringifyType(generatedTypes, emit.cfg, field.selectedFields))
                    appendDecoder(field.selectedFields!!, true)
                }
                // TODO: does it even exist?
//                else if (subType is QListField) {
//                    appendDecoder(field.selectedFields, false)
//                }
                else if (isSubTypeScalar) {
                    emit.lineContinue(emitCfg.backendTypeToFrontendDecoder(subType))
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
            else if (field is QScalarField) {
                emit.lineContinue(emitCfg.backendTypeToFrontendDecoder(field.type))
            }
            else if (field is QEnumField) {
                emit.lineContinue(emitCfg.backendTypeToFrontendDecoder(field.type))
            }

            if (emitCfg.emitMaybeForNullableFields && field.isNullable) {
                if (field is QObjectField) {
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


    fun appendTypeDefinition(type: QType) {
        if (type is QObjectType) {
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
        else if (type is QEnumType) {
            emit.lineEmit("type ", emit.cfg.typePrefix, type.name)
            emit.indentForward()

            var isFirstValue = true
            for (value in type.enumValues) {
                emit.lineBegin(if (isFirstValue) "=" else "|")
                emit.lineEnd(" $value")
                isFirstValue = false
            }
        }
        else if (type is QScalarType) {
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

    fun rec(fields: List<QField>) {
        emit.indentForward(2)
        for (field in fields) {
            emit.lineBegin(field.name)

            if (emit.cfg.representNullableInEmittedGraphQLComment && field.isNullable)
                emit.lineContinue("?")

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

/**
 * Infers the shortest type it's needed.
 * For instance, if there is selection like: 1 field -> 1 field -> 3 fields
 * then it's reduced by the first two levels of that selection or
 * just a first one if the second is a list.
 * TODO check if that's all true (needs tests)
 */
fun inferReturnType(op: OperationDef): Pair<QField, Boolean> {
    var fields = op.fields
    var cur: QField? = null
    var depth = 0
    var foundAnyNullable = false

    loop@ while (fields.size == 1) {
        cur = fields[0]
        if (cur.isNullable) {
            foundAnyNullable = true
        }

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

    return Pair(cur!!, foundAnyNullable)
}

fun traverseForNewTypes(op: OperationDef, root: QField, emitCfg: CodeEmitterConfig): Pair<List<QType>, Set<QType>> {
    val newTypes = mutableSetOf<QType>()
    val allTypes = mutableSetOf<QType>()
    val visitedFields = mutableSetOf<QField>()

    fun rec(fields: List<QField>) {
        for (field in fields) {
            if (field == root) {
                // the root (return type of whole query) reduces some types,
                // we won't need those
                newTypes.clear()
                allTypes.clear()
            }

            if (visitedFields.contains(field))
                continue

            visitedFields.add(field)

            when (field) {
                is QObjectField -> {
                    val newType = field.toType()
                    allTypes.add(field.fullType)

                    if (!emitCfg.isKnownType(newType)) {
                        newTypes.add(newType)
                        rec(field.selectedFields)
                    }
                }
                is QListField -> {
                    allTypes.add(field.ofType.fullType())

                    if (!field.ofType.isScalarType) {
                        val newType = QObjectType(field.ofType.name, field.selectedFields!!, field.ofType)
                        if (!emitCfg.isKnownType(newType)) {
                            newTypes.add(newType)
                            field.selectedFields.let { rec(it) }
                        }
                    }
                    else {
                        if (!emitCfg.isKnownType(field.ofType)) {
                            newTypes.add(field.ofType)
                        }
                    }
                }
                is QEnumField -> {
                    allTypes.add(field.type.fullType())

                    if (!emitCfg.isKnownType(field.type)) {
                        newTypes.add(field.type)
                    }
                }
                is QScalarField -> {
                    allTypes.add(field.type.fullType())

                    if (!emitCfg.isKnownType(field.type)) {
                        newTypes.add(field.type)
                    }
                }
            }
        }
    }
    rec(op.fields)

    // now let's rename types which names are duplicated
    val generatedTypes = newTypes
            .toList()
            .sortedBy { "${it.fullType().name}_${it.name}" }
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


    return Pair(generatedTypes, allTypes)
}
