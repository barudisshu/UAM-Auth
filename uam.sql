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
values (UUID(), 'admin', '5c5e385a1baf9eea9e34abab476368fd', '0570fabb2d50f9723f6cec76f463680c');
insert into `oauth2_client`(`client_name`, `client_id`, `client_secret`)
values ('uam-client', '302d111b-666f-4e49-ad1e-22ac605d6efe', 'a34b9e7a-4508-45d0-871c-ed4d7c3dcf9c');

SET FOREIGN_KEY_CHECKS = 1;
