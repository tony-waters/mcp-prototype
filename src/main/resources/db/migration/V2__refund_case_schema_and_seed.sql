create table customers (
    id text primary key,
    name text not null,
    email text not null,
    status text not null,
    risk_level text not null
);

create index idx_customers_email on customers (email);

create table customer_orders (
    id text primary key,
    customer_id text not null references customers (id),
    placed_at timestamptz not null,
    delivered_at timestamptz,
    status text not null,
    total_minor bigint not null,
    currency char(3) not null,
    refunded_at timestamptz
);

create index idx_customer_orders_customer_recent on customer_orders (customer_id, placed_at desc);

insert into customers (id, name, email, status, risk_level) values
    ('cus_eligible', 'Sam Rivera', 'sam@example.com', 'active', 'normal'),
    ('cus_refunded', 'Priya Shah', 'priya@example.com', 'active', 'normal'),
    ('cus_out_of_window', 'Jon Bell', 'jon@example.com', 'active', 'normal'),
    ('cus_blocked', 'Marta Chen', 'marta@example.com', 'active', 'blocked'),
    ('cus_watch', 'Alex Morgan', 'alex@example.com', 'active', 'watch'),
    ('cus_duplicate_a', 'Taylor One', 'duplicate@example.com', 'active', 'normal'),
    ('cus_duplicate_b', 'Taylor Two', 'duplicate@example.com', 'active', 'normal');

insert into customer_orders (id, customer_id, placed_at, delivered_at, status, total_minor, currency, refunded_at) values
    ('ord_eligible', 'cus_eligible', '2026-09-01T10:15:00Z', '2026-09-03T10:15:00Z', 'delivered', 7999, 'GBP', null),
    ('ord_refunded', 'cus_refunded', '2026-08-28T09:00:00Z', '2026-08-30T12:00:00Z', 'refunded', 4299, 'GBP', '2026-09-01T08:30:00Z'),
    ('ord_out_of_window', 'cus_out_of_window', '2026-06-01T11:20:00Z', '2026-06-03T16:45:00Z', 'delivered', 15999, 'GBP', null),
    ('ord_blocked_customer', 'cus_blocked', '2026-09-02T14:10:00Z', '2026-09-04T14:00:00Z', 'delivered', 2399, 'GBP', null),
    ('ord_watch_customer', 'cus_watch', '2026-09-04T10:00:00Z', '2026-09-05T13:00:00Z', 'delivered', 5899, 'GBP', null),
    ('ord_cancelled', 'cus_eligible', '2026-08-25T09:15:00Z', null, 'cancelled', 2199, 'GBP', null);
