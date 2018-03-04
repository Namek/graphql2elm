package net.namekdev.graphql2elm.parsers

class GraphQLParser(private val buffer: String) : AbstractParser() {
    private val whitespace = " \t\n\r"
    private val numbers0to9 = (0..9).map { it.toString() }
    private val numbers1to9 = (1..9).map { it.toString() }

    fun parse(): Document? {
        try {
            return document()
        } catch (exc: Exception) {
            return null
        }
    }

    override fun fetchCharacter(): Char {
        return buffer[pos++]
    }

    override fun isNextCharacterMeaningful(): Boolean {
        val ch = buffer[pos]
        return !whitespace.contains(ch)
    }

    override fun fetchString(start: Int, end: Int): String {
        return buffer.substring(start, end)
    }

    override fun debugPos(): String {
        return buffer.substring(0, pos) + "|" + buffer.substring(pos)
    }

    private fun document(): Document =
            Document(listOf(definition()) + multipleMaybes(::definition))

    private fun definition(): Definition = block("Definition", {
        expectOneOf(::operationDefinition, ::fragmentDefinition)
    })

    private fun operationDefinition(): OperationDefinition = block("OperationDefinition", {
        expectOneOf(
                {
                    OperationDefinition(
                            operationType(),
                            maybe { -> NAME() },
                            maybe { -> variableDefinitions() },
                            maybe { -> directives() },
                            selectionSet()
                    )
                },
                {
                    OperationDefinition(
                            "query",
                            null,
                            null,
                            null,
                            selectionSet()
                    )
                }
        )
    })

    private fun directives(): Directives =
            block("Directives", {
                Directives(multipleMaybes { directive() })
            })


    private fun directive(): Directive =
    // '@' NAME ':' valueOrVariable | '@' NAME | '@' NAME '(' argument ')'
            block("Directive", {
                expectWord("@")
                val name = NAME()

                expectOneOf(
                        {
                            expectWord(":")
                            Directive(name, valueOrVariable())
                        },
                        {
                            expectWord("(")
                            val arg = argument()
                            expectWord(")")

                            Directive(name, argument = arg)
                        },
                        { Directive(name) }
                )
            })

    private fun variableDefinitions(): VariableDefinitions = block("VariableDefinitions", {
        expectWord("(")

        val firstDef = variableDefinition()
        val restDefs = multipleMaybes {
            expectWord(",")
            variableDefinition()
        }

        expectWord(")")

        VariableDefinitions(listOf(firstDef) + restDefs)
    })

    private fun variableDefinition(): VariableDefinition = block("VariableDefinition", {
        val varr = variable()
        expectWord(":")
        val t = type()
        val defaultValue = maybe { defaultValue() }

        VariableDefinition(varr, t, defaultValue)
    })

    private fun defaultValue(): Value = block("defaultValue: Value", {
        expectWord("=")
        value()
    })

    private fun NAME(): String {
        // regex: """[_A-Za-z][_0-9A-Za-z]*"""

        pushPos()
        var str = ""
        var ch = fetchMeaningfulCharacter()
        if (ch == '_' || ch in 'a'..'z' || ch in 'A'..'Z') {
            str += ch

            while (isNextCharacterMeaningful()) {
                pushPos()
                ch = fetchMeaningfulCharacter()

                if (ch == '_' || ch in 'a'..'z' || ch in 'A'..'Z' || ch in '0'..'9') {
                    str += ch
                    popPos()
                }
                else {
                    popSavePos()
                    break;
                }
            }

            return str
        }
        else {
            popPos()
        }

        throw parseBackTrack("expected NAME")
    }

    private fun operationType(): String = block("OperationType: 'query' or 'mutation'", {
        expectOneOf<String>(
                { expectAndGetWord("query") },
                { expectAndGetWord("mutation") }
        )
    })

    private fun selectionSet(): SelectionSet = block("SelectionSet", {
        expectWord("{")

        val list = arrayListOf(selection())

        list += multipleMaybes {
            maybe { expectWord(",") }
            selection()
        }

        expectWord("}")

        SelectionSet(list)
    })

