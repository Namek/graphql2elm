package net.namekdev.graphql2elm.parsers

import net.namekdev.graphql2elm.misc.*

private val whitespace =
        listOf(
                '\t',    // Horizontal Tab
                '\n',    // New Line
                '\r',    // Carriage Return
                '\b',    // Back Space
                '\u000c',// Form Feed
                ' '      // Space
        )

private val numbers0to9 = (0..9).map { it.toChar() }
private val numbers1to9 = (1..9).map { it.toChar() }
private val exponentialPartPrefixes = listOf('e', 'E')


class GraphQLParser(buffer: String) : AbstractParser(buffer, whitespace) {
    private val variableRefs = mutableListOf<Variable>()


    fun parse(): Result<Document, List<String>> {
        val errors = mutableListOf<String>()
        var ret: Result<Document, List<String>> = Result.err(errors)

        try {
            val doc = document()

            val allVariablesExist = variableRefs.all { varRef ->
                doc.definitions.mapNotNull { it as OperationDefinition }
                        .any {
                            val variableExists = it.variableDefinitions?.definitions?.any {
                                it.variable.name == varRef.name
                            } ?: false

                            if (!variableExists) {
                                errors.add("variable ${varRef.name} is not defined")
                            }

                            variableExists
                        }
            }

            if (!allVariablesExist) {
                throw ParseErrorException("some variables are not defined")
            }

            if (errors.size == 0) {
                ret = Result.ok(doc)
            }
        } catch (exc: ParseErrorException) {
            errors.add(exc.toString())
        }

        return ret
    }

    private fun document(): Document = block("document", {
        Document(listOf(definition()) + multipleMaybes(::definition))
    })

    private fun definition(): Definition = block("Definition", {
        expectOneOf(::operationDefinition, ::fragmentDefinition)
    })

