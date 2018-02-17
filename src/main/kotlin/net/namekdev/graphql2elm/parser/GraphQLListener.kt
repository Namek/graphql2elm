// Generated from C:/Users/Namek/.babun/cygwin/home/Namek/graphql2elm/src\GraphQL.g4 by ANTLR 4.7
package net.namekdev.graphql2elm.parser

import org.antlr.v4.runtime.tree.ParseTreeListener

/**
 * This interface defines a complete listener for a parse tree produced by
 * [GraphQLParser].
 */
interface GraphQLListener : ParseTreeListener {
    /**
     * Enter a parse tree produced by [GraphQLParser.document].
     * @param ctx the parse tree
     */
    fun enterDocument(ctx: GraphQLParser.DocumentContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.document].
     * @param ctx the parse tree
     */
    fun exitDocument(ctx: GraphQLParser.DocumentContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.definition].
     * @param ctx the parse tree
     */
    fun enterDefinition(ctx: GraphQLParser.DefinitionContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.definition].
     * @param ctx the parse tree
     */
    fun exitDefinition(ctx: GraphQLParser.DefinitionContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.operationDefinition].
     * @param ctx the parse tree
     */
    fun enterOperationDefinition(ctx: GraphQLParser.OperationDefinitionContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.operationDefinition].
     * @param ctx the parse tree
     */
    fun exitOperationDefinition(ctx: GraphQLParser.OperationDefinitionContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.selectionSet].
     * @param ctx the parse tree
     */
    fun enterSelectionSet(ctx: GraphQLParser.SelectionSetContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.selectionSet].
     * @param ctx the parse tree
     */
    fun exitSelectionSet(ctx: GraphQLParser.SelectionSetContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.operationType].
     * @param ctx the parse tree
     */
    fun enterOperationType(ctx: GraphQLParser.OperationTypeContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.operationType].
     * @param ctx the parse tree
     */
    fun exitOperationType(ctx: GraphQLParser.OperationTypeContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.selection].
     * @param ctx the parse tree
     */
    fun enterSelection(ctx: GraphQLParser.SelectionContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.selection].
     * @param ctx the parse tree
     */
    fun exitSelection(ctx: GraphQLParser.SelectionContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.field].
     * @param ctx the parse tree
     */
    fun enterField(ctx: GraphQLParser.FieldContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.field].
     * @param ctx the parse tree
     */
    fun exitField(ctx: GraphQLParser.FieldContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.fieldName].
     * @param ctx the parse tree
     */
    fun enterFieldName(ctx: GraphQLParser.FieldNameContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.fieldName].
     * @param ctx the parse tree
     */
    fun exitFieldName(ctx: GraphQLParser.FieldNameContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.alias].
     * @param ctx the parse tree
     */
    fun enterAlias(ctx: GraphQLParser.AliasContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.alias].
     * @param ctx the parse tree
     */
    fun exitAlias(ctx: GraphQLParser.AliasContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.arguments].
     * @param ctx the parse tree
     */
    fun enterArguments(ctx: GraphQLParser.ArgumentsContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.arguments].
     * @param ctx the parse tree
     */
    fun exitArguments(ctx: GraphQLParser.ArgumentsContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.argument].
     * @param ctx the parse tree
     */
    fun enterArgument(ctx: GraphQLParser.ArgumentContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.argument].
     * @param ctx the parse tree
     */
    fun exitArgument(ctx: GraphQLParser.ArgumentContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.fragmentSpread].
     * @param ctx the parse tree
     */
    fun enterFragmentSpread(ctx: GraphQLParser.FragmentSpreadContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.fragmentSpread].
     * @param ctx the parse tree
     */
    fun exitFragmentSpread(ctx: GraphQLParser.FragmentSpreadContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.inlineFragment].
     * @param ctx the parse tree
     */
    fun enterInlineFragment(ctx: GraphQLParser.InlineFragmentContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.inlineFragment].
     * @param ctx the parse tree
     */
    fun exitInlineFragment(ctx: GraphQLParser.InlineFragmentContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.fragmentDefinition].
     * @param ctx the parse tree
     */
    fun enterFragmentDefinition(ctx: GraphQLParser.FragmentDefinitionContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.fragmentDefinition].
     * @param ctx the parse tree
     */
    fun exitFragmentDefinition(ctx: GraphQLParser.FragmentDefinitionContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.fragmentName].
     * @param ctx the parse tree
     */
    fun enterFragmentName(ctx: GraphQLParser.FragmentNameContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.fragmentName].
     * @param ctx the parse tree
     */
    fun exitFragmentName(ctx: GraphQLParser.FragmentNameContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.directives].
     * @param ctx the parse tree
     */
    fun enterDirectives(ctx: GraphQLParser.DirectivesContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.directives].
     * @param ctx the parse tree
     */
    fun exitDirectives(ctx: GraphQLParser.DirectivesContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.directive].
     * @param ctx the parse tree
     */
    fun enterDirective(ctx: GraphQLParser.DirectiveContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.directive].
     * @param ctx the parse tree
     */
    fun exitDirective(ctx: GraphQLParser.DirectiveContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.typeCondition].
     * @param ctx the parse tree
     */
    fun enterTypeCondition(ctx: GraphQLParser.TypeConditionContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.typeCondition].
     * @param ctx the parse tree
     */
    fun exitTypeCondition(ctx: GraphQLParser.TypeConditionContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.variableDefinitions].
     * @param ctx the parse tree
     */
    fun enterVariableDefinitions(ctx: GraphQLParser.VariableDefinitionsContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.variableDefinitions].
     * @param ctx the parse tree
     */
    fun exitVariableDefinitions(ctx: GraphQLParser.VariableDefinitionsContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.variableDefinition].
     * @param ctx the parse tree
     */
    fun enterVariableDefinition(ctx: GraphQLParser.VariableDefinitionContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.variableDefinition].
     * @param ctx the parse tree
     */
    fun exitVariableDefinition(ctx: GraphQLParser.VariableDefinitionContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.variable].
     * @param ctx the parse tree
     */
    fun enterVariable(ctx: GraphQLParser.VariableContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.variable].
     * @param ctx the parse tree
     */
    fun exitVariable(ctx: GraphQLParser.VariableContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.defaultValue].
     * @param ctx the parse tree
     */
    fun enterDefaultValue(ctx: GraphQLParser.DefaultValueContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.defaultValue].
     * @param ctx the parse tree
     */
    fun exitDefaultValue(ctx: GraphQLParser.DefaultValueContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.valueOrVariable].
     * @param ctx the parse tree
     */
    fun enterValueOrVariable(ctx: GraphQLParser.ValueOrVariableContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.valueOrVariable].
     * @param ctx the parse tree
     */
    fun exitValueOrVariable(ctx: GraphQLParser.ValueOrVariableContext)

