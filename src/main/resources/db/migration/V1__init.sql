create table if not exists accounts (
    account_id uuid primary key,
    balance numeric(18, 2) not null default 0,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    created_at timestamptz not null default now()
);

create table if not exists transactions (
    account_id uuid not null,
    tx_id uuid not null,
    tx_type text not null,
    amount numeric(18, 2) not null,
    created_at timestamptz not null default now(),
    primary key (account_id, tx_id),
    foreign key (account_id) references accounts(account_id)
);

create index if not exists transactions_account_created_idx
    on transactions (account_id, created_at desc, tx_id);