    private fun operationDefinition(): OperationDefinition = block("OperationDefinition", {
        expectOneOf(
                {
                    OperationDefinition(
                            operationType(),
                            maybe { NAME() },
                            maybe(::variableDefinitions),
                            maybe(::directives),
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
                Directives(multipleMaybes(::directive, 1))
            })


    private fun directive(): Directive = block("Directive", {
        // '@' NAME ':' valueOrVariable | '@' NAME | '@' NAME '(' argument ')'

        expectMeaningfulCharacter('@')
        val name = NAME(true)

        val ch = fetchMeaningfulCharacter()

        if (ch == ':') {
            Directive(name, valueOrVariable())
        } else if (ch == '(') {
            val arg = argument()
            expectMeaningfulCharacter(')')

            Directive(name, argument = arg)
        } else {
            pos--
            Directive(name)
        }
    })

    private fun variableDefinitions(): VariableDefinitions = block("VariableDefinitions", {
        expectMeaningfulCharacter('(')

        val firstDef = variableDefinition()
        val restDefs = multipleMaybes({
            expectMeaningfulCharacter(',')
            variableDefinition()
        })

        expectMeaningfulCharacter(')')

        VariableDefinitions(listOf(firstDef) + restDefs)
    })

    private fun variableDefinition(): VariableDefinition = block("VariableDefinition", {
        val varr = variable()
        expectMeaningfulCharacter(':')
        val t = type()
        val defaultValue = maybe(::defaultValue)

        VariableDefinition(varr, t, defaultValue)
    })

    private fun defaultValue(): Value = block("defaultValue: Value", {
        expectMeaningfulCharacter('=')
        value()
    })

    private fun NAME(immediateNeighbour: Boolean = false): String {
        // regex: """[_A-Za-z][_0-9A-Za-z]*"""

        pushPos()
        var str = ""
        var ch =
                if (immediateNeighbour)
                    fetchCharacter()
                else
                    fetchMeaningfulCharacter()

        if (ch == '_' || ch in 'a'..'z' || ch in 'A'..'Z') {
            str += ch

            while (isNextCharacterMeaningful()) {
                pushPos()
                ch = fetchMeaningfulCharacter()

                if (ch == '_' || ch in 'a'..'z' || ch in 'A'..'Z' || ch in '0'..'9') {
                    str += ch
                    popPos()
                } else {
                    popSavePos()
                    break
                }
            }

            return str
        } else {
            popPos()
        }

        throw parseBackTrack()//"expected NAME")
    }

    private fun operationType(): String = block("OperationType: 'query' or 'mutation'", {
        expectAnyOfWords(listOf("query", "mutation"))
    })

    private fun selectionSet(): SelectionSet = block("SelectionSet", {
        expectMeaningfulCharacter('{')

        val list = arrayListOf(selection())

        while (true) {
            val ch = fetchMeaningfulCharacter()

            if (ch == ',') {
                // there may be more commas that should be ignored
                continue
            } else if (ch == '}') {
                break
            } else {
                // suppose there was whitespace so we moved 1 byte too far
                --pos
                list += selection()
            }
        }

        SelectionSet(list)
    })

    private fun selection(): Selection = block("Selection", {
        expectOneOf(::field, ::fragmentSpread, ::inlineFragment)
    })

    private fun field(): Field = block("Field", {
        Field(
                fieldName(),
                maybe(::arguments),
                maybe(::directives),
                maybe(::selectionSet)
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
        expectMeaningfulCharacter(':')
        val alias = NAME()
        FieldName(name, alias)
    })

    private fun arguments(): List<Argument> = block("expected argument list", {
        expectMeaningfulCharacter('(')
        val list = mutableListOf(argument())

        while (followsMeaningfulCharacter(',')) {
            list += argument()
        }

        expectMeaningfulCharacter(')')
        list
    })

    private fun argument(): Argument = block("expected argument", {
        val name = NAME()
        expectMeaningfulCharacter(':')
        Argument(name, valueOrVariable())
    })

    private fun valueOrVariable(): ValueOrVariable = block("ValueOrVariable", {
        val valOrVar = expectOneOf(::value, ::variable)

        if (valOrVar is Variable) {
            variableRefs.add(valOrVar)
        }

        valOrVar
    })

    private fun value(): Value = block("Value", {
        expectOneOf(::STRING, ::NUMBER, ::BOOLEAN, ::array, ::enumValue)
    })

    private fun variable(): Variable = block("Variable", {
        expectMeaningfulCharacter('$')
        Variable(NAME(true))
    })

    private fun type(): Type = block("Type", {
        expectOneOf(
                {
                    Type(
                            typeName(),
                            followsCharacter('!'),
                            false
                    )
                },
                {
                    listType()
                })
    })

    private fun typeName(): String =
            NAME()

    private fun listType(): Type = block("ListType", {
        expectMeaningfulCharacter('[')
        val t = type()
        expectMeaningfulCharacter(']')
        Type(t.typeName, t.isNonNullType, true)
    })

    private fun array(): Array = block("Array", {
        expectMeaningfulCharacter('[')

        val values = mutableListOf<Value>()
        val firstVal = maybe(::value)
        if (firstVal != null) {
            values.add(firstVal)

            while (true) {
                val ch = fetchMeaningfulCharacter()

                if (ch == ',') {
                    // there may be more commas that should be ignored
                    continue
                } else if (ch == ']') {
                    break
                } else {
                    // suppose there was whitespace so we moved 1 byte too far
                    --pos
                    values.add(value())
                }
            }
        } else {
            expectMeaningfulCharacter(']')
        }

        Array(values)
    })

    private fun enumValue(): EnumValue = block("EnumValue", {
        val str = NAME()

        if (str == "true" || str == "false" || str == "null") {
            throw parseBackTrack()//"true/false/null can't be an enum value")
        }

        EnumValue(str)
    })

    private fun STRING(): StringValue = block("StringValue", {
        expectMeaningfulCharacter('"')

        val startPos = pos
        val avoidPoints = mutableListOf<Int>()

        while (pos < buffer.length) {
            val ch = buffer[pos++]

            if (ch == '"') {
                break
            }

            //escaping
            if (ch == '\\') {
                val esc = buffer[pos]

                if (esc == '"' || esc == '\\' || esc == '/' || esc == 'b' || esc == 'f' || esc == 'n' || esc == 'r' || esc == 't') {
                    avoidPoints.add(pos++)
                } else if (esc == 'u') {
                    // unicode
                    block("escaped unicode", {
                        for (i in 1..4) {
                            expectAndGet { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
                        }
                    })
                } else {
                    throw parseError("unknown char escape: '\\$esc'")
                }
            } else if (ch in '\u0000'..'\u001F') {
                throw parseError("illegal unicode in string: '$ch'")
            }
        }

        if (pos >= buffer.length - 1) {
            throw parseError("string finished before meeting closing '\"'")
        }

        StringValue(startPos, pos - 1, avoidPoints)
    })

    private fun BOOLEAN(): BooleanValue = block("BooleanValue", {
        expectOneOf(
                {
                    expectWord("true")
                    BooleanValue(true)
                },
                {
                    expectWord("false")
                    BooleanValue(false)
                }
        )
    })

    private fun NUMBER(): NumberValue = block("NumberValue", {
        // '-'? INT
        val minus = followsCharacter('-')
        val integralPart = INT(true)

        expectOneOf(
                {
                    // '.' [0-9]+ EXP?
                    expectCharacter('.')
                    val firstDigit = expectAnyCharacterOf(numbers0to9, true)
                    val restDigits = multipleMaybes({ expectAnyCharacterOf(numbers0to9, true) })
                    val fractionalPart = (firstDigit + restDigits.joinToString("")).toInt()
                    NumberValue(minus, integralPart, fractionalPart, maybe(::EXP), getCurrentBlockAsString("ValueNumber").trim())
                },
                {
                    // EXP?
                    NumberValue(minus, integralPart, 0, maybe(::EXP), getCurrentBlockAsString("ValueNumber").trim())
                })
    })

    private fun INT(immediateNeighbour: Boolean = false): Int = block("INT", {
        if (followsCharacter('0')) {
            0
        } else {
            val firstDigit = expectAnyCharacterOf(numbers1to9, immediateNeighbour)
            val restDigits = multipleMaybes({ expectAnyCharacterOf(numbers0to9, true) })

            val numberAsString = firstDigit + restDigits.joinToString("")
            numberAsString.toInt()
        }
    })

    private fun EXP(): Int = block("EXP", {
        expectAnyCharacterOf(exponentialPartPrefixes)

        val sign =
                if (followsCharacter('-')) {
                    fetchCharacter()

                    -1
                } else {
                    if (followsCharacter('+'))
                        fetchCharacter()

                    +1
                }

        sign * INT(true)
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
                maybe(::directives),
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

    abstract class ValueOrVariable {
        fun isConstValue() = this is Value
        fun isVariable() = this is Variable

        fun varName() = (this as Variable).name
        fun asValue() = this as GraphQLParser.Value
    }

    abstract class Value : ValueOrVariable()
    inner class StringValue(val startPos: Int, val endPos: Int, val avoidPoints: List<Int>) : Value() {
        val string: String by lazy {
            if (avoidPoints.isEmpty()) {
                buffer.substring(startPos, endPos)
            } else {
                val sb = StringBuilder()
                var pos = startPos
                val avoid = avoidPoints.iterator()
                while (pos < endPos && avoid.hasNext()) {
                    val avoidNext = avoid.next()
                    sb.append(buffer.substring(pos, avoidNext))
                    pos = avoidNext
                }

                if (pos < endPos - 1)
                    sb.append(buffer.substring(pos, endPos))

                sb.toString()
            }
        }
    }

    class NumberValue(val isMinus: Boolean, val integralPart: Int, val fractionalPart: Int, val exponentialPart: Int?, val valueAsString: String) : Value() {
        val value: Number = valueAsString.toDouble()
    }

    class BooleanValue(val boolean: Boolean) : Value()
    class Array(val values: List<Value>) : Value()
    class EnumValue(val value: String) : Value()

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

