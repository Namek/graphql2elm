package net.namekdev.graphql2elm

import java.lang.Integer.max

class CodeEmitter(val cfg: CodeEmitterConfig) {
    private val sb = StringBuilder()
    private var curIndent: Int = 0


    fun indentForward(spaces: Int = 4) {
        curIndent += spaces
    }

    fun indentBackward(spaces: Int = 4) {
        curIndent = max(0, curIndent - spaces)
    }

    fun indentReset() {
        curIndent = 0
    }

    fun lineBegin(vararg str: String) {
        if (!isLineFinished())
            sb.append('\n')

        doIndent()
        lineContinue(*str)
    }

    fun lineContinue(vararg str: String) {
        if (isLineFinished())
            doIndent()

        sb.append(*str)
    }

    fun lineEnd(vararg str: String) {
        if (isLineFinished())
            doIndent()

        lineContinue(*str)
        sb.append('\n')
    }

    fun lineEmit(vararg str: String) {
        lineBegin(*str)
        sb.append('\n')
    }

    fun lineEmpty() {
        if (!isLineFinished())
            sb.append('\n')

        sb.append('\n')
    }

    private fun isLineFinished(): Boolean {
        return sb.isEmpty() || sb.last() == '\n'
    }

    private fun doIndent() {
        sb.append("".padEnd(curIndent, ' '))
    }

    override fun toString(): String {
        return sb.toString()
    }
}
