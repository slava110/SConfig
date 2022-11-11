package com.slava_110.sconfig

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.antlr.v4.runtime.tree.TerminalNode

class STransformer: SContext() {

}

class SFunctionContext(parent: SContext, private val parameters: Map<String, JsonElement>): SContext(parent) {



    override fun findVariable(name: String): JsonElement? =
        parameters[name] ?: super.findVariable(name)
}

open class SContext(val parent: SContext? = null): SConfigBaseVisitor<JsonElement>() {
    val variables = mutableMapOf<String, JsonElement>()
    val functions = mutableMapOf<String, SFunction>()

    override fun visitConfig(ctx: SConfigParser.ConfigContext): JsonElement =
        ctx.`object`()?.accept(this) ?: ctx.list().accept(this)

    override fun visitFunction(ctx: SConfigParser.FunctionContext): JsonElement? {
        val funcName = ctx.STRINGLIKE().textWithoutBrackets()
        parent?.functions?.set(funcName,
            SFunction(
                params = ctx.functionParams().STRINGLIKE().map { it.textWithoutBrackets() },
                expression = ctx.expression()
            )
        )
        return null
    }

    override fun visitFunctionCall(ctx: SConfigParser.FunctionCallContext): JsonElement {
        val funcName = ctx.STRINGLIKE().textWithoutBrackets()
        return findFunction(funcName)?.invoke(this, ctx.expression().map { it.accept(this) }) ?: throw SException("Function $funcName not found!")
    }

    override fun visitVariable(ctx: SConfigParser.VariableContext): JsonElement? {
        val varName = ctx.STRINGLIKE().textWithoutBrackets()
        parent?.variables?.set(varName, ctx.expression().accept(parent))
        return null
    }

    override fun visitPlaceholder(ctx: SConfigParser.PlaceholderContext): JsonElement {
        val varName = ctx.STRINGLIKE().textWithoutBrackets()
        return findVariable(varName) ?: throw SException("Variable not found: `$varName`")
    }

    override fun visitObject(ctx: SConfigParser.ObjectContext): JsonElement {
        return JsonObject(
            ctx.children
                .asSequence()
                .filterIsInstance<SConfigParser.KeyPairContext>()
                .mapNotNull { keyPair ->
                    keyPair.expression().accept(childContext())?.let {
                        keyPair.STRINGLIKE().textWithoutBrackets() to it
                    }
                }
                .associate { it }
        )
    }

    override fun visitList(ctx: SConfigParser.ListContext): JsonElement {
        return JsonArray(
            ctx.children
                .asSequence()
                .filterIsInstance<SConfigParser.ExpressionContext>()
                .mapNotNull { it.accept(childContext()) }
                .toList()
        )
    }

    override fun visitPair(ctx: SConfigParser.PairContext): JsonElement =
        JsonObject(mapOf(
            "key" to ctx.expression(0).accept(childContext()),
            "value" to ctx.expression(1).accept(childContext())
        ))

    override fun visitAtom(ctx: SConfigParser.AtomContext): JsonElement =
        ctx.NUMBER()?.let {
            JsonPrimitive(it.text.toInt())
        } ?: JsonPrimitive(ctx.STRINGLIKE().textWithoutBrackets())

    protected open fun findVariable(name: String): JsonElement? =
        variables[name] ?: parent?.findVariable(name)

    protected open fun findFunction(name: String): SFunction? =
        functions[name] ?: parent?.findFunction(name)

    private fun childContext(): SContext =
        SContext(this)

    private fun TerminalNode.textWithoutBrackets() =
        text.removeSurrounding("\"").replace("\\", "")
}

class SFunction(val params: List<String>, val expression: SConfigParser.ExpressionContext) {

    fun invoke(ctx: SContext, arguments: List<JsonElement>): JsonElement =
        expression.accept(SFunctionContext(ctx, params.withIndex().associate { (i, name) -> name to arguments[i] }))
}