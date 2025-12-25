CREATE TABLE IF NOT EXISTS "users" (
                         "user_id" SERIAL NOT NULL,
                         "openid" VARCHAR(512) NULL DEFAULT NULL::character varying,
                         "username" VARCHAR(50) NOT NULL,
                         "password" VARCHAR(255) NULL DEFAULT NULL::character varying,
                         "email" VARCHAR(100) NULL DEFAULT NULL::character varying,
                         "nickname" VARCHAR(50) NOT NULL,
                         "avatar_url" VARCHAR(255) NULL DEFAULT NULL::character varying,
                         "status" BOOLEAN NOT NULL DEFAULT true,
                         "created_time" TIMESTAMP NOT NULL,
                         "updated_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         "comment_count" INTEGER NOT NULL DEFAULT 0,
                         "post_count" INTEGER NOT NULL DEFAULT 0,
                         "rating_count" INTEGER NOT NULL DEFAULT 0,
                         "follower_count" INTEGER NOT NULL DEFAULT 0,
                         "following_count" INTEGER NOT NULL DEFAULT 0,
                         PRIMARY KEY ("user_id"),
                         UNIQUE ("openid")
)
;
COMMENT ON TABLE "users" IS '用户表，储存用户信息';
COMMENT ON COLUMN "users"."user_id" IS '用户id';
COMMENT ON COLUMN "users"."openid" IS '微信OpenID，通过微信登录获取';
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
COMMENT ON COLUMN "users"."follower_count" IS '粉丝数量';
COMMENT ON COLUMN "users"."following_count" IS '关注数量';
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
    "text_query"   tsvector,
    "has_image" BOOLEAN NOT NULL DEFAULT false,
    "has_video" BOOLEAN NOT NULL DEFAULT false,
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
-- 创建GIN索引需要在数据库中以postgres用户执行 CREATE EXTENSION pg_trgm;
CREATE INDEX IF NOT EXISTS "idx_posts_text_query" ON "posts" USING gin ("text_query");
CREATE INDEX IF NOT EXISTS idx_posts_user_active ON posts(user_id) WHERE is_deleted = false;


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
CREATE INDEX IF NOT EXISTS idx_comments_post_active ON comments(post_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_comments_user_active ON comments(user_id) WHERE is_deleted = false;


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
CREATE INDEX IF NOT EXISTS idx_ratings_post ON ratings(post_id);
CREATE INDEX IF NOT EXISTS idx_ratings_user ON ratings(user_id);


CREATE TABLE IF NOT EXISTS "admins" (
	"admin_id" SERIAL NOT NULL,
	"user_id" INTEGER NULL DEFAULT NULL,
	"username" VARCHAR(50) NOT NULL,
	"password" VARCHAR(255) NOT NULL,
	"admin_name" VARCHAR(50) NULL DEFAULT NULL,
	"role" VARCHAR(20) NOT NULL DEFAULT 'admin',
	"status" BOOLEAN NOT NULL DEFAULT true,
	"last_login" TIMESTAMP NULL,
	"created_time" TIMESTAMP NOT NULL,
	PRIMARY KEY ("admin_id"),
    UNIQUE ("username"),
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

CREATE TABLE IF NOT EXISTS "follows" (
                           "follower_id" INTEGER NOT NULL,
                           "followee_id" INTEGER NOT NULL,
                           "created_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY ("follower_id", "followee_id"),
                           CONSTRAINT "FK_follows_users" FOREIGN KEY ("follower_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE,
                           CONSTRAINT "FK_follows_users_2" FOREIGN KEY ("followee_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE
)
;
COMMENT ON TABLE "follows" IS '储存关注记录，按照自连接判断互相关注';
COMMENT ON COLUMN "follows"."follower_id" IS '关注者id';
COMMENT ON COLUMN "follows"."followee_id" IS '被关注者id';
COMMENT ON COLUMN "follows"."created_time" IS '关注时间';
CREATE INDEX IF NOT EXISTS "idx_follower" ON "follows" ("follower_id", "created_time");
CREATE INDEX IF NOT EXISTS "idx_followee" ON "follows" ("followee_id", "created_time");

CREATE TABLE IF NOT EXISTS "conversations" (
                                 "conversation_id" SERIAL NOT NULL,
                                 "created_time" TIMESTAMP NOT NULL,
                                 "updated_time" TIMESTAMP NOT NULL,
                                 "last_message_id" INTEGER NULL DEFAULT NULL,
                                 "last_message_time" TIMESTAMP NULL DEFAULT NULL,
                                 PRIMARY KEY ("conversation_id")
)
;
COMMENT ON TABLE "conversations" IS '会话记录表，目前暂时一个会话 = 两个用户的固定对话';
COMMENT ON COLUMN "conversations"."conversation_id" IS '对话id';
COMMENT ON COLUMN "conversations"."created_time" IS '创建时间';
COMMENT ON COLUMN "conversations"."updated_time" IS '更新时间';
COMMENT ON COLUMN "conversations"."last_message_id" IS '最新消息id';
COMMENT ON COLUMN "conversations"."last_message_time" IS '最新消息时间，用来做会话列表排序';

CREATE TABLE IF NOT EXISTS "messages" (
                            "message_id" SERIAL NOT NULL,
                            "conversation_id" INTEGER NOT NULL,
                            "sender_id" INTEGER NOT NULL,
                            "msg_type" VARCHAR(50) NOT NULL,
                            "content" VARCHAR(1024) NOT NULL,
                            "created_time" TIMESTAMP NOT NULL,
                            "is_recalled" BOOLEAN NOT NULL DEFAULT false,
                            "recalled_time" TIMESTAMP NULL DEFAULT NULL,
                            PRIMARY KEY ("message_id"),
                            CONSTRAINT "FK__conversations" FOREIGN KEY ("conversation_id") REFERENCES "conversations" ("conversation_id") ON UPDATE CASCADE ON DELETE CASCADE,
                            CONSTRAINT "FK__users" FOREIGN KEY ("sender_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE
)
;
COMMENT ON TABLE "messages" IS '消息表';
COMMENT ON COLUMN "messages"."message_id" IS '消息id';
COMMENT ON COLUMN "messages"."conversation_id" IS '对话id';
COMMENT ON COLUMN "messages"."sender_id" IS '发送者用户id';
COMMENT ON COLUMN "messages"."msg_type" IS '消息类型，text 文本，(预留图片/文件)';
COMMENT ON COLUMN "messages"."content" IS '文本内容';
COMMENT ON COLUMN "messages"."created_time" IS '创建时间';
COMMENT ON COLUMN "messages"."is_recalled" IS '是否撤回，软删除/撤回（可选）';
COMMENT ON COLUMN "messages"."recalled_time" IS '撤回时间';

CREATE TABLE IF NOT EXISTS "participants" (
                                "record_id" SERIAL NOT NULL,
                                "conversation_id" INTEGER NOT NULL,
                                "user_id" INTEGER NOT NULL,
                                "last_read_message_id" INTEGER NULL DEFAULT NULL,
                                "last_read_time" TIMESTAMP NULL DEFAULT NULL,
                                "is_hidden" BOOLEAN NOT NULL DEFAULT false,
                                "hidden_time" TIMESTAMP NULL DEFAULT NULL,
                                "created_time" TIMESTAMP NOT NULL,
                                "updated_time" TIMESTAMP NOT NULL,
                                PRIMARY KEY ("record_id"),
                                UNIQUE ("conversation_id", "user_id"),
                                CONSTRAINT "FK__conversations" FOREIGN KEY ("conversation_id") REFERENCES "conversations" ("conversation_id") ON UPDATE CASCADE ON DELETE CASCADE,
                                CONSTRAINT "FK__users" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE,
                                CONSTRAINT "FK__messages" FOREIGN KEY ("last_read_message_id") REFERENCES "messages" ("message_id") ON UPDATE CASCADE ON DELETE SET NULL
)
;
COMMENT ON TABLE "participants" IS '一条会话里每个用户一行，用来存“已读到哪、是否删除会话、未读数”等“用户视角状态”';
COMMENT ON COLUMN "participants"."record_id" IS '记录id';
COMMENT ON COLUMN "participants"."conversation_id" IS '对话id，外键对应对话表';
COMMENT ON COLUMN "participants"."user_id" IS '用户id';
COMMENT ON COLUMN "participants"."last_read_message_id" IS '已读游标，读到哪条消息了';
COMMENT ON COLUMN "participants"."last_read_time" IS '已读时间';
COMMENT ON COLUMN "participants"."is_hidden" IS '是否隐藏，会话对用户的软删除/隐藏';
COMMENT ON COLUMN "participants"."hidden_time" IS '隐藏时间';
COMMENT ON COLUMN "participants"."created_time" IS '创建时间';
COMMENT ON COLUMN "participants"."updated_time" IS '更新时间';

CREATE TABLE IF NOT EXISTS "conversation_one_to_one_map" (
                                               "user_low_id" INTEGER NOT NULL,
                                               "user_high_id" INTEGER NOT NULL,
                                               "conversation_id" INTEGER NOT NULL,
                                               "created_time" TIMESTAMP NOT NULL,
                                               UNIQUE ("user_low_id", "user_high_id", "conversation_id"),
                                               CONSTRAINT "FK__users" FOREIGN KEY ("user_low_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE,
                                               CONSTRAINT "FK__users_2" FOREIGN KEY ("user_high_id") REFERENCES "users" ("user_id") ON UPDATE CASCADE ON DELETE CASCADE,
                                               CONSTRAINT "FK__conversations" FOREIGN KEY ("conversation_id") REFERENCES "conversations" ("conversation_id") ON UPDATE CASCADE ON DELETE CASCADE
)
;
COMMENT ON TABLE "conversation_one_to_one_map" IS '解决“我和你只能有一个会话”的问题，不然并发下容易创建两个会话';
COMMENT ON COLUMN "conversation_one_to_one_map"."user_low_id" IS '用户id，较小的参与者id';
COMMENT ON COLUMN "conversation_one_to_one_map"."user_high_id" IS '用户id，较大的参与者id';
COMMENT ON COLUMN "conversation_one_to_one_map"."conversation_id" IS '对话id';
COMMENT ON COLUMN "conversation_one_to_one_map"."created_time" IS '创建时间';

