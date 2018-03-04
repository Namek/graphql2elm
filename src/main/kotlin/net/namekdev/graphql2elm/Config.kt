package net.namekdev.graphql2elm


data class CodeEmitterConfig(
        /**
         * Prefix for names of generated types (record aliases, enums).
         */
        var typePrefix: String,

        /**
         * Should put a question mark after nullable field name?
         */
        var representNullableInEmittedGraphQLComment: Boolean,

        /**
         * Put "nullable" as type decoder wrapper and wrap with "Maybe" for fields in record aliases.
         */
        val emitMaybeForNullableFields: Boolean,

        /**
         * Registered types exist so emitter can avoid
         * creation of new types and use proper decoders/encoders.
         */
        val knownTypes: List<RegisteredType> = listOf(),

        val backendTypesMap: Map<String, String> = hashMapOf<String, String>()

) {
    /**
     * @see knownTypes
     * @see RegisteredType
     */
    fun isKnownType(type: QType): Boolean {
        return when (type) {
            is QScalarType -> {
                val name = backendTypesMap[type.name] ?: type.name
                isStandardScalarElmType(name)
            }
            else -> {
                getType(type) != null || backendTypesMap.contains(type.name)
            }
        }
    }

    fun backendTypeToFrontendType(str: String): String {
        return backendTypesMap[str] ?: str
    }

    fun backendTypeToFrontendDecoder(type: QType): String {
        val name = backendTypeToFrontendType(type.name)

        if (isStandardScalarElmType(name)) {
            return name.decapitalize()
        }

        val srcType = if (type.name != name) type.getRenamed(name) else type
        val registeredType = getType(srcType)

        return registeredType?.decoderFunction ?: "decode${name.capitalize()}"
    }

    fun getType(type: QType): RegisteredType? {
        val frontendName = backendTypeToFrontendType(type.name)
        if (type is QScalarType && isStandardScalarElmType(frontendName)) {
            return null
        }

        return knownTypes.firstOrNull {
            if (it.name != type.name) {
                false
            }
            else if (type is QObjectType) {
                if (it.fieldList == null) {
                    // specified no fields as known type and this is the type with all fields
                    type.fullType() == type.fullType().originalType
                }
                else {
                    if (type.fields.size != it.fieldList.size) {
                        false
                    }
                    else {
                        it.fieldList.all { fieldName ->
                            type.fields.any { field -> field.name == fieldName }
                        }
                    }
                }
            }
            else true
        }
    }
}

/**
 * Registered types exist so emitter can avoid
 * creation of new types and use proper decoders/encoders.
 */
data class RegisteredType(
        /**
         * Name of the type.
         */
        val name: String,

        /**
         * Field list if this is a type alias (record alias).
         * May be `null` for all fields.
         */
        val fieldList: List<String>?,

        /**
         * Name of decoder function, like decodeDateTime.
         */
        val decoderFunction: String?,

        /**
         * Name of encoder function, like encodeDateTime.
         */
        val encoderFunction: String?,

        /**
         * Package name containing the type, needed to deliver `import` instruction.
         */
        val importPackageName: String?
)
