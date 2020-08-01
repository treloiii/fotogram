create table user_refresh_token (
    id bigint primary key auto_increment not null,
    user_id bigint not null,
    token varchar(512) not null
)