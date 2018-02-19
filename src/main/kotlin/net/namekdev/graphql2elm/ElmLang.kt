package net.namekdev.graphql2elm

fun isStandardScalarElmType(name: String): Boolean {
    return arrayOf("String", "Int", "Bool", "Float", "Char")
        .any { it == name }
}
