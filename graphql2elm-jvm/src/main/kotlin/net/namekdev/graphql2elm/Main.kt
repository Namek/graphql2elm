package net.namekdev.graphql2elm

import net.namekdev.graphql2elm.parsers.GraphQLParser
import net.namekdev.graphql2elm.parsers.mergeSchemaIntoQuery
import net.namekdev.graphql2elm.parsers.parseSchemaJson


actual fun main(args: Array<String>) {
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
    """

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
    """

    val str4 = """
    mutation(${'$'}theAmount: Float) {
      addTransaction(amount: ${'$'}theAmount) {
        id
      }
    }
    """


    // this should error: unknown variable name "thAmount"
    val str5 = """
    mutation(${'$'}theAmount: Float) {
      addTransaction(amount: ${'$'}thAmount) {
        id
      }
    }
    """


    val elmCode = generateElmCode(query = str4, schema = queryForSchema())
    print(elmCode)
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
    val elmCode = emitElmQuery(output.operations[0], emitterConfig)

    return elmCode
}

