package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.misc.Result
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
                val emitterConfig = CodeEmitterConfig(
                        s.typePrefix,
                        s.representNullableInEmittedGraphQLComment,
                        s.emitMaybeForNullableFields,
                        knownTypes = listOf(),
                        backendTypesMap = defaultBackendTypesMap
                )

                localStorage.setItem("settings", JSON.stringify(s))

                generateElmCode(s.query, s.schema, emitterConfig)
                        .fold(
                                { res -> app.ports.elmCodeGenerationResult.send(res) },
                                { err -> app.ports.elmCodeGenerationError.send(err.toTypedArray()) }
                        )
            }
            catch (exc: Exception) {
                app.ports.elmCodeGenerationError.send(arrayOf(exc.message ?: exc.toString()))
            }

            0
        }

        app.ports.selectText_Pre.subscribe { elId ->
            val el: dynamic = document.getElementById(elId)!!
            val sel = js("window.getSelection()")
            sel.selectAllChildren(el)
        }

        app.ports.selectText_TextArea.subscribe { elId ->
            val el: dynamic = document.getElementById(elId)!!
            el.focus()
            el.select()
        }
    }
}

fun generateElmCode(query: String, schema: String, emitterConfig: CodeEmitterConfig): Result<String, List<String>> {
    val parser = GraphQLParser(query)
    val typeSystem = parseSchemaJson(schema)

    return parser.parse()
            .map { doc ->
                val output = mergeSchemaIntoQuery(doc, typeSystem)
                val elmCode = emitElmCode(output.operations[0], emitterConfig)

                elmCode
            }
}

//val defaultKnownTypes = arrayListOf<RegisteredType>(
//        RegisteredType(
//                "TransactionType",
//                null,
//                "decodeTransactionType",
//                null,
//                "Data.Transaction"
//        )
//)

val defaultBackendTypesMap = hashMapOf(Pair("Boolean", "Bool"))