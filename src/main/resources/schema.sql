CREATE TABLE IF NOT EXISTS "users" (
	"user_id" SERIAL NOT NULL,
	"username" VARCHAR(50) NOT NULL,
	"password" VARCHAR(255) NOT NULL,
	"email" VARCHAR(100) NULL DEFAULT NULL,
	"nickname" VARCHAR(50) NOT NULL,
	"avatar_url" VARCHAR(255) NULL DEFAULT NULL,
	"status" BOOLEAN NOT NULL DEFAULT true,
	"created_time" TIMESTAMP NOT NULL,
	"updated_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"comment_count" INTEGER NOT NULL DEFAULT 0,
	"post_count" INTEGER NOT NULL DEFAULT 0,
	"rating_count" INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY ("user_id")
)
;
COMMENT ON TABLE "users" IS '用户表，储存用户信息';
COMMENT ON COLUMN "users"."user_id" IS '用户id';
COMMENT ON COLUMN "users"."username" IS '用户名';
COMMENT ON COLUMN "users"."password" IS '用户密码（已hash加盐）';
COMMENT ON COLUMN "users"."email" IS '用户邮箱';
COMMENT ON COLUMN "users"."nickname" IS '用户昵称';
COMMENT ON COLUMN "users"."avatar_url" IS '用户头像url';
COMMENT ON COLUMN "users"."status" IS '用户状态：1正常，0禁用';
COMMENT ON COLUMN "users"."created_time" IS '创建时间';
COMMENT ON COLUMN "users"."updated_time" IS '更新时间';
COMMENT ON COLUMN "users"."comment_count" IS '用户的评论数量';
COMMENT ON COLUMN "users"."post_count" IS '用户的帖子数量';
COMMENT ON COLUMN "users"."rating_count" IS '用户的评分数量';
CREATE INDEX IF NOT EXISTS "username_password" ON "users" ("username", "password");

CREATE TABLE IF NOT EXISTS "posts" (
	"post_id" SERIAL NOT NULL,
	"user_id" INTEGER NULL DEFAULT NULL,
	"post_title" TEXT NOT NULL,
	"post_text" TEXT NOT NULL,
	"is_deleted" BOOLEAN NOT NULL DEFAULT false,
	"deleted_time" TIMESTAMP NULL DEFAULT NULL,
	"created_time" TIMESTAMP NOT NULL,
	"updated_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	"rating_count" INTEGER NOT NULL DEFAULT 0,
	"comment_count" INTEGER NOT NULL DEFAULT 0,
    "cover_media_id" INTEGER NULL DEFAULT NULL,
	PRIMARY KEY ("post_id")
)
;
COMMENT ON TABLE "posts" IS '帖子表，储存用户发表的帖子';
COMMENT ON COLUMN "posts"."post_id" IS '帖子id';
COMMENT ON COLUMN "posts"."user_id" IS '发布者id（为空代表用户已不存在）';
COMMENT ON COLUMN "posts"."post_title" IS '帖子标题';
COMMENT ON COLUMN "posts"."post_text" IS '帖子文本';
COMMENT ON COLUMN "posts"."is_deleted" IS '软删除：0正常，1删除';
COMMENT ON COLUMN "posts"."deleted_time" IS '删除时间';
COMMENT ON COLUMN "posts"."created_time" IS '创建时间';
COMMENT ON COLUMN "posts"."updated_time" IS '更新时间';
COMMENT ON COLUMN "posts"."rating_count" IS '评分数量';
COMMENT ON COLUMN "posts"."comment_count" IS '评论数量';
COMMENT ON COLUMN "posts"."cover_media_id" IS '冗余字段，储存封面对应的postMediaId';
CREATE INDEX IF NOT EXISTS "fk__posts__users" ON "posts" ("user_id");

