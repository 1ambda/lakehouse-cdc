CREATE SCHEMA IF NOT EXISTS iceberg.raw WITH (LOCATION = 's3a://datalake/raw/');
CREATE SCHEMA IF NOT EXISTS iceberg.stage WITH (LOCATION = 's3a://datalake/stage/');
CREATE SCHEMA IF NOT EXISTS iceberg.warehouse WITH (LOCATION = 's3a://datalake/warehouse/');
