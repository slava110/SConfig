package com.slava_110.sconfig

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.Path
import kotlin.io.path.outputStream

private val json = Json { prettyPrint = true }

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val argParser = ArgParser(
        programName = "SConfig"
    )

    val input by argParser.argument(ArgType.String, fullName = "input-file", description = "Config file input")

    argParser.parse(args)

    val res = SConfig.parse(Path(input))

    Path("first.json").outputStream().use {
        json.encodeToStream(res, it)
    }

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
