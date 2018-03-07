package net.namekdev.graphql2elm.parsers

class JsonParser(private val buffer: String) : AbstractParser() {
    private val whitespace = " \t\n\r"
    private val numbers0to9 = (0..9).map { it.toString() }
    private val numbers1to9 = (1..9).map { it.toString() }
    private val SAFECODEPOINT = listOf('\"', '\\') + ('\u0000'..'\u001F')


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
        return buffer.substring(0, pos) + "|" + buffer.substring(pos) //To change body of created functions use File | Settings | File Templates.
    }

    fun parse(): Value {
        return value()
    }

    private fun value(): Value = block("Value", {
        expectOneOf(::STRING, ::NUMBER, ::obj, ::array, ::BOOLEAN, ::NULL)
    })

    private fun obj(): ValueObject = block("ValueObject", {
        expectWord("{")

        val pairs = mutableMapOf<String, Value>()

        val firstPair = maybe { pair() }
        if (firstPair != null) {
            pairs.put(firstPair.key, firstPair.value)

            multipleMaybes {
                expectWord(",")
                val p = pair()
                pairs.put(p.key, p.value)
                p
            }
        }

        expectWord("}")
        ValueObject(pairs)
    })

    private fun pair(): ObjPair = block("Pair", {
        val key = STRING().string
        expectWord(":")
        val value = value()

        ObjPair(key, value)
    })

    private fun array(): ValueArray = block("ValueArray", {
        expectWord("[")

        val values = mutableListOf<Value>()
        val firstVal = maybe { value() }
        if (firstVal != null) {
            values.add(firstVal)

            multipleMaybes {
                expectWord(",")
                values.add(value())
            }
        }

        expectWord("]")
        ValueArray(values)
    })

    private fun STRING(): ValueString = block("ValueString", {
        // '"' ( ESC | ~ SAFECODEPOINT )* '"'

        expectWord("\"")

        val chars = multipleMaybes {
            expectOneOf(
                    { ESC() },
                    { expectNeitherOf(SAFECODEPOINT) }
            )
        }

        expectWord("\"")

        ValueString(chars.joinToString(""))
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

    private fun NUMBER(): ValueNumber = block("ValueNumber", {
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
                        ValueNumber(minus, integralPart, fractionalPart, maybe { EXP() }, getCurrentBlockAsString("ValueNumber").trim())
                    },
                    {
                        // EXP
                        ValueNumber(minus, integralPart, 0, EXP(), getCurrentBlockAsString("ValueNumber").trim())
                    },
                    {
                        ValueNumber(minus, integralPart, 0, null, getCurrentBlockAsString("ValueNumber").trim())
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

    private fun BOOLEAN(): ValueBoolean = block("ValueBoolean", {
        expectOneOf(
                {
                    if (expectWord("true"))
                        ValueBoolean(true)
                    else
                        throw Exception()
                },
                {
                    if (expectWord("false"))
                        ValueBoolean(false)
                    else
                        throw Exception()
                }
        )
    })

    private fun NULL(): ValueNull = block("ValueNull", {
        expectWord("null")
        ValueNull()
    })



    class ObjPair(val key: String, val value: JsonParser.Value)

    abstract class Value {
        open fun isNull(): Boolean = false

        fun asObject() = this as ValueObject
        fun asArray() = this as ValueArray
        fun asString() = this as ValueString
        fun asBoolean() = this as ValueBoolean
    }

    class ValueString(val string: String) : Value()
    class ValueNumber(val isMinus: Boolean, val integralPart: Int, val fractionalPart: Int, val exponentialPart: Int?, private val valueAsString: String) : Value() {
        val value: Number = valueAsString.toDouble()
    }
    class ValueObject(val pairs: Map<String, Value>) : Value() {
        val isEmpty = pairs.isEmpty()

        operator fun get(key: String): Value =
                pairs.get(key)!!

        fun has(key: String): Boolean =
                pairs.containsKey(key)

        fun getString(key: String): String =
                get(key).asString().string

        fun getArray(key: String) =
                get(key).asArray().values

        fun getObject(key: String) =
                get(key).asObject()
    }
    class ValueArray(val values: List<Value>) : Value() {
        val isEmpty = values.isEmpty()

        fun size() = values.size

        operator fun get(i: Int): Value = values[i]
    }
    class ValueBoolean(val bool: Boolean) : Value()
    class ValueNull : Value() {
        override fun isNull(): Boolean = true
    }
}