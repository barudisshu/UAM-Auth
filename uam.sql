drop database `uam`;
create database `uam`;

use `uam`;

SET FOREIGN_KEY_CHECKS = 0;

drop table if exists `oauth2_client`;
drop table if exists `oauth2_user`;

create table `oauth2_user`
(
    `uid`        varchar(64),
    `username`   varchar(100) not null,
    `password`   varchar(100) not null,
    `salt`       varchar(100) not null,
    `created_time` timestamp default now(),
    `updated_time` timestamp default now() on update now(),
    constraint pk_oauth2_user primary key (`uid`)
) charset = utf8
  ENGINE = InnoDB;
create unique index idx_oauth2_user_username on oauth2_user (`username`);

create table `oauth2_client`
(
    `client_id`     varchar(64),
    `client_secret` varchar(64),
    `client_name`   varchar(100),
    `created_time`    timestamp default now(),
    `updated_time`    timestamp default now() on update now(),
    constraint pk_oauth2_client primary key (`client_id`)
) charset = utf8
  ENGINE = InnoDB;
create unique index idx_oauth2_client_name on oauth2_client (`client_name`);


insert into `oauth2_user`(`uid`, `username`, `password`, `salt`)
values (UUID(), 'admin', 'd3e184ae-00e2-4187-8a45-af5ef174ef7f', '5c5dec14-6267-4182-b241-0f6c6a5dbc7c');
insert into `oauth2_client`(`client_name`, `client_id`, `client_secret`)
values ('uam-client', 'c1ebe466-1cdc-4bd3-ab69-77c3561b9dee', 'd8346ea2-6017-43ed-ad68-19c0f971738b');

SET FOREIGN_KEY_CHECKS = 1;
