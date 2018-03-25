package net.namekdev.graphql2elm.parsers

abstract class AbstractParser(val buffer: String, val whitespace: List<Char>) {
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

    fun debugPos(): String {
        return debugPos(this.pos)
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

    protected fun fetchMeaningfulCharacter(): Char {
        pushPos()

        try {
            while (true) {
                if (isNextCharacterMeaningful()) {
                    val ch = fetchCharacter()
                    popPos()
                    return ch
                }
                else {
                    pos++
                }
            }
        }
        catch (exc: Exception) {
            popSavePos()
        }

        throw parseBackTrack()
    }

    protected fun goToFirstMeaningfulCharacter() {
        pushPos()
        try {
            while (!isNextCharacterMeaningful()) {
                pos++
            }
            popPos()
        }
        catch (exc: IndexOutOfBoundsException) {
            popSavePos()
            throw parseBackTrack()
        }
    }

    protected fun expectMeaningfulCharacter(ch: Char): Boolean {
        pushPos("expected character: '$ch'")
        try {
            goToFirstMeaningfulCharacter()
        }
        catch(exc: Exception) {
            popSavePos()
            throw parseBackTrack()
        }

        if (getCharacter(pos) != ch) {
            popSavePos()
            throw parseBackTrack()
        }
        else {
            popPos()
            pos++
            return true
        }
    }

    protected fun expectCharacter(ch: Char) {
        if (buffer[pos] == ch) {
            pos++
        }
        else {
            throw parseBackTrack()
        }
    }

    protected fun followsCharacter(ch: Char): Boolean {
        return if (buffer[pos] == ch) {
            pos++
            true
        }
        else {
            false
        }
    }

    protected fun followsMeaningfulCharacter(ch: Char): Boolean {
        val startPos = pos
        goToFirstMeaningfulCharacter()
        if (followsCharacter(ch)) {
            return true
        }
        else {
            pos = startPos
            return false
        }
    }

    protected fun followsWord(word: String): Boolean {
        pushPos()

        try {
            goToFirstMeaningfulCharacter()
            var ch: Char
            var i = 0
            do {
                ch = fetchCharacter()

                if (ch != word[i++]) {
                    popSavePos()
                    return false
                }
            } while (i < word.length)

            popPos()
            return true
        }
        catch (exc: Exception) {
            popSavePos()
            return false
        }
    }

    protected fun expectWord(word: String) {
        pushPos()

        var fail = false
        try {
            goToFirstMeaningfulCharacter()
            var ch: Char
            var i = 0
            do {
                ch = fetchCharacter()

                if (ch != word[i++]) {
                    fail = true
                    break
                }
            } while (i < word.length)
        }
        catch (exc: Exception) {
            fail = true
        }
        finally {
            if (fail) {
                popSavePos()
                throw parseBackTrack()
            }
            else {
                popPos()
            }
        }
    }

    protected inline fun expectAndGetWord(word: String): String {
        expectWord(word)
        return word
    }

    protected fun expectAnyOfWords(words: List<String>): String {
        val word = words.firstOrNull {
           followsWord(it)
        }

        return word ?: throw parseBackTrack()
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

    protected fun expectAnyCharacterOf(chars: List<Char>, immediateNeighbour: Boolean = false): Char {
        val ch =
                if (immediateNeighbour)
                    fetchCharacter()
                else
                    fetchMeaningfulCharacter()

        if (ch !in chars)
            throw parseBackTrack()

        return ch
    }

    protected inline fun expectAndGet(char: Char): Char {
        return expectAndGet { it == char }
    }

    protected inline fun expectNeitherOf(chars: List<Char>): Char {
        return expectAndGet { !chars.contains(it) }
    }

    protected inline fun maybeExpect(char: Char): Boolean {
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

    protected inline fun <T>multipleMaybes(noinline function: () -> T?, minimum: Int = 0): List<T> {
        val list = arrayListOf<T>()

        do {
            val el = maybe(function)

            if (el != null)
                list.add(el)
        }
        while (el != null)

        if (list.size < minimum)
            throw parseBackTrack()

        return list
    }

    protected fun <T>expectOneOf(vararg expressions: () -> T, throwErrorIfNotFound: Boolean = true): T {
        var ret: T? = null

        for (func in expressions) {
            ret = maybe(func)
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

    protected inline fun getCurrentBlockAsString(id: String): String {
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
