TAG = "MAKE"

.PHONY: compose
compose:
	docker compose -f docker-compose.yaml up

.PHONY: connect.create
connect.create:
	curl -i -X POST \
		-H "Accept:application/json" -H "Content-Type:application/json" \
		http://localhost:8083/connectors/ \
		-d @_docker/debezium/cdc-mysql-connector.json

.PHONY: connect.check
connect.check:
	curl --location --request GET 'localhost:8083/connector-plugins' | jq


