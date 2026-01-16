create table if not exists accounts (
    account_id uuid primary key,
    balance numeric(18, 2) not null default 0,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    created_at timestamptz not null default now()
);

create table if not exists transactions (
    tx_id uuid not null,
    account_id uuid not null,
    tx_type text not null,
    amount numeric(18, 2) not null,
    created_at timestamptz not null default now(),
    primary key (tx_id, created_at),
    foreign key (account_id) references accounts(account_id)
) partition by range (created_at);

create index if not exists transactions_created_account_idx
    on transactions (created_at, account_id);

create table transactions_2025_12
    partition of transactions
    for values from ('2025-12-01') to ('2026-01-01');

create table transactions_2026_01
    partition of transactions
    for values from ('2026-01-01') to ('2026-02-01');

create table transactions_default
    partition of transactions
    default;

-- TEST DATA

create extension if not exists pgcrypto;

-- seed 100 accounts
insert into accounts (account_id, balance, first_name, last_name, created_at)
select
    gen_random_uuid(),
    0,
    'first_' || i,
    'last_' || i,
    now() - (random() * interval '30 days')
from generate_series(1, 100) s(i)
    on conflict do nothing;

-- 1000 txs in 2025-12
insert into transactions (tx_id, account_id, tx_type, amount, created_at)
select
    gen_random_uuid(),
    (select account_id from accounts order by random() limit 1),
  case when random() < 0.5 then 'DEPOSIT' else 'WITHDRAWAL' end,
  round((random() * 10000)::numeric, 2),
  timestamptz '2025-12-01'
    + (random() * (timestamptz '2026-01-01' - timestamptz '2025-12-01'))
from generate_series(1, 1000);

-- 1000 txs in 2026-01
insert into transactions (tx_id, account_id, tx_type, amount, created_at)
select
    gen_random_uuid(),
    (select account_id from accounts order by random() limit 1),
  case when random() < 0.5 then 'DEPOSIT' else 'WITHDRAWAL' end,
  round((random() * 10000)::numeric, 2),
  timestamptz '2026-01-01'
    + (random() * (timestamptz '2026-02-01' - timestamptz '2026-01-01'))
from generate_series(1, 1000);
