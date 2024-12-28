TAG = "MAKE"

.PHONY: open
open:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Open service UI"
	@ open http://localhost:8080; # Kafka UI
	@ open http://localhost:9001; # Minio UI
	@ open http://localhost:8889; # Trino UI
	@ open http://localhost:8081; # Flink UI
	@ echo ""

.PHONY: compose
compose:
	docker compose \
		-f docker-compose.cdc.yaml \
		-f docker-compose.storage.yaml \
		up

.PHONY: compose.cdc
compose.cdc:
	docker compose \
		-f docker-compose.cdc.yaml \
		up

.PHONY: compose.clean
compose.clean:
	@ echo ""
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Cleaning container volumes ('docker/volume')"
	@ rm -rf docker/volume
	@ docker container prune -f
	@ docker volume prune -f
	@ echo ""
	@ echo ""

.PHONY: connect.create
connect.create:
	curl -i -X POST \
		-H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ \
		-d @_docker/debezium/cdc-mysql-connector.json

.PHONY: connect.check
connect.check:
	curl --location --request GET 'localhost:8083/connector-plugins' | jq

.PHONY: trino.cli
trino.cli:
	trino --server http://localhost:8889

.PHONY: trino.shell
trino.shell:
	docker exec -w /etc/trino -it trino /bin/bash

.PHONY: trino.init
trino.init:
	@ echo ""
	@ echo "[$(TAG)] ($(shell date '+%H:%M:%S')) - Creating trino schemas"
	@ echo ""
	@ trino --server http://localhost:8889 --file _script/trino.init-schema.sql;
	@ echo ""