    /**
     * Enter a parse tree produced by the `stringValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun enterStringValue(ctx: GraphQLParser.StringValueContext)

    /**
     * Exit a parse tree produced by the `stringValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun exitStringValue(ctx: GraphQLParser.StringValueContext)

    /**
     * Enter a parse tree produced by the `numberValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun enterNumberValue(ctx: GraphQLParser.NumberValueContext)

    /**
     * Exit a parse tree produced by the `numberValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun exitNumberValue(ctx: GraphQLParser.NumberValueContext)

    /**
     * Enter a parse tree produced by the `booleanValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun enterBooleanValue(ctx: GraphQLParser.BooleanValueContext)

    /**
     * Exit a parse tree produced by the `booleanValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun exitBooleanValue(ctx: GraphQLParser.BooleanValueContext)

    /**
     * Enter a parse tree produced by the `arrayValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun enterArrayValue(ctx: GraphQLParser.ArrayValueContext)

    /**
     * Exit a parse tree produced by the `arrayValue`
     * labeled alternative in [GraphQLParser.value].
     * @param ctx the parse tree
     */
    fun exitArrayValue(ctx: GraphQLParser.ArrayValueContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.type].
     * @param ctx the parse tree
     */
    fun enterType(ctx: GraphQLParser.TypeContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.type].
     * @param ctx the parse tree
     */
    fun exitType(ctx: GraphQLParser.TypeContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.typeName].
     * @param ctx the parse tree
     */
    fun enterTypeName(ctx: GraphQLParser.TypeNameContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.typeName].
     * @param ctx the parse tree
     */
    fun exitTypeName(ctx: GraphQLParser.TypeNameContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.listType].
     * @param ctx the parse tree
     */
    fun enterListType(ctx: GraphQLParser.ListTypeContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.listType].
     * @param ctx the parse tree
     */
    fun exitListType(ctx: GraphQLParser.ListTypeContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.nonNullType].
     * @param ctx the parse tree
     */
    fun enterNonNullType(ctx: GraphQLParser.NonNullTypeContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.nonNullType].
     * @param ctx the parse tree
     */
    fun exitNonNullType(ctx: GraphQLParser.NonNullTypeContext)

    /**
     * Enter a parse tree produced by [GraphQLParser.array].
     * @param ctx the parse tree
     */
    fun enterArray(ctx: GraphQLParser.ArrayContext)

    /**
     * Exit a parse tree produced by [GraphQLParser.array].
     * @param ctx the parse tree
     */
    fun exitArray(ctx: GraphQLParser.ArrayContext)
}