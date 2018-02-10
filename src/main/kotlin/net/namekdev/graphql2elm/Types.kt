package net.namekdev.graphql2elm

class TypeSystem {
    val types: HashMap<String, AType> = hashMapOf()

    operator fun get(key: String): AType {
        return types.get(key)!!
    }

    fun findFieldByPath(opType: OpType, path: List<String>): AField {
        val rootTypeName = "Root" + opType.name + "Type"
        val root = types[rootTypeName]!!

        var curField: AField = enter(root, path[0])
        for (i in 1 until path.size) {
            curField = enter(curField.type, path[i])
        }

        return curField
    }

    private fun enter(type: AType, into: String): AField {
        if (type.kind == AType.Kind.LIST) {
            return type.ofType!!.fields.find { it.name == into }!!
        }
        return type.fields.find { it.name == into }!!
    }
}

data class AType(
    var name: String?,
    val kind: Kind,
    val enumValues: List<String>?,
    var ofType: AType? = null,
    val isFullInfo: Boolean = true
) {
    var fields: ArrayList<AField> = ArrayList<AField>()

    enum class Kind {
        SCALAR,
        OBJECT,
        LIST,
        ENUM
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AType)
            return false

        return equals(other, false)
    }

    fun equals(other: AType, ignoreFields: Boolean): Boolean {
        if (isFullInfo != other.isFullInfo) {
            return false
        }

        if (name != other.name)
            return false

        if (kind != other.kind)
            return false

        if (kind == Kind.ENUM && enumValues != other.enumValues)
            return false

        if (kind == Kind.LIST) {
            if (ofType == null && other.ofType != null || ofType != null && other.ofType == null)
                return false

            if (ofType!!.name != other.ofType!!.name || ofType!!.kind != other.ofType!!.kind)
                return false
        }

        if (!ignoreFields) {
            if (fields.size != other.fields.size)
                return false

            return fields
                .mapIndexed { idx, f -> other.fields[idx] == f }
                .all { it }
        }

        return true
    }

    override fun toString(): String {
        var str = "$kind "

        if (kind == Kind.LIST)
            str += "of (" + ofType!!.toString() + ")"
        else
            str += name

        return str
    }

    fun stringify(generatedTypes: List<AType>? = null, typePrefix: String = ""): String {
        val sb = StringBuilder()
        when (this.kind) {
            Kind.LIST -> {
                sb.append("List (", this.ofType!!.stringify(generatedTypes, typePrefix), ")")
            }

            else -> {
                // we have to check if it's a generated type
                if (generatedTypes?.any { it.equals(this, true) } == true) {
                    sb.append(typePrefix)
                }

                sb.append(backendTypeToFrontendType(name!!))
            }
        }
        return sb.toString()
    }
}

data class AField(
    val name: String,
    val type: AType,
    val isFullInfo: Boolean = true
) {
    val args = ArrayList<Arg>()

    class Arg {

    }

    override fun equals(other: Any?): Boolean {
        if (other !is AField || isFullInfo != other.isFullInfo)
            return false

        return name == other.name && type.kind == other.type.kind
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
    val name: String?
)
{
//    var returnType: AType? = null // should be getter calculated from fields?
    var inputType: AType? = null

    var fields: List<AField> = ArrayList()
}
