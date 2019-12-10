
-- 共11张表, 该模型属于简易RBAC模型(参见light_security_simple.ddl)的拓展版(测试使用数据库: MYSQL, 版本: 8.0+)

# ===========================================================================================================

-- 创建数据库 light_security_db
-- drop DATABASE if exists `light_security_db`;
-- create database `light_security_db` charset utf8 collate utf8_general_ci;

-- 切换到数据库 enterprise_service_management_db
-- use `light_security_db`;

# ===========================================================================================================


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

# ------------------------------------------------------------------------------------------------------------

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

# ------------------------------------------------------------------------------------------------------------

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

# ------------------------------------------------------------------------------------------------------------

-- 创建权限表
create table authority (
	id int(11) not null auto_increment comment '自增主键',
	type varchar(64) not null comment '权限类型',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_authority_id (id)
)engine = innodb default charset = utf8 comment '权限表';

# ------------------------------------------------------------------------------------------------------------

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

# ------------------------------------------------------------------------------------------------------------

-- 创建API权限表
create table action (
	id int(11) not null auto_increment comment '自增主键',
	parent_id int(11) comment '自我关联, 父ID',
	action_code varchar(100) not null comment 'API权限编码',
	action_name varchar(100) not null comment 'API权限名称',
	pattern varchar(500) not null comment 'API作用URL',
	action_desc varchar(100) comment 'API权限描述',
	enabled boolean not null default 0 comment '权限是否可用,  {1: true, 0: false}, 默认为false, 便是不可用',
	opened boolean not null default 0 comment '是否为公共权限, {1: true, 0: false}, 默认为false, 便是非公共',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_action_id (id),
	unique key union_key_action_code (action_code)
)engine = innodb default charset = utf8 comment 'API权限表';

# ------------------------------------------------------------------------------------------------------------

-- 创建权限与API权限的关联表
create table authority_action (
	id int(11) not null auto_increment comment '自增主键',
	authority_id int(11) not null comment '权限外键',
	action_id int(11) not null comment 'API权限外键',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_authority_action_id (id),
	unique key union_key_authority_action (authority_id, action_id),
	foreign key fk_authority_action_authority_id (authority_id) references authority (id),
	foreign key fk_authority_action_action_id (action_id) references action (id)
)engine = innodb default charset = utf8 comment '权限与API权限的关联表';

# ------------------------------------------------------------------------------------------------------------

-- 创建菜单权限表
create table menu (
	id int(11) not null auto_increment comment '自增主键',
	parent_id int(11) comment '自我关联, 父ID',
	menu_code varchar(100) not null comment '菜单编码',
	menu_name varchar(100) not null comment '菜单名称',
	menu_link varchar(500) comment '菜单链接地址',
	menu_icon varchar(500) comment '菜单图标(可以是样式名, 可以是链接地址)',
	menu_desc varchar(100) comment '菜单描述',
	enabled boolean not null default 0 comment '权限是否可用,  {1: true, 0: false}, 默认为false, 便是不可用',
	opened boolean not null default 0 comment '是否为公共权限, {1: true, 0: false}, 默认为false, 便是非公共',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_menu_id (id),
	unique key union_key_menu (menu_code)
)engine = innodb default charset = utf8 comment '菜单权限表';

# ------------------------------------------------------------------------------------------------------------

-- 创建权限与菜单权限关联表
create table authority_menu (
	id int(11) not null auto_increment comment '自增主键',
	authority_id int(11) not null comment '权限外键',
	menu_id int(11) not null comment '菜单权限外键',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_authority_action_id (id),
	unique key union_key_authority_action (authority_id, menu_id),
	foreign key fk_authority_menu_authority_id (authority_id) references authority (id),
	foreign key fk_authority_menu_menu_id (menu_id) references menu (id)
)engine = innodb default charset = utf8 comment '权限与菜单权限的关联表';

# ------------------------------------------------------------------------------------------------------------

-- 创建元素权限表
create table element (
	id int(11) not null auto_increment comment '自增主键',
	parent_id int(11) comment '自我关联, 父ID',
	element_code varchar(100) not null comment '元素编码',
	element_name varchar(100) not null comment '元素名称',
	element_desc varchar(100) comment '元素描述',
	enabled boolean not null default 0 comment '权限是否可用,  {1: true, 0: false}, 默认为false, 便是不可用',
	opened boolean not null default 0 comment '是否为公共权限, {1: true, 0: false}, 默认为false, 便是非公共',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_element_id (id),
	unique key union_key_element (element_code)
)engine = innodb default charset = utf8 comment '元素权限表';

# ------------------------------------------------------------------------------------------------------------

-- 创建权限与元素权限关联表
create table authority_element (
	id int(11) not null auto_increment comment '自增主键',
	authority_id int(11) not null comment '权限外键',
	element_id int(11) not null comment '元素权限外键',
	create_time timestamp not null default current_timestamp comment '记录创建时间',
	update_time timestamp not null default current_timestamp on update current_timestamp comment '记录更新时间',
	primary key pk_authority_element_id (id),
	unique key union_key_authority_element (authority_id, element_id),
	foreign key fk_authority_element_authority_id (authority_id) references authority (id),
	foreign key fk_authority_element_element_id (element_id) references element (id)
)engine = innodb default charset = utf8 comment '权限与元素权限的关联表';


-- 删除表
-- drop table if exists subject_role;
-- drop table if exists `subject`;

-- drop table if exists role_authority;
-- drop table if exists role;

-- drop table if exists authority_action;
-- drop table if exists authority_menu;
-- drop table if exists authority_element;
-- drop table if exists authority;
-- drop table if exists action;
-- drop table if exists menu;
-- drop table if exists element;