package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parser.GraphQLBaseListener
import net.namekdev.graphql2elm.parser.GraphQLLexer
import net.namekdev.graphql2elm.parser.GraphQLParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.json.JSONObject
import org.teavm.jso.browser.Window
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection




fun main(args: Array<String>) {
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
    """.trimIndent()

    val str2 = """
    query {
      currentUser{
        transactions{
          amount
          description
          tags
          transactionType
          paidAt
          payeeIds
          payees {
            id
          }
        }
      }
    }
    """.trimIndent()

    val str3 = """
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
        transactions{
          amount
          description
          tags
          transactionType
          paidAt
          payeeIds
          payees {
            id
          }
        }
      }
    }
    """.trimIndent()

    val elmCode = generateElmCode(query = str3, schema = queryForSchema())
    print(elmCode)

    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(elmCode), null)
}

fun generateElmCode(query: String, schema: String): String {
    val input = CharStreams.fromString(query)
    val lexer = GraphQLLexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = GraphQLParser(tokens)

    val typeSystem = parseSchemaJson(schema)
    val listener = QueryParser(typeSystem)
    ParseTreeWalker().walk(listener, parser.document())

    val knownTypes = arrayListOf<RegisteredType>(
            RegisteredType("TransactionType", null, "decodeTransactionType", null, "Data.Transaction")
    )
    val backendTypesMap = hashMapOf(Pair("Boolean", "Bool"))
    val emitterConfig = CodeEmitterConfig("Q", true, true, knownTypes, backendTypesMap)
    val elmCode = emitElmQuery(listener.output.operations[0], emitterConfig)

    return elmCode
}

fun queryForSchema(): String {
    val query = """
      query IntrospectionQuery {
        __schema {
          queryType { name }
          mutationType { name }
          subscriptionType { name }
          types {
            ...FullType
          }
          directives {
            name
            description
            locations
            args {
              ...InputValue
            }
          }
        }
      }

      fragment FullType on __Type {
        kind
        name
        description
        fields(includeDeprecated: true) {
          name
          description
          args {
            ...InputValue
          }
          type {
            ...TypeRef
          }
          isDeprecated
          deprecationReason
        }
        inputFields {
          ...InputValue
        }
        interfaces {
          ...TypeRef
        }
        enumValues(includeDeprecated: true) {
          name
          description
          isDeprecated
          deprecationReason
        }
        possibleTypes {
          ...TypeRef
        }
      }

      fragment InputValue on __InputValue {
        name
        description
        type { ...TypeRef }
        defaultValue
      }

      fragment TypeRef on __Type {
        kind
        name
        ofType {
          kind
          name
          ofType {
            kind
            name
            ofType {
              kind
              name
              ofType {
                kind
                name
                ofType {
                  kind
                  name
                  ofType {
                    kind
                    name
                    ofType {
                      kind
                      name
                    }
                  }
                }
              }
            }
          }
        }
      }
    """

    return """
       {"data":{"__schema":{"types":[{"possibleTypes":null,"name":"__Directive","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":{"ofType":null,"name":"__InputValue","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"args","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"__DirectiveLocation","kind":"ENUM"},"name":null,"kind":"LIST"},"name":"locations","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"onField","isDeprecated":true,"description":null,"deprecationReason":"Check `locations` field for enum value FIELD","args":[]},{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"onFragment","isDeprecated":true,"description":null,"deprecationReason":"Check `locations` field for enum value FRAGMENT_SPREAD","args":[]},{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"onOperation","isDeprecated":true,"description":null,"deprecationReason":"Check `locations` field for enum value OPERATION","args":[]}],"enumValues":null,"description":"Represents a directive"},{"possibleTypes":null,"name":"__DirectiveLocation","kind":"ENUM","interfaces":null,"inputFields":null,"fields":null,"enumValues":[{"name":"FIELD","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"FRAGMENT_DEFINITION","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"FRAGMENT_SPREAD","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"INLINE_FRAGMENT","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"MUTATION","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"QUERY","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"SUBSCRIPTION","isDeprecated":false,"description":null,"deprecationReason":null}],"description":null},{"possibleTypes":null,"name":"__EnumValue","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"deprecationReason","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"isDeprecated","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"__Field","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":{"ofType":null,"name":"__InputValue","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"args","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"deprecationReason","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"isDeprecated","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":"type","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"__InputValue","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"defaultValue","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":"type","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"__Schema","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":{"ofType":null,"name":"__Directive","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"directives","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":"mutationType","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":"queryType","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":"subscriptionType","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"types","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":"Represents a schema"},{"possibleTypes":null,"name":"__Type","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"__EnumValue","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"enumValues","isDeprecated":false,"description":null,"deprecationReason":null,"args":[{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"includeDeprecated","description":null,"defaultValue":"false"}]},{"type":{"ofType":{"ofType":null,"name":"__Field","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"fields","isDeprecated":false,"description":null,"deprecationReason":null,"args":[{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"includeDeprecated","description":null,"defaultValue":"false"}]},{"type":{"ofType":{"ofType":null,"name":"__InputValue","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"inputFields","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"interfaces","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"kind","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":"ofType","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"__Type","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"possibleTypes","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":"Represents scalars, interfaces, object types, unions, enums in the system"},{"possibleTypes":null,"name":"Balance","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"balanceDen","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"balanceNum","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"DateTime","kind":"SCALAR"},"name":"lastUpdateAt","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"sharedPaymentCount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"transferCount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"unseenUpdateCount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"user1HasMore","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"user1Id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"user2Id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"BalanceToOtherUser","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":"iHaveMore","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"DateTime","kind":"SCALAR"},"name":"lastUpdateAt","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"User","kind":"OBJECT"},"name":"otherUser","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"otherUserId","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"sharedPaymentCount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"transferCount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"unseenUpdateCount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Float","kind":"SCALAR"},"name":"value","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"Boolean","kind":"SCALAR","interfaces":null,"inputFields":null,"fields":null,"enumValues":null,"description":"The `Boolean` scalar type represents `true` or `false`."},{"possibleTypes":null,"name":"DateTime","kind":"SCALAR","interfaces":null,"inputFields":null,"fields":null,"enumValues":null,"description":"The `DateTime` scalar type represents a date and time in the UTC\ntimezone. Format is `YYYY-MM-DD HH:MM:SS`."},{"possibleTypes":null,"name":"Float","kind":"SCALAR","interfaces":null,"inputFields":null,"fields":null,"enumValues":null,"description":"The `Float` scalar type represents signed double-precision fractional\nvalues as specified by\n[IEEE 754](http://en.wikipedia.org/wiki/IEEE_floating_point)."},{"possibleTypes":null,"name":"ID","kind":"SCALAR","interfaces":null,"inputFields":null,"fields":null,"enumValues":null,"description":"The `ID` scalar type represents a unique identifier, often used to\nrefetch an object or as key for a cache. The ID type appears in a JSON\nresponse as a String; however, it is not intended to be human-readable.\nWhen expected as an input type, any string (such as `\"4\"`) or integer\n(such as `4`) input value will be accepted as an ID."},{"possibleTypes":null,"name":"Int","kind":"SCALAR","interfaces":null,"inputFields":null,"fields":null,"enumValues":null,"description":"The `Int` scalar type represents non-fractional signed whole numeric values.\nInt can represent values between `-(2^53 - 1)` and `2^53 - 1` since it is\nrepresented in JSON as double-precision floating point numbers specified\nby [IEEE 754](http://en.wikipedia.org/wiki/IEEE_floating_point)."},{"possibleTypes":null,"name":"RootMutationType","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"UserTransaction","kind":"OBJECT"},"name":"addTransaction","isDeprecated":false,"description":null,"deprecationReason":null,"args":[{"type":{"ofType":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"amount","description":null,"defaultValue":null},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","description":null,"defaultValue":null},{"type":{"ofType":null,"name":"DateTime","kind":"SCALAR"},"name":"paidAt","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":null,"kind":"LIST"},"name":null,"kind":"NON_NULL"},"name":"payeeIds","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"payorId","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"LIST"},"name":"tags","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":null,"name":"TransactionType","kind":"ENUM"},"name":null,"kind":"NON_NULL"},"name":"transactionType","description":null,"defaultValue":null}]},{"type":{"ofType":null,"name":"RegisteredUser","kind":"OBJECT"},"name":"registerUser","isDeprecated":false,"description":null,"deprecationReason":null,"args":[{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"email","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"name","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"password","description":null,"defaultValue":null}]},{"type":{"ofType":null,"name":"Session","kind":"OBJECT"},"name":"signIn","isDeprecated":false,"description":null,"deprecationReason":null,"args":[{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"email","description":null,"defaultValue":null},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"password","description":null,"defaultValue":null}]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"RootQueryType","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"User","kind":"OBJECT"},"name":"currentUser","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"User","kind":"OBJECT"},"name":"user","isDeprecated":false,"description":null,"deprecationReason":null,"args":[{"type":{"ofType":{"ofType":null,"name":"ID","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"id","description":null,"defaultValue":null}]},{"type":{"ofType":{"ofType":null,"name":"User","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"users","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"RegisteredUser","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"Session","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"token","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"String","kind":"SCALAR","interfaces":null,"inputFields":null,"fields":null,"enumValues":null,"description":"The `String` scalar type represents textual data, represented as UTF-8\ncharacter sequences. The String type is most often used by GraphQL to\nrepresent free-form human-readable text."},{"possibleTypes":null,"name":"Transaction","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"Float","kind":"SCALAR"},"name":"amount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"DateTime","kind":"SCALAR"},"name":"paidAt","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":null,"kind":"LIST"},"name":"payeeIds","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"User","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"payees","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"payorId","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"LIST"},"name":"tags","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"TransactionType","kind":"ENUM"},"name":"transactionType","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"TransactionType","kind":"ENUM","interfaces":null,"inputFields":null,"fields":null,"enumValues":[{"name":"SHARED","isDeprecated":false,"description":null,"deprecationReason":null},{"name":"TRANSFER","isDeprecated":false,"description":null,"deprecationReason":null}],"description":null},{"possibleTypes":null,"name":"User","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":{"ofType":{"ofType":null,"name":"BalanceToOtherUser","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":null,"kind":"NON_NULL"},"name":"balances","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"email","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"name","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":{"ofType":null,"name":"UserTransaction","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":null,"kind":"NON_NULL"},"name":"transactions","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null},{"possibleTypes":null,"name":"UserTransaction","kind":"OBJECT","interfaces":[],"inputFields":null,"fields":[{"type":{"ofType":null,"name":"Float","kind":"SCALAR"},"name":"amount","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"String","kind":"SCALAR"},"name":"description","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":"id","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"DateTime","kind":"SCALAR"},"name":"paidAt","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"Int","kind":"SCALAR"},"name":null,"kind":"LIST"},"name":"payeeIds","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"User","kind":"OBJECT"},"name":null,"kind":"LIST"},"name":"payees","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":{"ofType":null,"name":"String","kind":"SCALAR"},"name":null,"kind":"LIST"},"name":"tags","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]},{"type":{"ofType":null,"name":"TransactionType","kind":"ENUM"},"name":"transactionType","isDeprecated":false,"description":null,"deprecationReason":null,"args":[]}],"enumValues":null,"description":null}],"subscriptionType":null,"queryType":{"name":"RootQueryType"},"mutationType":{"name":"RootMutationType"},"directives":[{"name":"include","locations":["INLINE_FRAGMENT","FRAGMENT_SPREAD","FIELD"],"description":"Directs the executor to include this field or fragment only when the `if` argument is true.\"","args":[{"type":{"ofType":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"if","description":"Included when true.","defaultValue":null}]},{"name":"skip","locations":["INLINE_FRAGMENT","FRAGMENT_SPREAD","FIELD"],"description":"Directs the executor to skip this field or fragment when the `if` argument is true.","args":[{"type":{"ofType":{"ofType":null,"name":"Boolean","kind":"SCALAR"},"name":null,"kind":"NON_NULL"},"name":"if","description":"Skipped when true.","defaultValue":null}]}]}}}
    """
}

fun parseSchemaJson(schemaJson: String): Schema {
    val jsonRoot = JSONObject(schemaJson).getJSONObject("data").getJSONObject("__schema")
    val jsonTypes = jsonRoot.getJSONArray("types")
    val queryTypeName = jsonRoot.getJSONObject("queryType").getString("name")
    val mutationTypeName = jsonRoot.getJSONObject("mutationType").getString("name")

    val schema = Schema(queryTypeName, mutationTypeName)

    for (i in 0 until jsonTypes.length()) {
        val jsonType = jsonTypes.getJSONObject(i)
        val name = jsonType.getString("name")

        if (name.startsWith("__"))
            continue

        val kind = Kind.valueOf(jsonType.getString("kind"))

        val newType = when (kind) {
            Kind.LIST ->
                throw IllegalStateException("no type should be defined as list")

            Kind.SCALAR ->
                QScalarType(name)

            Kind.OBJECT -> {
                QObjectType(name, arrayListOf(), null)
            }

            Kind.ENUM -> {
                val enumValues = jsonType.getJSONArray("enumValues")
                    .map {
                        (it as JSONObject).getString("name")
                    }

                QEnumType(name, enumValues)
            }
        }
        schema.types[name] = newType
    }

    val fieldsToFillWithTypes = arrayListOf<QField>()

    for (i in 0 until jsonTypes.length()) {
        val jsonType = jsonTypes.getJSONObject(i)
        val name = jsonType.getString("name")

        if (name.startsWith("__"))
            continue

        val type = schema[name] as? QObjectType ?: continue
        val jsonFields = jsonType.getJSONArray("fields")

        for (j in 0 until jsonFields.length()) {
            val jsonField = jsonFields.getJSONObject(j)
            val fieldName = jsonField.getString("name")
            val jsonFieldType = jsonField.getJSONObject("type")
            val isNonNull = jsonFieldType.getString("kind") == "NON_NULL"

            fun rec(jsonFieldType: JSONObject, isNonNull: Boolean): QField {
                val typeName = if (jsonFieldType.isNull("name")) null else jsonFieldType.getString("name")
                val typeKind = jsonFieldType.getString("kind")

                // TODO read jsonField['args']

                return when (typeKind) {
                    "OBJECT" -> {
                        val objectType = schema[typeName!!] as QObjectType
                        val fields = arrayListOf<QField>()

                        // If `objectType` is same as `type` then we may not have `objectType.selectedFields` filled yet.
                        // So, schedule it.

                        val objectField = QObjectField(fieldName, fields, objectType, !isNonNull)
                        fieldsToFillWithTypes.add(objectField)
                        objectField
                    }

                    "SCALAR" -> {
                        val scalarType = schema[typeName!!] as QScalarType
                        QScalarField(fieldName, scalarType, !isNonNull)
                    }

                    "ENUM" -> {
                        val enumType = schema[typeName!!] as QEnumType
                        QEnumField(fieldName, enumType, !isNonNull)
                    }

                    "LIST" -> {
                        val jsonFieldTypeOfType = jsonFieldType.getJSONObject("ofType")
                        val fieldSubTypeName = jsonFieldTypeOfType.getString("name")
                        val ofType = schema[fieldSubTypeName]

                        val selectedFields =
                                if (ofType is QObjectType)
                                    arrayListOf<QField>()
                                else
                                    null

                        val listField = QListField(fieldName, ofType, selectedFields, !isNonNull)

                        if (selectedFields != null)
                            fieldsToFillWithTypes.add(listField)

                        listField
                    }

                    "NON_NULL" -> {
                        val jsonFieldTypeOfType = jsonFieldType.getJSONObject("ofType")
                        rec(jsonFieldTypeOfType, true)
                    }

                    else -> {
                        throw IllegalStateException()
                    }
                }
            }

            val newField: QField =
                rec(jsonFieldType, isNonNull)

            type.fields.add(newField)
        }
    }

    for (field in fieldsToFillWithTypes) {
        if (field is QObjectField) {
            field.selectedFields.addAll(field.fullType.fields)
        }
        else if (field is QListField) {
            if (field.ofType !is QObjectType)
                throw IllegalStateException()

            field.selectedFields!!.addAll(field.ofType.fields)
        }
    }

    return schema
}

class SelectedFieldOutput(val schema: Schema) {
    val operations = mutableListOf<OperationDef>()
}

/**
 * Query parser filters entire possible field graph to a small subset defined by the query.
 */
class QueryParser(val schema: Schema) : GraphQLBaseListener() {
    val output = SelectedFieldOutput(schema)

    override fun enterOperationDefinition(ctx: GraphQLParser.OperationDefinitionContext) {
        val opType = OpType.guess(ctx.operationType()?.text ?: "query")
        val selections = ctx.selectionSet()

        val opFields = traverseFields(selections, opType, listOf())
        val op = OperationDef(opType, ctx.NAME()?.symbol?.text, opFields)

        output.operations.add(op)
    }

    private fun traverseFields(selections: GraphQLParser.SelectionSetContext, opType: OpType, path: List<String>): ArrayList<QField> {
        val selectedFields = arrayListOf<QField>()

        for (selection in selections.selection()) {
            val fieldCtx = selection.field()

            if (fieldCtx != null) {
                val fieldName = fieldCtx.fieldName().text
                val field = schema.findFieldByPath(opType, path + fieldName)
                val selectedField: QField =
                    if (field is QObjectField) {
                        val subFields = traverseFields(fieldCtx.selectionSet()!!, opType, path + fieldName)
                        QObjectField(field.name, subFields, field.fullType, field.isNullable)
                    }
                    else if (field is QListField) {
                        val selectedSubFields =
                            if (fieldCtx.selectionSet() != null)
                                traverseFields(fieldCtx.selectionSet()!!, opType, path + fieldName)
                            else null

                        QListField(field.name, field.ofType, selectedSubFields, field.isNullable)
                    }
                    else {
                        field
                    }

                selectedFields.add(selectedField)
            }
        }

        return selectedFields
    }
}

