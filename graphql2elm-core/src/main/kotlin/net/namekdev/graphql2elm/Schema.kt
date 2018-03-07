package net.namekdev.graphql2elm


import net.namekdev.graphql2elm.parsers.GraphQLParser

class Schema(val queryTypeName: String, val mutationTypeName: String) {
    val types: HashMap<String, AType> = hashMapOf()

    operator fun get(key: String): AType? {
        return types.get(key)
    }

    fun findFieldByPath(opType: OpType, path: List<String>): AField {
        val rootTypeName =
                when (opType) {
                    OpType.Query -> queryTypeName
                    OpType.Mutation -> mutationTypeName
                }

        val root = types[rootTypeName]!!
        var cur: AField = enter(root, path[0])
        for (i in 1 until path.size) {
            val t = cur.type
            if (t is TList) {
                cur = enter(t.innerType, path[i])
            } else if (t is TObject) {
                cur = enter(t, path[i])
            } else {
                throw IllegalStateException("can't enter into field ${cur.name}")
            }
        }

        return cur
    }

    private fun enter(type: AType, into: String): AField {
        if (type is TObject) {
            val field = type.fields.find { it.name == into }
            return field ?: throw Exception("no field '$into' in '${type.name}'")
        }

        throw IllegalStateException("can't enter into type ${type}")
    }
}


abstract class AType(val name: String) {
    val isScalarType = this is TEnum || this is TScalar

    abstract fun stringifyType(generatedTypes: List<ANonGenericType>? = null, cfg: CodeEmitterConfig? = null): String
}

abstract class ANonGenericType(name: String) : AType(name) {
    var originalType: ANonGenericType = this

    abstract fun getRenamed(newName: String): ANonGenericType

    override fun stringifyType(generatedTypes: List<ANonGenericType>?, cfg: CodeEmitterConfig?): String {
        val sb = StringBuilder()

        fun matchesMe(type: ANonGenericType): Boolean {
            if (type.originalType.name != this.originalType.name) {
                return false
            }

            if (this is TObject && type is TObject) {
                return type.fields == this.fields
            }

            return true
        }

        val theType = generatedTypes?.find { matchesMe(it) } ?: this
        if (cfg != null) {
            if (!cfg.isKnownType(theType)) {
                sb.append(cfg.typePrefix)
            }
            sb.append(cfg.backendTypeToFrontendType(theType.name))
        } else {
            sb.append(theType.name)
        }

        return sb.toString()
    }
}

class TList(val innerType: AType, val allowNulls: Boolean) : AType("List") {
    override fun stringifyType(generatedTypes: List<ANonGenericType>?, cfg: CodeEmitterConfig?): String {
        val innerStringified = innerType.stringifyType(generatedTypes, cfg)

        return if (innerStringified.contains(' ')) {
            "List ($innerStringified)"
        } else {
            "List $innerStringified"
        }
    }

    fun hasObject(): Boolean {
        if (innerType is TObject)
            return true
        else if (innerType is TList)
            return innerType.hasObject()

        return false
    }
}

class TObject(name: String, val fields: ArrayList<AField>) : ANonGenericType(name) {
    override fun getRenamed(newName: String): ANonGenericType {
        val t = TObject(newName, fields)
        t.originalType = this
        return t
    }

    fun specialize(fields: ArrayList<AField>, newName: String? = null): TObject {
        val t = TObject(newName ?: name, fields)
        t.originalType = this
        return t
    }
}

class TEnum(name: String, val enumValues: List<String>) : ANonGenericType(name) {
    override fun getRenamed(newName: String): ANonGenericType {
        val t = TEnum(newName, enumValues)
        t.originalType = this
        return t
    }
}

class TScalar(name: String) : ANonGenericType(name) {
    override fun getRenamed(newName: String): ANonGenericType {
        val t = TScalar(newName)
        t.originalType = this
        return t
    }
}


class AField(val name: String, val type: AType, val isNullable: Boolean, val arguments: List<AArgument>) {
    fun stringifyType(generatedTypes: List<ANonGenericType>? = null, cfg: CodeEmitterConfig? = null): String {
        val sb = StringBuilder()
        val shouldEmitMaybe = cfg?.emitMaybeForNullableFields == true && isNullable

        if (shouldEmitMaybe) {
            sb.append("Maybe ")

            if (!type.isScalarType) {
                sb.append("(")
            }
        }

        when (type) {
            is TList -> {
                sb.append("List ")
                if (type.innerType.isScalarType) {
                    sb.append((type.innerType as ANonGenericType).name)
                } else {
                    val ofTypeAsStr = type.innerType.stringifyType(generatedTypes, cfg)
                    val isCompoundType = ofTypeAsStr.contains(' ')

                    if (isCompoundType)
                        sb.append("(")

                    sb.append(ofTypeAsStr)

                    if (isCompoundType)
                        sb.append(")")
                }
            }
            is TObject -> {
                fun matchesMe(type: ANonGenericType): Boolean {
                    if (type.originalType.name != this.type.originalType.name) {
                        return false
                    }

                    if (type is TObject) {
                        return type.fields == this.type.fields
                    }

                    return true
                }

                val theType = generatedTypes?.find { matchesMe(it) } ?: this.type.originalType
                sb.append(theType.stringifyType(generatedTypes, cfg))
            }
            is TScalar -> {
                sb.append(this.type.stringifyType(generatedTypes, cfg))
            }
            is TEnum -> {
                sb.append(this.type.stringifyType(generatedTypes, cfg))
            }
        }

        if (shouldEmitMaybe) {
            if (!type.isScalarType) {
                sb.append(")")
            }
        }

        return sb.toString()
    }
}

class AArgument(
        val name: String,
        val type: AType,
        val isNullable: Boolean,
        val valueOrVariable: GraphQLParser.ValueOrVariable? = null
) {
    fun asValue() = valueOrVariable!!.asValue()
}


enum class OpType {
    Query,
    Mutation;

    companion object {
        fun guess(str: String): OpType {
            if (str == "mutation")
                return Mutation

            return Query;
        }
    }
}

class OperationDef(
        val opType: OpType,
        val name: String?,
        val fields: List<AField> = ArrayList()
) {
    //    var returnType: QType? = null // should be getter calculated from fields?
    var inputType: AType? = null

}
