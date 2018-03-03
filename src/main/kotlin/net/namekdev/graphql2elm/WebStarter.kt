package net.namekdev.graphql2elm

import org.teavm.jso.browser.*
import org.teavm.jso.dom.events.Event
import org.teavm.jso.dom.events.EventListener
import org.teavm.jso.dom.html.*

fun main(args: Array<String>) {
    val document = Window.current().document

    val preEl = document.createElement("pre")
    preEl.innerHTML = "hey just a test"
    document.body.appendChild(preEl)

    val str1 = """
    query {
      currentUser {
        balances {
          otherUser {
            id
            name
          }
          value
          iHaveMore
          sharedPaymentCount
          transferCount
          unseenUpdateCount
        }
      }
    }
    """

    preEl.innerHTML = generateElmCode(query = str1, schema = queryForSchema())
}