package com.slava_110.sconfig

import kotlinx.serialization.json.JsonElement
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.Reader
import java.nio.file.Path
import kotlin.io.path.bufferedReader

object SConfig {

    fun parse(raw: String): JsonElement =
        parse(CharStreams.fromString(raw))

    fun parse(path: Path): JsonElement =
        path.bufferedReader().use { parse(it) }

    fun parse(reader: Reader): JsonElement =
        parse(CharStreams.fromReader(reader))

    private fun parse(chS: CharStream): JsonElement {
        val lexer = SConfigLexer(chS)

        val tokenStream = CommonTokenStream(lexer)

        val parser = SConfigParser(tokenStream)

        return parser.config().accept(STransformer())
    }
}