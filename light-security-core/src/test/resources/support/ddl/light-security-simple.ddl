
-- 共5张表, 该模型属于简易RBAC模型(测试使用数据库: MYSQL, 版本: 8.0+)

-- 创建数据库 light_security_db
-- drop DATABASE if exists `light_security_db`;
-- create database `light_security_db` charset utf8 collate utf8_general_ci;

-- 切换到数据库 enterprise_service_management_db
-- use `light_security_db`;


-- 注意事项, 在这里不能使用"#"作为注释, 不然可能会造成sql不能正常执行



-- 创建认证主体表
create table `subject` (
	id int(11) not null auto_increment comment '自增主键',
	`subject_name` varchar(64) not null comment '认证主体名称, 通常对应用户名',
	`password` varchar(500) not null comment '认证主体密码',
	enabled boolean not null default 0 comment '主体是否可用, {1: true, 0: false}, 默认为false, 便是不可用',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_subject_id (id),
	unique key union_key_subject_name (`subject_name`)
)engine = innodb default charset = utf8 comment '认证主体表';


-- 创建角色表
create table role (
	id int(11) not null auto_increment comment '自增主键',
	role_name varchar(64) not null comment '角色名称',
	role_code varchar(100) comment '角色编码',
	role_desc varchar(100) comment '角色描述',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_role_id (id),
	unique key union_key_role_name (role_name)
)engine = innodb default charset = utf8 comment '角色表';


-- 创建认证主体与角色关联表
create table subject_role (
	id int(11) not null auto_increment comment '自增主键',
	subject_id int(11) not null comment '认证主体表外键',
	role_id int(11) not null comment '角色表外键',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_subjects_role_id (id),
	unique key union_key_subject_role (subject_id, role_id),
	foreign key fk_subjects_role_subject_id (subject_id) references subject (id),
	foreign key fk_subjects_role_role_id (role_id) references role (id)
)engine = innodb default charset = utf8 comment '认证主体与角色关联表';


-- 创建权限表(权限表囊括了所有可能的权限, 如API、菜单、元素等)
create table authority (
	id int(11) not null auto_increment comment '自增主键',
	type varchar(64) not null comment '权限类型',

	parent_id int(11) comment '自我关联, 父ID',
	authority_code varchar(100) not null comment '权限编码',
	authority_name varchar(100) not null comment '权限名称',
	authority_desc varchar(100) comment '权限描述',

	pattern varchar(500) comment 'API作用URL',
	method varchar(10) comment 'API请求方法, {1: post, 2: get, 3: put, 4: delete, 5: head, 6: options ...}',

	authority_link varchar(500) comment '菜单链接地址',
	authority_icon varchar(500) comment '菜单图标(可以是样式名, 可以是链接地址)',

	enabled boolean not null default 0 comment '权限是否可用,  {1: true, 0: false}, 默认为false, 便是不可用',
	opened boolean not null default 0 comment '是否为公共权限, {1: true, 0: false}, 默认为false, 便是非公共',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_authority_id (id)
)engine = innodb default charset = utf8 comment '权限表';


-- 创建角色权限关联表
create table role_authority (
	id int(11) not null auto_increment comment '自增主键',
	role_id int(11) not null comment '角色外键',
	authority_id int(11) not null comment '权限外键',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_role_authority_id (id),
	unique key union_key_role_authority (role_id, authority_id),
	foreign key fk_role_authority_role_id (role_id) references role (id),
	foreign key fk_role_authority_authority_id (authority_id) references authority (id)
)engine = innodb default charset = utf8 comment '角色权限关联表';


-- 删除表
-- drop table if exists subject_role;
-- drop table if exists `subject`;

-- drop table if exists role_authority;
-- drop table if exists role;

-- drop table if exists authority;