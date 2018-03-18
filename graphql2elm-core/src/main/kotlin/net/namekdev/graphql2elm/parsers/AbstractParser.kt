package net.namekdev.graphql2elm.parsers

abstract class AbstractParser(val buffer: String, val whitespace: String) {
    /**
     * Position of next character.
     */
    var pos = 0

    protected var posStack = arrayListOf<StackPos>()
    protected var deepestAchievedBlockChain = listOf<StackPos>()

    data class StackPos(val pos: Int, val id: String)

    open fun debugPosForDeepestBlock(): String {
        val curPos = pos
        pos = deepestAchievedBlockChain.lastOrNull()?.pos ?: pos
        val str = debugPos(pos)
        pos = curPos
        return str
    }

    protected fun debugPos(pos: Int): String {
        return buffer.substring(0, pos) + "|" + buffer.substring(pos)
    }

    protected inline fun isCharacterMeaningful(pos: Int): Boolean {
        val ch = buffer[pos]
        return !whitespace.contains(ch)
    }

    protected inline fun getCharacter(pos: Int): Char {
        return buffer[pos]
    }

    protected inline fun fetchString(start: Int, end: Int): String {
        return buffer.substring(start, end)
    }

    protected inline fun isNextCharacterMeaningful() = isCharacterMeaningful(pos)

    protected inline fun fetchCharacter() = getCharacter(pos++)

    protected inline fun fetchMeaningfulCharacter(): Char {
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

    protected inline fun goToFirstMeaningfulCharacter() {
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

    protected inline fun expectWord(word: String): Boolean {
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

    protected inline fun expectAndGetWord(word: String): String {
        expectWord(word)
        return word
    }

    protected inline fun expectAndGetAnyOfWords(words: List<String>): String {
        val word = words.firstOrNull {
            maybe { expectWord(it) } == true
        }

        return word ?: throw parseBackTrack()//"none of words was found: " + (words.joinToString(",")))
    }

    protected inline fun expectAndGet(matches: (Char) -> Boolean): Char {
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

    protected inline fun expectAndGet(char: Char): Char {
        return expectAndGet { it == char }
    }

    protected inline fun expectNeitherOf(chars: List<Char>): Char {
        return expectAndGet { !chars.contains(it) }
    }

    protected fun maybeExpect(char: Char): Boolean {
        return maybe { expectAndGet(char) } == char
    }

    protected inline fun pushPos(id: String = "") {
        posStack.add(StackPos(pos, id))

        if (id.isNotEmpty()) {
            // remember deepest achieved block
            val stack = posStack.filter {it.id.isNotEmpty()}

            if (stack.size >= deepestAchievedBlockChain.size) {
                deepestAchievedBlockChain = stack
            }
        }
    }

    protected inline fun popPos(): StackPos {
        return posStack.removeAt(posStack.lastIndex)
    }

    protected inline fun popSavePos() {
        pos = popPos().pos
    }

    protected inline fun <T>maybe(function: () -> T?): T? {
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

    protected inline fun <T>maybeOr(noinline function: () -> T?, getDefault: () -> T): T {
        return maybe(function) ?: getDefault()
    }

    protected inline fun <T>multipleMaybes(noinline function: () -> T?): List<T> {
        val list = arrayListOf<T>()

        do {
            val el = maybe(function)

            if (el != null)
                list.add(el)
        }
        while (el != null)

        return list
    }

    protected inline fun <T>expectOneOf(vararg expressions: () -> T, throwErrorIfNotFound: Boolean = true): T {
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

    protected inline fun <T>block(expectedTypeName: String, function: () -> T?): T {
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

    protected inline fun parseBackTrack(): Exception {
        return ParseBackTrack
    }

    protected inline fun parseError(msg: String): ParseErrorException {
        return ParseErrorException(msg)
    }

    object ParseBackTrack : RuntimeException()
    class ParseErrorException(msg: String) : RuntimeException(msg)
}
