package com.github.lambda.lakehouse;

import com.github.lambda.lakehouse.common.ExecutionBase;
import java.time.Duration;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.source.IcebergSource;
import org.apache.iceberg.flink.source.StreamingStartingStrategy;
import org.apache.iceberg.flink.source.assigner.SimpleSplitAssignerFactory;

public class StreamTableJobIcebergToIceberg extends ExecutionBase {

  public static void main(String[] args) throws Exception {
    // Get parameters
    ParameterTool param = getParameters();
    displayParameters(param);

    // Create execution environment
    final StreamExecutionEnvironment env = getExecutionEnv();
    configureParameters(param, env);

    // Create Catalog
    TableLoader tableLoader = getIcebergTableLoader(param, "application.source.table");

    // Execute Application SQL
    IcebergSource source = IcebergSource.forRowData()
        .tableLoader(tableLoader)
        .assignerFactory(new SimpleSplitAssignerFactory())
        .streaming(true)
        .streamingStartingStrategy(StreamingStartingStrategy.INCREMENTAL_FROM_LATEST_SNAPSHOT)
        .monitorInterval(Duration.ofSeconds(5))
        .build();

    DataStream<RowData> stream = env.fromSource(
        source,
        WatermarkStrategy.noWatermarks(),
        "source",
        TypeInformation.of(RowData.class));

    // Print all records to stdout.
    stream.print();

    // Submit and execute this streaming read job.
    env.execute();
  }

}
