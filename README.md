# Demo project for Postgres Partitioning

This repository is a small demo of partitioning setup for PostgreSQL.

## What this POC demonstrates

- PostgreSQL table partitioning by range
- Usage of `pg_partman` extension for automated partition management
 
## How to run locally

### Dependencies
* JDK >= 21
* Docker

For running locally, start DB:
```bash
docker compose up -d
```
Fixed port 15433 is used which must be available!

Start the app:
```
./gradlew bootRun
```

Access the app at http://localhost:8080/


For running tests the gradle project requires a custom image of Postgres with the Partman extension, which can be built like:
```bash
docker build -t postgres-partman:17 .
```

## Data model

- `accounts(account_id uuid primary key, created_at ...)`
- `transactions(tx_id uuid, account_id uuid, created_at timestamp ...)` have composite PK: `tx_id, created_at`

## Partitioning
The table is range-partitioned by the `created_at` column in this POC.

### Partitioning options

#### range partitioning manually
```sql
create table transactions ... partition by range (created_at);

create table transactions_2026_01
    partition of transactions for values from ('2026-01-01') to ('2026-02-01');

create table transactions_default
    partition of transactions default;
```
#### hash partitioning
```sql
create table transactions ... partition by hash (account_id);

create table transactions_p0
    partition of transactions for values with (modulus 2, remainder 0);

create table transactions_p1
    partition of transactions for values with (modulus 2, remainder 1);
```
#### list partitioning
```sql
create table transactions ... partition by list (tx_type);

create table transactions_deposit
  partition of transactions for values in ('DEPOSIT');

create table transactions_withdrawal
    partition of transactions for values in ('WITHDRAWAL');
```

See https://www.postgresql.org/docs/current/ddl-partitioning.html#DDL-PARTITIONING-OVERVIEW

## Partman

Automated partitioning management extension `pg_partman` is added into the Postgres image (see `Dockerfile`).

The extension itself is installed in the `partman` schema and is configured for partitioning of the `transactions` table monthly.

The extension takes care of creating new partitions over time, reducing the operational overhead.

In addition to the partitioning configuration, it also requires a cron job that should run daily the following function:
```sql
select partman.run_maintenance();
```
This can be done with the `pg_cron` extension.

:exclamation: Partman can deal only with the range and list partitioning strategies.

## Partitioning gotchas

Partitioning breaks global uniqueness of PKs and unique indexes at the parent-table level.
This is because indexes are local per partition.
Uniqueness can be enforced if the PK or unique includes the partition key.

Efficient queries must include the partition key in the predicate to enable partition pruning.

Indexes do not strictly need to include the partition key, but in many scenarios it is useful.

Rule of thumb: partitioning usually starts to make sense at around ~100 gb table size.

A `default` partition is used for rows that don’t match any defined partition range/list.
If it’s not present, inserts with no matching partition will fail.

Partitioning can simplify maintenance by:
* improving data locality (hot vs cold data separated)
* reducing index size per partition (but total index size across all partitions may increase)
* improving bloat control (bloat contained inside partitions; old partitions become mostly read-only)
* making vacuum/analyze more targeted (runs per partition; less work on untouched partitions)
* simplifying retention (old data removed by dropping/truncating whole partitions instead of running huge deletes)

Some partitioning strategies work well together with partial indexes, e.g. an outbox table partiotioned into indexed `published` and un-indexed `unpublished`.

See also https://www.postgresql.org/docs/current/ddl-partitioning.html#DDL-PARTITIONING-DECLARATIVE-BEST-PRACTICES