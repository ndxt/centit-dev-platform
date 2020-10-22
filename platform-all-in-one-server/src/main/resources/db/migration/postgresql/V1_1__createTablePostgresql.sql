/*==============================================================*/
/* Postgresql 数据库脚本                                        */
/* nj_stat                                                      */
/*==============================================================*/


DROP SEQUENCE IF EXISTS "public"."s_databasecode";
DROP SEQUENCE IF EXISTS "public"."s_filter_no";
DROP SEQUENCE IF EXISTS "public"."s_msgcode";
DROP SEQUENCE IF EXISTS "public"."s_optdefcode";
DROP SEQUENCE IF EXISTS "public"."s_recipient";
DROP SEQUENCE IF EXISTS "public"."s_rolecode";
DROP SEQUENCE IF EXISTS "public"."s_sys_log";
DROP SEQUENCE IF EXISTS "public"."s_unitcode";
DROP SEQUENCE IF EXISTS "public"."s_user_unit_id";
DROP SEQUENCE IF EXISTS "public"."s_usercode";
DROP TABLE IF EXISTS "public"."d_database_info";
DROP TABLE IF EXISTS "public"."d_exchange_task";
DROP TABLE IF EXISTS "public"."d_os_info";
DROP TABLE IF EXISTS "public"."d_task_detail_log";
DROP TABLE IF EXISTS "public"."d_task_log";
DROP TABLE IF EXISTS "public"."f_bbs_annex";
DROP TABLE IF EXISTS "public"."f_bbs_piece";
DROP TABLE IF EXISTS "public"."f_database_info";
DROP TABLE IF EXISTS "public"."f_datacatalog";
DROP TABLE IF EXISTS "public"."f_datadictionary";
DROP TABLE IF EXISTS "public"."f_inner_msg";
DROP TABLE IF EXISTS "public"."f_inner_msg_annex";
DROP TABLE IF EXISTS "public"."f_inner_msg_recipient";
DROP TABLE IF EXISTS "public"."f_md_column";
DROP TABLE IF EXISTS "public"."f_md_rel_detail";
DROP TABLE IF EXISTS "public"."f_md_relation";
DROP TABLE IF EXISTS "public"."f_md_table";
DROP TABLE IF EXISTS "public"."f_meta_chang_log";
DROP TABLE IF EXISTS "public"."f_meta_column";
DROP TABLE IF EXISTS "public"."f_meta_rel_detial";
DROP TABLE IF EXISTS "public"."f_meta_relation";
DROP TABLE IF EXISTS "public"."f_meta_table";
DROP TABLE IF EXISTS "public"."f_mysql_sequence";
DROP TABLE IF EXISTS "public"."f_opt_log";
DROP TABLE IF EXISTS "public"."f_optdatascope";
DROP TABLE IF EXISTS "public"."f_optdef";
DROP TABLE IF EXISTS "public"."f_optinfo";
DROP TABLE IF EXISTS "public"."f_os_info";
DROP TABLE IF EXISTS "public"."f_pending_meta_column";
DROP TABLE IF EXISTS "public"."f_pending_meta_rel_detial";
DROP TABLE IF EXISTS "public"."f_pending_meta_relation";
DROP TABLE IF EXISTS "public"."f_pending_meta_table";
DROP TABLE IF EXISTS "public"."f_query_filter_condition";
DROP TABLE IF EXISTS "public"."f_roleinfo";
DROP TABLE IF EXISTS "public"."f_rolepower";
DROP TABLE IF EXISTS "public"."f_sys_notify";
DROP TABLE IF EXISTS "public"."f_unitinfo";
DROP TABLE IF EXISTS "public"."f_unitrole";
DROP TABLE IF EXISTS "public"."f_user_query_filter";
DROP TABLE IF EXISTS "public"."f_userinfo";
DROP TABLE IF EXISTS "public"."f_userrole";
DROP TABLE IF EXISTS "public"."f_usersetting";
DROP TABLE IF EXISTS "public"."f_userunit";
DROP TABLE IF EXISTS "public"."file_access_log";
DROP TABLE IF EXISTS "public"."file_info";
DROP TABLE IF EXISTS "public"."file_store_info";
DROP TABLE IF EXISTS "public"."file_upload_authorized";
DROP TABLE IF EXISTS "public"."flyway_schema_history";
DROP TABLE IF EXISTS "public"."m_application_info";
DROP TABLE IF EXISTS "public"."m_innermsg";
DROP TABLE IF EXISTS "public"."m_innermsg_recipient";
DROP TABLE IF EXISTS "public"."m_meta_form_model";
DROP TABLE IF EXISTS "public"."m_msgannex";
DROP TABLE IF EXISTS "public"."m_page_model";
DROP TABLE IF EXISTS "public"."pdman_db_version";
DROP TABLE IF EXISTS "public"."q_chart_model";
DROP TABLE IF EXISTS "public"."q_chart_resource_column";
DROP TABLE IF EXISTS "public"."q_data_packet";
DROP TABLE IF EXISTS "public"."q_data_packet_param";
DROP TABLE IF EXISTS "public"."q_data_resource";
DROP TABLE IF EXISTS "public"."q_data_resource_column";
DROP TABLE IF EXISTS "public"."q_data_resource_param";
DROP TABLE IF EXISTS "public"."q_dataset_columndesc";
DROP TABLE IF EXISTS "public"."q_dataset_define";
DROP TABLE IF EXISTS "public"."q_form_column";
DROP TABLE IF EXISTS "public"."q_form_model";
DROP TABLE IF EXISTS "public"."q_form_param";
DROP TABLE IF EXISTS "public"."q_query_column";
DROP TABLE IF EXISTS "public"."q_query_condition";
DROP TABLE IF EXISTS "public"."q_query_model";
DROP TABLE IF EXISTS "public"."q_report_model";
DROP TABLE IF EXISTS "public"."q_report_sql";
DROP TABLE IF EXISTS "public"."q_report_sql_column";
DROP TABLE IF EXISTS "public"."q_report_sql_param";
DROP TABLE IF EXISTS "public"."wf_action_log";
DROP TABLE IF EXISTS "public"."wf_action_task";
DROP TABLE IF EXISTS "public"."wf_flow_define";
DROP TABLE IF EXISTS "public"."wf_flow_instance";
DROP TABLE IF EXISTS "public"."wf_flow_instance_group";
DROP TABLE IF EXISTS "public"."wf_flow_role";
DROP TABLE IF EXISTS "public"."wf_flow_role_define";
DROP TABLE IF EXISTS "public"."wf_flow_stage";
DROP TABLE IF EXISTS "public"."wf_flow_team_role";
DROP TABLE IF EXISTS "public"."wf_flow_variable";
DROP TABLE IF EXISTS "public"."wf_flow_variable_define";
DROP TABLE IF EXISTS "public"."wf_inst_attention";
DROP TABLE IF EXISTS "public"."wf_node";
DROP TABLE IF EXISTS "public"."wf_node_instance";
DROP TABLE IF EXISTS "public"."wf_optinfo";
DROP TABLE IF EXISTS "public"."wf_optpage";
DROP TABLE IF EXISTS "public"."wf_organize";
DROP TABLE IF EXISTS "public"."wf_role_formula";
DROP TABLE IF EXISTS "public"."wf_role_relegate";
DROP TABLE IF EXISTS "public"."wf_runtime_warning";
DROP TABLE IF EXISTS "public"."wf_stage_instance";
DROP TABLE IF EXISTS "public"."wf_team";
DROP TABLE IF EXISTS "public"."wf_transition";
DROP VIEW IF EXISTS "public"."f_v_lastversionflow";
DROP VIEW IF EXISTS "public"."f_v_opt_role_map";
DROP VIEW IF EXISTS "public"."f_v_optdef_url_map";
DROP VIEW IF EXISTS "public"."f_v_useroptdatascopes";
DROP VIEW IF EXISTS "public"."f_v_useroptlist";
DROP VIEW IF EXISTS "public"."f_v_useroptmoudlelist";
DROP VIEW IF EXISTS "public"."f_v_userroles";
DROP VIEW IF EXISTS "public"."v_hi_unitinfo";
DROP VIEW IF EXISTS "public"."v_inner_user_task_list";
DROP VIEW IF EXISTS "public"."v_opt_tree";
DROP VIEW IF EXISTS "public"."v_user_task_list";
CREATE SEQUENCE "s_databasecode"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
CREATE SEQUENCE "s_filter_no"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
CREATE SEQUENCE "s_msgcode"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
CREATE SEQUENCE "s_optdefcode"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1001000
CACHE 1;
CREATE SEQUENCE "s_recipient"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
CREATE SEQUENCE "s_rolecode"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;
CREATE SEQUENCE "s_sys_log"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
CREATE SEQUENCE "s_unitcode"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;
CREATE SEQUENCE "s_user_unit_id"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;
CREATE SEQUENCE "s_usercode"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 10
CACHE 1;
/*==============================================================*/
/* Table: d_database_info                                       */
/*==============================================================*/
CREATE TABLE "d_database_info" (
  "database_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "database_name" varchar(100) COLLATE "pg_catalog"."default",
  "os_id" varchar(64) COLLATE "pg_catalog"."default",
  "database_url" varchar(1000) COLLATE "pg_catalog"."default",
  "username" varchar(100) COLLATE "pg_catalog"."default",
  "password" varchar(100) COLLATE "pg_catalog"."default",
  "database_desc" varchar(500) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "create_time" timestamp(6),
  "created" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "d_database_info" OWNER TO "nj_public";
COMMENT ON COLUMN "d_database_info"."password" IS '加密';
/*==============================================================*/
/* Table: d_exchange_task                                       */
/*==============================================================*/
CREATE TABLE "d_exchange_task" (
  "task_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "packet_id" varchar(32) COLLATE "pg_catalog"."default",
  "task_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "task_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "task_cron" varchar(100) COLLATE "pg_catalog"."default",
  "task_desc" varchar(500) COLLATE "pg_catalog"."default",
  "exchange_desc_json" text COLLATE "pg_catalog"."default",
  "last_run_time" timestamp(6),
  "next_run_time" timestamp(6),
  "is_valid" varchar(1) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "created" varchar(8) COLLATE "pg_catalog"."default",
  "last_update_time" timestamp(6),
  "application_id" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "d_exchange_task" OWNER TO "nj_public";
COMMENT ON COLUMN "d_exchange_task"."task_type" IS '1: 直接交换 2 :导出离线文件 3：监控文件夹导入文件 4：调用接口 5:接口事件';
/*==============================================================*/
/* Table: d_os_info                                             */
/*==============================================================*/
CREATE TABLE "d_os_info" (
  "os_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "os_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "os_url" char(10) COLLATE "pg_catalog"."default",
  "dde_sync_url" char(10) COLLATE "pg_catalog"."default",
  "sys_data_push_option" char(10) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "create_time" timestamp(6),
  "created" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "d_os_info" OWNER TO "nj_public";
COMMENT ON COLUMN "d_os_info"."dde_sync_url" IS '这个仅供DDE使用';
/*==============================================================*/
/* Table: d_task_detail_log                                     */
/*==============================================================*/
CREATE TABLE "d_task_detail_log" (
  "log_detail_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "task_id" varchar(32) COLLATE "pg_catalog"."default",
  "log_id" varchar(32) COLLATE "pg_catalog"."default",
  "log_type" varchar(200) COLLATE "pg_catalog"."default",
  "run_begin_time" timestamp(6),
  "run_end_time" timestamp(6),
  "log_info" varchar(2000) COLLATE "pg_catalog"."default",
  "success_pieces" numeric(12),
  "error_pieces" numeric(12)
)
;
ALTER TABLE "d_task_detail_log" OWNER TO "nj_public";
COMMENT ON COLUMN "d_task_detail_log"."log_type" IS 'info error defug';
COMMENT ON COLUMN "d_task_detail_log"."log_info" IS 'opttype=1,3,4';
/*==============================================================*/
/* Table: d_task_log                                            */
/*==============================================================*/
CREATE TABLE "d_task_log" (
  "log_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "task_id" varchar(32) COLLATE "pg_catalog"."default",
  "run_begin_time" timestamp(6),
  "run_end_time" timestamp(6),
  "run_type" varchar(100) COLLATE "pg_catalog"."default",
  "runner" varchar(8) COLLATE "pg_catalog"."default",
  "other_message" varchar(500) COLLATE "pg_catalog"."default",
  "error_pieces" varchar(100) COLLATE "pg_catalog"."default",
  "success_pieces" varchar(100) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "d_task_log" OWNER TO "nj_public";
COMMENT ON COLUMN "d_task_log"."run_type" IS '手动执行，自动执行';
COMMENT ON COLUMN "d_task_log"."other_message" IS '比如数据库连接失败';
/*==============================================================*/
/* Table: f_bbs_annex                                           */
/*==============================================================*/
CREATE TABLE "f_bbs_annex" (
  "annex_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "subject_id" varchar(32) COLLATE "pg_catalog"."default",
  "piece_id" varchar(32) COLLATE "pg_catalog"."default",
  "annex_file_name" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "annex_file_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "upload_date" date
)
;
ALTER TABLE "f_bbs_annex" OWNER TO "nj_public";
/*==============================================================*/
/* Table: f_bbs_piece                                           */
/*==============================================================*/
CREATE TABLE "f_bbs_piece" (
  "piece_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "subject_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "deliverer" varchar(32) COLLATE "pg_catalog"."default",
  "deliver_date" timestamp(6),
  "bbs_content" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_bbs_piece" OWNER TO "nj_public";
/*==============================================================*/
/* Table: f_database_info                                       */
/*==============================================================*/
CREATE TABLE "f_database_info" (
  "database_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "database_name" varchar(100) COLLATE "pg_catalog"."default",
  "os_id" varchar(20) COLLATE "pg_catalog"."default",
  "database_url" varchar(1000) COLLATE "pg_catalog"."default",
  "username" varchar(100) COLLATE "pg_catalog"."default",
  "password" varchar(100) COLLATE "pg_catalog"."default",
  "database_desc" varchar(500) COLLATE "pg_catalog"."default",
  "last_modify_date" date,
  "create_time" date,
  "created" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_database_info" OWNER TO "nj_public";
/*==============================================================*/
/* Table: f_datacatalog                                         */
/*==============================================================*/
CREATE TABLE "f_datacatalog" (
  "catalog_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "catalog_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "catalog_style" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "catalog_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "catalog_desc" varchar(256) COLLATE "pg_catalog"."default",
  "field_desc" varchar(1024) COLLATE "pg_catalog"."default",
  "update_date" date,
  "create_date" date,
  "opt_id" varchar(32) COLLATE "pg_catalog"."default",
  "need_cache" char(1) COLLATE "pg_catalog"."default" DEFAULT '1'::bpchar,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_datacatalog" OWNER TO "nj_public";
COMMENT ON COLUMN "f_datacatalog"."catalog_style" IS 'F : 框架固有的 U:用户 S：系统  G国标';
COMMENT ON COLUMN "f_datacatalog"."catalog_type" IS 'T：树状表格 L:列表';
COMMENT ON COLUMN "f_datacatalog"."field_desc" IS '字段描述，不同字段用分号隔开';
COMMENT ON COLUMN "f_datacatalog"."opt_id" IS '业务分类，使用数据字典DICTIONARYTYPE中数据';
COMMENT ON TABLE "f_datacatalog" IS '类别状态   U:用户 S：系统，G国标类别形式  T：树状表格 L:列表';
/*==============================================================*/
/* Table: f_datadictionary                                      */
/*==============================================================*/
CREATE TABLE "f_datadictionary" (
  "catalog_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "data_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "extra_code" varchar(16) COLLATE "pg_catalog"."default",
  "extra_code2" varchar(16) COLLATE "pg_catalog"."default",
  "data_tag" char(1) COLLATE "pg_catalog"."default",
  "data_value" varchar(2048) COLLATE "pg_catalog"."default",
  "data_style" char(1) COLLATE "pg_catalog"."default",
  "data_desc" varchar(256) COLLATE "pg_catalog"."default",
  "last_modify_date" date,
  "create_date" date,
  "data_order" numeric(6)
)
;
ALTER TABLE "f_datadictionary" OWNER TO "nj_public";
COMMENT ON COLUMN "f_datadictionary"."extra_code" IS '树型字典的父类代码';
COMMENT ON COLUMN "f_datadictionary"."data_tag" IS 'N正常，D已停用，用户可以自解释这个字段';
COMMENT ON COLUMN "f_datadictionary"."data_style" IS 'F : 框架固有的 U:用户 S：系统  G国标';
COMMENT ON TABLE "f_datadictionary" IS '数据字典：存放一些常量数据 比如出物提示信息，还有一些 代码与名称的对应表，比如 状态，角色名，头衔 等等';
/*==============================================================*/
/* Table: f_inner_msg                                           */
/*==============================================================*/
CREATE TABLE "f_inner_msg" (
  "msg_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "reply_msg_code" varchar(32) COLLATE "pg_catalog"."default",
  "sender" varchar(32) COLLATE "pg_catalog"."default",
  "send_date" timestamp(6),
  "msg_title" varchar(512) COLLATE "pg_catalog"."default",
  "msg_type" char(1) COLLATE "pg_catalog"."default",
  "mail_type" char(1) COLLATE "pg_catalog"."default",
  "receive_name" varchar(2048) COLLATE "pg_catalog"."default",
  "msg_state" char(1) COLLATE "pg_catalog"."default",
  "msg_content" text COLLATE "pg_catalog"."default",
  "opt_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_method" varchar(64) COLLATE "pg_catalog"."default",
  "opt_tag" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_inner_msg" OWNER TO "nj_public";
COMMENT ON COLUMN "f_inner_msg"."msg_code" IS '消息主键自定义，通过S_M_INNERMSG序列生成';
COMMENT ON COLUMN "f_inner_msg"."msg_type" IS 'P= 个人为消息  A= 机构为公告（通知）
M=邮件';
COMMENT ON COLUMN "f_inner_msg"."mail_type" IS 'I=收件箱
O=发件箱
D=草稿箱
T=废件箱
';
COMMENT ON COLUMN "f_inner_msg"."receive_name" IS '使用部门，个人中文名，中间使用英文分号分割';
COMMENT ON COLUMN "f_inner_msg"."opt_id" IS '模块，或者表';
COMMENT ON COLUMN "f_inner_msg"."opt_method" IS '方法，或者字段';
COMMENT ON COLUMN "f_inner_msg"."opt_tag" IS '一般用于关联到业务主体';
/*==============================================================*/
/* Table: f_inner_msg_annex                                     */
/*==============================================================*/
CREATE TABLE "f_inner_msg_annex" (
  "annex_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "msg_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "annex_file_name" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "annex_file_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "upload_date" date
)
;
ALTER TABLE "f_inner_msg_annex" OWNER TO "nj_public";
/*==============================================================*/
/* Table: f_inner_msg_recipient                                 */
/*==============================================================*/
CREATE TABLE "f_inner_msg_recipient" (
  "receive" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "msg_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "receive_date" timestamp(6),
  "reply_msg_code" varchar(32) COLLATE "pg_catalog"."default",
  "mail_type" char(1) COLLATE "pg_catalog"."default",
  "msg_state" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_inner_msg_recipient" OWNER TO "nj_public";
COMMENT ON COLUMN "f_inner_msg_recipient"."mail_type" IS 'T=收件人
C=抄送
B=密送';
COMMENT ON COLUMN "f_inner_msg_recipient"."msg_state" IS '未读/已读/删除，收件人在线时弹出提示
U=未读
R=已读
D=删除';
COMMENT ON TABLE "f_inner_msg_recipient" IS '内部消息（邮件）与公告收件人及消息信息';
/*==============================================================*/
/* Table: f_md_column                                           */
/*==============================================================*/
CREATE TABLE "f_md_column" (
  "table_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "field_label_name" varchar(64) COLLATE "pg_catalog"."default",
  "column_length" numeric(6),
  "scale" numeric(3),
  "access_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "column_type" varchar(32) COLLATE "pg_catalog"."default",
  "field_type" varchar(32) COLLATE "pg_catalog"."default",
  "primary_key" char(1) COLLATE "pg_catalog"."default",
  "mandatory" char(1) COLLATE "pg_catalog"."default",
  "lazy_fetch" char(1) COLLATE "pg_catalog"."default",
  "column_comment" varchar(1024) COLLATE "pg_catalog"."default",
  "column_order" numeric(3),
  "last_modify_date" timestamp(6),
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "reference_type" varchar(1) COLLATE "pg_catalog"."default",
  "reference_data" varchar(256) COLLATE "pg_catalog"."default",
  "validate_regex" varchar(32) COLLATE "pg_catalog"."default",
  "validate_info" varchar(32) COLLATE "pg_catalog"."default",
  "auto_create_rule" varchar(1) COLLATE "pg_catalog"."default",
  "auto_create_param" varchar(200) COLLATE "pg_catalog"."default",
  "workflow_variable_type" varchar(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_md_column" OWNER TO "nj_public";
/*==============================================================*/
/* Table: f_md_rel_detail                                       */
/*==============================================================*/
CREATE TABLE "f_md_rel_detail" (
  "relation_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_column_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "child_column_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "f_md_rel_detail" OWNER TO "nj_public";
/*==============================================================*/
/* Table: f_md_relation                                        */
/*==============================================================*/
CREATE TABLE "f_md_relation" (
  "relation_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_table_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "child_table_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "relation_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "relation_state" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "relation_comment" varchar(256) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "relation_type" char(3) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_md_relation" OWNER TO "nj_public";
CREATE TABLE "f_md_table" (
  "table_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "table_label_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "database_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "table_name" varchar(64) COLLATE "pg_catalog"."default",
  "table_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "access_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "table_comment" varchar(256) COLLATE "pg_catalog"."default",
  "workflow_opt_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "record_date" timestamp(6),
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "update_check_timestamp" char(1) COLLATE "pg_catalog"."default",
  "fulltext_search" char(1) COLLATE "pg_catalog"."default",
  "write_opt_log" char(1) COLLATE "pg_catalog"."default",
  "object_title" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_md_table" OWNER TO "nj_public";
COMMENT ON COLUMN "f_md_table"."database_code" IS '数据库代码';
COMMENT ON COLUMN "f_md_table"."table_type" IS '表/视图 目前只能是表';
COMMENT ON COLUMN "f_md_table"."access_type" IS '系统 S / R 查询(只读)/ N 新建(读写)';
COMMENT ON TABLE "f_md_table" IS '状态分为 系统/查询/更新 系统，不可以做任何操作 查询，仅用于通用查询模块，不可以更新';
CREATE TABLE "f_meta_chang_log" (
  "change_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "table_id" varchar(64) COLLATE "pg_catalog"."default",
  "change_date" timestamp(6) NOT NULL,
  "changer" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "change_script" text COLLATE "pg_catalog"."default",
  "change_comment" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_meta_chang_log" OWNER TO "nj_public";
COMMENT ON COLUMN "f_meta_chang_log"."table_id" IS '表单主键';
CREATE TABLE "f_meta_column" (
  "table_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "column_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "field_label_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "column_comment" varchar(256) COLLATE "pg_catalog"."default",
  "column_order" numeric(3),
  "column_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "field_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "column_length" numeric(6),
  "scale" numeric(3),
  "access_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "mandatory" char(1) COLLATE "pg_catalog"."default",
  "primary_key" char(1) COLLATE "pg_catalog"."default",
  "reference_type" char(1) COLLATE "pg_catalog"."default",
  "reference_data" varchar(1000) COLLATE "pg_catalog"."default",
  "validate_regex" varchar(200) COLLATE "pg_catalog"."default",
  "validate_info" varchar(200) COLLATE "pg_catalog"."default",
  "auto_create_rule" char(1) COLLATE "pg_catalog"."default",
  "auto_create_param" varchar(1000) COLLATE "pg_catalog"."default",
  "workflow_variable_type" char(1) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_meta_column" OWNER TO "nj_public";
COMMENT ON COLUMN "f_meta_column"."table_id" IS '表单主键';
COMMENT ON COLUMN "f_meta_column"."column_length" IS 'precision';
COMMENT ON COLUMN "f_meta_column"."reference_type" IS ' 0：没有：1： 数据字典(列表)   2： 数据字典(树型)   3：JSON表达式 4：sql语句   5：SQL（树）
            	   9 :框架内置字典（用户、机构、角色等等）  Y：年份 M：月份   F:文件（column_Type 必须为 varchar（64））';
COMMENT ON COLUMN "f_meta_column"."reference_data" IS '根据paramReferenceType类型（1,2,3）填写对应值';
COMMENT ON COLUMN "f_meta_column"."validate_regex" IS 'regex表达式';
COMMENT ON COLUMN "f_meta_column"."validate_info" IS '约束不通过提示信息';
COMMENT ON COLUMN "f_meta_column"."auto_create_rule" IS 'C 常量  U uuid S sequence';
COMMENT ON COLUMN "f_meta_column"."auto_create_param" IS '常量（默认值）或则 sequence名字';
COMMENT ON COLUMN "f_meta_column"."workflow_variable_type" IS '0: 不是流程变量 1：流程业务变量 2： 流程过程变量';
CREATE TABLE "f_meta_rel_detial" (
  "relation_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_column_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "child_column_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "f_meta_rel_detial" OWNER TO "nj_public";
CREATE TABLE "f_meta_relation" (
  "relation_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_table_id" varchar(32) COLLATE "pg_catalog"."default",
  "child_table_id" varchar(32) COLLATE "pg_catalog"."default",
  "relation_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "relation_state" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "relation_comment" varchar(256) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_meta_relation" OWNER TO "nj_public";
COMMENT ON COLUMN "f_meta_relation"."relation_id" IS '关联关系，类似与外键，但不创建外键';
COMMENT ON COLUMN "f_meta_relation"."parent_table_id" IS '表单主键';
COMMENT ON COLUMN "f_meta_relation"."child_table_id" IS '表单主键';
CREATE TABLE "f_meta_table" (
  "table_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "table_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "access_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "table_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "table_label_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "table_state" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "table_comment" varchar(256) COLLATE "pg_catalog"."default",
  "workflow_opt_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "fulltext_search" char(1) COLLATE "pg_catalog"."default",
  "write_opt_log" char(1) COLLATE "pg_catalog"."default",
  "update_check_timestamp" char(1) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "recorder" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_meta_table" OWNER TO "nj_public";
COMMENT ON COLUMN "f_meta_table"."table_id" IS '表编号';
COMMENT ON COLUMN "f_meta_table"."table_type" IS '表/视图/已存在表的扩展字段    目前只能是表';
COMMENT ON COLUMN "f_meta_table"."access_type" IS '表的存储类别  H：隐藏；R：只读；N：可读写';
COMMENT ON COLUMN "f_meta_table"."table_state" IS '系统 S / R 查询(只读)/ N 新建(读写)';
COMMENT ON COLUMN "f_meta_table"."workflow_opt_type" IS '0: 不关联工作流 1：和流程业务关联 2： 和流程过程关联
            1, 添加  WFINSTID 流程实例ID
            2, 添加 NODEINSTID WFINSTID	节点实例编号 流程实例ID


            Name	Code	Comment	Data Type	Length	Precision	Primary	Foreign Key	Mandatory
            节点实例编号	NODEINSTID		NUMBER(12)	12		TRUE	FALSE	TRUE
            流程实例ID	WFINSTID		NUMBER(12)	12		FALSE	TRUE	FALSE';
COMMENT ON COLUMN "f_meta_table"."update_check_timestamp" IS 'Y/N 更新时是否校验时间戳 添加 Last_modify_time datetime';
COMMENT ON TABLE "f_meta_table" IS '状态分为 系统/查询/更新
系统，不可以做任何操作
查询，仅用于通用查询模块，不可以更新
                                 -&';
CREATE TABLE "f_mysql_sequence" (
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "currvalue" int4 NOT NULL,
  "increment" int4 NOT NULL
)
;
ALTER TABLE "f_mysql_sequence" OWNER TO "nj_public";
CREATE TABLE "f_opt_log" (
  "log_id" numeric(12) NOT NULL,
  "log_level" varchar(2) COLLATE "pg_catalog"."default" NOT NULL,
  "user_code" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_time" date NOT NULL,
  "opt_content" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL,
  "new_value" text COLLATE "pg_catalog"."default",
  "old_value" text COLLATE "pg_catalog"."default",
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_method" varchar(64) COLLATE "pg_catalog"."default",
  "opt_tag" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_opt_log" OWNER TO "nj_public";
COMMENT ON COLUMN "f_opt_log"."opt_id" IS '一般用于关联到业务主体的标识、表的主键等等';
CREATE TABLE "f_optdatascope" (
  "opt_scope_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_id" varchar(32) COLLATE "pg_catalog"."default",
  "scope_name" varchar(64) COLLATE "pg_catalog"."default",
  "filter_condition" varchar(1024) COLLATE "pg_catalog"."default",
  "scope_memo" varchar(1024) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_optdatascope" OWNER TO "nj_public";
COMMENT ON COLUMN "f_optdatascope"."filter_condition" IS '条件语句，可以有的参数 [mt] 业务表 [uc] 用户代码 [uu] 用户机构代码';
COMMENT ON COLUMN "f_optdatascope"."scope_memo" IS '数据权限说明';
CREATE TABLE "f_optdef" (
  "opt_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_id" varchar(32) COLLATE "pg_catalog"."default",
  "opt_name" varchar(100) COLLATE "pg_catalog"."default",
  "opt_method" varchar(50) COLLATE "pg_catalog"."default",
  "opt_url" varchar(256) COLLATE "pg_catalog"."default",
  "opt_desc" varchar(256) COLLATE "pg_catalog"."default",
  "opt_order" numeric(4),
  "is_in_workflow" char(1) COLLATE "pg_catalog"."default",
  "update_date" date,
  "create_date" date,
  "opt_req" varchar(8) COLLATE "pg_catalog"."default",
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_optdef" OWNER TO "nj_public";
COMMENT ON COLUMN "f_optdef"."opt_method" IS '操作参数 方法';
COMMENT ON COLUMN "f_optdef"."is_in_workflow" IS '是否为流程操作方法 F：不是  T ： 是';
CREATE TABLE "f_optinfo" (
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "pre_opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_route" varchar(256) COLLATE "pg_catalog"."default",
  "opt_url" varchar(256) COLLATE "pg_catalog"."default",
  "form_code" varchar(4) COLLATE "pg_catalog"."default",
  "opt_type" char(1) COLLATE "pg_catalog"."default",
  "msg_no" numeric(10),
  "msg_prm" varchar(256) COLLATE "pg_catalog"."default",
  "is_in_toolbar" char(1) COLLATE "pg_catalog"."default",
  "img_index" numeric(10),
  "top_opt_id" varchar(32) COLLATE "pg_catalog"."default",
  "order_ind" numeric(4),
  "flow_code" varchar(8) COLLATE "pg_catalog"."default",
  "page_type" char(1) COLLATE "pg_catalog"."default" DEFAULT 'I'::bpchar,
  "icon" varchar(512) COLLATE "pg_catalog"."default",
  "height" numeric(10),
  "width" numeric(10),
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_optinfo" OWNER TO "nj_public";
COMMENT ON COLUMN "f_optinfo"."opt_route" IS '与angularjs路由匹配';
COMMENT ON COLUMN "f_optinfo"."opt_type" IS ' S:实施业务, O:普通业务, W:流程业务, I :项目业务';
COMMENT ON COLUMN "f_optinfo"."order_ind" IS '这个顺序只需在同一个父业务下排序';
COMMENT ON COLUMN "f_optinfo"."page_type" IS 'D : DIV I:iFrame';
CREATE TABLE "f_os_info" (
  "os_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "os_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "os_url" varchar(200) COLLATE "pg_catalog"."default",
  "oauth_password" varchar(100) COLLATE "pg_catalog"."default",
  "last_modify_date" date,
  "create_time" date,
  "created" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_os_info" OWNER TO "nj_public";
CREATE TABLE "f_pending_meta_column" (
  "table_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "field_label_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_comment" varchar(1000) COLLATE "pg_catalog"."default",
  "column_order" numeric(3),
  "field_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "max_length" numeric(12),
  "scale" numeric(3),
  "mandatory" char(1) COLLATE "pg_catalog"."default",
  "primary_key" char(1) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_pending_meta_column" OWNER TO "nj_public";
COMMENT ON COLUMN "f_pending_meta_column"."table_id" IS '表单主键';
COMMENT ON COLUMN "f_pending_meta_column"."max_length" IS 'precision';
CREATE TABLE "f_pending_meta_rel_detial" (
  "relation_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_column_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "child_column_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "f_pending_meta_rel_detial" OWNER TO "nj_public";
CREATE TABLE "f_pending_meta_relation" (
  "relation_id" numeric(12) NOT NULL,
  "parent_table_id" numeric(12),
  "child_table_id" numeric(12),
  "relation_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "relation_state" char(1) COLLATE "pg_catalog"."default",
  "relation_comment" varchar(256) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_pending_meta_relation" OWNER TO "nj_public";
COMMENT ON COLUMN "f_pending_meta_relation"."relation_id" IS '关联关系，类似与外键，但不创建外键';
COMMENT ON COLUMN "f_pending_meta_relation"."parent_table_id" IS '表单主键';
COMMENT ON COLUMN "f_pending_meta_relation"."child_table_id" IS '表单主键';
CREATE TABLE "f_pending_meta_table" (
  "table_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "table_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "table_label_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "table_comment" varchar(256) COLLATE "pg_catalog"."default",
  "table_state" char(1) COLLATE "pg_catalog"."default",
  "workflow_opt_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "update_check_timestamp" char(1) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(64) COLLATE "pg_catalog"."default",
  "primary_key" char(1) COLLATE "pg_catalog"."default",
  "table_type" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_pending_meta_table" OWNER TO "nj_public";
COMMENT ON COLUMN "f_pending_meta_table"."table_id" IS '表单主键';
COMMENT ON COLUMN "f_pending_meta_table"."table_state" IS '系统 S / R 查询(只读)/ N 新建(读写)';
COMMENT ON COLUMN "f_pending_meta_table"."workflow_opt_type" IS '0: 不关联工作流 1：和流程业务关联 2： 和流程过程关联';
COMMENT ON COLUMN "f_pending_meta_table"."update_check_timestamp" IS 'Y/N 更新时是否校验时间戳';
CREATE TABLE "f_query_filter_condition" (
  "condition_no" numeric(12) NOT NULL,
  "table_class_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_label" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "param_type" varchar(8) COLLATE "pg_catalog"."default",
  "default_value" varchar(100) COLLATE "pg_catalog"."default",
  "filter_sql" varchar(200) COLLATE "pg_catalog"."default",
  "select_data_type" char(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'N'::bpchar,
  "select_data_catalog" varchar(64) COLLATE "pg_catalog"."default",
  "select_sql" varchar(1000) COLLATE "pg_catalog"."default",
  "select_json" varchar(2000) COLLATE "pg_catalog"."default",
  "create_date" date
)
;
ALTER TABLE "f_query_filter_condition" OWNER TO "nj_public";
COMMENT ON COLUMN "f_query_filter_condition"."table_class_name" IS '数据库表代码或者po的类名';
COMMENT ON COLUMN "f_query_filter_condition"."param_type" IS '参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月';
COMMENT ON COLUMN "f_query_filter_condition"."filter_sql" IS '过滤语句，将会拼装到sql语句中';
COMMENT ON COLUMN "f_query_filter_condition"."select_data_type" IS '数据下拉框内容； N ：没有， D 数据字典, S 通过sql语句获得， J json数据直接获取 ';
COMMENT ON COLUMN "f_query_filter_condition"."select_sql" IS '有两个返回字段的sql语句';
COMMENT ON COLUMN "f_query_filter_condition"."select_json" IS 'KEY,Value数值对，JSON格式';
CREATE TABLE "f_roleinfo" (
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_name" varchar(64) COLLATE "pg_catalog"."default",
  "role_type" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "unit_code" varchar(32) COLLATE "pg_catalog"."default",
  "is_valid" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "role_desc" varchar(256) COLLATE "pg_catalog"."default",
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_roleinfo" OWNER TO "nj_public";
COMMENT ON COLUMN "f_roleinfo"."role_type" IS 'F 为系统 固有的 G 全局的 P 公用的 D 部门的 I 为项目角色 W工作量角色';
CREATE TABLE "f_rolepower" (
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_scope_codes" varchar(1000) COLLATE "pg_catalog"."default",
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_rolepower" OWNER TO "nj_public";
COMMENT ON COLUMN "f_rolepower"."opt_scope_codes" IS '用逗号隔开的数据范围结合（空\all 表示全部）';
CREATE TABLE "f_sys_notify" (
  "notify_id" numeric(12) NOT NULL,
  "notify_sender" varchar(100) COLLATE "pg_catalog"."default",
  "notify_receiver" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "msg_subject" varchar(200) COLLATE "pg_catalog"."default",
  "msg_content" varchar(2000) COLLATE "pg_catalog"."default" NOT NULL,
  "notice_type" varchar(100) COLLATE "pg_catalog"."default",
  "notify_state" char(1) COLLATE "pg_catalog"."default",
  "error_msg" varchar(500) COLLATE "pg_catalog"."default",
  "notify_time" date,
  "opt_tag" varchar(200) COLLATE "pg_catalog"."default",
  "opt_method" varchar(64) COLLATE "pg_catalog"."default",
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "f_sys_notify" OWNER TO "nj_public";
COMMENT ON COLUMN "f_sys_notify"."notify_state" IS '0 成功， 1 失败 2 部分成功';
COMMENT ON COLUMN "f_sys_notify"."opt_tag" IS '一般用于关联到业务主体';
COMMENT ON COLUMN "f_sys_notify"."opt_method" IS '方法，或者字段';
COMMENT ON COLUMN "f_sys_notify"."opt_id" IS '模块，或者表';
CREATE TABLE "f_unitinfo" (
  "unit_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_unit" varchar(32) COLLATE "pg_catalog"."default",
  "unit_type" char(1) COLLATE "pg_catalog"."default",
  "is_valid" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "unit_tag" varchar(100) COLLATE "pg_catalog"."default",
  "unit_name" varchar(300) COLLATE "pg_catalog"."default" NOT NULL,
  "english_name" varchar(300) COLLATE "pg_catalog"."default",
  "dep_no" varchar(100) COLLATE "pg_catalog"."default",
  "unit_desc" varchar(256) COLLATE "pg_catalog"."default",
  "unit_short_name" varchar(32) COLLATE "pg_catalog"."default",
  "unit_word" varchar(100) COLLATE "pg_catalog"."default",
  "unit_grade" numeric(4),
  "unit_order" numeric(4),
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default",
  "unit_path" varchar(1000) COLLATE "pg_catalog"."default",
  "unit_manager" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_unitinfo" OWNER TO "nj_public";
COMMENT ON COLUMN "f_unitinfo"."unit_type" IS '发布任务/ 邮电规划/组队/接收任务';
COMMENT ON COLUMN "f_unitinfo"."unit_tag" IS '用户第三方系统管理';
COMMENT ON COLUMN "f_unitinfo"."dep_no" IS '组织机构代码：';
CREATE TABLE "f_unitrole" (
  "unit_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "obtain_date" date NOT NULL,
  "secede_date" date,
  "change_desc" varchar(256) COLLATE "pg_catalog"."default",
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_unitrole" OWNER TO "nj_public";
CREATE TABLE "f_user_query_filter" (
  "filter_no" numeric(12) NOT NULL,
  "user_code" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "modle_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "filter_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "filter_value" varchar(3200) COLLATE "pg_catalog"."default" NOT NULL,
  "is_default" char(1) COLLATE "pg_catalog"."default",
  "create_date" date
)
;
ALTER TABLE "f_user_query_filter" OWNER TO "nj_public";
COMMENT ON COLUMN "f_user_query_filter"."modle_code" IS '开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法';
COMMENT ON COLUMN "f_user_query_filter"."filter_name" IS '用户自行定义的名称';
COMMENT ON COLUMN "f_user_query_filter"."filter_value" IS '变量值，json格式，对应一个map';
CREATE TABLE "f_userinfo" (
  "user_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "user_pin" varchar(100) COLLATE "pg_catalog"."default",
  "user_type" char(1) COLLATE "pg_catalog"."default" DEFAULT 'U'::bpchar,
  "is_valid" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "login_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "user_name" varchar(300) COLLATE "pg_catalog"."default" NOT NULL,
  "user_tag" varchar(100) COLLATE "pg_catalog"."default",
  "english_name" varchar(300) COLLATE "pg_catalog"."default",
  "user_desc" varchar(256) COLLATE "pg_catalog"."default",
  "login_times" numeric(6),
  "active_time" date,
  "top_unit" varchar(32) COLLATE "pg_catalog"."default",
  "reg_email" varchar(60) COLLATE "pg_catalog"."default",
  "user_pwd" varchar(20) COLLATE "pg_catalog"."default",
  "pwd_expired_time" date,
  "reg_cell_phone" varchar(15) COLLATE "pg_catalog"."default",
  "id_card_no" varchar(20) COLLATE "pg_catalog"."default",
  "primary_unit" varchar(32) COLLATE "pg_catalog"."default",
  "user_word" varchar(100) COLLATE "pg_catalog"."default",
  "user_order" numeric(4),
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_userinfo" OWNER TO "nj_public";
COMMENT ON COLUMN "f_userinfo"."user_type" IS '发布任务/接收任务/系统管理';
COMMENT ON COLUMN "f_userinfo"."reg_email" IS '注册用Email，不能重复';
COMMENT ON COLUMN "f_userinfo"."user_word" IS '微信号';
CREATE TABLE "f_userrole" (
  "user_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "obtain_date" date NOT NULL,
  "secede_date" date,
  "change_desc" varchar(256) COLLATE "pg_catalog"."default",
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_userrole" OWNER TO "nj_public";
CREATE TABLE "f_usersetting" (
  "user_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "param_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "param_value" varchar(2048) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "param_name" varchar(200) COLLATE "pg_catalog"."default",
  "create_date" date
)
;
ALTER TABLE "f_usersetting" OWNER TO "nj_public";
COMMENT ON COLUMN "f_usersetting"."user_code" IS 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案';
CREATE TABLE "f_userunit" (
  "user_unit_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "unit_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "user_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "is_primary" char(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1'::bpchar,
  "user_station" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "user_rank" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "rank_memo" varchar(256) COLLATE "pg_catalog"."default",
  "user_order" numeric(8) DEFAULT 0,
  "update_date" date,
  "create_date" date,
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "updator" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "f_userunit" OWNER TO "nj_public";
COMMENT ON COLUMN "f_userunit"."is_primary" IS 'T：为主， F：兼职';
COMMENT ON COLUMN "f_userunit"."user_rank" IS 'RANK 代码不是 0开头的可以进行授予';
COMMENT ON COLUMN "f_userunit"."rank_memo" IS '任职备注';
COMMENT ON TABLE "f_userunit" IS '同一个人可能在多个部门担任不同的职位';
CREATE TABLE "file_access_log" (
  "access_token" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "file_id" varchar(36) COLLATE "pg_catalog"."default",
  "auth_time" timestamp(6) NOT NULL,
  "access_usercode" varchar(8) COLLATE "pg_catalog"."default",
  "access_usename" varchar(50) COLLATE "pg_catalog"."default",
  "access_right" varchar(2) COLLATE "pg_catalog"."default" NOT NULL,
  "token_expire_time" timestamp(6) NOT NULL,
  "access_times" numeric(6) NOT NULL,
  "last_access_time" timestamp(6),
  "last_access_host" varchar(100) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "file_access_log" OWNER TO "nj_public";
COMMENT ON COLUMN "file_access_log"."access_right" IS 'A： 所有权限  S: 下载源文件  T ：下载附属文件 ';
CREATE TABLE "file_info" (
  "file_id" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "file_md5" varchar(36) COLLATE "pg_catalog"."default",
  "file_name" varchar(200) COLLATE "pg_catalog"."default",
  "file_show_path" varchar(1000) COLLATE "pg_catalog"."default",
  "file_type" varchar(8) COLLATE "pg_catalog"."default",
  "file_desc" varchar(200) COLLATE "pg_catalog"."default",
  "file_state" char(1) COLLATE "pg_catalog"."default",
  "download_times" numeric(6),
  "os_id" varchar(20) COLLATE "pg_catalog"."default",
  "opt_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_method" varchar(64) COLLATE "pg_catalog"."default",
  "opt_tag" varchar(200) COLLATE "pg_catalog"."default",
  "created" varchar(8) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "index_state" char(1) COLLATE "pg_catalog"."default",
  "encrypt_type" char(1) COLLATE "pg_catalog"."default",
  "file_owner" varchar(32) COLLATE "pg_catalog"."default",
  "file_unit" varchar(32) COLLATE "pg_catalog"."default",
  "attached_file_md5" varchar(36) COLLATE "pg_catalog"."default",
  "attached_type" varchar(8) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "file_info" OWNER TO "nj_public";
COMMENT ON COLUMN "file_info"."file_md5" IS '文件MD5编码';
COMMENT ON COLUMN "file_info"."file_name" IS '原始文件名称';
COMMENT ON COLUMN "file_info"."file_type" IS '文件后缀名 or T：缩略图  P：PDF只读文件';
COMMENT ON COLUMN "file_info"."file_state" IS 'C : 正在上传  N : 正常 Z:空文件 F:文件上传失败';
COMMENT ON COLUMN "file_info"."opt_id" IS '模块，或者表';
COMMENT ON COLUMN "file_info"."opt_method" IS '方法，或者字段';
COMMENT ON COLUMN "file_info"."opt_tag" IS '一般用于关联到业务主体';
COMMENT ON COLUMN "file_info"."index_state" IS 'N ：不需要索引 S：等待索引 I：已索引 F:索引失败';
COMMENT ON COLUMN "file_info"."encrypt_type" IS 'N : 没有加密   Z：ZIPFILE    D:DES加密';
COMMENT ON COLUMN "file_info"."attached_type" IS '附属文件类别：文件后缀名  文件预处理后，新生成的文件作为主文件，原文件作为附属文件';
CREATE TABLE "file_store_info" (
  "file_md5" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "file_store_path" varchar(200) COLLATE "pg_catalog"."default",
  "file_size" numeric(20),
  "file_reference_count" numeric(6),
  "is_temp" char(1) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6)
)
;
ALTER TABLE "file_store_info" OWNER TO "nj_public";
COMMENT ON COLUMN "file_store_info"."file_md5" IS '文件MD5编码';
CREATE TABLE "file_upload_authorized" (
  "upload_token" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "max_upload_files" numeric(10) NOT NULL,
  "rest_upload_files" numeric(10) NOT NULL,
  "create_time" timestamp(6),
  "last_upload_time" timestamp(6)
)
;
ALTER TABLE "file_upload_authorized" OWNER TO "nj_public";
CREATE TABLE "flyway_schema_history" (
  "installed_rank" int4 NOT NULL,
  "version" varchar(50) COLLATE "pg_catalog"."default",
  "description" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "script" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL,
  "checksum" int4,
  "installed_by" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "installed_on" timestamp(6) NOT NULL,
  "execution_time" int4 NOT NULL,
  "success" int2 NOT NULL
)
;
ALTER TABLE "flyway_schema_history" OWNER TO "nj_public";
CREATE TABLE "m_application_info" (
  "application_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "application_id_name" varchar(200) COLLATE "pg_catalog"."default",
  "page_flow" text COLLATE "pg_catalog"."default",
  "owner_unit" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "m_application_info" OWNER TO "nj_public";
CREATE TABLE "m_innermsg" (
  "msg_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "sender" varchar(128) COLLATE "pg_catalog"."default",
  "send_date" date,
  "msg_title" varchar(128) COLLATE "pg_catalog"."default",
  "msg_type" varchar(16) COLLATE "pg_catalog"."default",
  "mail_type" char(1) COLLATE "pg_catalog"."default",
  "mail_undel_type" char(1) COLLATE "pg_catalog"."default",
  "receive_name" varchar(2048) COLLATE "pg_catalog"."default",
  "hold_users" numeric(8),
  "msg_state" char(1) COLLATE "pg_catalog"."default",
  "msg_content" bytea,
  "email_id" varchar(8) COLLATE "pg_catalog"."default",
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_method" varchar(64) COLLATE "pg_catalog"."default",
  "opt_tag" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "m_innermsg" OWNER TO "nj_public";
COMMENT ON COLUMN "m_innermsg"."msg_code" IS '消息主键自定义，通过S_M_INNERMSG序列生成';
COMMENT ON COLUMN "m_innermsg"."msg_type" IS 'P= 个人为消息  A= 机构为公告（通知） M=邮件';
COMMENT ON COLUMN "m_innermsg"."mail_type" IS 'I=收件箱O=发件箱 D=草稿箱T=废件箱 ';
COMMENT ON COLUMN "m_innermsg"."receive_name" IS '使用部门，个人中文名，中间使用英文分号分割';
COMMENT ON COLUMN "m_innermsg"."hold_users" IS '总数为发送人和接收人数量相加，发送和接收人删除消息时-1，当数量为0时真正删除此条记录 消息类型为邮件时不需要设置';
COMMENT ON COLUMN "m_innermsg"."msg_state" IS '未读/已读/删除';
COMMENT ON COLUMN "m_innermsg"."email_id" IS '用户配置多邮箱时使用';
COMMENT ON COLUMN "m_innermsg"."opt_id" IS '模块，或者表';
COMMENT ON COLUMN "m_innermsg"."opt_method" IS '方法，或者字段';
COMMENT ON COLUMN "m_innermsg"."opt_tag" IS '一般用于关联到业务主体';
COMMENT ON TABLE "m_innermsg" IS '内部消息与公告接受代码,  其实可以独立出来, 因为他 和发送人 是 一对多的关系 ';
CREATE TABLE "m_innermsg_recipient" (
  "msg_code" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "receive" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "reply_msg_code" int4,
  "receive_type" char(1) COLLATE "pg_catalog"."default",
  "mail_type" char(1) COLLATE "pg_catalog"."default",
  "msg_state" char(1) COLLATE "pg_catalog"."default",
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "m_innermsg_recipient" OWNER TO "nj_public";
COMMENT ON COLUMN "m_innermsg_recipient"."receive_type" IS 'P=个人为消息A=机构为公告M=邮件';
COMMENT ON COLUMN "m_innermsg_recipient"."mail_type" IS 'T=收件人C=抄送B=密送';
COMMENT ON COLUMN "m_innermsg_recipient"."msg_state" IS '未读/已读/删除，收件人在线时弹出提示U=未读R=已读D=删除';
COMMENT ON TABLE "m_innermsg_recipient" IS '内部消息（邮件）与公告收件人及消息信息';
CREATE TABLE "m_meta_form_model" (
  "model_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "table_id" varchar(64) COLLATE "pg_catalog"."default",
  "model_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "application_id" varchar(64) COLLATE "pg_catalog"."default",
  "model_type" char(1) COLLATE "pg_catalog"."default",
  "relation_id" varchar(64) COLLATE "pg_catalog"."default",
  "form_template" text COLLATE "pg_catalog"."default",
  "rel_flow_code" varchar(32) COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "model_comment" varchar(256) COLLATE "pg_catalog"."default",
  "extend_opt_js" text COLLATE "pg_catalog"."default",
  "data_filter_sql" varchar(2000) COLLATE "pg_catalog"."default",
  "mode_opt_url" varchar(800) COLLATE "pg_catalog"."default",
  "flow_opt_title" varchar(500) COLLATE "pg_catalog"."default",
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "mobile_form_template" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "m_meta_form_model" OWNER TO "nj_public";
COMMENT ON COLUMN "m_meta_form_model"."table_id" IS '表单主表';
COMMENT ON COLUMN "m_meta_form_model"."model_type" IS ' N 正常表单 S 子模块表单 L 列表表单';
COMMENT ON COLUMN "m_meta_form_model"."data_filter_sql" IS '条件语句';
COMMENT ON COLUMN "m_meta_form_model"."mode_opt_url" IS 'json String 格式的参数';
COMMENT ON COLUMN "m_meta_form_model"."database_code" IS '数据库代码';
CREATE TABLE "m_msgannex" (
  "msg_code" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "info_code" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "msg_annex_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "m_msgannex" OWNER TO "nj_public";
CREATE TABLE "m_page_model" (
  "page_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "page_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "application_id" varchar(64) COLLATE "pg_catalog"."default",
  "page_comment" varchar(256) COLLATE "pg_catalog"."default",
  "page_type" char(1) COLLATE "pg_catalog"."default",
  "page_design_json" text COLLATE "pg_catalog"."default",
  "last_modify_date" timestamp(6),
  "recorder" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "m_page_model" OWNER TO "nj_public";
CREATE TABLE "pdman_db_version" (
  "db_version" varchar(256) COLLATE "pg_catalog"."default",
  "version_desc" varchar(1024) COLLATE "pg_catalog"."default",
  "created_time" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "pdman_db_version" OWNER TO "nj_public";
CREATE TABLE "q_chart_model" (
  "chart_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "packet_id" varchar(32) COLLATE "pg_catalog"."default",
  "query_id" varchar(32) COLLATE "pg_catalog"."default",
  "application_id" varchar(64) COLLATE "pg_catalog"."default",
  "data_set_name" varchar(64) COLLATE "pg_catalog"."default",
  "chart_name_format" varchar(256) COLLATE "pg_catalog"."default",
  "chart_type" varchar(16) COLLATE "pg_catalog"."default",
  "chart_catalog" char(1) COLLATE "pg_catalog"."default",
  "chart_design_json" text COLLATE "pg_catalog"."default",
  "report_doc_fileid" varchar(64) COLLATE "pg_catalog"."default",
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "has_data_opt" char(1) COLLATE "pg_catalog"."default",
  "data_opt_desc_json" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_chart_model" OWNER TO "nj_public";
COMMENT ON COLUMN "q_chart_model"."chart_catalog" IS 'C chart F form R report  default:C';
COMMENT ON COLUMN "q_chart_model"."chart_design_json" IS 'json格式的图表自定义说明';
CREATE TABLE "q_chart_resource_column" (
  "chart_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "show_type" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_chart_resource_column" OWNER TO "nj_public";
CREATE TABLE "q_data_packet" (
  "packet_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "application_id" varchar(64) COLLATE "pg_catalog"."default",
  "owner_type" char(1) COLLATE "pg_catalog"."default",
  "owner_code" varchar(64) COLLATE "pg_catalog"."default",
  "packet_name" varchar(200) COLLATE "pg_catalog"."default",
  "packet_type" char(1) COLLATE "pg_catalog"."default",
  "packet_desc" varchar(500) COLLATE "pg_catalog"."default",
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "has_data_opt" varchar(32) COLLATE "pg_catalog"."default",
  "data_opt_desc_json" text COLLATE "pg_catalog"."default",
  "buffer_fresh_period" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_data_packet" OWNER TO "nj_public";
CREATE TABLE "q_data_packet_param" (
  "packet_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_label" varchar(200) COLLATE "pg_catalog"."default",
  "param_display_style" char(1) COLLATE "pg_catalog"."default",
  "param_type" varchar(64) COLLATE "pg_catalog"."default",
  "param_reference_type" char(1) COLLATE "pg_catalog"."default",
  "param_reference_data" varchar(1000) COLLATE "pg_catalog"."default",
  "param_validate_regex" varchar(200) COLLATE "pg_catalog"."default",
  "param_validate_info" varchar(200) COLLATE "pg_catalog"."default",
  "param_default_value" varchar(200) COLLATE "pg_catalog"."default",
  "param_order" numeric(3)
)
;
ALTER TABLE "q_data_packet_param" OWNER TO "nj_public";
CREATE TABLE "q_data_resource" (
  "resource_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "owner_type" char(1) COLLATE "pg_catalog"."default",
  "owner_code" varchar(64) COLLATE "pg_catalog"."default",
  "query_sql" varchar(4000) COLLATE "pg_catalog"."default",
  "query_desc" varchar(512) COLLATE "pg_catalog"."default",
  "resource_name_format" varchar(256) COLLATE "pg_catalog"."default",
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6)
)
;
ALTER TABLE "q_data_resource" OWNER TO "nj_public";
COMMENT ON COLUMN "q_data_resource"."owner_type" IS '机构或者个人';
COMMENT ON COLUMN "q_data_resource"."resource_name_format" IS '根据参数转换';
CREATE TABLE "q_data_resource_column" (
  "resource_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_name" varchar(64) COLLATE "pg_catalog"."default",
  "is_stat_data" char(1) COLLATE "pg_catalog"."default",
  "data_type" varchar(16) COLLATE "pg_catalog"."default",
  "catalog_code" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_data_resource_column" OWNER TO "nj_public";
CREATE TABLE "q_data_resource_param" (
  "resource_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_label" varchar(200) COLLATE "pg_catalog"."default",
  "param_display_style" char(1) COLLATE "pg_catalog"."default",
  "param_type" varchar(64) COLLATE "pg_catalog"."default",
  "param_reference_type" char(1) COLLATE "pg_catalog"."default",
  "param_reference_data" varchar(1000) COLLATE "pg_catalog"."default",
  "param_validate_regex" varchar(200) COLLATE "pg_catalog"."default",
  "param_validate_info" varchar(200) COLLATE "pg_catalog"."default",
  "param_default_value" varchar(200) COLLATE "pg_catalog"."default",
  "param_order" numeric(2)
)
;
ALTER TABLE "q_data_resource_param" OWNER TO "nj_public";
COMMENT ON COLUMN "q_data_resource_param"."param_display_style" IS 'N:普通 normal H 隐藏 hide R 只读 readonly';
COMMENT ON COLUMN "q_data_resource_param"."param_type" IS 'S:文本 N数字  D：日期 T：时间戳（datetime)  ';
COMMENT ON COLUMN "q_data_resource_param"."param_reference_type" IS '0：没有：1： 数据字典 2：JSON表达式 3：sql语句  Y：年份 M：月份';
COMMENT ON COLUMN "q_data_resource_param"."param_reference_data" IS '根据paramReferenceType类型（1,2,3）填写对应值';
COMMENT ON COLUMN "q_data_resource_param"."param_validate_regex" IS 'regex表达式';
COMMENT ON COLUMN "q_data_resource_param"."param_validate_info" IS '约束不通过提示信息';
COMMENT ON COLUMN "q_data_resource_param"."param_default_value" IS '参数默认值';
CREATE TABLE "q_dataset_columndesc" (
  "packet_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "query_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "column_name" varchar(64) COLLATE "pg_catalog"."default",
  "is_stat_data" char(10) COLLATE "pg_catalog"."default",
  "data_type" varchar(16) COLLATE "pg_catalog"."default",
  "catalog_code" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_dataset_columndesc" OWNER TO "nj_public";
CREATE TABLE "q_dataset_define" (
  "query_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "packet_id" varchar(32) COLLATE "pg_catalog"."default",
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "query_sql" varchar(4000) COLLATE "pg_catalog"."default",
  "query_desc" varchar(512) COLLATE "pg_catalog"."default",
  "query_name" varchar(255) COLLATE "pg_catalog"."default",
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "set_type" char(1) COLLATE "pg_catalog"."default",
  "dataset_params_json" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_dataset_define" OWNER TO "nj_public";
CREATE TABLE "q_form_column" (
  "form_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "column_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_type" char(1) COLLATE "pg_catalog"."default",
  "column_type" char(1) COLLATE "pg_catalog"."default",
  "mine_url_format" varchar(512) COLLATE "pg_catalog"."default",
  "column_order" numeric(4)
)
;
ALTER TABLE "q_form_column" OWNER TO "nj_public";
COMMENT ON COLUMN "q_form_column"."opt_type" IS '0 无操作 1 合计 2 平均 3 平均合计';
COMMENT ON COLUMN "q_form_column"."column_type" IS 'R 行头 、 C 列头 、 D 数据';
COMMENT ON COLUMN "q_form_column"."mine_url_format" IS '模板可以引用行数据和 数据包查询参数';
CREATE TABLE "q_form_model" (
  "form_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "packet_id" varchar(64) COLLATE "pg_catalog"."default",
  "query_id" int4,
  "data_set_name" varchar(64) COLLATE "pg_catalog"."default",
  "from_name_format" varchar(256) COLLATE "pg_catalog"."default",
  "form_type" varchar(16) COLLATE "pg_catalog"."default",
  "recorder" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6)
)
;
ALTER TABLE "q_form_model" OWNER TO "nj_public";
COMMENT ON COLUMN "q_form_model"."form_type" IS '2 二维表 3 同比分析 4 环比分析 5 交叉制表';
CREATE TABLE "q_form_param" (
  "form_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "param_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "compare_type" char(1) COLLATE "pg_catalog"."default",
  "param_default_value" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_form_param" OWNER TO "nj_public";
COMMENT ON COLUMN "q_form_param"."compare_type" IS '1 同比 2 环比';
COMMENT ON COLUMN "q_form_param"."param_default_value" IS '参数默认值';
COMMENT ON TABLE "q_form_param" IS '报表参数 和 数据包一样，这里只是自定那些是 用于 同比 和 环比 的时间参数';
CREATE TABLE "q_query_column" (
  "model_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "col_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "show_type" char(1) COLLATE "pg_catalog"."default",
  "opt_type" char(1) COLLATE "pg_catalog"."default",
  "draw_chart" char(1) COLLATE "pg_catalog"."default",
  "col_type" char(1) COLLATE "pg_catalog"."default",
  "col_logic" varchar(120) COLLATE "pg_catalog"."default",
  "col_order" numeric(2),
  "is_show" char(1) COLLATE "pg_catalog"."default",
  "col_format" varchar(64) COLLATE "pg_catalog"."default",
  "link_type" varchar(32) COLLATE "pg_catalog"."default",
  "default_value" varchar(64) COLLATE "pg_catalog"."default",
  "catalog_code" varchar(64) COLLATE "pg_catalog"."default",
  "css_style" varchar(120) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_query_column" OWNER TO "nj_public";
COMMENT ON COLUMN "q_query_column"."show_type" IS 'R 行头  C 列头  D 数值';
COMMENT ON COLUMN "q_query_column"."opt_type" IS '0 : 不做 1 求和 2 求平均值 3 求和 求平均值';
COMMENT ON COLUMN "q_query_column"."draw_chart" IS ' T 画统计图 F 不画';
CREATE TABLE "q_query_condition" (
  "model_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "cond_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "cond_label" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "cond_display_style" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "param_type" varchar(64) COLLATE "pg_catalog"."default",
  "compare_type" char(1) COLLATE "pg_catalog"."default",
  "param_reference_type" char(1) COLLATE "pg_catalog"."default",
  "param_reference_data" varchar(1000) COLLATE "pg_catalog"."default",
  "param_validate_regex" varchar(200) COLLATE "pg_catalog"."default",
  "param_validate_info" varchar(200) COLLATE "pg_catalog"."default",
  "param_default_value" varchar(200) COLLATE "pg_catalog"."default",
  "cond_order" numeric(2)
)
;
ALTER TABLE "q_query_condition" OWNER TO "nj_public";
COMMENT ON COLUMN "q_query_condition"."cond_display_style" IS 'N:普通 nomal H 隐藏 hide R 只读 readonly';
COMMENT ON COLUMN "q_query_condition"."param_type" IS 'S:文本 N数字  D：日期 T：时间戳（datetime)  ';
COMMENT ON COLUMN "q_query_condition"."compare_type" IS '必需是时间字段， 3 同比分析  4 环比分析 0 其他';
COMMENT ON COLUMN "q_query_condition"."param_reference_type" IS '0：没有：1： 数据字典 2：JSON表达式 3：sql语句  Y：年份 M：月份';
COMMENT ON COLUMN "q_query_condition"."param_reference_data" IS '根据paramReferenceType类型（1,2,3）填写对应值';
COMMENT ON COLUMN "q_query_condition"."param_validate_regex" IS 'regex表达式';
COMMENT ON COLUMN "q_query_condition"."param_validate_info" IS '约束不通过提示信息';
COMMENT ON COLUMN "q_query_condition"."param_default_value" IS '参数默认值';
CREATE TABLE "q_query_model" (
  "model_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "model_type" char(1) COLLATE "pg_catalog"."default",
  "owner_type" char(1) COLLATE "pg_catalog"."default",
  "owner_code" varchar(64) COLLATE "pg_catalog"."default",
  "query_sql" varchar(4000) COLLATE "pg_catalog"."default",
  "query_desc" varchar(512) COLLATE "pg_catalog"."default",
  "form_name_format" varchar(256) COLLATE "pg_catalog"."default",
  "result_name" varchar(64) COLLATE "pg_catalog"."default",
  "row_draw_chart" char(1) COLLATE "pg_catalog"."default",
  "draw_chart_begin_col" numeric(4),
  "draw_chart_end_col" numeric(4),
  "addition_row" char(1) COLLATE "pg_catalog"."default",
  "row_logic" varchar(64) COLLATE "pg_catalog"."default",
  "row_logic_value" numeric(4),
  "logic_url" varchar(512) COLLATE "pg_catalog"."default",
  "column_sql" varchar(2048) COLLATE "pg_catalog"."default",
  "is_tree" varchar(8) COLLATE "pg_catalog"."default",
  "has_sum" varchar(8) COLLATE "pg_catalog"."default",
  "wizard_no" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_query_model" OWNER TO "nj_public";
COMMENT ON COLUMN "q_query_model"."model_type" IS '2 ： 二维表  3 ：同比分析 4：环比分析 5：交叉制表';
COMMENT ON COLUMN "q_query_model"."owner_type" IS '机构或者个人';
COMMENT ON COLUMN "q_query_model"."row_draw_chart" IS '是否按行 T 画统计图 F 不画';
COMMENT ON COLUMN "q_query_model"."addition_row" IS '0 : 没有  1 合计  2 均值  3 合计 和 均值';
COMMENT ON COLUMN "q_query_model"."wizard_no" IS '如果不为空说明是通过查询向导生成的';
CREATE TABLE "q_report_model" (
  "report_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "packet_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "report_name_format" varchar(500) COLLATE "pg_catalog"."default",
  "report_doc_fileid" varchar(64) COLLATE "pg_catalog"."default",
  "recorder" varchar(8) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "application_id" varchar(64) COLLATE "pg_catalog"."default",
  "photo_js" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_report_model" OWNER TO "nj_public";
CREATE TABLE "q_report_sql" (
  "sql_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "model_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_sql_id" varchar(32) COLLATE "pg_catalog"."default",
  "property_name" varchar(64) COLLATE "pg_catalog"."default",
  "database_code" varchar(32) COLLATE "pg_catalog"."default",
  "query_sql" varchar(4000) COLLATE "pg_catalog"."default",
  "query_desc" varchar(512) COLLATE "pg_catalog"."default",
  "query_type" char(1) COLLATE "pg_catalog"."default",
  "creator" varchar(32) COLLATE "pg_catalog"."default",
  "creat_time" timestamp(6)
)
;
ALTER TABLE "q_report_sql" OWNER TO "nj_public";
COMMENT ON COLUMN "q_report_sql"."query_type" IS 'S: 只有一个值 V：向量只有一行 T 表格';
COMMENT ON TABLE "q_report_sql" IS '这个制作 列表查询，没有统计语句';
CREATE TABLE "q_report_sql_column" (
  "sql_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "col_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "col_type" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_report_sql_column" OWNER TO "nj_public";
COMMENT ON TABLE "q_report_sql_column" IS '这个表不需要维护，它 通过sql 语句自动生成 作用是作为 维护模板是做参考';
CREATE TABLE "q_report_sql_param" (
  "sql_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "param_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "map_parent_field" varchar(64) COLLATE "pg_catalog"."default",
  "param_default_value" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "q_report_sql_param" OWNER TO "nj_public";
COMMENT ON COLUMN "q_report_sql_param"."map_parent_field" IS '只有这个查询是子查询才有效';
COMMENT ON COLUMN "q_report_sql_param"."param_default_value" IS '参数默认值';
CREATE TABLE "wf_action_log" (
  "action_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "node_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "action_type" varchar(2) COLLATE "pg_catalog"."default" NOT NULL,
  "action_time" timestamp(6) NOT NULL,
  "user_code" varchar(8) COLLATE "pg_catalog"."default",
  "role_type" varchar(8) COLLATE "pg_catalog"."default",
  "role_code" varchar(32) COLLATE "pg_catalog"."default",
  "grantor" varchar(8) COLLATE "pg_catalog"."default",
  "log_detail" varchar(500) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_action_log" OWNER TO "nj_public";
CREATE TABLE "wf_action_task" (
  "task_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "node_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "assign_time" timestamp(6) NOT NULL,
  "user_code" varchar(8) COLLATE "pg_catalog"."default",
  "task_state" char(1) COLLATE "pg_catalog"."default",
  "auth_desc" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_action_task" OWNER TO "nj_public";
CREATE TABLE "wf_flow_define" (
  "flow_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "version" numeric(4) NOT NULL,
  "flow_name" varchar(120) COLLATE "pg_catalog"."default",
  "flow_class" varchar(4) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_publish_date" timestamp(6),
  "flow_state" char(1) COLLATE "pg_catalog"."default",
  "flow_desc" varchar(500) COLLATE "pg_catalog"."default",
  "flow_xml_desc" text COLLATE "pg_catalog"."default",
  "time_limit" varchar(20) COLLATE "pg_catalog"."default",
  "expire_opt" char(1) COLLATE "pg_catalog"."default",
  "opt_id" varchar(32) COLLATE "pg_catalog"."default",
  "at_publish_date" timestamp(6),
  "os_id" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_define" OWNER TO "nj_public";
CREATE TABLE "wf_flow_instance" (
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "version" numeric(4),
  "flow_code" varchar(32) COLLATE "pg_catalog"."default",
  "flow_opt_name" varchar(800) COLLATE "pg_catalog"."default",
  "flow_opt_tag" varchar(200) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL,
  "is_timer" char(1) COLLATE "pg_catalog"."default",
  "promise_time" numeric(10),
  "time_limit" numeric(10),
  "last_update_user" varchar(8) COLLATE "pg_catalog"."default",
  "last_update_time" timestamp(6),
  "inst_state" char(1) COLLATE "pg_catalog"."default",
  "is_sub_inst" char(1) COLLATE "pg_catalog"."default",
  "pre_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "pre_node_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "unit_code" varchar(8) COLLATE "pg_catalog"."default",
  "user_code" varchar(8) COLLATE "pg_catalog"."default",
  "flow_group_id" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_instance" OWNER TO "nj_public";
CREATE TABLE "wf_flow_instance_group" (
  "flow_group_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_group_name" varchar(200) COLLATE "pg_catalog"."default",
  "flow_group_desc" varchar(1000) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_instance_group" OWNER TO "nj_public";
CREATE TABLE "wf_flow_role" (
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_name" varchar(100) COLLATE "pg_catalog"."default",
  "role_state" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_role" OWNER TO "nj_public";
CREATE TABLE "wf_flow_role_define" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "related_type" varchar(100) COLLATE "pg_catalog"."default",
  "related_code" varchar(100) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_role_define" OWNER TO "nj_public";
CREATE TABLE "wf_flow_stage" (
  "stage_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "version" numeric(4),
  "flow_code" varchar(32) COLLATE "pg_catalog"."default",
  "stage_code" varchar(32) COLLATE "pg_catalog"."default",
  "stage_name" varchar(60) COLLATE "pg_catalog"."default",
  "is_account_time" char(1) COLLATE "pg_catalog"."default",
  "limit_type" char(1) COLLATE "pg_catalog"."default",
  "time_limit" varchar(20) COLLATE "pg_catalog"."default",
  "expire_opt" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_stage" OWNER TO "nj_public";
CREATE TABLE "wf_flow_team_role" (
  "flow_team_role_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "role_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "team_role_order" int2,
  "create_time" timestamp(6) NOT NULL,
  "modify_time" timestamp(6) NOT NULL,
  "version" numeric(4),
  "formula_code" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_team_role" OWNER TO "nj_public";
CREATE TABLE "wf_flow_variable" (
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "run_token" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "var_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "var_value" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "var_type" char(1) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "wf_flow_variable" OWNER TO "nj_public";
CREATE TABLE "wf_flow_variable_define" (
  "flow_variable_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "variable_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "variable_type" varchar(100) COLLATE "pg_catalog"."default",
  "modify_time" timestamp(6) NOT NULL,
  "version" numeric(4),
  "default_value" varchar(256) COLLATE "pg_catalog"."default",
  "variable_desc" varchar(100) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_flow_variable_define" OWNER TO "nj_public";
CREATE TABLE "wf_inst_attention" (
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "user_code" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "att_set_time" timestamp(6),
  "att_set_user" varchar(8) COLLATE "pg_catalog"."default",
  "att_set_memo" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_inst_attention" OWNER TO "nj_public";
CREATE TABLE "wf_node" (
  "node_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_code" varchar(32) COLLATE "pg_catalog"."default",
  "version" numeric(4),
  "node_type" varchar(1) COLLATE "pg_catalog"."default" NOT NULL,
  "node_name" varchar(120) COLLATE "pg_catalog"."default",
  "opt_type" varchar(1) COLLATE "pg_catalog"."default",
  "os_id" varchar(32) COLLATE "pg_catalog"."default",
  "opt_id" varchar(32) COLLATE "pg_catalog"."default",
  "opt_code" varchar(64) COLLATE "pg_catalog"."default",
  "opt_bean" varchar(100) COLLATE "pg_catalog"."default",
  "opt_param" varchar(100) COLLATE "pg_catalog"."default",
  "sub_flow_code" varchar(32) COLLATE "pg_catalog"."default",
  "router_type" varchar(1) COLLATE "pg_catalog"."default",
  "role_type" varchar(8) COLLATE "pg_catalog"."default",
  "role_code" varchar(32) COLLATE "pg_catalog"."default",
  "unit_exp" varchar(64) COLLATE "pg_catalog"."default",
  "power_exp" varchar(512) COLLATE "pg_catalog"."default",
  "multi_inst_type" char(1) COLLATE "pg_catalog"."default",
  "multi_inst_param" varchar(512) COLLATE "pg_catalog"."default",
  "converge_type" char(1) COLLATE "pg_catalog"."default",
  "converge_param" varchar(64) COLLATE "pg_catalog"."default",
  "node_desc" varchar(500) COLLATE "pg_catalog"."default",
  "is_account_time" char(1) COLLATE "pg_catalog"."default",
  "limit_type" char(1) COLLATE "pg_catalog"."default",
  "time_limit" varchar(20) COLLATE "pg_catalog"."default",
  "inherit_type" char(1) COLLATE "pg_catalog"."default",
  "inherit_node_code" varchar(20) COLLATE "pg_catalog"."default",
  "expire_opt" char(1) COLLATE "pg_catalog"."default",
  "warning_rule" char(1) COLLATE "pg_catalog"."default",
  "warning_param" varchar(20) COLLATE "pg_catalog"."default",
  "is_trunk_line" char(1) COLLATE "pg_catalog"."default",
  "stage_code" varchar(32) COLLATE "pg_catalog"."default",
  "node_code" varchar(20) COLLATE "pg_catalog"."default",
  "risk_info" varchar(4) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_node" OWNER TO "nj_public";
CREATE TABLE "wf_node_instance" (
  "node_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "node_id" varchar(32) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "start_time" timestamp(6),
  "is_timer" char(1) COLLATE "pg_catalog"."default",
  "promise_time" numeric(10),
  "time_limit" numeric(10),
  "prev_node_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "node_state" varchar(2) COLLATE "pg_catalog"."default",
  "sub_flow_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "unit_code" varchar(8) COLLATE "pg_catalog"."default",
  "stage_code" varchar(32) COLLATE "pg_catalog"."default",
  "role_type" varchar(8) COLLATE "pg_catalog"."default",
  "role_code" varchar(32) COLLATE "pg_catalog"."default",
  "user_code" varchar(8) COLLATE "pg_catalog"."default",
  "node_param" varchar(128) COLLATE "pg_catalog"."default",
  "task_assigned" varchar(1) COLLATE "pg_catalog"."default",
  "run_token" varchar(20) COLLATE "pg_catalog"."default",
  "grantor" varchar(8) COLLATE "pg_catalog"."default",
  "last_update_user" varchar(8) COLLATE "pg_catalog"."default",
  "last_update_time" timestamp(6),
  "trans_path" varchar(256) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_node_instance" OWNER TO "nj_public";
CREATE TABLE "wf_optinfo" (
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_url" varchar(500) COLLATE "pg_catalog"."default",
  "opt_view_url" varchar(500) COLLATE "pg_catalog"."default",
  "update_date" date,
  "model_id" varchar(64) COLLATE "pg_catalog"."default",
  "title_template" varchar(500) COLLATE "pg_catalog"."default",
  "default_flow" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_optinfo" OWNER TO "nj_public";
CREATE TABLE "wf_optpage" (
  "opt_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "opt_method" varchar(50) COLLATE "pg_catalog"."default",
  "update_date" date,
  "page_url" varchar(500) COLLATE "pg_catalog"."default",
  "page_type" char(1) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_optpage" OWNER TO "nj_public";
CREATE TABLE "wf_organize" (
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "unit_code" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "unit_order" numeric(4),
  "auth_desc" varchar(255) COLLATE "pg_catalog"."default",
  "auth_time" timestamp(6) NOT NULL
)
;
ALTER TABLE "wf_organize" OWNER TO "nj_public";
CREATE TABLE "wf_role_formula" (
  "formula_code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "formula_name" varchar(200) COLLATE "pg_catalog"."default",
  "role_formula" varchar(500) COLLATE "pg_catalog"."default",
  "role_level" varchar(20) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6)
)
;
ALTER TABLE "wf_role_formula" OWNER TO "nj_public";
CREATE TABLE "wf_role_relegate" (
  "relegate_no" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "grantor" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "grantee" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "is_valid" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "recorder" varchar(8) COLLATE "pg_catalog"."default",
  "relegate_time" timestamp(6) NOT NULL,
  "expire_time" timestamp(6),
  "unit_code" varchar(8) COLLATE "pg_catalog"."default",
  "role_type" varchar(8) COLLATE "pg_catalog"."default",
  "role_code" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "grant_desc" varchar(256) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_role_relegate" OWNER TO "nj_public";
CREATE TABLE "wf_runtime_warning" (
  "warning_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default",
  "node_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "flow_stage" varchar(32) COLLATE "pg_catalog"."default",
  "obj_type" char(1) COLLATE "pg_catalog"."default",
  "warning_type" char(1) COLLATE "pg_catalog"."default",
  "warning_state" char(1) COLLATE "pg_catalog"."default",
  "warning_code" varchar(16) COLLATE "pg_catalog"."default",
  "warning_time" timestamp(6),
  "warningid_msg" varchar(500) COLLATE "pg_catalog"."default",
  "notice_state" char(1) COLLATE "pg_catalog"."default",
  "send_msg_time" timestamp(6),
  "send_users" varchar(100) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "wf_runtime_warning" OWNER TO "nj_public";
CREATE TABLE "wf_stage_instance" (
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "stage_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "stage_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "begin_time" timestamp(6),
  "stage_begin" char(1) COLLATE "pg_catalog"."default",
  "promise_time" numeric(10),
  "time_limit" numeric(10),
  "last_update_time" timestamp(6)
)
;
ALTER TABLE "wf_stage_instance" OWNER TO "nj_public";
CREATE TABLE "wf_team" (
  "flow_inst_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "user_code" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "user_order" numeric(4),
  "auth_desc" varchar(255) COLLATE "pg_catalog"."default",
  "auth_time" timestamp(6) NOT NULL
)
;
ALTER TABLE "wf_team" OWNER TO "nj_public";
CREATE TABLE "wf_transition" (
  "trans_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "version" numeric(4),
  "flow_code" varchar(32) COLLATE "pg_catalog"."default",
  "trans_class" varchar(4) COLLATE "pg_catalog"."default",
  "trans_name" varchar(120) COLLATE "pg_catalog"."default",
  "start_node_id" varchar(32) COLLATE "pg_catalog"."default",
  "end_node_id" varchar(32) COLLATE "pg_catalog"."default",
  "trans_condition" varchar(500) COLLATE "pg_catalog"."default",
  "trans_desc" varchar(500) COLLATE "pg_catalog"."default",
  "is_account_time" char(1) COLLATE "pg_catalog"."default",
  "limit_type" char(1) COLLATE "pg_catalog"."default",
  "time_limit" varchar(20) COLLATE "pg_catalog"."default",
  "can_ignore" char(1) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "wf_transition" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_lastversionflow                                    */
/*==============================================================*/
CREATE VIEW "f_v_lastversionflow" AS  SELECT a.flow_code,
    b.version,
    a.flow_name,
    a.flow_class,
    b.flow_state,
    a.flow_desc,
    a.flow_xml_desc,
    a.time_limit,
    a.expire_opt,
    a.opt_id,
    a.os_id,
    a.flow_publish_date,
    a.at_publish_date
   FROM ((( SELECT wf_flow_define.flow_code,
            max(wf_flow_define.version) AS version
           FROM wf_flow_define
          GROUP BY wf_flow_define.flow_code) lastversion
     JOIN wf_flow_define a ON ((((a.flow_code)::text = (lastversion.flow_code)::text) AND (a.version = (0)::numeric))))
     JOIN wf_flow_define b ON ((((lastversion.flow_code)::text = (b.flow_code)::text) AND (lastversion.version = b.version))));
ALTER TABLE "f_v_lastversionflow" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_opt_role_map                                       */
/*==============================================================*/
CREATE VIEW "f_v_opt_role_map" AS  SELECT concat(c.opt_url, b.opt_url) AS opt_url,
    b.opt_req,
    a.role_code,
    c.opt_id,
    b.opt_code
   FROM ((f_rolepower a
     JOIN f_optdef b ON (((a.opt_code)::text = (b.opt_code)::text)))
     JOIN f_optinfo c ON (((b.opt_id)::text = (c.opt_id)::text)))
  WHERE ((c.opt_type <> 'W'::bpchar) AND ((c.opt_url)::text <> '...'::text))
  ORDER BY c.opt_url, b.opt_req, a.role_code;
ALTER TABLE "f_v_opt_role_map" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_optdef_url_map                                     */
/*==============================================================*/
CREATE VIEW "f_v_optdef_url_map" AS  SELECT concat(c.opt_url, b.opt_url) AS opt_def_url,
    b.opt_req,
    b.opt_code
   FROM (f_optdef b
     JOIN f_optinfo c ON (((b.opt_id)::text = (c.opt_id)::text)))
  WHERE ((c.opt_type <> 'W'::bpchar) AND ((c.opt_url)::text <> '...'::text) AND (b.opt_req IS NOT NULL));
ALTER TABLE "f_v_optdef_url_map" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_userroles                                          */
/*==============================================================*/
CREATE VIEW "f_v_userroles" AS  SELECT b.role_code,
    b.role_name,
    b.is_valid,
    'D'::text AS obtain_type,
    b.role_type,
    b.unit_code,
    b.role_desc,
    b.create_date,
    b.update_date,
    a.user_code,
    NULL::character varying AS inherited_from
   FROM (f_userrole a
     JOIN f_roleinfo b ON (((a.role_code)::text = (b.role_code)::text)))
  WHERE ((a.obtain_date <= now()) AND ((a.secede_date IS NULL) OR (a.secede_date > now())) AND (b.is_valid = 'T'::bpchar))
UNION
 SELECT b.role_code,
    b.role_name,
    b.is_valid,
    'I'::text AS obtain_type,
    b.role_type,
    b.unit_code,
    b.role_desc,
    b.create_date,
    b.update_date,
    c.user_code,
    a.unit_code AS inherited_from
   FROM ((f_unitrole a
     JOIN f_roleinfo b ON (((a.role_code)::text = (b.role_code)::text)))
     JOIN f_userunit c ON (((a.unit_code)::text = (c.unit_code)::text)))
  WHERE ((a.obtain_date <= now()) AND ((a.secede_date IS NULL) OR (a.secede_date > now())) AND (b.is_valid = 'T'::bpchar))
UNION
 SELECT 'public'::character varying(32) AS role_code,
    '公共角色'::character varying(64) AS role_name,
    'T'::character(1) AS is_valid,
    'D'::text AS obtain_type,
    'P'::character(1) AS role_type,
    NULL::character varying(32) AS unit_code,
    '公共角色'::character varying(256) AS role_desc,
    NULL::date AS create_date,
    NULL::date AS update_date,
    a.user_code,
    NULL::character varying AS inherited_from
   FROM f_userinfo a;
ALTER TABLE "f_v_userroles" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_useroptdatascopes                                  */
/*==============================================================*/
 CREATE VIEW f_v_useroptdatascopes as
    SELECT DISTINCT a.user_code,
    c.opt_id,
    c.opt_method,
    b.opt_scope_codes
   FROM ((f_v_userroles a
     JOIN f_rolepower b ON (((a.role_code)::text = (b.role_code)::text)))
     JOIN f_optdef c ON (((b.opt_code)::text = (c.opt_code)::text)));
ALTER TABLE "f_v_useroptdatascopes" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_useroptlist                                        */
/*==============================================================*/
 CREATE VIEW f_v_useroptlist as
 SELECT DISTINCT a.user_code,
    c.opt_code,
    c.opt_name,
    c.opt_id,
    c.opt_method
   FROM ((f_v_userroles a
     JOIN f_rolepower b ON (((a.role_code)::text = (b.role_code)::text)))
     JOIN f_optdef c ON (((b.opt_code)::text = (c.opt_code)::text)));
ALTER TABLE "f_v_useroptlist" OWNER TO "nj_public";
/*==============================================================*/
/* View: f_v_useroptmoudlelist                                  */
/*==============================================================*/
CREATE VIEW f_v_useroptmoudlelist as
 SELECT DISTINCT a.user_code,
    d.opt_id,
    d.opt_name,
    d.pre_opt_id,
    d.form_code,
    d.opt_url,
    d.opt_route,
    d.msg_no,
    d.msg_prm,
    d.is_in_toolbar,
    d.img_index,
    d.top_opt_id,
    d.order_ind,
    d.page_type,
    d.opt_type
   FROM (((f_v_userroles a
     JOIN f_rolepower b ON (((a.role_code)::text = (b.role_code)::text)))
     JOIN f_optdef c ON (((b.opt_code)::text = (c.opt_code)::text)))
     JOIN f_optinfo d ON (((c.opt_id)::text = (d.opt_id)::text)))
  WHERE ((d.opt_url)::text <> '...'::text) ;
ALTER TABLE "f_v_useroptmoudlelist" OWNER TO "nj_public";
/*==============================================================*/
/* View: v_hi_unitinfo                                          */
/*==============================================================*/
CREATE VIEW "v_hi_unitinfo" AS  SELECT a.unit_code AS top_unit_code,
    b.unit_code,
    b.unit_type,
    b.parent_unit,
    b.is_valid,
    b.unit_name,
    b.unit_desc,
    b.unit_short_name,
    b.unit_order,
    b.dep_no,
    b.unit_word,
    b.unit_grade,
    ((((length((b.unit_path)::text) - length(replace((b.unit_path)::text, '/'::text, ''::text))) - length((a.unit_path)::text)) + length(replace((a.unit_path)::text, '/'::text, ''::text))) + 1) AS hi_level,
    substr((b.unit_path)::text, (length((a.unit_path)::text) + 1)) AS unit_path
   FROM f_unitinfo a,
    f_unitinfo b
  WHERE ((b.unit_path)::text ~~ concat(a.unit_path, '%'));
ALTER TABLE "v_hi_unitinfo" OWNER TO "nj_public";
/*==============================================================*/
/* View: v_inner_user_task_list                                 */
/*==============================================================*/
CREATE VIEW "v_inner_user_task_list" AS  SELECT a.flow_inst_id,
    w.flow_code,
    w.version,
    w.flow_opt_name,
    w.flow_opt_tag,
    a.node_inst_id,
    COALESCE(a.unit_code, COALESCE(w.unit_code, '0000000'::character varying)) AS unit_code,
    a.user_code,
    c.role_type,
    c.role_code,
    '系统指定'::character varying AS auth_desc,
    c.node_code,
    c.node_name,
    c.node_type,
    c.opt_type AS node_opt_type,
    c.opt_param,
    a.create_time,
    a.promise_time,
    a.time_limit,
    c.opt_code,
    c.expire_opt,
    c.stage_code,
    a.last_update_user,
    a.last_update_time,
    w.inst_state,
    c.os_id,
    a.node_param
   FROM ((wf_node_instance a
     JOIN wf_flow_instance w ON (((a.flow_inst_id)::text = (w.flow_inst_id)::text)))
     JOIN wf_node c ON (((a.node_id)::text = (c.node_id)::text)))
  WHERE (((a.node_state)::text = 'N'::text) AND (w.inst_state = 'N'::bpchar) AND ((a.task_assigned)::text = 'S'::text))
UNION ALL
 SELECT a.flow_inst_id,
    w.flow_code,
    w.version,
    w.flow_opt_name,
    w.flow_opt_tag,
    a.node_inst_id,
    COALESCE(a.unit_code, COALESCE(w.unit_code, '0000000'::character varying)) AS unit_code,
    b.user_code,
    ''::character varying AS role_type,
    ''::character varying AS role_code,
    b.auth_desc,
    c.node_code,
    c.node_name,
    c.node_type,
    c.opt_type AS node_opt_type,
    c.opt_param,
    a.create_time,
    a.promise_time,
    a.time_limit,
    c.opt_code,
    c.expire_opt,
    c.stage_code,
    a.last_update_user,
    a.last_update_time,
    w.inst_state,
    c.os_id,
    a.node_param
   FROM (((wf_node_instance a
     JOIN wf_flow_instance w ON (((a.flow_inst_id)::text = (w.flow_inst_id)::text)))
     JOIN wf_action_task b ON (((a.node_inst_id)::text = (b.node_inst_id)::text)))
     JOIN wf_node c ON (((a.node_id)::text = (c.node_id)::text)))
  WHERE (((a.node_state)::text = 'N'::text) AND (w.inst_state = 'N'::bpchar) AND ((a.task_assigned)::text = 'T'::text) AND (b.task_state = 'A'::bpchar));
ALTER TABLE "v_inner_user_task_list" OWNER TO "nj_public";
/*==============================================================*/
/* View: v_opt_tree                                             */
/*==============================================================*/
CREATE VIEW "v_opt_tree" AS  SELECT i.opt_id AS menu_id,
    i.pre_opt_id AS parent_id,
    i.opt_name AS menu_name,
    i.order_ind
   FROM f_optinfo i
  WHERE (i.is_in_toolbar = 'Y'::bpchar)
UNION ALL
 SELECT d.opt_code AS menu_id,
    d.opt_id AS parent_id,
    d.opt_name AS menu_name,
    0 AS order_ind
   FROM f_optdef d;
ALTER TABLE "v_opt_tree" OWNER TO "nj_public";
/*==============================================================*/
/* View: v_user_task_list                                       */
/*==============================================================*/
CREATE VIEW "v_user_task_list" AS  SELECT a.flow_inst_id,
    a.flow_code,
    a.version,
    a.flow_opt_name,
    a.flow_opt_tag,
    a.node_inst_id,
    a.unit_code,
    a.user_code,
    a.role_type,
    a.role_code,
    a.auth_desc,
    a.node_code,
    a.node_name,
    a.node_type,
    a.node_opt_type,
    a.opt_param,
    a.create_time,
    a.promise_time,
    a.time_limit,
    a.opt_code,
    a.expire_opt,
    a.stage_code,
    ''::character varying AS grantor,
    a.last_update_user,
    a.last_update_time,
    a.inst_state,
    a.opt_code AS opt_url,
    a.os_id,
    a.node_param
   FROM v_inner_user_task_list a
UNION
 SELECT a.flow_inst_id,
    a.flow_code,
    a.version,
    a.flow_opt_name,
    a.flow_opt_tag,
    a.node_inst_id,
    a.unit_code,
    a.user_code,
    a.role_type,
    a.role_code,
    a.auth_desc,
    a.node_code,
    a.node_name,
    a.node_type,
    a.node_opt_type,
    a.opt_param,
    a.create_time,
    a.promise_time,
    a.time_limit,
    a.opt_code,
    a.expire_opt,
    a.stage_code,
    b.grantor,
    a.last_update_user,
    a.last_update_time,
    a.inst_state,
    a.opt_code AS opt_url,
    a.os_id,
    a.node_param
   FROM (v_inner_user_task_list a
     JOIN wf_role_relegate b ON (((b.unit_code)::text = (a.unit_code)::text)))
  WHERE ((b.is_valid = 'T'::bpchar) AND (b.relegate_time <= now()) AND ((a.user_code)::text = (b.grantor)::text) AND (b.expire_time >= now()));
ALTER TABLE "v_user_task_list" OWNER TO "nj_public";

/*==============================================================*/
/* Postgresql 数据库脚本                                        */
/* nj_public                                                     */
/*==============================================================*/

DROP SEQUENCE IF EXISTS "public"."msg_id_seq";
DROP TABLE IF EXISTS "public"."answer_caliber";
DROP TABLE IF EXISTS "public"."assgin_feedback";
DROP TABLE IF EXISTS "public"."attach_file";
DROP TABLE IF EXISTS "public"."clue";
DROP TABLE IF EXISTS "public"."clue_assgin_staff";
DROP TABLE IF EXISTS "public"."clue_assign";
DROP TABLE IF EXISTS "public"."clue_report";
DROP TABLE IF EXISTS "public"."clue_staff";
DROP TABLE IF EXISTS "public"."control_info";
DROP TABLE IF EXISTS "public"."group_sign";
DROP TABLE IF EXISTS "public"."key_staff";
DROP TABLE IF EXISTS "public"."report_staff";
DROP TABLE IF EXISTS "public"."shift_duty";
DROP TABLE IF EXISTS "public"."special_report";
DROP TABLE IF EXISTS "public"."virtual_info";
DROP TABLE IF EXISTS "public"."wait_msg";
DROP TABLE IF EXISTS "public"."white_list";
DROP VIEW IF EXISTS "public"."v_assign_similar";
DROP VIEW IF EXISTS "public"."v_clue_assign";
DROP VIEW IF EXISTS "public"."v_down_assign";
DROP VIEW IF EXISTS "public"."v_feedback_change";
DROP VIEW IF EXISTS "public"."v_feedback_last";
DROP VIEW IF EXISTS "public"."v_feedback_last_finished";
DROP VIEW IF EXISTS "public"."v_feedback_limit";
DROP VIEW IF EXISTS "public"."v_feedback_outtime";
DROP VIEW IF EXISTS "public"."v_history_assign_staff";
DROP VIEW IF EXISTS "public"."v_last_feedback";
DROP VIEW IF EXISTS "public"."v_last_finished_feedback";
DROP VIEW IF EXISTS "public"."v_lastest_assign_feeback_staff";
DROP VIEW IF EXISTS "public"."v_lastest_assign_feeback_staff_finished";
DROP VIEW IF EXISTS "public"."v_unit_newterm";
DROP VIEW IF EXISTS "public"."v_up_assign";

CREATE SEQUENCE "msg_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
CREATE TABLE "public"."answer_caliber" (
                                     "answer_content" varchar(2000) COLLATE "pg_catalog"."default",
                                     "answer_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
                                     "answer_titile" varchar(200) COLLATE "pg_catalog"."default",
                                     "begin_date" timestamp(6),
                                     "end_date" timestamp(6),
                                     "model_type" varchar(32) COLLATE "pg_catalog"."default",
                                     "reg_date" timestamp(6),
                                     "reg_dept" varchar(200) COLLATE "pg_catalog"."default",
                                     "reg_idcard" varchar(32) COLLATE "pg_catalog"."default",
                                     "reg_user" varchar(32) COLLATE "pg_catalog"."default",
                                     "update_date" timestamp(6)
)
;
ALTER TABLE "answer_caliber" OWNER TO "nj_public";
CREATE TABLE "assgin_feedback" (
  "feedback_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "assign_staff_id" varchar(32) COLLATE "pg_catalog"."default",
  "assign_id" varchar(32) COLLATE "pg_catalog"."default",
  "staff_id" varchar(32) COLLATE "pg_catalog"."default",
  "clue_id" varchar(32) COLLATE "pg_catalog"."default",
  "aim" varchar(32) COLLATE "pg_catalog"."default",
  "involved_behavior" varchar(32) COLLATE "pg_catalog"."default",
  "concrete_behavior" varchar(32) COLLATE "pg_catalog"."default",
  "work_manner" varchar(32) COLLATE "pg_catalog"."default",
  "work_feedback" varchar(2000) COLLATE "pg_catalog"."default",
  "stedy_state" varchar(32) COLLATE "pg_catalog"."default",
  "group_name" varchar(32) COLLATE "pg_catalog"."default",
  "link_way" varchar(32) COLLATE "pg_catalog"."default",
  "group_property" varchar(32) COLLATE "pg_catalog"."default",
  "duty_police" varchar(32) COLLATE "pg_catalog"."default",
  "check_policeman" varchar(64) COLLATE "pg_catalog"."default",
  "policeman_link" varchar(32) COLLATE "pg_catalog"."default",
  "is_core" varchar(64) COLLATE "pg_catalog"."default",
  "is_white" varchar(64) COLLATE "pg_catalog"."default",
  "assess_state" varchar(32) COLLATE "pg_catalog"."default",
  "assess_content" varchar(2000) COLLATE "pg_catalog"."default",
  "feedback_state" varchar(32) COLLATE "pg_catalog"."default",
  "feedback_dept" varchar(32) COLLATE "pg_catalog"."default",
  "feedback_date" timestamp(6),
  "feedback_previd" varchar(32) COLLATE "pg_catalog"."default",
  "sys_date" timestamp(6) DEFAULT now()
)
;
ALTER TABLE "assgin_feedback" OWNER TO "nj_public";
CREATE TABLE "attach_file" (
  "all_id" varchar(32) COLLATE "pg_catalog"."default",
  "file_md5" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "file_name" varchar(200) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "attach_file" OWNER TO "nj_public";
CREATE TABLE "clue" (
  "add_class" varchar(16) COLLATE "pg_catalog"."default",
  "clue_content" text COLLATE "pg_catalog"."default",
  "clue_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "clue_title" varchar(200) COLLATE "pg_catalog"."default",
  "group_name" varchar(200) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6) DEFAULT now(),
  "record_dept" varchar(32) COLLATE "pg_catalog"."default",
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "source_mainclass" varchar(16) COLLATE "pg_catalog"."default",
  "source_subclass" varchar(16) COLLATE "pg_catalog"."default",
  "is_check" varchar(64) COLLATE "pg_catalog"."default",
  "send_dept" varchar(64) COLLATE "pg_catalog"."default",
  "send_user" varchar(64) COLLATE "pg_catalog"."default",
  "sys_date" timestamp(6) DEFAULT now(),
  "accept_state" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "clue" OWNER TO "nj_public";
CREATE TABLE "clue_assgin_staff" (
  "assign_staff_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "assign_id" varchar(32) COLLATE "pg_catalog"."default",
  "staff_id" varchar(32) COLLATE "pg_catalog"."default",
  "clue_id" varchar(32) COLLATE "pg_catalog"."default",
  "signin_dept" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "assign_state" varchar(6) COLLATE "pg_catalog"."default" DEFAULT 'F'::character varying,
  "assign_police" varchar(32) COLLATE "pg_catalog"."default",
  "signin_dept_code" varchar(32) COLLATE "pg_catalog"."default",
  "unit_code" varchar(32) COLLATE "pg_catalog"."default",
  "unit_name" varchar(200) COLLATE "pg_catalog"."default",
  "is_check" varchar(1) COLLATE "pg_catalog"."default",
  "check_user" varchar(32) COLLATE "pg_catalog"."default",
  "check_time" timestamp(6),
  "domicile" varchar(200) COLLATE "pg_catalog"."default",
  "domicile_place" varchar(500) COLLATE "pg_catalog"."default",
  "email" varchar(64) COLLATE "pg_catalog"."default",
  "foms" varchar(500) COLLATE "pg_catalog"."default",
  "id_card" varchar(32) COLLATE "pg_catalog"."default",
  "link_way" varchar(32) COLLATE "pg_catalog"."default",
  "memo1" varchar(500) COLLATE "pg_catalog"."default",
  "memo2" varchar(500) COLLATE "pg_catalog"."default",
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "plat_number" varchar(64) COLLATE "pg_catalog"."default",
  "qq_number" varchar(64) COLLATE "pg_catalog"."default",
  "residence" varchar(500) COLLATE "pg_catalog"."default",
  "under_group" varchar(64) COLLATE "pg_catalog"."default",
  "venue" varchar(64) COLLATE "pg_catalog"."default",
  "venue_add" varchar(64) COLLATE "pg_catalog"."default",
  "weixin" varchar(64) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6) DEFAULT now(),
  "record_dept" varchar(32) COLLATE "pg_catalog"."default",
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "aim_place" varchar(64) COLLATE "pg_catalog"."default",
  "sys_date" timestamp(6) DEFAULT now()
)
;
ALTER TABLE "clue_assgin_staff" OWNER TO "nj_public";
CREATE TABLE "clue_assign" (
  "assign_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "title" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "record_date" date NOT NULL,
  "term" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "fact_sheet" text COLLATE "pg_catalog"."default" NOT NULL,
  "job_require" text COLLATE "pg_catalog"."default",
  "accept_user" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "issue_user" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "assign_type" varchar(6) COLLATE "pg_catalog"."default" NOT NULL,
  "importance_level" varchar(6) COLLATE "pg_catalog"."default" NOT NULL,
  "mobile" varchar(32) COLLATE "pg_catalog"."default",
  "time_limit" timestamp(6),
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "is_detailed" char(1) COLLATE "pg_catalog"."default",
  "is_war" varchar(1) COLLATE "pg_catalog"."default" DEFAULT 'F'::character varying,
  "send_channel" varchar(6) COLLATE "pg_catalog"."default" DEFAULT 'ZYTZ'::character varying,
  "unit_code" varchar(32) COLLATE "pg_catalog"."default",
  "unit_name" varchar(200) COLLATE "pg_catalog"."default",
  "assign_previd" varchar(32) COLLATE "pg_catalog"."default",
  "assign_sourceid" varchar(32) COLLATE "pg_catalog"."default",
  "level_num" int4 DEFAULT 0,
  "sys_date" timestamp(0) DEFAULT now()
)
;
ALTER TABLE "clue_assign" OWNER TO "nj_public";
CREATE TABLE "clue_report" (
  "report_date" timestamp(6),
  "report_dept" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "report_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "clue_id" varchar(32) COLLATE "pg_catalog"."default",
  "is_share" varchar(64) COLLATE "pg_catalog"."default",
  "report_content" varchar(4000) COLLATE "pg_catalog"."default",
  "report_keys" varchar(200) COLLATE "pg_catalog"."default",
  "report_term" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "report_title" varchar(200) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "clue_report" OWNER TO "nj_public";
CREATE TABLE "clue_staff" (
  "assign_count" int4,
  "clue_id" varchar(32) COLLATE "pg_catalog"."default",
  "domicile" varchar(200) COLLATE "pg_catalog"."default",
  "domicile_place" varchar(500) COLLATE "pg_catalog"."default",
  "email" varchar(64) COLLATE "pg_catalog"."default",
  "foms" varchar(500) COLLATE "pg_catalog"."default",
  "id_card" varchar(32) COLLATE "pg_catalog"."default",
  "link_way" varchar(32) COLLATE "pg_catalog"."default",
  "memo1" varchar(500) COLLATE "pg_catalog"."default",
  "memo2" varchar(500) COLLATE "pg_catalog"."default",
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "plat_number" varchar(64) COLLATE "pg_catalog"."default",
  "qq_number" varchar(64) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6) DEFAULT now(),
  "record_dept" varchar(32) COLLATE "pg_catalog"."default",
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "residence" varchar(500) COLLATE "pg_catalog"."default",
  "staff_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "under_group" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "venue" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "venue_add" varchar(64) COLLATE "pg_catalog"."default",
  "weixin" varchar(64) COLLATE "pg_catalog"."default",
  "aim_place" varchar(64) COLLATE "pg_catalog"."default",
  "sys_date" timestamp(6) DEFAULT now()
)
;
ALTER TABLE "clue_staff" OWNER TO "nj_public";
CREATE TABLE "control_info" (
  "control_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "duty_office" varchar(200) COLLATE "pg_catalog"."default",
  "duty_police" varchar(200) COLLATE "pg_catalog"."default",
  "duty_policeman" varchar(200) COLLATE "pg_catalog"."default",
  "key_staff_id" varchar(32) COLLATE "pg_catalog"."default",
  "police_link" varchar(200) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6)
)
;
ALTER TABLE "control_info" OWNER TO "nj_public";
CREATE TABLE "group_sign" (
  "action_detail" varchar(200) COLLATE "pg_catalog"."default",
  "action_type" varchar(200) COLLATE "pg_catalog"."default",
  "group_detail" varchar(200) COLLATE "pg_catalog"."default",
  "group_name" varchar(200) COLLATE "pg_catalog"."default",
  "key_staff_id" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "sign_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "group_sign" OWNER TO "nj_public";
CREATE TABLE "key_staff" (
  "key_staff_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "id_card" varchar(32) COLLATE "pg_catalog"."default",
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "domicile_block" varchar(200) COLLATE "pg_catalog"."default",
  "domicile_police" varchar(200) COLLATE "pg_catalog"."default",
  "domicile_place" varchar(500) COLLATE "pg_catalog"."default",
  "residence_block" varchar(200) COLLATE "pg_catalog"."default",
  "residence_police" varchar(200) COLLATE "pg_catalog"."default",
  "residence_place" varchar(500) COLLATE "pg_catalog"."default",
  "under_group" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "data_source" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "clean_state" varchar(32) COLLATE "pg_catalog"."default",
  "memo" varchar(2000) COLLATE "pg_catalog"."default",
  "telephone" varchar(32) COLLATE "pg_catalog"."default",
  "age" varchar(32) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "sex" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "key_staff" OWNER TO "nj_public";
CREATE TABLE "report_staff" (
  "staff_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "report_id" varchar(32) COLLATE "pg_catalog"."default",
  "id_card" varchar(32) COLLATE "pg_catalog"."default",
  "link_way" varchar(32) COLLATE "pg_catalog"."default",
  "email" varchar(64) COLLATE "pg_catalog"."default",
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "weixin" varchar(64) COLLATE "pg_catalog"."default",
  "qq_number" varchar(64) COLLATE "pg_catalog"."default",
  "domicile" varchar(200) COLLATE "pg_catalog"."default",
  "venue" varchar(64) COLLATE "pg_catalog"."default",
  "group_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "capital_aim" varchar(32) COLLATE "pg_catalog"."default",
  "feedback_state" varchar(32) COLLATE "pg_catalog"."default",
  "involve_behavier" varchar(32) COLLATE "pg_catalog"."default",
  "work_type" varchar(32) COLLATE "pg_catalog"."default",
  "staff_property" varchar(32) COLLATE "pg_catalog"."default",
  "behavier_type" varchar(32) COLLATE "pg_catalog"."default",
  "group_type" varchar(32) COLLATE "pg_catalog"."default",
  "assign_term" varchar(100) COLLATE "pg_catalog"."default",
  "assign_title" varchar(200) COLLATE "pg_catalog"."default",
  "assign_source" varchar(32) COLLATE "pg_catalog"."default",
  "assign_leval" varchar(32) COLLATE "pg_catalog"."default",
  "assign_date" timestamp(6),
  "duty_dept" varchar(32) COLLATE "pg_catalog"."default",
  "capital_type" varchar(32) COLLATE "pg_catalog"."default",
  "provience_city" varchar(100) COLLATE "pg_catalog"."default",
  "check_police" varchar(100) COLLATE "pg_catalog"."default",
  "check_police_link" varchar(100) COLLATE "pg_catalog"."default",
  "aim_date" timestamp(6),
  "feedback_memo" varchar(2000) COLLATE "pg_catalog"."default",
  "foms" varchar(500) COLLATE "pg_catalog"."default",
  "memo1" varchar(500) COLLATE "pg_catalog"."default",
  "memo2" varchar(500) COLLATE "pg_catalog"."default",
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "record_dept" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "clue_source" varchar(32) COLLATE "pg_catalog"."default",
  "detail_behavior" varchar(32) COLLATE "pg_catalog"."default",
  "out_type" varchar(32) COLLATE "pg_catalog"."default",
  "steady_type" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "report_staff" OWNER TO "nj_public";
CREATE TABLE "shift_duty" (
  "clue_check" varchar(2000) COLLATE "pg_catalog"."default",
  "clue_group" varchar(2000) COLLATE "pg_catalog"."default",
  "clue_service" varchar(2000) COLLATE "pg_catalog"."default",
  "duty_date" timestamp(6),
  "duty_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "duty_term" varchar(32) COLLATE "pg_catalog"."default",
  "duty_titile" varchar(200) COLLATE "pg_catalog"."default",
  "give_user" varchar(32) COLLATE "pg_catalog"."default",
  "key_staff" varchar(2000) COLLATE "pg_catalog"."default",
  "take_user" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "shift_duty" OWNER TO "nj_public";
CREATE TABLE "special_report" (
  "report_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "report_name" varchar(200) COLLATE "pg_catalog"."default",
  "report_memo" varchar(2000) COLLATE "pg_catalog"."default",
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "record_dept" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "begin_date" timestamp(6),
  "end_date" timestamp(6),
  "work_require" varchar(2000) COLLATE "pg_catalog"."default",
  "lead_require" varchar(2000) COLLATE "pg_catalog"."default",
  "state" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "special_report" OWNER TO "nj_public";
CREATE TABLE "virtual_info" (
  "key_staff_id" varchar(32) COLLATE "pg_catalog"."default",
  "memo" varchar(2000) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "virtual_account" varchar(200) COLLATE "pg_catalog"."default",
  "virtual_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "virtual_type" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "virtual_info" OWNER TO "nj_public";
CREATE TABLE "wait_msg" (
  "id" int4 NOT NULL DEFAULT nextval('msg_id_seq'::regclass),
  "unit_code" varchar(64) COLLATE "pg_catalog"."default",
  "msg_title" varchar(512) COLLATE "pg_catalog"."default",
  "msg_content" varchar(4000) COLLATE "pg_catalog"."default",
  "msg_state" varchar(1) COLLATE "pg_catalog"."default",
  "sys_date" timestamp(6) DEFAULT now(),
  "out_date" timestamp(6)
)
;
ALTER TABLE "wait_msg" OWNER TO "nj_public";
CREATE TABLE "white_list" (
  "white_list_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "id_card" varchar(32) COLLATE "pg_catalog"."default",
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "telephone" varchar(32) COLLATE "pg_catalog"."default",
  "record_user" varchar(32) COLLATE "pg_catalog"."default",
  "record_dept" varchar(32) COLLATE "pg_catalog"."default",
  "record_date" timestamp(6),
  "record_memo" varchar(2000) COLLATE "pg_catalog"."default",
  "valid_date" varchar(200) COLLATE "pg_catalog"."default",
  "check_user" varchar(32) COLLATE "pg_catalog"."default",
  "check_dept" varchar(32) COLLATE "pg_catalog"."default",
  "check_date" timestamp(6),
  "check_memo" varchar(2000) COLLATE "pg_catalog"."default",
  "state" varchar(32) COLLATE "pg_catalog"."default",
  "begin_date" timestamp(6),
  "end_date" timestamp(6),
  "superior_dept" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "white_list" OWNER TO "nj_public";
CREATE VIEW "v_assign_similar" AS  SELECT a.name,
    a.id_card,
    b.assign_id,
    b.importance_level,
    b.title,
    b.record_date
   FROM ((clue_assgin_staff a
     JOIN clue_assign b ON (((a.assign_id)::text = (b.assign_id)::text)))
     JOIN ( SELECT clue_assgin_staff.id_card
           FROM clue_assgin_staff
          GROUP BY clue_assgin_staff.id_card
         HAVING (count(0) > 1)) c ON (((a.id_card)::text = (c.id_card)::text)))
  ORDER BY a.id_card, b.record_date;
ALTER TABLE "v_assign_similar" OWNER TO "nj_public";
CREATE VIEW "v_clue_assign" AS  SELECT DISTINCT a.add_class,
    a.clue_content,
    a.clue_id,
    a.clue_title,
    a.group_name,
    a.record_date,
    a.record_dept,
    a.record_user,
    a.source_mainclass,
    a.source_subclass,
    a.is_check,
    a.send_dept,
    b.assign_id
   FROM (clue a
     JOIN clue_assgin_staff b ON (((a.clue_id)::text = (b.clue_id)::text)));
ALTER TABLE "v_clue_assign" OWNER TO "nj_public";
CREATE VIEW "v_down_assign" AS  SELECT DISTINCT a.assign_id,
    a.unit_code,
    a.unit_name,
    a.all_counts,
    a.feedback_counts,
    b.record_date,
    b.title,
    b.term,
    b.send_channel,
    b.assign_type,
    b.importance_level,
    b.is_war,
    ((date_part('day'::text, ((b.time_limit)::timestamp with time zone - now())) * (24)::double precision) + date_part('hour'::text, ((b.time_limit)::timestamp with time zone - now()))) AS time_left
   FROM (( SELECT a_1.assign_id,
            a_1.unit_code,
            a_1.unit_name,
            count(DISTINCT a_1.assign_staff_id) AS all_counts,
            count(DISTINCT b_1.assign_staff_id) AS feedback_counts
           FROM (clue_assgin_staff a_1
             LEFT JOIN assgin_feedback b_1 ON (((a_1.assign_staff_id)::text = (b_1.assign_staff_id)::text)))
          GROUP BY a_1.assign_id, a_1.unit_code, a_1.unit_name) a
     JOIN clue_assign b ON (((a.assign_id)::text = (b.assign_id)::text)));
ALTER TABLE "v_down_assign" OWNER TO "nj_public";
CREATE VIEW "v_feedback_change" AS  SELECT DISTINCT a.feedback_id,
    a.feedback_dept,
    d.name,
    d.id_card,
    c.term,
    c.title,
    c.record_date,
    b.stedy_state AS pre_feedback_state,
    a.stedy_state AS feedback_state,
    b.feedback_date AS pre_feedback_date,
    a.feedback_date,
    e.signin_dept
   FROM ((((assgin_feedback a
     LEFT JOIN assgin_feedback b ON (((a.feedback_previd)::text = (b.feedback_id)::text)))
     JOIN clue_assign c ON (((a.assign_id)::text = (c.assign_id)::text)))
     JOIN clue_assgin_staff e ON (((c.assign_id)::text = (e.assign_id)::text)))
     JOIN clue_staff d ON (((a.staff_id)::text = (d.staff_id)::text)))
  ORDER BY c.title, a.feedback_date;
ALTER TABLE "v_feedback_change" OWNER TO "nj_public";
CREATE VIEW "v_feedback_last" AS  SELECT a.assign_staff_id,
    a.assign_id,
    a.staff_id,
    a.clue_id,
    a.signin_dept,
    a.assign_state,
    a.assign_police,
    a.signin_dept_code,
    a.unit_code,
    a.unit_name,
    a.is_check,
    a.check_user,
    a.check_time,
    a.domicile,
    a.domicile_place,
    a.email,
    a.foms,
    a.id_card,
    a.link_way,
    a.memo1,
    a.memo2,
    a.name,
    a.plat_number,
    a.qq_number,
    a.residence,
    a.under_group,
    a.venue,
    a.venue_add,
    a.weixin,
    a.record_date,
    a.record_dept,
    a.record_user,
    a.aim_place,
    a.sys_date,
    b.feedback_id,
    b.aim,
    b.involved_behavior,
    b.concrete_behavior,
    b.work_manner,
    b.work_feedback,
    b.stedy_state,
    b.group_name,
    b.link_way AS fdbk_link_way,
    b.group_property,
    b.duty_police,
    b.check_policeman,
    b.policeman_link,
    b.is_core,
    b.is_white,
    b.assess_state,
    b.assess_content,
    b.feedback_state,
    b.feedback_dept,
    b.feedback_date,
    b.feedback_previd
   FROM (clue_assgin_staff a
     LEFT JOIN v_last_feedback b ON (((a.assign_staff_id)::text = (b.assign_staff_id)::text)));
ALTER TABLE "v_feedback_last" OWNER TO "nj_public";
CREATE VIEW "v_feedback_last_finished" AS  SELECT a.assign_staff_id,
    a.assign_id,
    a.staff_id,
    a.clue_id,
    a.signin_dept,
    a.assign_state,
    a.assign_police,
    a.signin_dept_code,
    a.unit_code,
    a.unit_name,
    a.is_check,
    a.check_user,
    a.check_time,
    a.domicile,
    a.domicile_place,
    a.email,
    a.foms,
    a.id_card,
    a.link_way,
    a.memo1,
    a.memo2,
    a.name,
    a.plat_number,
    a.qq_number,
    a.residence,
    a.under_group,
    a.venue,
    a.venue_add,
    a.weixin,
    a.record_date,
    a.record_dept,
    a.record_user,
    a.aim_place,
    a.sys_date,
    b.feedback_id,
    b.aim,
    b.involved_behavior,
    b.concrete_behavior,
    b.work_manner,
    b.work_feedback,
    b.stedy_state,
    b.group_name,
    b.link_way AS fdbk_link_way,
    b.group_property,
    b.duty_police,
    b.check_policeman,
    b.policeman_link,
    b.is_core,
    b.is_white,
    b.assess_state,
    b.assess_content,
    b.feedback_state,
    b.feedback_dept,
    b.feedback_date,
    b.feedback_previd
   FROM (clue_assgin_staff a
     LEFT JOIN v_last_finished_feedback b ON (((a.assign_staff_id)::text = (b.assign_staff_id)::text)));
ALTER TABLE "v_feedback_last_finished" OWNER TO "nj_public";
CREATE VIEW "v_feedback_limit" AS  SELECT DISTINCT a.assign_id,
    a.title,
    a.record_date,
    a.term,
    a.fact_sheet,
    a.job_require,
    a.accept_user,
    a.issue_user,
    a.assign_type,
    a.importance_level,
    a.mobile,
    a.time_limit,
    a.record_user,
    a.is_detailed,
    a.is_war,
    a.send_channel,
    a.unit_code,
    a.unit_name,
    a.assign_previd,
    a.assign_sourceid,
    a.level_num,
    a.sys_date,
    b.signin_dept
   FROM (clue_assign a
     JOIN clue_assgin_staff b ON (((a.assign_id)::text = (b.assign_id)::text)));
ALTER TABLE "v_feedback_limit" OWNER TO "nj_public";
CREATE VIEW "v_feedback_outtime" AS  SELECT DISTINCT d.title,
    d.issue_user,
    e.id_card,
    e.name,
    e.assign_state,
    f.work_feedback,
    b.clue_title,
    e.record_date,
    e.signin_dept,
    e.signin_dept_code,
    d.record_date AS publish_date,
    d.importance_level,
    d.time_limit,
        CASE
            WHEN ((f.feedback_id IS NULL) OR ((f.feedback_state)::text = 'F'::text)) THEN '未反馈'::character varying(20)
            ELSE '已反馈'::character varying(20)
        END AS feedback_state,
    d.is_war,
    d.send_channel,
    d.term,
    f.feedback_dept,
    f.check_policeman,
    f.policeman_link,
    e.venue,
    e.domicile,
    e.check_time,
    f.stedy_state,
    d.unit_name,
    e.assign_id,
    e.assign_staff_id,
    e.clue_id,
    e.staff_id,
    e.foms,
    f.feedback_date,
    f.assess_state,
    d.unit_code
   FROM (((clue_assgin_staff e
     LEFT JOIN clue b ON (((e.clue_id)::text = (b.clue_id)::text)))
     JOIN clue_assign d ON (((e.assign_id)::text = (d.assign_id)::text)))
     LEFT JOIN v_last_finished_feedback f ON (((f.assign_staff_id)::text = (e.assign_staff_id)::text)))
  WHERE ((f.feedback_id IS NULL) OR (f.feedback_date > d.time_limit));
ALTER TABLE "v_feedback_outtime" OWNER TO "nj_public";
CREATE VIEW "v_history_assign_staff" AS  SELECT DISTINCT d.title,
    d.issue_user,
    e.id_card,
    e.name,
    e.assign_state,
    b.clue_title,
    e.record_date,
    e.signin_dept,
    e.signin_dept_code,
    e.link_way,
    d.record_date AS publish_date,
    d.importance_level,
    d.time_limit,
        CASE
            WHEN ((f.feedback_id IS NULL) OR ((f.feedback_state)::text = 'F'::text)) THEN '未反馈'::character varying(20)
            ELSE '已反馈'::character varying(20)
        END AS feedback_state,
    d.is_war,
    d.send_channel,
    d.term,
    e.venue,
    e.domicile,
    e.check_time,
    d.unit_name,
    e.assign_id,
    e.assign_staff_id,
    e.clue_id,
    e.staff_id,
    e.foms,
    d.unit_code,
    f.aim,
    f.assess_content,
    f.assess_state,
    f.check_policeman,
    f.concrete_behavior,
    f.duty_police,
    f.feedback_date,
    f.feedback_dept,
    f.feedback_id,
    f.feedback_previd,
    f.group_name,
    f.group_property,
    f.involved_behavior,
    f.is_core,
    f.is_white,
    f.link_way AS fdbk_link_way,
    f.policeman_link,
    f.stedy_state,
    f.work_feedback,
    f.work_manner
   FROM (((clue_assgin_staff e
     LEFT JOIN clue b ON (((e.clue_id)::text = (b.clue_id)::text)))
     JOIN clue_assign d ON (((e.assign_id)::text = (d.assign_id)::text)))
     LEFT JOIN assgin_feedback f ON ((((f.assign_staff_id)::text = (e.assign_staff_id)::text) AND ((f.feedback_state)::text = 'T'::text))));
ALTER TABLE "v_history_assign_staff" OWNER TO "nj_public";
CREATE VIEW "v_last_feedback" AS  SELECT sub.feedback_id,
    sub.assign_staff_id,
    sub.assign_id,
    sub.staff_id,
    sub.clue_id,
    sub.aim,
    sub.involved_behavior,
    sub.concrete_behavior,
    sub.work_manner,
    sub.work_feedback,
    sub.stedy_state,
    sub.group_name,
    sub.link_way,
    sub.group_property,
    sub.duty_police,
    sub.check_policeman,
    sub.policeman_link,
    sub.is_core,
    sub.is_white,
    sub.assess_state,
    sub.assess_content,
    sub.feedback_state,
    sub.feedback_dept,
    sub.feedback_date,
    sub.feedback_previd,
    sub.sys_date,
    sub.rn
   FROM ( SELECT a_1.feedback_id,
            a_1.assign_staff_id,
            a_1.assign_id,
            a_1.staff_id,
            a_1.clue_id,
            a_1.aim,
            a_1.involved_behavior,
            a_1.concrete_behavior,
            a_1.work_manner,
            a_1.work_feedback,
            a_1.stedy_state,
            a_1.group_name,
            a_1.link_way,
            a_1.group_property,
            a_1.duty_police,
            a_1.check_policeman,
            a_1.policeman_link,
            a_1.is_core,
            a_1.is_white,
            a_1.assess_state,
            a_1.assess_content,
            a_1.feedback_state,
            a_1.feedback_dept,
            a_1.feedback_date,
            a_1.feedback_previd,
            a_1.sys_date,
            row_number() OVER (PARTITION BY a_1.assign_staff_id ORDER BY a_1.sys_date DESC) AS rn
           FROM assgin_feedback a_1) sub
  WHERE (sub.rn = 1);
ALTER TABLE "v_last_feedback" OWNER TO "nj_public";
CREATE VIEW "v_last_finished_feedback" AS  SELECT sub.feedback_id,
    sub.assign_staff_id,
    sub.assign_id,
    sub.staff_id,
    sub.clue_id,
    sub.aim,
    sub.involved_behavior,
    sub.concrete_behavior,
    sub.work_manner,
    sub.work_feedback,
    sub.stedy_state,
    sub.group_name,
    sub.link_way,
    sub.group_property,
    sub.duty_police,
    sub.check_policeman,
    sub.policeman_link,
    sub.is_core,
    sub.is_white,
    sub.assess_state,
    sub.assess_content,
    sub.feedback_state,
    sub.feedback_dept,
    sub.feedback_date,
    sub.feedback_previd,
    sub.sys_date,
    sub.rn
   FROM ( SELECT a_1.feedback_id,
            a_1.assign_staff_id,
            a_1.assign_id,
            a_1.staff_id,
            a_1.clue_id,
            a_1.aim,
            a_1.involved_behavior,
            a_1.concrete_behavior,
            a_1.work_manner,
            a_1.work_feedback,
            a_1.stedy_state,
            a_1.group_name,
            a_1.link_way,
            a_1.group_property,
            a_1.duty_police,
            a_1.check_policeman,
            a_1.policeman_link,
            a_1.is_core,
            a_1.is_white,
            a_1.assess_state,
            a_1.assess_content,
            a_1.feedback_state,
            a_1.feedback_dept,
            a_1.feedback_date,
            a_1.feedback_previd,
            a_1.sys_date,
            row_number() OVER (PARTITION BY a_1.assign_staff_id ORDER BY a_1.sys_date DESC) AS rn
           FROM assgin_feedback a_1
          WHERE ((a_1.feedback_state)::text = 'T'::text)) sub
  WHERE (sub.rn = 1);
ALTER TABLE "v_last_finished_feedback" OWNER TO "nj_public";
CREATE VIEW "v_lastest_assign_feeback_staff" AS  SELECT DISTINCT d.title,
    d.issue_user,
    e.id_card,
    e.name,
    e.assign_state,
    b.clue_title,
    e.record_date,
    e.signin_dept,
    e.signin_dept_code,
    d.record_date AS publish_date,
    d.importance_level,
    d.time_limit,
        CASE
            WHEN ((f.feedback_id IS NULL) OR ((f.feedback_state)::text = 'F'::text)) THEN '未反馈'::character varying(20)
            ELSE '已反馈'::character varying(20)
        END AS feedback_state,
    d.is_war,
    d.send_channel,
    d.term,
    e.venue,
    e.domicile,
    e.check_time,
    d.unit_name,
    e.assign_id,
    e.assign_staff_id,
    e.clue_id,
    e.staff_id,
    e.foms,
    d.unit_code,
    f.aim,
    f.assess_content,
    f.assess_state,
    f.check_policeman,
    f.concrete_behavior,
    f.duty_police,
    f.feedback_date,
    f.feedback_dept,
    f.feedback_id,
    f.feedback_previd,
    f.group_name,
    f.group_property,
    f.involved_behavior,
    f.is_core,
    f.is_white,
    f.link_way,
    f.policeman_link,
    f.stedy_state,
    f.work_feedback,
    f.work_manner
   FROM (((clue_assgin_staff e
     LEFT JOIN clue b ON (((e.clue_id)::text = (b.clue_id)::text)))
     JOIN clue_assign d ON (((e.assign_id)::text = (d.assign_id)::text)))
     LEFT JOIN v_last_feedback f ON (((f.assign_staff_id)::text = (e.assign_staff_id)::text)));
ALTER TABLE "v_lastest_assign_feeback_staff" OWNER TO "nj_public";
CREATE VIEW "v_lastest_assign_feeback_staff_finished" AS  SELECT DISTINCT d.title,
    d.issue_user,
    e.id_card,
    e.name,
    e.assign_state,
    b.clue_title,
    e.record_date,
    e.signin_dept,
    e.signin_dept_code,
    d.record_date AS publish_date,
    d.importance_level,
    d.time_limit,
        CASE
            WHEN ((f.feedback_id IS NULL) OR ((f.feedback_state)::text = 'F'::text)) THEN '未反馈'::character varying(20)
            ELSE '已反馈'::character varying(20)
        END AS feedback_state,
    d.is_war,
    d.send_channel,
    d.term,
    e.venue,
    e.domicile,
    e.check_time,
    d.unit_name,
    e.assign_id,
    e.assign_staff_id,
    e.clue_id,
    e.staff_id,
    e.foms,
    d.unit_code,
    f.aim,
    f.assess_content,
    f.assess_state,
    f.check_policeman,
    f.concrete_behavior,
    f.duty_police,
    f.feedback_date,
    f.feedback_dept,
    f.feedback_id,
    f.feedback_previd,
    f.group_name,
    f.group_property,
    f.involved_behavior,
    f.is_core,
    f.is_white,
    f.link_way,
    f.policeman_link,
    f.stedy_state,
    f.work_feedback,
    f.work_manner
   FROM (((clue_assgin_staff e
     LEFT JOIN clue b ON (((e.clue_id)::text = (b.clue_id)::text)))
     JOIN clue_assign d ON (((e.assign_id)::text = (d.assign_id)::text)))
     LEFT JOIN v_last_finished_feedback f ON (((f.assign_staff_id)::text = (e.assign_staff_id)::text)));
ALTER TABLE "v_lastest_assign_feeback_staff_finished" OWNER TO "nj_public";
CREATE VIEW "v_unit_newterm" AS  SELECT (date_part(('year'::character varying(4))::text, clue_assign.sys_date))::character varying(20) AS unit_year,
    clue_assign.unit_code,
    ((((date_part(('year'::character varying(4))::text, clue_assign.sys_date) || ('年第'::character varying(20))::text) || (count(0) + 1)) || ('号'::character varying(20))::text))::character varying(200) AS term_num
   FROM clue_assign
  GROUP BY (date_part(('year'::character varying(4))::text, clue_assign.sys_date)), clue_assign.unit_code;
ALTER TABLE "v_unit_newterm" OWNER TO "nj_public";
CREATE VIEW "v_up_assign" AS  SELECT DISTINCT a.signin_dept AS signin_dept_code,
    a.assign_id,
    a.unit_code,
    a.unit_name,
    a.all_counts,
    a.feedback_counts,
    b.record_date,
    b.title,
    b.term,
    b.send_channel,
    b.assign_type,
    b.importance_level,
    a.is_check,
    b.is_war,
    ((date_part('day'::text, ((b.time_limit)::timestamp with time zone - now())) * (24)::double precision) + date_part('hour'::text, ((b.time_limit)::timestamp with time zone - now()))) AS time_left
   FROM (( SELECT a_1.signin_dept,
            a_1.assign_id,
            a_1.unit_code,
            a_1.unit_name,
            a_1.is_check,
            count(DISTINCT a_1.assign_staff_id) AS all_counts,
            sum(
                CASE
                    WHEN ((b_1.feedback_id IS NULL) OR ((b_1.feedback_state)::text = 'F'::text)) THEN 0
                    ELSE 1
                END) AS feedback_counts
           FROM (clue_assgin_staff a_1
             LEFT JOIN v_last_feedback b_1 ON (((a_1.assign_staff_id)::text = (b_1.assign_staff_id)::text)))
          GROUP BY a_1.signin_dept, a_1.is_check, a_1.assign_id, a_1.unit_code, a_1.unit_name) a
     JOIN clue_assign b ON (((a.assign_id)::text = (b.assign_id)::text)));
ALTER TABLE "v_up_assign" OWNER TO "nj_public";
CREATE VIEW "v_wait_assign_msg" AS  SELECT a.signin_dept AS unit_code,
    '即将到期提醒'::text AS msg_title,
    (((('您有'::text || a.num) || '条指令'::text) || a.con) || '小时内到达反馈期限，请及时反馈'::text) AS msg_content,
    'F'::text AS msg_state,
    a.timelimit
   FROM ( SELECT a_1.signin_dept,
            a_1.hours AS con,
            a_1.timelimit,
            count(DISTINCT a_1.assign_id) AS num
           FROM ( SELECT a_2.signin_dept,
                    ((date_part('day'::text, a_2.t) * (24)::double precision) + date_part('hour'::text, a_2.t)) AS hours,
                    a_2.assign_id,
                    a_2.timelimit
                   FROM ( SELECT a_3.signin_dept,
                            ((b.time_limit)::timestamp with time zone - now()) AS t,
                            a_3.assign_id,
                            date_trunc('hour'::text, b.time_limit) AS timelimit
                           FROM (v_feedback_last_finished a_3
                             JOIN clue_assign b ON (((a_3.assign_id)::text = (b.assign_id)::text)))
                          WHERE ((a_3.feedback_id IS NULL) OR ((a_3.feedback_state)::text = 'F'::text))) a_2) a_1
          WHERE ((a_1.hours < (48)::double precision) AND (a_1.hours > (0)::double precision))
          GROUP BY a_1.signin_dept, a_1.hours, a_1.timelimit) a;
ALTER TABLE "v_wait_assign_msg" OWNER TO "nj_public";

BEGIN;
LOCK TABLE "public"."answer_caliber" IN SHARE MODE;
DELETE FROM "public"."answer_caliber";
COMMIT;
BEGIN;
LOCK TABLE "public"."assgin_feedback" IN SHARE MODE;
DELETE FROM "public"."assgin_feedback";
COMMIT;
BEGIN;
LOCK TABLE "public"."attach_file" IN SHARE MODE;
DELETE FROM "public"."attach_file";
COMMIT;
BEGIN;
LOCK TABLE "public"."clue" IN SHARE MODE;
DELETE FROM "public"."clue";
COMMIT;
BEGIN;
LOCK TABLE "public"."clue_assgin_staff" IN SHARE MODE;
DELETE FROM "public"."clue_assgin_staff";
COMMIT;
BEGIN;
LOCK TABLE "public"."clue_assign" IN SHARE MODE;
DELETE FROM "public"."clue_assign";
COMMIT;
BEGIN;
LOCK TABLE "public"."clue_report" IN SHARE MODE;
DELETE FROM "public"."clue_report";
COMMIT;
BEGIN;
LOCK TABLE "public"."clue_staff" IN SHARE MODE;
DELETE FROM "public"."clue_staff";
COMMIT;
BEGIN;
LOCK TABLE "public"."control_info" IN SHARE MODE;
DELETE FROM "public"."control_info";
COMMIT;
BEGIN;
LOCK TABLE "public"."group_sign" IN SHARE MODE;
DELETE FROM "public"."group_sign";
COMMIT;
BEGIN;
LOCK TABLE "public"."key_staff" IN SHARE MODE;
DELETE FROM "public"."key_staff";
COMMIT;
BEGIN;
LOCK TABLE "public"."report_staff" IN SHARE MODE;
DELETE FROM "public"."report_staff";
COMMIT;
BEGIN;
LOCK TABLE "public"."shift_duty" IN SHARE MODE;
DELETE FROM "public"."shift_duty";
COMMIT;
BEGIN;
LOCK TABLE "public"."special_report" IN SHARE MODE;
DELETE FROM "public"."special_report";
COMMIT;
BEGIN;
LOCK TABLE "public"."virtual_info" IN SHARE MODE;
DELETE FROM "public"."virtual_info";
COMMIT;
BEGIN;
LOCK TABLE "public"."wait_msg" IN SHARE MODE;
DELETE FROM "public"."wait_msg";
COMMIT;
BEGIN;
LOCK TABLE "public"."white_list" IN SHARE MODE;
DELETE FROM "public"."white_list";
COMMIT;
ALTER TABLE "answer_caliber" ADD CONSTRAINT "pk_answer_caliber" PRIMARY KEY ("answer_id");
ALTER TABLE "assgin_feedback" ADD CONSTRAINT "pk_assgin_feedback" PRIMARY KEY ("feedback_id");
ALTER TABLE "attach_file" ADD CONSTRAINT "pk_attach_file" PRIMARY KEY ("file_md5");
ALTER TABLE "clue" ADD CONSTRAINT "pk_clue" PRIMARY KEY ("clue_id");
ALTER TABLE "clue_assgin_staff" ADD CONSTRAINT "pk_clue_assgin_staff" PRIMARY KEY ("assign_staff_id");
ALTER TABLE "clue_assign" ADD CONSTRAINT "pk_clue_assign" PRIMARY KEY ("assign_id");
ALTER TABLE "clue_report" ADD CONSTRAINT "pk_clue_report" PRIMARY KEY ("report_id");
ALTER TABLE "clue_staff" ADD CONSTRAINT "pk_clue_staff" PRIMARY KEY ("staff_id");
ALTER TABLE "control_info" ADD CONSTRAINT "pk_control_info" PRIMARY KEY ("control_id");
ALTER TABLE "group_sign" ADD CONSTRAINT "pk_group_sign" PRIMARY KEY ("sign_id");
ALTER TABLE "key_staff" ADD CONSTRAINT "pk_key_staff" PRIMARY KEY ("key_staff_id");
ALTER TABLE "report_staff" ADD CONSTRAINT "pk_report_staff" PRIMARY KEY ("staff_id");
ALTER TABLE "shift_duty" ADD CONSTRAINT "pk_shift_duty" PRIMARY KEY ("duty_id");
ALTER TABLE "special_report" ADD CONSTRAINT "pk_special_report" PRIMARY KEY ("report_id");
ALTER TABLE "virtual_info" ADD CONSTRAINT "pk_virtual_info" PRIMARY KEY ("virtual_id");
ALTER TABLE "wait_msg" ADD CONSTRAINT "pk_wait_msg" PRIMARY KEY ("id");
ALTER TABLE "white_list" ADD CONSTRAINT "pk_white_list" PRIMARY KEY ("white_list_id");
SELECT setval('"msg_id_seq"', 57, true);
ALTER SEQUENCE "msg_id_seq" OWNER TO "nj_public";


