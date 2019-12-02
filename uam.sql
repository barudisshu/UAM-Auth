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

insert into `oauth2_user`(`uid`, `username`, `password`, `salt`)
values (UUID(), 'admin', 'd3c59d25033dbf980d29554025c23a75', '8d78869f470951332959580424d4bf4f');
insert into `oauth2_client`(`client_name`, `client_id`, `client_secret`)
values ('zetark-client', 'c1ebe466-1cdc-4bd3-ab69-77c3561b9dee', 'd8346ea2-6017-43ed-ad68-19c0f971738b');

SET FOREIGN_KEY_CHECKS = 1;
