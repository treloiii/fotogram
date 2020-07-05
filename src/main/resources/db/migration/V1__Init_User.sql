create table usr (
    id bigint primary key auto_increment,
    username varchar(255) not null,
    password varchar(255) not null
) engine=InnoDB;