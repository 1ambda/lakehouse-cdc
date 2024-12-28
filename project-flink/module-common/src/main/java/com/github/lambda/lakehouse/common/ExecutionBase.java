package com.github.lambda.lakehouse.common;

import java.io.IOException;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExecutionBase {

  private static final Logger logger = LoggerFactory.getLogger(ExecutionBase.class);

  public static boolean isLocalEnv() {
    String env = System.getenv().getOrDefault("ENV", "LOCAL");
    return env.equals("LOCAL");
  }

  public static StreamExecutionEnvironment getExecutionEnv() {
    if (isLocalEnv()) {
      Configuration conf = GlobalConfiguration.loadConfiguration(
          ExecutionBase.class.getResource("/").getPath());
      return StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
    }

    Configuration conf = new Configuration();
    return StreamExecutionEnvironment.createLocalEnvironment(conf);
  }

  public static ParameterTool getParameters() throws IOException {
    String path = ExecutionBase.class.getResource("/application.properties").getPath();
    ParameterTool parameter = ParameterTool.fromPropertiesFile(path);

    if (!isLocalEnv()) {
      // TODO (Kun): Support non-resource property files
      parameter = ParameterTool.fromSystemProperties();
    }

    return parameter;
  }

  public static void displayParameters(ParameterTool parameter) {
    parameter.getProperties().forEach((k, v) -> {
      if (!k.toString().contains("sql")) {
        logger.info("Property: {} = {}", k, v);
      }
    });
  }

  public static void configureParameters(ParameterTool parameter, StreamExecutionEnvironment env) {
    env.setParallelism(parameter.getInt("default.parallelism", 1));
  }
}
