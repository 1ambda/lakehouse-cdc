package com.github.lambda.lakehouse.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.flink.api.java.utils.ParameterTool

private val logger = KotlinLogging.logger {}


class ExecutionParameters {

    // create static func
    companion object {
        fun getParameters(propertiesPath: String): ParameterTool {
            val currentEnv = System.getenv("ENV") ?: "LOCAL"

            // TODO: handle non-local parameters
            val parameters = ParameterTool.fromSystemProperties()

            if (currentEnv == "LOCAL") {
                // use default properties file from resources
                return ParameterTool.fromPropertiesFile(propertiesPath)
            }

            return parameters
        }
    }
}