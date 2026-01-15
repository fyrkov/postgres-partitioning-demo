# Demo project for Postgres Partitioning

This repository is a small Postgres + Spring Boot demo of partitioning setup for PostgreSQL.

## How to run locally

### Dependencies
* JDK >= 21
* Docker

Start DBs:
```bash
docker compose up -d
```
Fixed ports 15433, 15434 are used which must be available!

Start the app:
```
./gradlew bootRun
```

Access the app at http://localhost:8080/

## What this POC demonstrates

- 2 independent PostgreSQL databases (“shards”)
- same schema is deployed to both shards via Flyway
- application-level routing: the app selects shard1 vs shard2 based on the shard key

## Data model

- `accounts(account_id uuid primary key, created_at ...)`
- `transactions(account_id uuid, tx_id uuid, ...)` have composite PK: `account_id, tx_id`

The shard key is `account_id`, so every transaction lookup within the same account is single-shard.

## Routing

A deterministic function `account_id -> shard_index` is used to route queries to shards.
For 2 shards selection: even/odd based on UUID hash

## What is not in scope of this POC

This is intentionally out of scope for the POC:

- cross-shard queries / joins
- distributed multi-shard transactions
- resharding / moving data between shards
- global uniqueness constraints across shards
- high availability (replicas / Patroni)

## Other sharding options (beyond this POC)

### 1) Shard map (lookup table)
Instead of `hash(account_id) % N`, store:

`account_id -> shard_id`

Pros:
- resharding possible (move one account at a time)

Cons:
- requires state
- extra lookups on every query

### 2) Hash-range sharding
Requires ranges of hash values to be assigned to shards.

Pros:
- resharding possible and local
- mapping can be cached

Cons:
- requires state
- more implementation complexity than modulo
- manual range boundaries management
- it can become skewed over time

### 3) Consistent hashing
Route by placing each shard multiple times on a hash ring (virtual nodes). 
A key maps to the next shard clockwise on the ring. 
Adding/removing a shard moves only a fraction of keys (roughly proportional to the shard change).

Pros:
- resharding possible and local
- mapping can be cached
- evenly distributed compared to hash-range sharding
- no manual range boundaries management

Cons:
- requires state
- more implementation complexity than modulo
- data movement in case of rebalancing is scattered (many small intervals), not one contiguous range

### 4) Citus (distributed Postgres)
Citus is a Postgres extension that provides coordinator + worker nodes and routes data automatically:
https://github.com/citusdata/citus
Note: managed services like RDS do not support Citus.

Pros:
- “one logical database” feel
- automatic routing

Cons:
- operational complexity
- not available on AWS RDS/Aurora Postgres by default

### 5) Shard keys with encoded shard id
Encode the shard identifier directly into the primary identifier.
This is commonly done with custom IDs (e.g. Snowflake-style): timestamp + shard id + sequence/random.
`UUIDv7` also allows encoding a shard id into the lower bits (part of the randomness).
Example – shard id stored in the last 3 hex chars:
```
0194f3a2-7c9b-7a3f-b8d2-6e4c9f12a001
                                 ||| -> shard1
```

Pros:
- no DB lookup
- resharding is explicit and controlled (new ids can go to new shards without moving old data)

Cons:
- requires a custom id scheme
- coupling: an id format becomes a contract across services
- In case of moving an entity to another shard, the id typically must change 
- often ends up with two identifiers: an internal “shard-aware” id and an external/public id

