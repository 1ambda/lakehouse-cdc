package com.github.lambda.lakehouse

import com.github.lambda.lakehouse.common.ExecutionBase
import com.github.lambda.lakehouse.common.ExecutionBase.Companion.configureDefaultParameters
import com.github.lambda.lakehouse.common.ExecutionBase.Companion.getApplicationParameters
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment


private val logger = KotlinLogging.logger {}

class FlinkKafkaToKafka : ExecutionBase {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Get parameters
            val parameters = getApplicationParameters("/application.properties")

            // Configure Application
            val conf = Configuration()
            val rawEnv = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
            val rawEnvConfigured = configureDefaultParameters(rawEnv, parameters)
            val streamEnv = StreamTableEnvironment.create(rawEnvConfigured)

            // Create Catalog
            streamEnv.executeSql(parameters.get("application.source.sql"))
            streamEnv.executeSql("SHOW CREATE TABLE ${parameters.get("application.source.table")}")
                .print()
            streamEnv.executeSql(parameters.get("application.sink.sql"))
            streamEnv.executeSql("SHOW CREATE TABLE ${parameters.get("application.sink.table")}")
                .print()

            // Execute Application SQL
            logger.info { "Executing Application SQL: ${parameters.get("application.apply.sql")}" }
            streamEnv.executeSql(parameters.get("application.apply.sql"))
        }

    }
}

