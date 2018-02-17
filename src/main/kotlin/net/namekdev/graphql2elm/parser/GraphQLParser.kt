// Generated from C:/Users/Namek/.babun/cygwin/home/Namek/graphql2elm/src\GraphQL.g4 by ANTLR 4.7
package net.namekdev.graphql2elm.parser

import org.antlr.v4.runtime.atn.*
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

class GraphQLParser(input: TokenStream) : Parser(input) {

    @Deprecated("")
    override fun getTokenNames(): Array<String> {
        return Companion.tokenNames
    }

    override fun getVocabulary(): Vocabulary {
        return VOCABULARY
    }

    override fun getGrammarFileName(): String {
        return "GraphQL.g4"
    }

    override fun getRuleNames(): Array<String> {
        return Companion.ruleNames
    }

    override fun getSerializedATN(): String {
        return _serializedATN
    }

    override fun getATN(): ATN {
        return _ATN
    }

    init {
        _interp = ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache)
    }

    class DocumentContext(parent: ParserRuleContext?, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun definition(): List<DefinitionContext> {
            return getRuleContexts(DefinitionContext::class.java)
        }

        fun definition(i: Int): DefinitionContext? {
            return getRuleContext(DefinitionContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_document
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterDocument(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitDocument(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitDocument(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun document(): DocumentContext {
        val _localctx = DocumentContext(_ctx, state)
        enterRule(_localctx, 0, RULE_document)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 59
                _errHandler.sync(this)
                _la = _input.LA(1)
                do {
                    run {
                        run {
                            state = 58
                            definition()
                        }
                    }
                    state = 61
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                } while (_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl T__0 or (1L shl T__3) or (1L shl T__4) or (1L shl T__10)) != 0L)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class DefinitionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun operationDefinition(): OperationDefinitionContext? {
            return getRuleContext(OperationDefinitionContext::class.java, 0)
        }

        fun fragmentDefinition(): FragmentDefinitionContext? {
            return getRuleContext(FragmentDefinitionContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_definition
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterDefinition(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitDefinition(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitDefinition(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun definition(): DefinitionContext {
        val _localctx = DefinitionContext(_ctx, state)
        enterRule(_localctx, 2, RULE_definition)
        try {
            state = 65
            _errHandler.sync(this)
            when (_input.LA(1)) {
                T__0, T__3, T__4 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 63
                        operationDefinition()
                    }
                }
                T__10 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 64
                        fragmentDefinition()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class OperationDefinitionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun selectionSet(): SelectionSetContext {
            return getRuleContext(SelectionSetContext::class.java, 0)
        }

        fun operationType(): OperationTypeContext? {
            return getRuleContext(OperationTypeContext::class.java, 0)
        }

        fun NAME(): TerminalNode? {
            return getToken(NAME, 0)
        }

        fun variableDefinitions(): VariableDefinitionsContext? {
            return getRuleContext(VariableDefinitionsContext::class.java, 0)
        }

        fun directives(): DirectivesContext? {
            return getRuleContext(DirectivesContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_operationDefinition
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterOperationDefinition(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitOperationDefinition(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitOperationDefinition(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun operationDefinition(): OperationDefinitionContext {
        val _localctx = OperationDefinitionContext(_ctx, state)
        enterRule(_localctx, 4, RULE_operationDefinition)
        var _la: Int
        try {
            state = 80
            _errHandler.sync(this)
            when (_input.LA(1)) {
                T__0 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 67
                        selectionSet()
                    }
                }
                T__3, T__4 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 68
                        operationType()
                        state = 70
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        if (_la == NAME) {
                            run {
                                state = 69
                                match(NAME)
                            }
                        }

                        state = 73
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        if (_la == T__6) {
                            run {
                                state = 72
                                variableDefinitions()
                            }
                        }

                        state = 76
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        if (_la == T__11) {
                            run {
                                state = 75
                                directives()
                            }
                        }

                        state = 78
                        selectionSet()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class SelectionSetContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun selection(): List<SelectionContext> {
            return getRuleContexts(SelectionContext::class.java)
        }

        fun selection(i: Int): SelectionContext? {
            return getRuleContext(SelectionContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_selectionSet
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterSelectionSet(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitSelectionSet(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitSelectionSet(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun selectionSet(): SelectionSetContext? {
        val _localctx = SelectionSetContext(_ctx, state)
        enterRule(_localctx, 6, RULE_selectionSet)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 82
                match(T__0)
                state = 83
                selection()
                state = 90
                _errHandler.sync(this)
                _la = _input.LA(1)
                while (_la and 0x3f.inv() == 0 && 1L shl _la and (1L shl T__1 or (1L shl T__8) or (1L shl NAME)) != 0L) {
                    run {
                        run {
                            state = 85
                            _errHandler.sync(this)
                            _la = _input.LA(1)
                            if (_la == T__1) {
                                run {
                                    state = 84
                                    match(T__1)
                                }
                            }

                            state = 87
                            selection()
                        }
                    }
                    state = 92
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                }
                state = 93
                match(T__2)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class OperationTypeContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        override fun getRuleIndex(): Int {
            return RULE_operationType
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterOperationType(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitOperationType(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitOperationType(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun operationType(): OperationTypeContext {
        val _localctx = OperationTypeContext(_ctx, state)
        enterRule(_localctx, 8, RULE_operationType)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 95
                _la = _input.LA(1)
                if (!(_la == T__3 || _la == T__4)) {
                    _errHandler.recoverInline(this)
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true
                    _errHandler.reportMatch(this)
                    consume()
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class SelectionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun field(): FieldContext? {
            return getRuleContext(FieldContext::class.java, 0)
        }

        fun fragmentSpread(): FragmentSpreadContext? {
            return getRuleContext(FragmentSpreadContext::class.java, 0)
        }

        fun inlineFragment(): InlineFragmentContext? {
            return getRuleContext(InlineFragmentContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_selection
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterSelection(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitSelection(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitSelection(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun selection(): SelectionContext {
        val _localctx = SelectionContext(_ctx, state)
        enterRule(_localctx, 10, RULE_selection)
        try {
            state = 100
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 8, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 97
                        field()
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 98
                        fragmentSpread()
                    }
                }
                3 -> {
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 99
                        inlineFragment()
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FieldContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun fieldName(): FieldNameContext {
            return getRuleContext(FieldNameContext::class.java, 0)
        }

        fun arguments(): ArgumentsContext? {
            return getRuleContext(ArgumentsContext::class.java, 0)
        }

        fun directives(): DirectivesContext? {
            return getRuleContext(DirectivesContext::class.java, 0)
        }

        fun selectionSet(): SelectionSetContext? {
            return getRuleContext(SelectionSetContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_field
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterField(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitField(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitField(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun field(): FieldContext {
        val _localctx = FieldContext(_ctx, state)
        enterRule(_localctx, 12, RULE_field)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 102
                fieldName()
                state = 104
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__6) {
                    run {
                        state = 103
                        arguments()
                    }
                }

                state = 107
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__11) {
                    run {
                        state = 106
                        directives()
                    }
                }

                state = 110
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__0) {
                    run {
                        state = 109
                        selectionSet()
                    }
                }

            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FieldNameContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun alias(): AliasContext? {
            return getRuleContext(AliasContext::class.java, 0)
        }

        fun NAME(): TerminalNode? {
            return getToken(NAME, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_fieldName
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterFieldName(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitFieldName(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitFieldName(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun fieldName(): FieldNameContext? {
        val _localctx = FieldNameContext(_ctx, state)
        enterRule(_localctx, 14, RULE_fieldName)
        try {
            state = 114
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 12, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 112
                        alias()
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 113
                        match(NAME)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class AliasContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun NAME(): List<TerminalNode>? {
            return getTokens(NAME)
        }

        fun NAME(i: Int): TerminalNode? {
            return getToken(NAME, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_alias
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterAlias(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitAlias(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitAlias(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun alias(): AliasContext {
        val _localctx = AliasContext(_ctx, state)
        enterRule(_localctx, 16, RULE_alias)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 116
                match(NAME)
                state = 117
                match(T__5)
                state = 118
                match(NAME)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ArgumentsContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun argument(): List<ArgumentContext>? {
            return getRuleContexts(ArgumentContext::class.java)
        }

        fun argument(i: Int): ArgumentContext? {
            return getRuleContext(ArgumentContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_arguments
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterArguments(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitArguments(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitArguments(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun arguments(): ArgumentsContext {
        val _localctx = ArgumentsContext(_ctx, state)
        enterRule(_localctx, 18, RULE_arguments)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 120
                match(T__6)
                state = 121
                argument()
                state = 126
                _errHandler.sync(this)
                _la = _input.LA(1)
                while (_la == T__1) {
                    run {
                        run {
                            state = 122
                            match(T__1)
                            state = 123
                            argument()
                        }
                    }
                    state = 128
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                }
                state = 129
                match(T__7)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ArgumentContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun NAME(): TerminalNode {
            return getToken(NAME, 0)
        }

        fun valueOrVariable(): ValueOrVariableContext {
            return getRuleContext(ValueOrVariableContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_argument
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterArgument(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitArgument(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitArgument(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun argument(): ArgumentContext {
        val _localctx = ArgumentContext(_ctx, state)
        enterRule(_localctx, 20, RULE_argument)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 131
                match(NAME)
                state = 132
                match(T__5)
                state = 133
                valueOrVariable()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FragmentSpreadContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun fragmentName(): FragmentNameContext {
            return getRuleContext(FragmentNameContext::class.java, 0)
        }

        fun directives(): DirectivesContext? {
            return getRuleContext(DirectivesContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_fragmentSpread
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterFragmentSpread(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitFragmentSpread(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitFragmentSpread(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun fragmentSpread(): FragmentSpreadContext {
        val _localctx = FragmentSpreadContext(_ctx, state)
        enterRule(_localctx, 22, RULE_fragmentSpread)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 135
                match(T__8)
                state = 136
                fragmentName()
                state = 138
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__11) {
                    run {
                        state = 137
                        directives()
                    }
                }

            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class InlineFragmentContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun typeCondition(): TypeConditionContext {
            return getRuleContext(TypeConditionContext::class.java, 0)
        }

        fun selectionSet(): SelectionSetContext {
            return getRuleContext(SelectionSetContext::class.java, 0)
        }

        fun directives(): DirectivesContext? {
            return getRuleContext(DirectivesContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_inlineFragment
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterInlineFragment(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitInlineFragment(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitInlineFragment(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun inlineFragment(): InlineFragmentContext {
        val _localctx = InlineFragmentContext(_ctx, state)
        enterRule(_localctx, 24, RULE_inlineFragment)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 140
                match(T__8)
                state = 141
                match(T__9)
                state = 142
                typeCondition()
                state = 144
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__11) {
                    run {
                        state = 143
                        directives()
                    }
                }

                state = 146
                selectionSet()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FragmentDefinitionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun fragmentName(): FragmentNameContext {
            return getRuleContext(FragmentNameContext::class.java, 0)
        }

        fun typeCondition(): TypeConditionContext {
            return getRuleContext(TypeConditionContext::class.java, 0)
        }

        fun selectionSet(): SelectionSetContext {
            return getRuleContext(SelectionSetContext::class.java, 0)
        }

        fun directives(): DirectivesContext? {
            return getRuleContext(DirectivesContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_fragmentDefinition
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterFragmentDefinition(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitFragmentDefinition(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitFragmentDefinition(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun fragmentDefinition(): FragmentDefinitionContext {
        val _localctx = FragmentDefinitionContext(_ctx, state)
        enterRule(_localctx, 26, RULE_fragmentDefinition)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 148
                match(T__10)
                state = 149
                fragmentName()
                state = 150
                match(T__9)
                state = 151
                typeCondition()
                state = 153
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__11) {
                    run {
                        state = 152
                        directives()
                    }
                }

                state = 155
                selectionSet()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class FragmentNameContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun NAME(): TerminalNode {
            return getToken(NAME, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_fragmentName
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterFragmentName(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitFragmentName(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitFragmentName(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun fragmentName(): FragmentNameContext {
        val _localctx = FragmentNameContext(_ctx, state)
        enterRule(_localctx, 28, RULE_fragmentName)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 157
                match(NAME)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class DirectivesContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun directive(): List<DirectiveContext> {
            return getRuleContexts(DirectiveContext::class.java)
        }

        fun directive(i: Int): DirectiveContext? {
            return getRuleContext(DirectiveContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_directives
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterDirectives(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitDirectives(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitDirectives(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun directives(): DirectivesContext {
        val _localctx = DirectivesContext(_ctx, state)
        enterRule(_localctx, 30, RULE_directives)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 160
                _errHandler.sync(this)
                _la = _input.LA(1)
                do {
                    run {
                        run {
                            state = 159
                            directive()
                        }
                    }
                    state = 162
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                } while (_la == T__11)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class DirectiveContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun NAME(): TerminalNode {
            return getToken(NAME, 0)
        }

        fun valueOrVariable(): ValueOrVariableContext {
            return getRuleContext(ValueOrVariableContext::class.java, 0)
        }

        fun argument(): ArgumentContext {
            return getRuleContext(ArgumentContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_directive
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterDirective(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitDirective(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitDirective(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun directive(): DirectiveContext {
        val _localctx = DirectiveContext(_ctx, state)
        enterRule(_localctx, 32, RULE_directive)
        try {
            state = 176
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 18, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 164
                        match(T__11)
                        state = 165
                        match(NAME)
                        state = 166
                        match(T__5)
                        state = 167
                        valueOrVariable()
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 168
                        match(T__11)
                        state = 169
                        match(NAME)
                    }
                }
                3 -> {
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 170
                        match(T__11)
                        state = 171
                        match(NAME)
                        state = 172
                        match(T__6)
                        state = 173
                        argument()
                        state = 174
                        match(T__7)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class TypeConditionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun typeName(): TypeNameContext {
            return getRuleContext(TypeNameContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_typeCondition
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterTypeCondition(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitTypeCondition(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitTypeCondition(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun typeCondition(): TypeConditionContext {
        val _localctx = TypeConditionContext(_ctx, state)
        enterRule(_localctx, 34, RULE_typeCondition)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 178
                typeName()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class VariableDefinitionsContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun variableDefinition(): List<VariableDefinitionContext> {
            return getRuleContexts(VariableDefinitionContext::class.java)
        }

        fun variableDefinition(i: Int): VariableDefinitionContext? {
            return getRuleContext(VariableDefinitionContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_variableDefinitions
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterVariableDefinitions(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitVariableDefinitions(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitVariableDefinitions(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun variableDefinitions(): VariableDefinitionsContext {
        val _localctx = VariableDefinitionsContext(_ctx, state)
        enterRule(_localctx, 36, RULE_variableDefinitions)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 180
                match(T__6)
                state = 181
                variableDefinition()
                state = 186
                _errHandler.sync(this)
                _la = _input.LA(1)
                while (_la == T__1) {
                    run {
                        run {
                            state = 182
                            match(T__1)
                            state = 183
                            variableDefinition()
                        }
                    }
                    state = 188
                    _errHandler.sync(this)
                    _la = _input.LA(1)
                }
                state = 189
                match(T__7)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class VariableDefinitionContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun variable(): VariableContext {
            return getRuleContext(VariableContext::class.java, 0)
        }

        fun type(): TypeContext {
            return getRuleContext(TypeContext::class.java, 0)
        }

        fun defaultValue(): DefaultValueContext? {
            return getRuleContext(DefaultValueContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_variableDefinition
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterVariableDefinition(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitVariableDefinition(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitVariableDefinition(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun variableDefinition(): VariableDefinitionContext {
        val _localctx = VariableDefinitionContext(_ctx, state)
        enterRule(_localctx, 38, RULE_variableDefinition)
        var _la: Int
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 191
                variable()
                state = 192
                match(T__5)
                state = 193
                type()
                state = 195
                _errHandler.sync(this)
                _la = _input.LA(1)
                if (_la == T__13) {
                    run {
                        state = 194
                        defaultValue()
                    }
                }

            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class VariableContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun NAME(): TerminalNode {
            return getToken(NAME, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_variable
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterVariable(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitVariable(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitVariable(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun variable(): VariableContext {
        val _localctx = VariableContext(_ctx, state)
        enterRule(_localctx, 40, RULE_variable)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 197
                match(T__12)
                state = 198
                match(NAME)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class DefaultValueContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun value(): ValueContext {
            return getRuleContext(ValueContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_defaultValue
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterDefaultValue(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitDefaultValue(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitDefaultValue(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun defaultValue(): DefaultValueContext {
        val _localctx = DefaultValueContext(_ctx, state)
        enterRule(_localctx, 42, RULE_defaultValue)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 200
                match(T__13)
                state = 201
                value()
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ValueOrVariableContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun value(): ValueContext? {
            return getRuleContext(ValueContext::class.java, 0)
        }

        fun variable(): VariableContext? {
            return getRuleContext(VariableContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_valueOrVariable
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterValueOrVariable(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitValueOrVariable(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitValueOrVariable(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun valueOrVariable(): ValueOrVariableContext {
        val _localctx = ValueOrVariableContext(_ctx, state)
        enterRule(_localctx, 44, RULE_valueOrVariable)
        try {
            state = 205
            _errHandler.sync(this)
            when (_input.LA(1)) {
                T__14, STRING, BOOLEAN, NUMBER -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 203
                        value()
                    }
                }
                T__12 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 204
                        variable()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    open class ValueContext : ParserRuleContext {
        constructor(parent: ParserRuleContext, invokingState: Int) : super(parent, invokingState) {}

        override fun getRuleIndex(): Int {
            return RULE_value
        }

        constructor() {}

        fun copyFrom(ctx: ValueContext) {
            super.copyFrom(ctx)
        }
    }

    class StringValueContext(ctx: ValueContext) : ValueContext() {
        fun STRING(): TerminalNode {
            return getToken(STRING, 0)
        }

        init {
            copyFrom(ctx)
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterStringValue(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitStringValue(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitStringValue(this)
            else
                visitor.visitChildren(this)
        }
    }

    class BooleanValueContext(ctx: ValueContext) : ValueContext() {
        fun BOOLEAN(): TerminalNode {
            return getToken(BOOLEAN, 0)
        }

        init {
            copyFrom(ctx)
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterBooleanValue(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitBooleanValue(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitBooleanValue(this)
            else
                visitor.visitChildren(this)
        }
    }

    class NumberValueContext(ctx: ValueContext) : ValueContext() {
        fun NUMBER(): TerminalNode {
            return getToken(NUMBER, 0)
        }

        init {
            copyFrom(ctx)
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterNumberValue(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitNumberValue(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitNumberValue(this)
            else
                visitor.visitChildren(this)
        }
    }

    class ArrayValueContext(ctx: ValueContext) : ValueContext() {
        fun array(): ArrayContext {
            return getRuleContext(ArrayContext::class.java, 0)
        }

        init {
            copyFrom(ctx)
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterArrayValue(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitArrayValue(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitArrayValue(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun value(): ValueContext {
        var _localctx = ValueContext(_ctx, state)
        enterRule(_localctx, 46, RULE_value)
        try {
            state = 211
            _errHandler.sync(this)
            when (_input.LA(1)) {
                STRING -> {
                    _localctx = StringValueContext(_localctx)
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 207
                        match(STRING)
                    }
                }
                NUMBER -> {
                    _localctx = NumberValueContext(_localctx)
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 208
                        match(NUMBER)
                    }
                }
                BOOLEAN -> {
                    _localctx = BooleanValueContext(_localctx)
                    enterOuterAlt(_localctx, 3)
                    run {
                        state = 209
                        match(BOOLEAN)
                    }
                }
                T__14 -> {
                    _localctx = ArrayValueContext(_localctx)
                    enterOuterAlt(_localctx, 4)
                    run {
                        state = 210
                        array()
                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class TypeContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun typeName(): TypeNameContext {
            return getRuleContext(TypeNameContext::class.java, 0)
        }

        fun nonNullType(): NonNullTypeContext? {
            return getRuleContext(NonNullTypeContext::class.java, 0)
        }

        fun listType(): ListTypeContext {
            return getRuleContext(ListTypeContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_type
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterType(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitType(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitType(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun type(): TypeContext {
        val _localctx = TypeContext(_ctx, state)
        enterRule(_localctx, 48, RULE_type)
        var _la: Int
        try {
            state = 221
            _errHandler.sync(this)
            when (_input.LA(1)) {
                NAME -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 213
                        typeName()
                        state = 215
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        if (_la == T__16) {
                            run {
                                state = 214
                                nonNullType()
                            }
                        }

                    }
                }
                T__14 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 217
                        listType()
                        state = 219
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        if (_la == T__16) {
                            run {
                                state = 218
                                nonNullType()
                            }
                        }

                    }
                }
                else -> throw NoViableAltException(this)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class TypeNameContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun NAME(): TerminalNode {
            return getToken(NAME, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_typeName
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterTypeName(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitTypeName(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitTypeName(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun typeName(): TypeNameContext {
        val _localctx = TypeNameContext(_ctx, state)
        enterRule(_localctx, 50, RULE_typeName)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 223
                match(NAME)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ListTypeContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun type(): TypeContext {
            return getRuleContext(TypeContext::class.java, 0)
        }

        override fun getRuleIndex(): Int {
            return RULE_listType
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterListType(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitListType(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitListType(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun listType(): ListTypeContext {
        val _localctx = ListTypeContext(_ctx, state)
        enterRule(_localctx, 52, RULE_listType)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 225
                match(T__14)
                state = 226
                type()
                state = 227
                match(T__15)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class NonNullTypeContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        override fun getRuleIndex(): Int {
            return RULE_nonNullType
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterNonNullType(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitNonNullType(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitNonNullType(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun nonNullType(): NonNullTypeContext {
        val _localctx = NonNullTypeContext(_ctx, state)
        enterRule(_localctx, 54, RULE_nonNullType)
        try {
            enterOuterAlt(_localctx, 1)
            run {
                state = 229
                match(T__16)
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    class ArrayContext(parent: ParserRuleContext, invokingState: Int) : ParserRuleContext(parent, invokingState) {
        fun value(): List<ValueContext>? {
            return getRuleContexts(ValueContext::class.java)
        }

        fun value(i: Int): ValueContext? {
            return getRuleContext(ValueContext::class.java, i)
        }

        override fun getRuleIndex(): Int {
            return RULE_array
        }

        override fun enterRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.enterArray(this)
        }

        override fun exitRule(listener: ParseTreeListener?) {
            (listener as? GraphQLListener)?.exitArray(this)
        }

        override fun <T> accept(visitor: ParseTreeVisitor<out T>): T {
            return if (visitor is GraphQLVisitor<*>)
                (visitor as GraphQLVisitor<out T>).visitArray(this)
            else
                visitor.visitChildren(this)
        }
    }

    @Throws(RecognitionException::class)
    fun array(): ArrayContext {
        val _localctx = ArrayContext(_ctx, state)
        enterRule(_localctx, 56, RULE_array)
        var _la: Int
        try {
            state = 244
            _errHandler.sync(this)
            when (interpreter.adaptivePredict(_input, 27, _ctx)) {
                1 -> {
                    enterOuterAlt(_localctx, 1)
                    run {
                        state = 231
                        match(T__14)
                        state = 232
                        value()
                        state = 237
                        _errHandler.sync(this)
                        _la = _input.LA(1)
                        while (_la == T__1) {
                            run {
                                run {
                                    state = 233
                                    match(T__1)
                                    state = 234
                                    value()
                                }
                            }
                            state = 239
                            _errHandler.sync(this)
                            _la = _input.LA(1)
                        }
                        state = 240
                        match(T__15)
                    }
                }
                2 -> {
                    enterOuterAlt(_localctx, 2)
                    run {
                        state = 242
                        match(T__14)
                        state = 243
                        match(T__15)
                    }
                }
            }
        } catch (re: RecognitionException) {
            _localctx.exception = re
            _errHandler.reportError(this, re)
            _errHandler.recover(this, re)
        } finally {
            exitRule()
        }
        return _localctx
    }

    companion object {
        init {
            RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION)
        }

        protected val _decisionToDFA: Array<DFA>
        protected val _sharedContextCache = PredictionContextCache()
        val T__0 = 1
        val T__1 = 2
        val T__2 = 3
        val T__3 = 4
        val T__4 = 5
        val T__5 = 6
        val T__6 = 7
        val T__7 = 8
        val T__8 = 9
        val T__9 = 10
        val T__10 = 11
        val T__11 = 12
        val T__12 = 13
        val T__13 = 14
        val T__14 = 15
        val T__15 = 16
        val T__16 = 17
        val NAME = 18
        val STRING = 19
        val BOOLEAN = 20
        val NUMBER = 21
        val WS = 22
        val RULE_document = 0
        val RULE_definition = 1
        val RULE_operationDefinition = 2
        val RULE_selectionSet = 3
        val RULE_operationType = 4
        val RULE_selection = 5
        val RULE_field = 6
        val RULE_fieldName = 7
        val RULE_alias = 8
        val RULE_arguments = 9
        val RULE_argument = 10
        val RULE_fragmentSpread = 11
        val RULE_inlineFragment = 12
        val RULE_fragmentDefinition = 13
        val RULE_fragmentName = 14
        val RULE_directives = 15
        val RULE_directive = 16
        val RULE_typeCondition = 17
        val RULE_variableDefinitions = 18
        val RULE_variableDefinition = 19
        val RULE_variable = 20
        val RULE_defaultValue = 21
        val RULE_valueOrVariable = 22
        val RULE_value = 23
        val RULE_type = 24
        val RULE_typeName = 25
        val RULE_listType = 26
        val RULE_nonNullType = 27
        val RULE_array = 28
        val ruleNames = arrayOf("document", "definition", "operationDefinition", "selectionSet", "operationType", "selection", "field", "fieldName", "alias", "arguments", "argument", "fragmentSpread", "inlineFragment", "fragmentDefinition", "fragmentName", "directives", "directive", "typeCondition", "variableDefinitions", "variableDefinition", "variable", "defaultValue", "valueOrVariable", "value", "type", "typeName", "listType", "nonNullType", "array")

        private val _LITERAL_NAMES = arrayOf<String?>(null, "'{'", "','", "'}'", "'query'", "'mutation'", "':'", "'('", "')'", "'...'", "'on'", "'fragment'", "'@'", "'\$'", "'='", "'['", "']'", "'!'")
        private val _SYMBOLIC_NAMES = arrayOf<String?>(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "NAME", "STRING", "BOOLEAN", "NUMBER", "WS")
        val VOCABULARY: Vocabulary = VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES)


        @Deprecated("Use {@link #VOCABULARY} instead.")
        val tokenNames: Array<String>

        init {
            tokenNames = IntRange(0, _SYMBOLIC_NAMES.size-1)
                    .map { i ->
                        (VOCABULARY.getLiteralName(i) ?: VOCABULARY.getSymbolicName(i)) ?: "<INVALID>"
                    }
                    .toTypedArray()
        }

        val _serializedATN = "\u0003\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\u0003\u0018\u00f9\u0004\u0002\t\u0002" +
                "\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b" +
                "\t\u000b\u0004\u000c\t\u000c\u0004\r\t\r\u0004\u000e\t\u000e\u0004\u000f\t\u000f\u0004\u0010\t\u0010\u0004\u0011\t\u0011\u0004\u0012\t\u0012" +
                "\u0004\u0013\t\u0013\u0004\u0014\t\u0014\u0004\u0015\t\u0015\u0004\u0016\t\u0016\u0004\u0017\t\u0017\u0004\u0018\t\u0018\u0004\u0019\t\u0019" +
                "\u0004\u001a\t\u001a\u0004\u001b\t\u001b\u0004\u001c\t\u001c\u0004\u001d\t\u001d\u0004\u001e\t\u001e\u0003\u0002\u0006\u0002>\n\u0002\r\u0002\u000e" +
                "\u0002?\u0003\u0003\u0003\u0003\u0005\u0003D\n\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0005\u0004I\n\u0004\u0003\u0004\u0005\u0004L\n\u0004\u0003\u0004\u0005\u0004O\n\u0004\u0003" +
                "\u0004\u0003\u0004\u0005\u0004S\n\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0005\u0005X\n\u0005\u0003\u0005\u0007\u0005[\n\u0005\u000c\u0005\u000e\u0005^\u000b\u0005\u0003\u0005\u0003" +
                "\u0005\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0005\u0007g\n\u0007\u0003\b\u0003\b\u0005\bk\n\b\u0003\b\u0005\bn\n\b\u0003\b\u0005\b" +
                "q\n\b\u0003\t\u0003\t\u0005\tu\n\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0007\u000b\u007f\n" +
                "\u000b\u000c\u000b\u000e\u000b\u0082\u000b\u000b\u0003\u000b\u0003\u000b\u0003\u000c\u0003\u000c\u0003\u000c\u0003\u000c\u0003\r\u0003\r\u0003\r\u0005\r\u008d" +
                "\n\r\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0005\u000e\u0093\n\u000e\u0003\u000e\u0003\u000e\u0003\u000f\u0003\u000f\u0003\u000f\u0003\u000f" +
                "\u0003\u000f\u0005\u000f\u009c\n\u000f\u0003\u000f\u0003\u000f\u0003\u0010\u0003\u0010\u0003\u0011\u0006\u0011\u00a3\n\u0011\r\u0011\u000e" +
                "\u0011\u00a4\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012\u0003\u0012" +
                "\u0005\u0012\u00b3\n\u0012\u0003\u0013\u0003\u0013\u0003\u0014\u0003\u0014\u0003\u0014\u0003\u0014\u0007\u0014\u00bb\n\u0014\u000c\u0014\u000e" +
                "\u0014\u00be\u000b\u0014\u0003\u0014\u0003\u0014\u0003\u0015\u0003\u0015\u0003\u0015\u0003\u0015\u0005\u0015\u00c6\n\u0015\u0003\u0016\u0003\u0016" +
                "\u0003\u0016\u0003\u0017\u0003\u0017\u0003\u0017\u0003\u0018\u0003\u0018\u0005\u0018\u00d0\n\u0018\u0003\u0019\u0003\u0019\u0003\u0019\u0003\u0019\u0005\u0019" +
                "\u00d6\n\u0019\u0003\u001a\u0003\u001a\u0005\u001a\u00da\n\u001a\u0003\u001a\u0003\u001a\u0005\u001a\u00de\n\u001a\u0005\u001a\u00e0" +
                "\n\u001a\u0003\u001b\u0003\u001b\u0003\u001c\u0003\u001c\u0003\u001c\u0003\u001c\u0003\u001d\u0003\u001d\u0003\u001e\u0003\u001e\u0003\u001e\u0003\u001e\u0007\u001e" +
                "\u00ee\n\u001e\u000c\u001e\u000e\u001e\u00f1\u000b\u001e\u0003\u001e\u0003\u001e\u0003\u001e\u0003\u001e\u0005\u001e\u00f7\n\u001e" +
                "\u0003\u001e\u0002\u0002\u001f\u0002\u0004\u0006\b\n\u000c\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"\$&(*,.\u0030\u0032\u0034" +
                "\u00368:\u0002\u0003\u0003\u0002\u0006\u0007\u0002\u00fb\u0002=\u0003\u0002\u0002\u0002\u0004C\u0003\u0002\u0002\u0002\u0006R\u0003\u0002\u0002\u0002\bT\u0003\u0002\u0002\u0002" +
                "\na\u0003\u0002\u0002\u0002\u000cf\u0003\u0002\u0002\u0002\u000eh\u0003\u0002\u0002\u0002\u0010t\u0003\u0002\u0002\u0002\u0012v\u0003\u0002\u0002\u0002\u0014z\u0003\u0002\u0002\u0002" +
                "\u0016\u0085\u0003\u0002\u0002\u0002\u0018\u0089\u0003\u0002\u0002\u0002\u001a\u008e\u0003\u0002\u0002\u0002\u001c\u0096\u0003\u0002\u0002\u0002\u001e" +
                "\u009f\u0003\u0002\u0002\u0002 \u00a2\u0003\u0002\u0002\u0002\"\u00b2\u0003\u0002\u0002\u0002\$\u00b4\u0003\u0002\u0002\u0002&\u00b6\u0003" +
                "\u0002\u0002\u0002(\u00c1\u0003\u0002\u0002\u0002*\u00c7\u0003\u0002\u0002\u0002,\u00ca\u0003\u0002\u0002\u0002.\u00cf\u0003\u0002\u0002\u0002\u0030" +
                "\u00d5\u0003\u0002\u0002\u0002\u0032\u00df\u0003\u0002\u0002\u0002\u0034\u00e1\u0003\u0002\u0002\u0002\u0036\u00e3\u0003\u0002\u0002\u00028\u00e7" +
                "\u0003\u0002\u0002\u0002:\u00f6\u0003\u0002\u0002\u0002<>\u0005\u0004\u0003\u0002=<\u0003\u0002\u0002\u0002>?\u0003\u0002\u0002\u0002?=\u0003\u0002\u0002\u0002?@\u0003\u0002" +
                "\u0002\u0002@\u0003\u0003\u0002\u0002\u0002AD\u0005\u0006\u0004\u0002BD\u0005\u001c\u000f\u0002CA\u0003\u0002\u0002\u0002CB\u0003\u0002\u0002\u0002D\u0005\u0003\u0002\u0002\u0002E" +
                "S\u0005\b\u0005\u0002FH\u0005\n\u0006\u0002GI\u0007\u0014\u0002\u0002HG\u0003\u0002\u0002\u0002HI\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0005&\u0014\u0002" +
                "KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LN\u0003\u0002\u0002\u0002MO\u0005 \u0011\u0002NM\u0003\u0002\u0002\u0002NO\u0003\u0002\u0002\u0002OP\u0003\u0002\u0002\u0002" +
                "PQ\u0005\b\u0005\u0002QS\u0003\u0002\u0002\u0002RE\u0003\u0002\u0002\u0002RF\u0003\u0002\u0002\u0002S\u0007\u0003\u0002\u0002\u0002TU\u0007\u0003\u0002\u0002U\\\u0005\u000c\u0007" +
                "\u0002VX\u0007\u0004\u0002\u0002WV\u0003\u0002\u0002\u0002WX\u0003\u0002\u0002\u0002XY\u0003\u0002\u0002\u0002Y[\u0005\u000c\u0007\u0002ZW\u0003\u0002\u0002\u0002[^\u0003\u0002\u0002" +
                "\u0002\\Z\u0003\u0002\u0002\u0002\\]\u0003\u0002\u0002\u0002]_\u0003\u0002\u0002\u0002^\\\u0003\u0002\u0002\u0002_`\u0007\u0005\u0002\u0002`\t\u0003\u0002\u0002\u0002ab\t" +
                "\u0002\u0002\u0002b\u000b\u0003\u0002\u0002\u0002cg\u0005\u000e\b\u0002dg\u0005\u0018\r\u0002eg\u0005\u001a\u000e\u0002fc\u0003\u0002\u0002\u0002fd\u0003\u0002\u0002" +
                "\u0002fe\u0003\u0002\u0002\u0002g\r\u0003\u0002\u0002\u0002hj\u0005\u0010\t\u0002ik\u0005\u0014\u000b\u0002ji\u0003\u0002\u0002\u0002jk\u0003\u0002\u0002\u0002km\u0003" +
                "\u0002\u0002\u0002ln\u0005 \u0011\u0002ml\u0003\u0002\u0002\u0002mn\u0003\u0002\u0002\u0002np\u0003\u0002\u0002\u0002oq\u0005\b\u0005\u0002po\u0003\u0002\u0002\u0002pq\u0003" +
                "\u0002\u0002\u0002q\u000f\u0003\u0002\u0002\u0002ru\u0005\u0012\n\u0002su\u0007\u0014\u0002\u0002tr\u0003\u0002\u0002\u0002ts\u0003\u0002\u0002\u0002u\u0011\u0003\u0002\u0002" +
                "\u0002vw\u0007\u0014\u0002\u0002wx\u0007\b\u0002\u0002xy\u0007\u0014\u0002\u0002y\u0013\u0003\u0002\u0002\u0002z{\u0007\t\u0002\u0002{\u0080\u0005\u0016\u000c" +
                "\u0002|}\u0007\u0004\u0002\u0002}\u007f\u0005\u0016\u000c\u0002~|\u0003\u0002\u0002\u0002\u007f\u0082\u0003\u0002\u0002\u0002\u0080~\u0003\u0002\u0002\u0002" +
                "\u0080\u0081\u0003\u0002\u0002\u0002\u0081\u0083\u0003\u0002\u0002\u0002\u0082\u0080\u0003\u0002\u0002\u0002\u0083\u0084" +
                "\u0007\n\u0002\u0002\u0084\u0015\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0014\u0002\u0002\u0086\u0087\u0007\b\u0002\u0002\u0087" +
                "\u0088\u0005.\u0018\u0002\u0088\u0017\u0003\u0002\u0002\u0002\u0089\u008a\u0007\u000b\u0002\u0002\u008a\u008c\u0005\u001e" +
                "\u0010\u0002\u008b\u008d\u0005 \u0011\u0002\u008c\u008b\u0003\u0002\u0002\u0002\u008c\u008d\u0003\u0002\u0002\u0002\u008d" +
                "\u0019\u0003\u0002\u0002\u0002\u008e\u008f\u0007\u000b\u0002\u0002\u008f\u0090\u0007\u000c\u0002\u0002\u0090\u0092\u0005\$\u0013" +
                "\u0002\u0091\u0093\u0005 \u0011\u0002\u0092\u0091\u0003\u0002\u0002\u0002\u0092\u0093\u0003\u0002\u0002\u0002\u0093\u0094" +
                "\u0003\u0002\u0002\u0002\u0094\u0095\u0005\b\u0005\u0002\u0095\u001b\u0003\u0002\u0002\u0002\u0096\u0097\u0007\r\u0002\u0002\u0097" +
                "\u0098\u0005\u001e\u0010\u0002\u0098\u0099\u0007\u000c\u0002\u0002\u0099\u009b\u0005\$\u0013\u0002\u009a\u009c\u0005" +
                " \u0011\u0002\u009b\u009a\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u009d\u0003\u0002\u0002\u0002\u009d" +
                "\u009e\u0005\b\u0005\u0002\u009e\u001d\u0003\u0002\u0002\u0002\u009f\u00a0\u0007\u0014\u0002\u0002\u00a0\u001f\u0003\u0002\u0002\u0002" +
                "\u00a1\u00a3\u0005\"\u0012\u0002\u00a2\u00a1\u0003\u0002\u0002\u0002\u00a3\u00a4\u0003\u0002\u0002\u0002\u00a4\u00a2" +
                "\u0003\u0002\u0002\u0002\u00a4\u00a5\u0003\u0002\u0002\u0002\u00a5!\u0003\u0002\u0002\u0002\u00a6\u00a7\u0007\u000e\u0002\u0002\u00a7" +
                "\u00a8\u0007\u0014\u0002\u0002\u00a8\u00a9\u0007\b\u0002\u0002\u00a9\u00b3\u0005.\u0018\u0002\u00aa\u00ab\u0007" +
                "\u000e\u0002\u0002\u00ab\u00b3\u0007\u0014\u0002\u0002\u00ac\u00ad\u0007\u000e\u0002\u0002\u00ad\u00ae\u0007\u0014\u0002\u0002" +
                "\u00ae\u00af\u0007\t\u0002\u0002\u00af\u00b0\u0005\u0016\u000c\u0002\u00b0\u00b1\u0007\n\u0002\u0002\u00b1\u00b3" +
                "\u0003\u0002\u0002\u0002\u00b2\u00a6\u0003\u0002\u0002\u0002\u00b2\u00aa\u0003\u0002\u0002\u0002\u00b2\u00ac\u0003\u0002\u0002\u0002\u00b3" +
                "#\u0003\u0002\u0002\u0002\u00b4\u00b5\u0005\u0034\u001b\u0002\u00b5%\u0003\u0002\u0002\u0002\u00b6\u00b7\u0007\t\u0002\u0002\u00b7" +
                "\u00bc\u0005(\u0015\u0002\u00b8\u00b9\u0007\u0004\u0002\u0002\u00b9\u00bb\u0005(\u0015\u0002\u00ba\u00b8\u0003\u0002" +
                "\u0002\u0002\u00bb\u00be\u0003\u0002\u0002\u0002\u00bc\u00ba\u0003\u0002\u0002\u0002\u00bc\u00bd\u0003\u0002\u0002\u0002\u00bd" +
                "\u00bf\u0003\u0002\u0002\u0002\u00be\u00bc\u0003\u0002\u0002\u0002\u00bf\u00c0\u0007\n\u0002\u0002\u00c0\'\u0003\u0002\u0002\u0002" +
                "\u00c1\u00c2\u0005*\u0016\u0002\u00c2\u00c3\u0007\b\u0002\u0002\u00c3\u00c5\u0005\u0032\u001a\u0002\u00c4\u00c6" +
                "\u0005,\u0017\u0002\u00c5\u00c4\u0003\u0002\u0002\u0002\u00c5\u00c6\u0003\u0002\u0002\u0002\u00c6)\u0003\u0002\u0002\u0002\u00c7" +
                "\u00c8\u0007\u000f\u0002\u0002\u00c8\u00c9\u0007\u0014\u0002\u0002\u00c9+\u0003\u0002\u0002\u0002\u00ca\u00cb\u0007\u0010\u0002" +
                "\u0002\u00cb\u00cc\u0005\u0030\u0019\u0002\u00cc-\u0003\u0002\u0002\u0002\u00cd\u00d0\u0005\u0030\u0019\u0002\u00ce\u00d0" +
                "\u0005*\u0016\u0002\u00cf\u00cd\u0003\u0002\u0002\u0002\u00cf\u00ce\u0003\u0002\u0002\u0002\u00d0/\u0003\u0002\u0002\u0002\u00d1" +
                "\u00d6\u0007\u0015\u0002\u0002\u00d2\u00d6\u0007\u0017\u0002\u0002\u00d3\u00d6\u0007\u0016\u0002\u0002\u00d4\u00d6" +
                "\u0005:\u001e\u0002\u00d5\u00d1\u0003\u0002\u0002\u0002\u00d5\u00d2\u0003\u0002\u0002\u0002\u00d5\u00d3\u0003\u0002\u0002\u0002\u00d5" +
                "\u00d4\u0003\u0002\u0002\u0002\u00d6\u0031\u0003\u0002\u0002\u0002\u00d7\u00d9\u0005\u0034\u001b\u0002\u00d8\u00da\u00058\u001d" +
                "\u0002\u00d9\u00d8\u0003\u0002\u0002\u0002\u00d9\u00da\u0003\u0002\u0002\u0002\u00da\u00e0\u0003\u0002\u0002\u0002\u00db\u00dd" +
                "\u0005\u0036\u001c\u0002\u00dc\u00de\u00058\u001d\u0002\u00dd\u00dc\u0003\u0002\u0002\u0002\u00dd\u00de\u0003\u0002\u0002\u0002" +
                "\u00de\u00e0\u0003\u0002\u0002\u0002\u00df\u00d7\u0003\u0002\u0002\u0002\u00df\u00db\u0003\u0002\u0002\u0002\u00e0\u0033" +
                "\u0003\u0002\u0002\u0002\u00e1\u00e2\u0007\u0014\u0002\u0002\u00e2\u0035\u0003\u0002\u0002\u0002\u00e3\u00e4\u0007\u0011\u0002\u0002\u00e4" +
                "\u00e5\u0005\u0032\u001a\u0002\u00e5\u00e6\u0007\u0012\u0002\u0002\u00e6\u0037\u0003\u0002\u0002\u0002\u00e7\u00e8\u0007\u0013" +
                "\u0002\u0002\u00e89\u0003\u0002\u0002\u0002\u00e9\u00ea\u0007\u0011\u0002\u0002\u00ea\u00ef\u0005\u0030\u0019\u0002\u00eb\u00ec" +
                "\u0007\u0004\u0002\u0002\u00ec\u00ee\u0005\u0030\u0019\u0002\u00ed\u00eb\u0003\u0002\u0002\u0002\u00ee\u00f1\u0003\u0002\u0002\u0002" +
                "\u00ef\u00ed\u0003\u0002\u0002\u0002\u00ef\u00f0\u0003\u0002\u0002\u0002\u00f0\u00f2\u0003\u0002\u0002\u0002\u00f1\u00ef" +
                "\u0003\u0002\u0002\u0002\u00f2\u00f3\u0007\u0012\u0002\u0002\u00f3\u00f7\u0003\u0002\u0002\u0002\u00f4\u00f5\u0007\u0011\u0002\u0002" +
                "\u00f5\u00f7\u0007\u0012\u0002\u0002\u00f6\u00e9\u0003\u0002\u0002\u0002\u00f6\u00f4\u0003\u0002\u0002\u0002\u00f7;\u0003" +
                "\u0002\u0002\u0002\u001e?CHKNRW\\fjmpt\u0080\u008c\u0092\u009b\u00a4\u00b2\u00bc\u00c5" +
                "\u00cf\u00d5\u00d9\u00dd\u00df\u00ef\u00f6"
        val _ATN = ATNDeserializer().deserialize(_serializedATN.toCharArray())

        init {
            _decisionToDFA = IntRange(0, _ATN.numberOfDecisions-1)
                    .map { i -> DFA(_ATN.getDecisionState(i), i) }
                    .toTypedArray()
        }
    }
}