    private fun selection(): Selection = block("Selection", {
        expectOneOf(::field, ::fragmentSpread, ::inlineFragment)
    })

    private fun field(): Field = block("Field", {
        Field(
                fieldName(),
                maybe { arguments() },
                maybe { directives() },
                maybe { selectionSet() }
        )
    })

    private fun fieldName(): FieldName = block("fieldName", {
        expectOneOf(
                ::alias,
                { FieldName(NAME(), null) }
        )
    })

    private fun alias(): FieldName = block("Alias: FieldName", {
        val name = NAME()
        expectWord(":")
        val alias = NAME()
        FieldName(name, alias)
    })

    private fun arguments(): List<Argument> = block("expected argument list", {
        expectWord("(")
        val firstArg = argument()

        val list = multipleMaybes<Argument> {
            expectWord(",")
            argument()
        }

        expectWord(")")

        listOf(firstArg) + list
    })

    private fun argument(): Argument = block("expected argument", {
        val name = NAME()
        expectAndGetWord(":")
        Argument(name, valueOrVariable())
    })

    private fun valueOrVariable(): ValueOrVariable = block("ValueOrVariable", {
        expectOneOf(::value, ::variable)
    })

    private fun value(): Value = block("Value", {
        expectOneOf(
                {STRING()},
                {NUMBER()},
                {BOOLEAN()},
                {array()}
        )
    })

    private fun variable(): Variable = block("Variable", {
        expectWord("$")
        Variable(NAME())
    })

    private fun type(): Type = block("Type", {
        expectOneOf(
                {
                    Type(
                            typeName(),
                            maybe { expectWord("!") } == true,
                            false
                    )
                },
                {
                    listType()
                }
        )
    })

    private fun typeName(): String =
            NAME()

    private fun listType(): Type = block("ListType", {
        expectWord("[")
        val t = type()
        expectWord("]")
        Type(t.typeName, t.isNonNullType, true)
    })

    private fun array(): Array = block("Array", {
        expectAndGetWord("[")
        val list = arrayListOf<Value>(value())
        list += multipleMaybes { value() }
        expectAndGetWord("]")

        Array(list)
    })

    private fun STRING(): StringValue = block("StringValue", {
        // '"' ( ESC | ~ ["\\] )* '"'

        expectWord("\"")

        val chars = multipleMaybes {
            expectOneOf(
                    { ESC() },
                    { expectNeitherOf(listOf('\"', '\\')) }
            )
        }

        expectWord("\"")

        StringValue(chars.joinToString(""))
    })

    private fun ESC(): String = block("ESC: String", {
        expectAndGet('\\')

        val escaped = expectOneOf(
                {expectAndGet {
                    it == '"' || it == '\\' || it == '/' || it == 'b' || it == 'f' || it == 'n' || it == 'r' || it == 't'
                }.toString() },
                {UNICODE()}
        )

        '\\' + escaped
    })

    private fun UNICODE(): String = block("UNICODE: String", {
        "" + expectAndGet('u') + HEX() + HEX() + HEX() + HEX()
    })

