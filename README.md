# Demo project for Postgres Partitioning

This repository is a small Postgres + Spring Boot demo of partitioning setup for PostgreSQL.

## How to run locally

### Dependencies
* JDK >= 21
* Docker

Start DB:
```bash
docker compose up -d
```
Fixed port 15433 is used which must be available!

Start the app:
```
./gradlew bootRun
```

Access the app at http://localhost:8080/

## What this POC demonstrates

- PostgreSQL table partitioning by range

## Data model

- `accounts(account_id uuid primary key, created_at ...)`
- `transactions(tx_id uuid, account_id uuid, created_at timestamp ...)` have composite PK: `tx_id, created_at`

The table is range-partitioned by the `created_at` column in this POC.

Other alternatives:
* range partitioning
* hash partitioning
* list partitioning

See https://www.postgresql.org/docs/current/ddl-partitioning.html#DDL-PARTITIONING-OVERVIEW

## What is not in the scope of this POC TODO

This is intentionally out of scope for the POC:

- cross-shard queries / joins
- distributed multi-shard transactions
- resharding / moving data between shards
- global uniqueness constraints across shards
- high availability (replicas / Patroni)

## Other partitioning options (beyond this POC) TODO

### 1) 