package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parsers.GraphQLParser
import net.namekdev.graphql2elm.parsers.mergeSchemaIntoQuery
import net.namekdev.graphql2elm.parsers.parseSchemaJson
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window

actual fun main(args: Array<String>) {
    window.onload = {
        val div = document.createElement("div")
        document.body!!.appendChild(div)

        val app = window["Elm"].Main.embed(div)

        app.ports.generateElmCode.subscribe { params : Array<String> ->
            val query= params[0]
            val schema = params[1]
            val res = generateElmCode(query, schema)
            app.ports.elmCodeGenerationResult.send(res)
        }
    }
}

fun generateElmCode(query: String, schema: String): String {
    val parser = GraphQLParser(query)
    val typeSystem = parseSchemaJson(schema)
    val doc = parser.parse()!!
    val output = mergeSchemaIntoQuery(doc, typeSystem)

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
    val elmCode = emitElmCode(output.operations[0], emitterConfig)

    return elmCode
}
