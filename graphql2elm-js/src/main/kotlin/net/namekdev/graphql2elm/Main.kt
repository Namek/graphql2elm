package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parsers.GraphQLParser
import net.namekdev.graphql2elm.parsers.mergeSchemaIntoQuery
import net.namekdev.graphql2elm.parsers.parseSchemaJson
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window

actual fun main(args: Array<String>) {
    window.onload = {
        val div = document.createElement("div")
        document.body!!.appendChild(div)

        val app = window["Elm"].Main.embed(
                div,
                JSON.parse(localStorage.getItem("settings") ?: "{}")
        )

        app.ports.generateElmCode.subscribe { settings ->
            val s = settings

            try {
                localStorage.setItem("settings", JSON.stringify(s))

                val res = generateElmCode(s.query, s.schema)
                app.ports.elmCodeGenerationResult.send(res)
            }
            catch (exc: Exception) {
                // TODO inform about errors
            }

            0
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
