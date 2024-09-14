-- auto-generated definition
create table user
(
    id                    bigint auto_increment primary key,
    role                  tinyint(1)                               null comment '0: admin; 1: user; 2: servant ',

    openid                varchar(255)                             null,
    session_key           varchar(255)                             null,
    unionid               varchar(255)                             null,

    nickname              varchar(255)                             null,
    age                   int                                      null,
    gender                varchar(10)                              null comment '0: male; 1: female',
    avatar                varchar(255)                             null,
    language              varchar(255)                             null,

    completed_order_count int            default 0                 null,
    balance               decimal(10, 2) default 0.00              null,
    current_location      varchar(255)                             null,

    is_deleted            tinyint(1)     default 0                 null comment '0: not deleted; 1: deleted',
    created_at            timestamp      default CURRENT_TIMESTAMP not null
) charset = utf8;

-- auto-generated definition
create table user_client
(
    id         bigint auto_increment primary key,
    user_id    bigint                               null,
    is_deleted tinyint(1) default 0                 null comment '0: not deleted; 1: deleted',
    created_at timestamp  default CURRENT_TIMESTAMP not null
) charset = utf8;

-- auto-generated definition
create table user_servant
(
    id         bigint auto_increment primary key,
    user_id    bigint                               null,
    bio        mediumtext                           null,
    is_deleted tinyint(1) default 0                 null comment '0: not deleted; 1: deleted',
    created_at timestamp  default CURRENT_TIMESTAMP not null
) charset = utf8;

-- auto-generated definition
create table `order`
(
    id                    bigint auto_increment primary key,

    client_id             bigint                               null,
    servant_id            bigint                               null,

    title                 varchar(255)                         not null,
    required_servant_type varchar(255)                         not null,
    required_gender       varchar(10)                          null,
    required_age_min      int                                  null,
    required_age_max      int                                  null,

    service_duration      int                                  null,

    state                 varchar(255)                         null,

    city                  varchar(255)                         null,
    address               text                                 null,
    price                 decimal(10, 2)                       not null,
    rating                decimal(3, 2)                        null,
    candidate_count       int        default 0                 null,

    status     int        default 0                 null comment '0: pending; 1: processing; 2: completed',

    effective_at          timestamp  default CURRENT_TIMESTAMP null,
    expire_at             timestamp  default CURRENT_TIMESTAMP null,
    close_at              timestamp  default CURRENT_TIMESTAMP null,

    completed_at          timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_completed           int        default 0                 null comment '0: not completed; 1: completed',

    is_deleted            tinyint(1) default 0                 null comment '0: not deleted; 1: deleted',
    created_at            timestamp  default CURRENT_TIMESTAMP not null
) charset = utf8;

-- auto-generated definition
create table order_candidate
(
    id         bigint auto_increment primary key,
    order_id   bigint                               not null,
    servant_id bigint                               not null,
    is_closed  tinyint(1)                           null comment '0: not closed; 1: closed',
    is_deleted tinyint(1) default 0                 null comment '0: not deleted; 1: deleted',
    created_at timestamp  default CURRENT_TIMESTAMP not null
) charset = utf8;

-- auto-generated definition
create table servant_type
(
    id         bigint auto_increment primary key,
    name  varchar(255)                         not null,
    servant_id bigint                               null,
    is_deleted tinyint(1) default 0                 null comment '0: not deleted; 1: deleted',
    created_at timestamp  default CURRENT_TIMESTAMP not null
) charset = utf8;

-- auto-generated definition
create table tag
(
    id         bigint auto_increment primary key,
    tag_name   varchar(255)                         not null,
    servant_id bigint                               not null,
    is_deleted tinyint(1) default 0                 null comment '0: not deleted; 1: deleted',
    created_at timestamp  default CURRENT_TIMESTAMP not null
)

