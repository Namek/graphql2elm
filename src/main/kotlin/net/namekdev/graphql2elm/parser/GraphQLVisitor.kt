// Generated from C:/Users/Namek/.babun/cygwin/home/Namek/graphql2elm/src\GraphQL.g4 by ANTLR 4.7
package net.namekdev.graphql2elm.parser

import org.antlr.v4.runtime.tree.ParseTreeVisitor

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by [GraphQLParser].
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * operations with no return type.
</T> */
interface GraphQLVisitor<T> : ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by [GraphQLParser.document].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitDocument(ctx: GraphQLParser.DocumentContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.definition].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitDefinition(ctx: GraphQLParser.DefinitionContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.operationDefinition].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitOperationDefinition(ctx: GraphQLParser.OperationDefinitionContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.selectionSet].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitSelectionSet(ctx: GraphQLParser.SelectionSetContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.operationType].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitOperationType(ctx: GraphQLParser.OperationTypeContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.selection].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitSelection(ctx: GraphQLParser.SelectionContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.field].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitField(ctx: GraphQLParser.FieldContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.fieldName].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitFieldName(ctx: GraphQLParser.FieldNameContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.alias].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitAlias(ctx: GraphQLParser.AliasContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.arguments].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArguments(ctx: GraphQLParser.ArgumentsContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.argument].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArgument(ctx: GraphQLParser.ArgumentContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.fragmentSpread].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitFragmentSpread(ctx: GraphQLParser.FragmentSpreadContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.inlineFragment].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitInlineFragment(ctx: GraphQLParser.InlineFragmentContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.fragmentDefinition].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitFragmentDefinition(ctx: GraphQLParser.FragmentDefinitionContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.fragmentName].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitFragmentName(ctx: GraphQLParser.FragmentNameContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.directives].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitDirectives(ctx: GraphQLParser.DirectivesContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.directive].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitDirective(ctx: GraphQLParser.DirectiveContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.typeCondition].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitTypeCondition(ctx: GraphQLParser.TypeConditionContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.variableDefinitions].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitVariableDefinitions(ctx: GraphQLParser.VariableDefinitionsContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.variableDefinition].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitVariableDefinition(ctx: GraphQLParser.VariableDefinitionContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.variable].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitVariable(ctx: GraphQLParser.VariableContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.defaultValue].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitDefaultValue(ctx: GraphQLParser.DefaultValueContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.valueOrVariable].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitValueOrVariable(ctx: GraphQLParser.ValueOrVariableContext): T

    /**
     * Visit a parse tree produced by the `stringValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitStringValue(ctx: GraphQLParser.StringValueContext): T

    /**
     * Visit a parse tree produced by the `numberValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitNumberValue(ctx: GraphQLParser.NumberValueContext): T

    /**
     * Visit a parse tree produced by the `booleanValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitBooleanValue(ctx: GraphQLParser.BooleanValueContext): T

    /**
     * Visit a parse tree produced by the `arrayValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArrayValue(ctx: GraphQLParser.ArrayValueContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.type].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitType(ctx: GraphQLParser.TypeContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.typeName].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitTypeName(ctx: GraphQLParser.TypeNameContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.listType].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitListType(ctx: GraphQLParser.ListTypeContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.nonNullType].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitNonNullType(ctx: GraphQLParser.NonNullTypeContext): T

    /**
     * Visit a parse tree produced by [GraphQLParser.array].
     * @param ctx the parse tree
     * @return the visitor result
     */
    fun visitArray(ctx: GraphQLParser.ArrayContext): T
}