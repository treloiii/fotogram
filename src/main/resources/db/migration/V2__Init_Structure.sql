alter table usr add tag varchar(32) not null, add avatar_url varchar(512);
create table usr_subscribers(
    usr_id bigint not null ,
    subscriber_id bigint not null,
    foreign key (usr_id) references usr (id) on delete cascade,
    foreign key (subscriber_id) references usr (id) on delete cascade
);
create table usr_subscriptions(
    usr_id bigint not null,
    subscription_id bigint not null,
    foreign key (usr_id) references usr (id) on delete cascade ,
    foreign key (subscription_id) references usr (id) on delete cascade
);
create table photo(
    id bigint primary key auto_increment not null,
    url varchar(512) not null ,
    owner_id bigint not null ,
    time date not null ,
    caption varchar(1024),
    foreign key (owner_id) references usr (id) on delete cascade
);
create table photo_comments(
  id bigint primary key auto_increment not null ,
  photo_id bigint not null ,
  commentator_id bigint not null ,
  text varchar(1024) not null ,
  foreign key (photo_id) references photo (id) on delete cascade,
  foreign key (commentator_id) references usr (id) on delete cascade
);
create table photo_likes(
  photo_id bigint not null ,
  owner_id bigint not null ,
  foreign key (photo_id) references photo (id) on delete cascade ,
  foreign key (owner_id) references usr (id) on delete cascade
);

