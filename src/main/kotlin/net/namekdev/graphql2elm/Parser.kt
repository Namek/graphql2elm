package net.namekdev.graphql2elm

abstract class AbstractParser {
    /**
     * Position of next character.
     */
    var pos = 0

    private var posStack = arrayListOf<Int>()

    abstract fun isNextCharacterMeaningful(): Boolean
    abstract fun fetchCharacter(): Char
    abstract fun fetchMeaningfulCharacter(): Char
    abstract fun fetchString(start: Int, end: Int): String
    abstract fun debugPos(): String

    private fun goToFirstMeaningfulCharacter() {
        pushPos()
        val startPos = pos
        try {
            while (!isNextCharacterMeaningful()) {
                pos++
            }
        }
        catch (exc: Exception) {
            throw parseBackTrack("couldn't find first meaningful character from pos: ${startPos}")
        }
        finally {
            popPos()
        }
    }

    protected fun expectWord(word: String): Boolean {
        pushPos()

        try {
            goToFirstMeaningfulCharacter()
            var ch: Char
            var i = 0
            do {
                ch = fetchMeaningfulCharacter()

                if (ch != word[i++]) {
                    throw parseBackTrack("expected word `${word}`")
                }
            } while (i < word.length)
        }
        finally {
            popPos()
        }
        return true
    }

    protected fun expectAndGetWord(word: String): String {
        expectWord(word)
        return word
    }

    protected fun expectAndGetAnyOfWords(words: List<String>): String {
        val word = words.firstOrNull {
            maybe { expectWord(it) } == true
        }

        return word ?: throw parseBackTrack("none of words was found: " + (words.joinToString(",")))
    }

    protected fun expectAndGet(matches: (Char) -> Boolean): Char {
        pushPos()
        val ch = fetchCharacter()
        if (matches(ch)) {
            popSavePos()
            return ch
        }
        else {
            popPos()
            throw Exception()
        }
    }

    protected fun expectAndGet(char: Char): Char {
        return expectAndGet { it == char }
    }

    protected fun expectNeitherOf(chars: List<Char>): Char {
        return expectAndGet { !chars.contains(it) }
    }

    protected fun expect(char: Char): Boolean {
        return maybe { expectAndGet(char) } == char
    }

    protected fun pushPos() {
        posStack.add(pos)
    }

    protected fun popPos(): Int {
        return posStack.removeAt(posStack.lastIndex)
    }

    protected fun popSavePos() {
        pos = popPos()
    }

    protected fun <T>maybe(function: () -> T?): T? {
        pushPos()

        var ret: T? = null
        try {
            ret = function()
        }
        catch (exc: Exception) {
            ret = null
        }
        finally {
            if (ret == null)
                popSavePos()
            else
                popPos()

            return ret
        }
    }

    protected fun <T>maybeOr(function: () -> T?, getDefault: () -> T): T {
        return maybe(function) ?: getDefault()
    }

    protected fun <T>multipleMaybes(function: () -> T?): List<T> {
        val list = arrayListOf<T>()

        do {
            val el = maybe(function)

            if (el != null)
                list.add(el)
        }
        while (el != null)

        return list
    }

    protected fun <T>expectAnyOf(vararg func: () -> T): T {
        var ret: T? = null
        func.firstOrNull {
            ret = maybe { it() }
            ret != null
        }

        if (ret != null)
            return ret as T

        throw parseBackTrack("none of ${func.size} options was found")
    }

    protected fun <T>block(expectedTypeName: String, function: () -> T?): T {
        pushPos()
        var ret: T? = null
        try {
            ret = function()
        }
        catch (exc: Exception) {
        }
        finally {
            if (ret != null) {
                popPos()
                return ret
            }
            else {
                popSavePos()
                throw parseBackTrack("expected $expectedTypeName")
            }
        }
    }

    protected fun getCurrentBlockAsString(): String {
        val end = this.pos
        val start = posStack.last()

        return fetchString(start, end)
    }

    protected fun parseBackTrack(str: String): Exception {
        return Exception(str)
    }
}

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

    override fun fetchMeaningfulCharacter(): Char {
        pushPos()

        try {
            do {
                if (isNextCharacterMeaningful()) {
                    val ch = buffer[pos++]
                    popPos()
                    return ch
                }
                else {
                    pos++
                }
            } while (true)
        }
        catch (exc: Exception) {
            popPos()
        }

        throw parseBackTrack("expected a meaningful character here")
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
        expectAnyOf(::operationDefinition, ::fragmentDefinition)
    })

    private fun operationDefinition(): OperationDefinition = block("OperationDefinition", {
        expectAnyOf(
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

                expectAnyOf(
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
        expectAnyOf<String>(
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
        expectAnyOf(::field, ::fragmentSpread, ::inlineFragment)
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
        expectAnyOf(
                ::alias,
                {FieldName(NAME(), null)}
        ) as FieldName
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
        expectAnyOf(::value, ::variable)
    })

    private fun value(): Value = block("Value", {
        expectAnyOf(
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
        expectAnyOf(
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
            expectAnyOf(
                    { ESC() },
                    { expectNeitherOf(listOf('\"', '\\')) }
            )
        }

        expectWord("\"")

        StringValue(chars.joinToString(""))
    })

    private fun ESC(): String = block("ESC: String", {
        expect('\\')

        val escaped = expectAnyOf(
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
        expectAnyOf(
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
            expectAnyOf(
                    {
                        // '.' [0-9]+ EXP?
                        expectWord(".")
                        val firstDigit = expectAndGetAnyOfWords(numbers0to9)
                        val restDigits = multipleMaybes { expectAndGetAnyOfWords(numbers0to9) }
                        val fractionalPart = (firstDigit + restDigits).toInt()
                        NumberValue(minus, integralPart, fractionalPart, maybe { EXP() }, getCurrentBlockAsString().trim())
                    },
                    {
                        // EXP
                        NumberValue(minus, integralPart, 0, EXP(), getCurrentBlockAsString().trim())
                    },
                    {
                        NumberValue(minus, integralPart, 0, null, getCurrentBlockAsString().trim())
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
