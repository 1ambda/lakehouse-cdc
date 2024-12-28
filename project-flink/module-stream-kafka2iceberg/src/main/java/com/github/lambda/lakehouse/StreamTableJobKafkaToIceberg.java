package com.github.lambda.lakehouse;

import com.github.lambda.lakehouse.common.ExecutionBase;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class StreamTableJobKafkaToIceberg extends ExecutionBase {
  public static void main(String[] args) throws Exception {
    // Get parameters
    ParameterTool param = getParameters();
    displayParameters(param);

    // Create execution environment
    final StreamExecutionEnvironment env = getExecutionEnv();
    configureParameters(param, env);
    StreamTableEnvironment envTable = StreamTableEnvironment.create(env);

    // Create Catalog
    envTable.executeSql(param.get("application.source.sql"));
    envTable.executeSql(param.get("application.sink.sql"));

    // Execute Application SQL
    envTable.executeSql(param.get("application.apply.sql"));
  }
}
