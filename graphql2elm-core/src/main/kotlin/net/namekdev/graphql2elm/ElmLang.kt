package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parsers.GraphQLParser
import net.namekdev.graphql2elm.parsers.GraphQLParser.*

fun isStandardScalarElmType(name: String): Boolean {
    return arrayOf("String", "Int", "Bool", "Float", "Char")
            .any { it == name }
}

fun emitElmValue(value: Value): String {
    return if (value is BooleanValue) {
        value.boolean.toString()
    }
    else if (value is NumberValue) {
        value.valueAsString
    }
    else if (value is StringValue) {
        "\"${value.string}\""
    }
    else if (value is GraphQLParser.Array) {
        "[ " + value.values.joinToString { emitElmValue(it) } + " ]"
    }
    else {
        throw Exception("unknown elm value")
    }
}