CREATE TABLE IF NOT EXISTS "post_media" (
	"media_id" SERIAL NOT NULL,
	"post_id" INTEGER NULL DEFAULT NULL,
	"upload_user_id" INTEGER NULL DEFAULT NULL,
	"media_url" VARCHAR(255) NOT NULL,
	"media_type" VARCHAR(10) NOT NULL,
    "sort_order" SMALLINT NULL DEFAULT NULL,
	PRIMARY KEY ("media_id"),
	CONSTRAINT "fk_post_media_posts" FOREIGN KEY ("post_id") REFERENCES "posts" ("post_id") ON UPDATE CASCADE ON DELETE SET NULL,
	CONSTRAINT "fk_post_media_users" FOREIGN KEY ("upload_user_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE SET NULL
)
;
COMMENT ON TABLE "post_media" IS '帖子内容表，记录帖子对应媒体的信息列表';
COMMENT ON COLUMN "post_media"."media_id" IS '媒体id';
COMMENT ON COLUMN "post_media"."post_id" IS '对应帖子id';
COMMENT ON COLUMN "post_media"."upload_user_id" IS '上传用户id';
COMMENT ON COLUMN "post_media"."media_url" IS '媒体url';
COMMENT ON COLUMN "post_media"."media_type" IS '媒体类型';
COMMENT ON COLUMN "post_media"."sort_order" IS '指示媒体在帖子中的顺序';
CREATE INDEX IF NOT EXISTS "fk__post_media__posts" ON "post_media" ("post_id");
CREATE INDEX IF NOT EXISTS "fk_post_media_users" ON "post_media" ("upload_user_id");

ALTER TABLE "posts" DROP CONSTRAINT IF EXISTS "FK_posts_post_media";

ALTER TABLE "posts" ADD CONSTRAINT "FK_posts_post_media" FOREIGN KEY ("cover_media_id") REFERENCES "post_media" ("media_id") ON UPDATE CASCADE ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS "comments" (
	"comment_id" SERIAL NOT NULL,
	"post_id" INTEGER NULL DEFAULT NULL,
	"user_id" INTEGER NULL DEFAULT NULL,
	"parent_id" INTEGER NULL DEFAULT NULL,
	"comment_text" TEXT NOT NULL,
	"is_deleted" BOOLEAN NOT NULL DEFAULT false,
	"created_time" TIMESTAMP NOT NULL,
	"updated_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("comment_id"),
	CONSTRAINT "fk_comments_comments" FOREIGN KEY ("parent_id") REFERENCES "comments" ("comment_id") ON UPDATE CASCADE ON DELETE SET NULL,
	CONSTRAINT "fk_comments_posts" FOREIGN KEY ("post_id") REFERENCES "posts" ("post_id") ON UPDATE CASCADE ON DELETE SET NULL,
	CONSTRAINT "fk_comments_users" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE SET NULL
)
;
COMMENT ON TABLE "comments" IS '评论表，储存所有评论';
COMMENT ON COLUMN "comments"."comment_id" IS '评论id';
COMMENT ON COLUMN "comments"."post_id" IS '对应帖子的id';
COMMENT ON COLUMN "comments"."user_id" IS '发出评论的用户id';
COMMENT ON COLUMN "comments"."parent_id" IS '父评论id（回复）';
COMMENT ON COLUMN "comments"."comment_text" IS '评论内容';
COMMENT ON COLUMN "comments"."is_deleted" IS '软删除：0正常，1删除';
COMMENT ON COLUMN "comments"."created_time" IS '创建时间';
COMMENT ON COLUMN "comments"."updated_time" IS '更新时间';
CREATE INDEX IF NOT EXISTS "fk_comments_posts" ON "comments" ("post_id");
CREATE INDEX IF NOT EXISTS "fk_comments_users" ON "comments" ("user_id");
CREATE INDEX IF NOT EXISTS "fk_comments_comments" ON "comments" ("parent_id");

CREATE TABLE IF NOT EXISTS "tags" (
	"tag_id" SERIAL NOT NULL,
	"name" VARCHAR(50) NOT NULL,
	"normalized_name" VARCHAR(50) NOT NULL,
	"created_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("tag_id")
)
;
COMMENT ON TABLE "tags" IS '标签表，储存所有的标签文本';
COMMENT ON COLUMN "tags"."tag_id" IS '标签id';
COMMENT ON COLUMN "tags"."name" IS '标签显示名称';
COMMENT ON COLUMN "tags"."normalized_name" IS '标签归一化后的名称，用于筛选排序';
COMMENT ON COLUMN "tags"."created_time" IS '创建时间';

CREATE TABLE IF NOT EXISTS "post_tags" (
	"post_id" INTEGER NOT NULL,
	"tag_id" INTEGER NOT NULL,
	PRIMARY KEY ("post_id", "tag_id"),
	CONSTRAINT "fk_post_tags_posts" FOREIGN KEY ("post_id") REFERENCES "posts" ("post_id") ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT "fk_post_tags_tags" FOREIGN KEY ("tag_id") REFERENCES "tags" ("tag_id") ON UPDATE CASCADE ON DELETE CASCADE
)
;
COMMENT ON TABLE "post_tags" IS '储存帖子与标签的多对多关系';
COMMENT ON COLUMN "post_tags"."post_id" IS '帖子id';
COMMENT ON COLUMN "post_tags"."tag_id" IS '标签id';
CREATE INDEX IF NOT EXISTS "fk__tags" ON "post_tags" ("tag_id");

CREATE TABLE IF NOT EXISTS "ratings" (
	"rating_id" SERIAL NOT NULL,
	"post_id" INTEGER NULL DEFAULT NULL,
	"user_id" INTEGER NULL DEFAULT NULL,
	"rating_value" SMALLINT NOT NULL,
	"created_time" TIMESTAMP NOT NULL,
	"updated_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("rating_id"),
	CONSTRAINT "fk_ratings_posts" FOREIGN KEY ("post_id") REFERENCES "posts" ("post_id") ON UPDATE CASCADE ON DELETE SET NULL,
	CONSTRAINT "fk_ratings_users" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE SET NULL
)
;
COMMENT ON TABLE "ratings" IS '评分表';
COMMENT ON COLUMN "ratings"."rating_id" IS '评分id';
COMMENT ON COLUMN "ratings"."post_id" IS '对应帖子id';
COMMENT ON COLUMN "ratings"."user_id" IS '评分用户id';
COMMENT ON COLUMN "ratings"."rating_value" IS '评分值：1-5分';
COMMENT ON COLUMN "ratings"."created_time" IS '创建时间';
COMMENT ON COLUMN "ratings"."updated_time" IS '更新时间';
CREATE INDEX IF NOT EXISTS "fk__ratings__posts" ON "ratings" ("post_id");
CREATE INDEX IF NOT EXISTS "fk__ratings__users" ON "ratings" ("user_id");

CREATE TABLE IF NOT EXISTS "admins" (
	"admin_id" SERIAL NOT NULL,
	"user_id" INTEGER NULL DEFAULT NULL,
	"username" VARCHAR(50) NOT NULL,
	"password" VARCHAR(255) NOT NULL,
	"admin_name" VARCHAR(50) NULL DEFAULT NULL,
	"role" VARCHAR(20) NOT NULL DEFAULT 'admin',
	"status" BOOLEAN NOT NULL DEFAULT true,
	"last_login" INTEGER NOT NULL,
	"created_time" INTEGER NOT NULL,
	PRIMARY KEY ("admin_id"),
	CONSTRAINT "fk_admins_users" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE SET NULL
)
;
COMMENT ON TABLE "admins" IS '管理员表，储存与管理员登录与权限相关信息';
COMMENT ON COLUMN "admins"."admin_id" IS '管理员id';
COMMENT ON COLUMN "admins"."user_id" IS '对应的用户id（可选）';
COMMENT ON COLUMN "admins"."username" IS '管理员登录用户名';
COMMENT ON COLUMN "admins"."password" IS '管理员登录密码（已hash加盐）';
COMMENT ON COLUMN "admins"."admin_name" IS '管理员姓名（可选）';
COMMENT ON COLUMN "admins"."role" IS '权限角色';
COMMENT ON COLUMN "admins"."status" IS '状态：1正常，0禁用';
COMMENT ON COLUMN "admins"."last_login" IS '最后登录时间';
COMMENT ON COLUMN "admins"."created_time" IS '创建时间';
CREATE INDEX IF NOT EXISTS "fk__admins__users" ON "admins" ("user_id");
CREATE INDEX IF NOT EXISTS "admins_username_password" ON "admins" ("username", "password");

CREATE TABLE IF NOT EXISTS "operation_logs" (
	"log_id" SERIAL NOT NULL,
	"admin_id" INTEGER NULL DEFAULT NULL,
	"operation_type" VARCHAR(50) NOT NULL,
	"target_id" INTEGER NOT NULL,
	"target_type" VARCHAR(20) NOT NULL,
	"operation_detail" TEXT NULL DEFAULT NULL,
	"ip_address" VARCHAR(45) NULL DEFAULT NULL,
	"created_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("log_id"),
	CONSTRAINT "fk_operation_logs_admins" FOREIGN KEY ("admin_id") REFERENCES "admins" ("admin_id") ON UPDATE CASCADE ON DELETE SET NULL
)
;
COMMENT ON TABLE "operation_logs" IS '操作日志表，储存管理员所有操作的日志';
COMMENT ON COLUMN "operation_logs"."log_id" IS '日志id';
COMMENT ON COLUMN "operation_logs"."admin_id" IS '管理员id';
COMMENT ON COLUMN "operation_logs"."operation_type" IS '操作类型';
COMMENT ON COLUMN "operation_logs"."target_id" IS '操作目标id';
COMMENT ON COLUMN "operation_logs"."target_type" IS '目标类型：user/post';
COMMENT ON COLUMN "operation_logs"."operation_detail" IS '操作详情';
COMMENT ON COLUMN "operation_logs"."ip_address" IS '操作IP地址';
COMMENT ON COLUMN "operation_logs"."created_time" IS '操作时间';
CREATE INDEX IF NOT EXISTS "fk__operation_logs__admins" ON "operation_logs" ("admin_id");

