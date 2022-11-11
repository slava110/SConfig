package com.slava_110.sconfig

import com.slava_110.sconfig.SConfigParser.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class STransformerConfig: SConfigBaseVisitor<JsonElement>() {

    override fun visitConfig(ctx: ConfigContext): JsonElement {
        return visit(ctx.list())
    }

    override fun visitObject(ctx: ObjectContext): JsonElement {
        return JsonObject(
            ctx.children
                .asSequence()
                .filterIsInstance<KeyPairContext>()
                .associate {
                    it.STRINGLIKE().text to it.expression().accept(this)
                }
        )
    }

    override fun visitList(ctx: ListContext): JsonElement {
        return JsonArray(
            ctx.children
                .asSequence()
                .filterIsInstance<ExpressionContext>()
                .map { it.accept(this) }
                .toList()
        )
    }

    override fun visitPair(ctx: PairContext): JsonElement =
        JsonObject(mapOf(
            "key" to ctx.expression(0).accept(this),
            "value" to ctx.expression(1).accept(this)
        ))

    override fun visitAtom(ctx: AtomContext): JsonElement =
        ctx.NUMBER()?.let {
            JsonPrimitive(it.text.toInt())
        } ?: JsonPrimitive(ctx.STRINGLIKE().text.removeSurrounding("\"").replace("\\", ""))
}