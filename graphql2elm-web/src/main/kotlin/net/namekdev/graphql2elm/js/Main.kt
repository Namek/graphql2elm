package net.namekdev.graphql2elm.js

import net.namekdev.graphql2elm.CodeEmitterConfig
import net.namekdev.graphql2elm.RegisteredType
import net.namekdev.graphql2elm.emitElmQuery
import net.namekdev.graphql2elm.parsers.GraphQLParser
import net.namekdev.graphql2elm.parsers.mergeQueryWithSchema
import net.namekdev.graphql2elm.parsers.parseSchemaJson

@JsName("generateElmCode")
fun generateElmCode(query: String, schema: String): String {
    val parser = GraphQLParser(query)
    val typeSystem = parseSchemaJson(schema)
    val doc = parser.parse()!!
    val output = mergeQueryWithSchema(doc, typeSystem)

    val knownTypes = arrayListOf<RegisteredType>(
            RegisteredType("TransactionType", null, "decodeTransactionType", null, "Data.Transaction")
    )
    val backendTypesMap = hashMapOf(Pair("Boolean", "Bool"))
    val emitterConfig = CodeEmitterConfig(
            "Q",
            true,
            true,
            knownTypes,
            backendTypesMap
    )
    val elmCode = emitElmQuery(output.operations[0], emitterConfig)

    return elmCode
}
