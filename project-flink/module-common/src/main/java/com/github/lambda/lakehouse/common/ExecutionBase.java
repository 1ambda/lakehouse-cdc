package com.github.lambda.lakehouse.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.TableLoader;
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

  public static TableLoader getIcebergTableLoader(ParameterTool params, String paramKey) {
    CatalogLoader catalogLoader = getIcebergCatalogLoader(params);
    TableLoader tableLoader = TableLoader.fromCatalog(catalogLoader,
        TableIdentifier.of(params.get(paramKey).split("\\.")[0],
            params.get(paramKey).split("\\.")[1]));
    return tableLoader;
  }

  public static CatalogLoader getIcebergCatalogLoader(ParameterTool params) {
    Map<String, String> properties = new HashMap<>();
    properties.put("type", "iceberg");
    properties.put("catalog-type", "hive");
    properties.put("property-version", "1");
    properties.put("uri", params.get("application.hms.uri"));
    properties.put("warehouse", params.get("application.hms.warehouse"));
    String catalogName = params.get("application.hms.catalog");

    CatalogLoader loader = CatalogLoader.hive(catalogName,
        new org.apache.hadoop.conf.Configuration(), properties);
    return loader;
  }

}
