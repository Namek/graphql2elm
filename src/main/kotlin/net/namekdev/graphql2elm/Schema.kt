package net.namekdev.graphql2elm

class Schema(val queryTypeName: String, val mutationTypeName: String) {
    val types: HashMap<String, QType> = hashMapOf()

    operator fun get(key: String): QType {
        return types.get(key)!!
    }

    fun findFieldByPath(opType: OpType, path: List<String>): QField {
        val rootTypeName =
            when (opType) {
                OpType.Query -> queryTypeName
                OpType.Mutation -> mutationTypeName
            }

        val root = types[rootTypeName]!!

        var curField: QField = enter(root, path[0])
        for (i in 1 until path.size) {
            if (curField is QListField) {
                curField = enter(curField.ofType, path[i])
            }
            else if (curField is QObjectField) {
                curField = enter(curField.fullType, path[i])
            }
            else {
                throw IllegalStateException("can't enter into field " + curField)
            }
        }

        return curField
    }

    private fun enter(type: QType, into: String): QField {
        if (type is QObjectType) {
            val field = type.fields.find { it.name == into }
            return field ?: throw Exception("no field '$into' in '${type.name}'")
        }

        throw IllegalStateException("can't enter into type $type")
    }
}


enum class Kind {
    SCALAR,
    OBJECT,
    LIST,
    ENUM
}

abstract class QField(val name: String, val kind: Kind, val isNullable: Boolean) {
    val isScalarType: Boolean = kind == Kind.SCALAR || kind == Kind.ENUM

    fun stringifyType(generatedTypes: List<QType>? = null, cfg: CodeEmitterConfig? = null): String {
        val sb = StringBuilder()
        val shouldEmitMaybe = cfg?.emitMaybeForNullableFields == true && isNullable

        if (shouldEmitMaybe) {
            sb.append("Maybe ")

            if (!this.isScalarType) {
                sb.append("(")
            }
        }

        when (this) {
            is QListField -> {
                sb.append("List ")
                if (this.ofType.isScalarType) {
                    sb.append(this.ofType.name)
                }
                else {
                    val ofTypeAsStr = this.ofType.stringifyType(generatedTypes, cfg, this.selectedFields)
                    val isCompoundType = ofTypeAsStr.contains(' ')

                    if (isCompoundType)
                        sb.append("(")

                    sb.append(ofTypeAsStr)

                    if (isCompoundType)
                        sb.append(")")
                }
            }
            is QObjectField -> {
                fun matchesMe(type: QType): Boolean {
                    if (type.fullType().name != this.fullType.name) {
                        return false
                    }

                    if (type is (QObjectType)) {
                        return type.fields == selectedFields
                    }

                    return true
                }

                val theType = generatedTypes?.find { matchesMe(it) } ?: this.fullType
                sb.append(theType.stringifyType(generatedTypes, cfg, this.selectedFields))
            }
            is QScalarField -> {
                sb.append(this.type.stringifyType(generatedTypes, cfg, null))
            }
            is QEnumField -> {
                sb.append(this.type.stringifyType(generatedTypes, cfg, null))
            }
        }

        if (shouldEmitMaybe) {
            if (!this.isScalarType) {
                sb.append(")")
            }
        }

        return sb.toString()
    }

    override fun toString(): String {
        return stringifyType()
    }
}
class QListField(name: String, val ofType: QType, val selectedFields: ArrayList<QField>?, isNullable: Boolean)
    : QField(name, Kind.LIST, isNullable) {

    override fun equals(other: Any?): Boolean {
        if (other !is QListField || !super.equals(other))
            return false

        if (ofType.name != other.ofType.name || ofType.isScalarType != other.ofType.isScalarType)
            return false

        if (selectedFields?.size != other.selectedFields?.size)
            return false

        return true
    }

    override fun toString(): String {
        return name
    }
}

class QScalarField(name: String, val type: QScalarType, isNullable: Boolean)
    : QField(name, Kind.SCALAR, isNullable) {
    override fun toString(): String {
        return name
    }
}

class QEnumField(name: String, val type: QEnumType, isNullable: Boolean)
    : QField(name, Kind.ENUM, isNullable) {
    override fun toString(): String {
        return name
    }
}

class QObjectField(name: String, val selectedFields: ArrayList<QField>, val fullType: QObjectType, isNullable: Boolean)
    : QField(name, Kind.OBJECT, isNullable) {

    fun toType(): QObjectType {
        return QObjectType(fullType.name, selectedFields, fullType)
    }

    override fun toString(): String {
        return "$kind $name"
    }
}

abstract class QType(val name: String, val isScalarType: Boolean, val originalType: QType?) {
    abstract fun getRenamed(newName: String): QType

    fun fullType(): QType {
        return originalType ?: this
    }

    fun stringifyType(generatedTypes: List<QType>? = null, cfg: CodeEmitterConfig? = null, selectedFields: ArrayList<QField>?): String {
        val sb = StringBuilder()

        fun matchesMe(type: QType): Boolean {
            if (type.fullType().name != this.fullType().name) {
                return false
            }

            if (type is (QObjectType)) {
                return type.fields == selectedFields
            }

            return true
        }

        val theType = generatedTypes?.find { matchesMe(it) } ?: this
        if (cfg != null) {
            if (!cfg.isKnownType(theType)) {
                sb.append(cfg.typePrefix)
            }
            sb.append(cfg.backendTypeToFrontendType(theType.name))
        }
        else {
            sb.append(theType.name)
        }

        return sb.toString()
    }

    override fun toString(): String {
        return "${javaClass.simpleName} ${name}"
    }
}

class QScalarType(name: String, originalType: QType? = null)
    : QType(name, true, originalType) {
    override fun getRenamed(newName: String): QScalarType {
        return QScalarType(newName, fullType())
    }

    override fun toString(): String {
        return name
    }
}

class QEnumType(name: String, val enumValues: List<String>, originalType: QType? = null)
    : QType(name, true, originalType) {
    override fun getRenamed(newName: String): QEnumType {
        return QEnumType(newName, enumValues, fullType())
    }
}

class QObjectType(name: String, val fields: ArrayList<QField>, originalType: QType?)
    : QType(name, false, originalType) {
    override fun getRenamed(newName: String): QObjectType {
        return QObjectType(newName, fields, fullType())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is QObjectType)
            return false

        if (name != other.name)
            return false

        return fields
            .mapIndexed { idx, f -> other.fields[idx] == f }
            .all { it }
    }

    override fun toString(): String {
        return "object ${name}"
    }
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
    val fields: List<QField> = ArrayList()
)
{
//    var returnType: QType? = null // should be getter calculated from fields?
    var inputType: QType? = null

}