    private fun HEX(): Char = block("HEX: Char", {
        expectAndGet { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    })

    private fun BOOLEAN(): BooleanValue = block("BooleanValue", {
        expectOneOf(
                {
                    if (expectWord("true"))
                        BooleanValue(true)
                    else
                        throw Exception()
                },
                {
                    if (expectWord("false"))
                        BooleanValue(false)
                    else
                        throw Exception()
                }
        )
    })

    private fun NUMBER(): NumberValue = block("NumberValue", {
        // '-'? INT
        val minus = maybe { expectWord("-") } == true
        val integralPart = INT()

        maybe {
            expectOneOf(
                    {
                        // '.' [0-9]+ EXP?
                        expectWord(".")
                        val firstDigit = expectAndGetAnyOfWords(numbers0to9)
                        val restDigits = multipleMaybes { expectAndGetAnyOfWords(numbers0to9) }
                        val fractionalPart = (firstDigit + restDigits).toInt()
                        NumberValue(minus, integralPart, fractionalPart, maybe { EXP() }, getCurrentBlockAsString("NumberValue").trim())
                    },
                    {
                        // EXP
                        NumberValue(minus, integralPart, 0, EXP(), getCurrentBlockAsString("NumberValue").trim())
                    },
                    {
                        NumberValue(minus, integralPart, 0, null, getCurrentBlockAsString("NumberValue").trim())
                    }
            )
        }
    })

    private fun INT(): Int = block("INT", {
        if (maybe({ expectWord("0") }) == true) {
            0
        }
        else {
            val firstDigit = expectAndGetAnyOfWords(numbers1to9)
            val restDigits = multipleMaybes { expectAndGetAnyOfWords(numbers0to9) }

            val numberAsString = firstDigit + restDigits.joinToString("")
            numberAsString.toInt()
        }
    })

    private fun EXP(): Int = block("EXP", {
        expectAndGetAnyOfWords(listOf("e", "E"))
        val signStr = maybe { expectAndGetAnyOfWords(listOf("+", "-")) }
        val sign = if (signStr == "-") -1 else 1

        sign * INT()
    })

    private fun fragmentSpread(): FragmentSpread = block("FragmentSpread", {
        expectWord("...")
        FragmentSpread(fragmentName(), maybe(::directives))
    })

    private fun inlineFragment(): InlineFragment = block("InlineFragment", {
        expectWord("...")
        expectWord("on")
        InlineFragment(
                typeCondition(),
                maybe(::directives),
                selectionSet()
        )
    })

    private fun typeCondition(): TypeName = block("TypeCondition=TypeName", {
        TypeName(NAME())
    })

    private fun fragmentName(): String = block("FragmentName", {
        NAME()
    })

    private fun fragmentDefinition(): FragmentDefinition = block("FragmentDefinition", {
        expectWord("fragment")
        val name = fragmentName()
        expectWord("on")
        FragmentDefinition(
                name,
                typeCondition(),
                maybe { directives() },
                selectionSet()
        )
    })



    class VariableDefinitions(val definitions: List<VariableDefinition>)
    class VariableDefinition(val variable: Variable, val type: Type, val defaultValue: Value?)

    class Document(val definitions: List<Definition>) {
        fun operations(): List<OperationDefinition> =
                definitions.mapNotNull { it as OperationDefinition }
    }

    class Directives(val directives: List<Directive>)
    class Directive(val name: String, val valueOrVariable: ValueOrVariable? = null, val argument: Argument? = null)


    class Argument(val name: String, val valueOrVariable: ValueOrVariable)

    abstract class ValueOrVariable

    abstract class Value : ValueOrVariable()
    class StringValue(val string: String) : Value()
    class NumberValue(val isMinus: Boolean, val integralPart: Int, val fractionalPart: Int, val exponentialPart: Int?, private val valueAsString: String) : Value() {
        val value: Number = valueAsString.toDouble()
    }
    class BooleanValue(val boolean: Boolean) : Value()
    class Array(val values: ArrayList<Value>) : Value()

    class Variable(val name: String) : ValueOrVariable()

    abstract class Definition
    class FragmentDefinition(val fragmentName: String, val typeCondition: TypeName, val directives: Directives?, val selectionSet: SelectionSet) : Definition()
    class OperationDefinition(val operationType: String, val name: String?, val variableDefinitions: VariableDefinitions?, val directives: Directives?, val selectionSet: SelectionSet) : Definition()


    abstract class Selection
    class Field(val fieldName: FieldName, val arguments: List<Argument>?, val directives: Directives?, val selectionSet: SelectionSet?) : Selection()
    class FieldName(val name: String, val alias: String?)
    class FragmentSpread(val fragmentName: String, val directives: Directives?) : Selection()
    class InlineFragment(val typeCondition: TypeName, val directives: Directives?, val selectionSet: SelectionSet) : Selection()

    class Type(val typeName: String, val isNonNullType: Boolean, val isList: Boolean)
    class TypeName(val name: String)

    class SelectionSet(val selections: List<Selection>) {
        fun fields(): List<Field> = selections.mapNotNull { it as Field }
    }
}
