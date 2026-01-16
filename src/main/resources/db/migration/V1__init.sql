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

create table transactions_2026_01
    partition of transactions
    for values from ('2026-01-01') to ('2026-02-01');

create table transactions_default
    partition of transactions
    default;