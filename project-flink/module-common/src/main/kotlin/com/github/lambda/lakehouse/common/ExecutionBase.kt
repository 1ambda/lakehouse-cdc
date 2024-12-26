package com.github.lambda.lakehouse.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

private val logger = KotlinLogging.logger {}

interface ExecutionBase {
    companion object {
        fun getApplicationParameters(propertiesPath: String): ParameterTool {
            val parameters = ExecutionParameters.getParameters(
                this::class.java.getResource(propertiesPath).path
            )

            if (logger.isInfoEnabled()) {
                displayParameters(parameters)
            }

            return parameters
        }

        fun displayParameters(parameters: ParameterTool) {
            parameters.properties.forEach { (k, v) ->
                if (!k.toString().contains("sql")) {
                    logger.info { "Application Property: $k = $v" }
                }
            }
        }

        fun configureDefaultParameters(
            env: StreamExecutionEnvironment, parameters: ParameterTool
        ): StreamExecutionEnvironment {

            env.setParallelism(parameters.getInt("parallelism", 2))
            env.checkpointConfig.checkpointInterval =
                parameters.getLong("checkpoint.interval", 60000)

            return env
        }
    }

}