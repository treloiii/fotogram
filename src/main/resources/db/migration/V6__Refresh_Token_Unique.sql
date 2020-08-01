create unique index user_refresh_token_token_uindex
    on user_refresh_token (token);

create unique index user_refresh_token_user_id_uindex
    on user_refresh_token (user_id);