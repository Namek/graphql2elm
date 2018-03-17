package net.namekdev.graphql2elm


/**
 * Infers the shortest type it's needed.
 * For instance, if there is selection like: 1 field -> 1 field -> 3 fields
 * then it's reduced by the first two levels of that selection or
 * just a first one if the second is a list.
 * TODO check if that's all true (needs tests)
 */
fun inferReturnType(op: OperationDef): Pair<AField, Boolean> {
    var fields = op.fields
    var cur: AField? = null
    var depth = 0
    var foundAnyNullable = false

    loop@ while (fields.size == 1) {
        cur = fields[0]
        if (cur.isNullable) {
            foundAnyNullable = true
        }

        if (cur.type is TObject) {
            fields = (cur.type as TObject).fields
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

fun inferInputType(op: OperationDef): TObject? {
    if (op.variables.isEmpty())
        return null

    val fields = op.variables
            .map { v ->
                AField(v.name, v.type, v.isNullable, listOf())
            }
            .toTypedArray()

    return TObject(op.name.capitalize() + "Input", arrayListOf(elements = *fields))
}

fun traverseForNewTypes(op: OperationDef, root: AField, emitCfg: CodeEmitterConfig): Pair<List<ANonGenericType>, Set<ANonGenericType>> {
    val newTypes = mutableSetOf<ANonGenericType>()
    val allTypes = mutableSetOf<ANonGenericType>()
    val visitedFields = mutableSetOf<AField>()

    fun rec(fields: List<AField>) {
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

            val fieldType = field.type
            when (fieldType) {
                is TObject -> {
                    allTypes.add(fieldType.originalType as TObject)

                    if (!emitCfg.isKnownType(fieldType)) {
                        newTypes.add(fieldType)
                        rec(fieldType.fields)
                    }
                }
                is TList -> {
                    if (fieldType.innerType is ANonGenericType) {
                        allTypes.add(fieldType.innerType.originalType)

                        if (fieldType.innerType is TObject) {
                            val newType = TObject(fieldType.innerType.name, fieldType.innerType.fields)

                            if (!emitCfg.isKnownType(newType)) {
                                newTypes.add(newType)
                                fieldType.innerType.fields.let { rec(it) }
                            }
                        }
                        else if (fieldType.innerType.isScalarType) {
                            if (!emitCfg.isKnownType(fieldType.innerType)) {
                                newTypes.add(fieldType.innerType)
                            }
                        }
                        else {
                            TODO("not supported?")
                        }
                    }
                }
                is TEnum -> {
                    allTypes.add(fieldType.originalType)

                    if (!emitCfg.isKnownType(fieldType)) {
                        newTypes.add(fieldType)
                    }
                }
                is TScalar -> {
                    allTypes.add(fieldType.originalType)

                    if (!emitCfg.isKnownType(fieldType)) {
                        newTypes.add(fieldType)
                    }
                }
            }
        }
    }
    rec(op.fields)

    // now let's rename types which names are duplicated
    val generatedTypes = newTypes
            .toList()
            .sortedBy { "${it.originalType.name}_${it.name}" }
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

