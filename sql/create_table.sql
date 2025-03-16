-- create database
    create database if not exists pixhive;

-- use database
use pixhive;

-- table: user
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null,
    userPassword varchar(512)                           not null,
    userName     varchar(256)                           null,
    userAvatar   varchar(1024)                          null,
    userProfile  varchar(512)                           null,
    userRole     varchar(256) default 'user'            not null comment 'role: user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null,
    createTime   datetime     default CURRENT_TIMESTAMP not null,
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete     tinyint      default 0                 not null,
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
    ) comment 'user' collate = utf8mb4_unicode_ci;

-- table: picture
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    url          varchar(512)                       not null,
    name         varchar(128)                       not null,
    introduction varchar(512)                       null,
    category     varchar(64)                        null,
    tags         varchar(512)                       null comment 'tag(JSON array)',
    picSize      bigint                             null,
    picWidth     int                                null,
    picHeight    int                                null,
    picScale     double                             null comment 'w/h',
    picFormat    varchar(32)                        null,
    userId       bigint                             not null,
    createTime   datetime default CURRENT_TIMESTAMP not null,
    editTime     datetime default CURRENT_TIMESTAMP not null,
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null,
    INDEX idx_name (name),                 -- search by name, improve query performance
    INDEX idx_introduction (introduction), -- blurring search picture introduction
    INDEX idx_category (category),         -- search by category
    INDEX idx_tags (tags),                 -- search by tag
    INDEX idx_userId (userId)              -- search by userId
) comment 'picture' collate = utf8mb4_unicode_ci;
