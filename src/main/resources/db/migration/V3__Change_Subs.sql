drop table if exists usr_subscriptions;
drop table if exists usr_subscribers;

create table subs(
    user bigint not null,
    subscribe_on bigint not null,
    foreign key (user) references usr(id) on delete cascade,
    foreign key (subscribe_on) references usr(id) on delete cascade
);