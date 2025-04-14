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

ALTER TABLE picture
    -- add new column
    ADD COLUMN reviewStatus INT DEFAULT 0 NOT NULL COMMENT 'review status：0-pending; 1-approved; 2-declined',
    ADD COLUMN reviewMessage VARCHAR(512) NULL,
    ADD COLUMN reviewerId BIGINT NULL,
    ADD COLUMN reviewTime DATETIME NULL;

CREATE INDEX idx_reviewStatus ON picture (reviewStatus);

ALTER TABLE picture
    -- add new col
    ADD COLUMN thumbnailUrl varchar(512) NULL COMMENT 'thumbnail url';

-- table: space
create table if not exists space
(
    id         bigint auto_increment comment 'id' primary key,
    spaceName  varchar(128)                       null,
    spaceLevel int      default 0                 null comment '0-Starter, 1-Pro, 2-Premium',
    maxSize    bigint   default 0                 null,
    maxCount   bigint   default 0                 null,
    totalSize  bigint   default 0                 null,
    totalCount bigint   default 0                 null,
    userId     bigint                             not null,
    createTime datetime default CURRENT_TIMESTAMP not null,
    editTime   datetime default CURRENT_TIMESTAMP not null,
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0                 not null,
    index idx_userId (userId),
    index idx_spaceName (spaceName),
    index idx_spaceLevel (spaceLevel)
) comment 'space' collate = utf8mb4_unicode_ci;


ALTER TABLE picture
    ADD COLUMN spaceId  bigint  null comment 'space id (null: public space)';
CREATE INDEX idx_spaceId ON picture (spaceId);
