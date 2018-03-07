package net.namekdev.graphql2elm.parsers

abstract class AbstractParser {
    /**
     * Position of next character.
     */
    var pos = 0

    private var posStack = arrayListOf<StackPos>()
    private var deepestAchievedBlockChain = listOf<StackPos>()

    data class StackPos(val pos: Int, val id: String)

    abstract fun isNextCharacterMeaningful(): Boolean
    abstract fun fetchCharacter(): Char
    abstract fun fetchString(start: Int, end: Int): String
    abstract fun debugPos(): String
    open fun debugPosForDeepestBlock(): String {
        val curPos = pos
        pos = deepestAchievedBlockChain.lastOrNull()?.pos ?: pos
        val str = debugPos()
        pos = curPos
        return str
    }

    protected fun fetchMeaningfulCharacter(): Char {
        pushPos()

        try {
            do {
                if (isNextCharacterMeaningful()) {
                    val ch = fetchCharacter()
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

        throw parseBackTrack()//"expected a meaningful character here")
    }

    private fun goToFirstMeaningfulCharacter() {
        pushPos()
        val startPos = pos
        try {
            while (!isNextCharacterMeaningful()) {
                pos++
            }
        }
        catch (exc: ParseBackTrack) {
            throw parseBackTrack()//"couldn't find first meaningful character from pos: ${startPos}")
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
                    throw parseBackTrack()//"expected word `${word}`")
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

        return word ?: throw parseBackTrack()//"none of words was found: " + (words.joinToString(",")))
    }

    protected fun expectAndGet(matches: (Char) -> Boolean): Char {
        pushPos()
        val ch = fetchCharacter()
        if (matches(ch)) {
            popPos()
            return ch
        }
        else {
            popSavePos()
            throw Exception()
        }
    }

    protected fun expectAndGet(char: Char): Char {
        return expectAndGet { it == char }
    }

    protected fun expectNeitherOf(chars: List<Char>): Char {
        return expectAndGet { !chars.contains(it) }
    }

    protected fun maybeExpect(char: Char): Boolean {
        return maybe { expectAndGet(char) } == char
    }

    protected fun pushPos(id: String = "") {
        posStack.add(StackPos(pos, id))

        if (id.isNotEmpty()) {
            // remember deepest achieved block
            val stack = posStack.filter {it.id.isNotEmpty()}

            if (stack.size >= deepestAchievedBlockChain.size) {
                deepestAchievedBlockChain = stack
            }
        }
    }

    protected fun popPos(): StackPos {
        return posStack.removeAt(posStack.lastIndex)
    }

    protected fun popSavePos() {
        pos = popPos().pos
    }

    protected fun <T>maybe(function: () -> T?): T? {
        pushPos()

        var ret: T? = null
        try {
            ret = function()
        }
        catch (exc: ParseBackTrack) {
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

    protected fun <T>expectOneOf(vararg expressions: () -> T, throwErrorIfNotFound: Boolean = true): T {
        var ret: T? = null

        for (func in expressions) {
            ret = maybe { func() }
            if (ret != null)
                break
        }

        if (ret != null)
            return ret

        if (throwErrorIfNotFound)
            throw parseError("none of ${expressions.size} options was found")
        else
            throw parseBackTrack()
    }

    protected fun <T>block(expectedTypeName: String, function: () -> T?): T {
        pushPos(expectedTypeName)

        var ret: T? = null
        try {
            ret = function()
        }
        catch (exc: ParseBackTrack) {
        }
        finally {
            if (ret != null) {
                popPos()
                return ret
            }
            else {
                popSavePos()
                throw parseBackTrack()
            }
        }
    }

    protected fun getCurrentBlockAsString(id: String): String {
        val end = this.pos
        val start = posStack.last { it.id == id }.pos

        return fetchString(start, end)
    }

    protected fun parseBackTrack(): Exception {
        return ParseBackTrack()
    }

    protected fun parseError(msg: String): ParseErrorException {
        return ParseErrorException(msg)
    }

    class ParseBackTrack : RuntimeException()
    class ParseErrorException(msg: String) : RuntimeException(msg)
}
