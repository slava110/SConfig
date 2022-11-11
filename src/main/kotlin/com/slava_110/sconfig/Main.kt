package com.slava_110.sconfig

import com.charleskorn.kaml.Yaml
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.outputStream
import kotlin.io.path.writeText

private val json = Json { prettyPrint = true }

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val argParser = ArgParser(
        programName = "SConfig"
    )

    val input by argParser.argument(ArgType.String, fullName = "input-file", description = "Config file input")
    val output by argParser.argument(ArgType.String, fullName = "output-file", description = "Config file output")
    val type by argParser.argument(ArgType.Choice(listOf("json", "yaml"), { it }), fullName = "type", description = "Output type")

    argParser.parse(args)

    val format: StringFormat = when(type) {
        "json" -> Json { prettyPrint = true }
        "yaml" -> Yaml()
        else -> throw IllegalArgumentException("Format $type not found!")
    }

    val res = SConfig.parse(Path(input))

    Path(output).writeText(format.encodeToString(res))

    println("Done")
}

/*
private fun parse(expressions: List<SConfigParser.ExpressionContext>): Any {
    var type = SComplexType.OBJECT

    for (expression in expressions) {
        expression.atom()?.let { atom ->
            if(type == SComplexType.OBJECT)
                type = SComplexType.ARRAY

        }
        expression.expressionList()?.let { list ->
            if(type == SComplexType.OBJECT)
                type = SComplexType.ARRAY

        }
        expression.pair()?.let { pair ->

        }
    }

    return if(type == SComplexType.OBJECT) {
        expressions.associateBy { it.pair().expression(0) to it.pair().expression(1) }
    } else {
        expressions
    }
}

private fun parse(expression: SConfigParser.ExpressionContext) {

}

enum class SComplexType {
    OBJECT,
    ARRAY;
}*/
