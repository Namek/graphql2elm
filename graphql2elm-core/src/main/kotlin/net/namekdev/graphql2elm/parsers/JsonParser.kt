package net.namekdev.graphql2elm.parsers

private val whitespace = listOf(' ', '\t', '\n', '\r')
private val numbers0to9 = (0..9).map { it.toChar() }
private val numbers1to9 = (1..9).map { it.toChar() }
private val exponentialPartPrefixes = listOf('e', 'E')


class JsonParser(buffer: String) : AbstractParser(buffer, whitespace) {
    fun parse(): Value {
        return value()
    }

    private fun value(): Value = block("Value", {
        expectOneOf(::STRING, ::NUMBER, ::obj, ::array, ::BOOLEAN, ::NULL)
    })

    private fun obj(): ValueObject = block("ValueObject", {
        expectMeaningfulCharacter('{')

        val pairs = mutableMapOf<String, Value>()
        val firstPair = maybe(::pair)
        if (firstPair != null) {
            pairs.put(firstPair.key, firstPair.value)

            while (true) {
                val ch = fetchMeaningfulCharacter()

                if (ch == ',') {
                    val p = pair()
                    pairs.put(p.key, p.value)
                }
                else if (ch == '}') {
                    break
                }
                else {
                    throw parseError("expected '}' for object enclose")
                }
            }
        }
        else {
            expectMeaningfulCharacter('}')
        }

        ValueObject(pairs)
    })

    private inline fun pair(): ObjPair = block("Pair", {
        val key = STRING().string
        expectMeaningfulCharacter(':')
        val value = value()

        ObjPair(key, value)
    })

    private fun array(): ValueArray = block("ValueArray", {
        expectMeaningfulCharacter('[')

        val values = mutableListOf<Value>()
        val firstVal = maybe(::value)
        if (firstVal != null) {
            values.add(firstVal)

            while (true) {
                val ch = fetchMeaningfulCharacter()
                if (ch == ',') {
                    values.add(value())
                }
                else if (ch == ']') {
                    break
                }
                else {
                    throw parseError("expected ']' for array enclose")
                }
            }
        }
        else {
            expectMeaningfulCharacter(']')
        }

        ValueArray(values)
    })

    private fun STRING(): ValueString = block("STRING", {
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
                }
                else if (esc == 'u') {
                    // unicode
                    block("escaped unicode", {
                        for (i in 1..4) {
                            expectAndGet { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
                        }
                    })
                }
                else {
                    throw parseError("unknown char escape: '\\$esc'")
                }
            }
            else if (ch in '\u0000'..'\u001F') {
                throw parseError("illegal unicode in string: '$ch'")
            }
        }

        if (pos >= buffer.length-1) {
            throw parseError("string finished before meeting closing '\"'")
        }

        ValueString(startPos, pos - 1, avoidPoints)
    })

    private inline fun NUMBER(): ValueNumber = block("ValueNumber", {
        // '-'? INT
        val minus = followsCharacter('-')
        val integralPart = INT()

        expectOneOf(
                {
                    // '.' [0-9]+ EXP?
                    expectCharacter('.')
                    val firstDigit = expectAnyCharacterOf(numbers0to9, true)
                    val restDigits = multipleMaybes({ expectAnyCharacterOf(numbers0to9, true) })
                    val fractionalPart = (firstDigit + restDigits.joinToString("")).toInt()
                    ValueNumber(minus, integralPart, fractionalPart, maybe(::EXP), getCurrentBlockAsString("ValueNumber").trim())
                },
                {
                    // EXP?
                    ValueNumber(minus, integralPart, 0, maybe(::EXP), getCurrentBlockAsString("ValueNumber").trim())
                })
    })

    private inline fun INT(): Int = block("INT", {
        if (followsCharacter('0')) {
            0
        }
        else {
            val firstDigit = expectAnyCharacterOf(numbers1to9)
            val restDigits = multipleMaybes({ expectAnyCharacterOf(numbers0to9) })

            val numberAsString = firstDigit + restDigits.joinToString("")
            numberAsString.toInt()
        }
    })

    private inline fun EXP(): Int = block("EXP", {
        expectAnyCharacterOf(exponentialPartPrefixes)
        val sign =
                if (followsCharacter('-')) {
                    fetchCharacter()
                    -1
                }
                else {
                    if (followsCharacter('+'))
                        fetchCharacter()

                    +1
                }

        sign * INT()
    })

    private inline fun BOOLEAN(): ValueBoolean = block("ValueBoolean", {
        expectOneOf(
                {
                    expectWord("true")
                    ValueBoolean(true)
                },
                {
                    expectWord("false")
                    ValueBoolean(false)
                }
        )
    })

    private inline fun NULL(): ValueNull = block("ValueNull", {
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

    inner class ValueString(val startPos: Int, val endPos: Int, val avoidPoints: List<Int>) : Value() {
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