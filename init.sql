-- =====================================================
-- puber-blog 数据库初始化脚本
-- =====================================================
-- 版本：v1.0.0
-- 创建时间：2026-04-13
-- 说明：PostgreSQL 数据库建表脚本，可重复执行
-- 变更记录：
--   v1.0.0 (2026-04-13) - 初始版本，创建所有基础表
-- =====================================================

-- 创建用户表（注意：user 是 PostgreSQL 保留字，使用 blog_user）
CREATE TABLE IF NOT EXISTS blog_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    avatar VARCHAR(255),
    bio TEXT,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_ADMIN',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE blog_user IS '用户表';
COMMENT ON COLUMN blog_user.id IS '用户ID';
COMMENT ON COLUMN blog_user.username IS '用户名';
COMMENT ON COLUMN blog_user.password IS '密码（加密存储）';
COMMENT ON COLUMN blog_user.nickname IS '昵称';
COMMENT ON COLUMN blog_user.email IS '邮箱';
COMMENT ON COLUMN blog_user.avatar IS '头像URL';
COMMENT ON COLUMN blog_user.bio IS '个人简介';
COMMENT ON COLUMN blog_user.role IS '角色（ROLE_ADMIN/ROLE_USER）';
COMMENT ON COLUMN blog_user.enabled IS '是否启用';
COMMENT ON COLUMN blog_user.created_at IS '创建时间';
COMMENT ON COLUMN blog_user.updated_at IS '更新时间';

-- 创建分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE category IS '分类表';
COMMENT ON COLUMN category.id IS '分类ID';
COMMENT ON COLUMN category.name IS '分类名称';
COMMENT ON COLUMN category.slug IS '分类别名（URL友好）';
COMMENT ON COLUMN category.description IS '分类描述';
COMMENT ON COLUMN category.sort_order IS '排序顺序';
COMMENT ON COLUMN category.created_at IS '创建时间';
COMMENT ON COLUMN category.updated_at IS '更新时间';

-- 创建标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tag IS '标签表';
COMMENT ON COLUMN tag.id IS '标签ID';
COMMENT ON COLUMN tag.name IS '标签名称';
COMMENT ON COLUMN tag.slug IS '标签别名（URL友好）';
COMMENT ON COLUMN tag.created_at IS '创建时间';
COMMENT ON COLUMN tag.updated_at IS '更新时间';

-- 创建文章表
CREATE TABLE IF NOT EXISTS article (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    summary TEXT,
    content TEXT NOT NULL,
    cover_image VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_top BOOLEAN NOT NULL DEFAULT false,
    view_count BIGINT NOT NULL DEFAULT 0,
    is_comment_enabled BOOLEAN NOT NULL DEFAULT true,
    author_id BIGINT NOT NULL,
    category_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_article_author FOREIGN KEY (author_id) REFERENCES blog_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_article_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

COMMENT ON TABLE article IS '文章表';
COMMENT ON COLUMN article.id IS '文章ID';
COMMENT ON COLUMN article.title IS '文章标题';
COMMENT ON COLUMN article.slug IS '文章别名（URL友好）';
COMMENT ON COLUMN article.summary IS '文章摘要';
COMMENT ON COLUMN article.content IS '文章内容（Markdown格式）';
COMMENT ON COLUMN article.cover_image IS '封面图片URL';
COMMENT ON COLUMN article.status IS '状态（PUBLISHED/DRAFT）';
COMMENT ON COLUMN article.is_top IS '是否置顶';
COMMENT ON COLUMN article.view_count IS '浏览次数';
COMMENT ON COLUMN article.is_comment_enabled IS '是否允许评论';
COMMENT ON COLUMN article.author_id IS '作者ID';
COMMENT ON COLUMN article.category_id IS '分类ID';
COMMENT ON COLUMN article.created_at IS '创建时间';
COMMENT ON COLUMN article.updated_at IS '更新时间';
COMMENT ON COLUMN article.published_at IS '发布时间';

-- 创建文章标签关联表
CREATE TABLE IF NOT EXISTS article_tag (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_article_tag_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    CONSTRAINT fk_article_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE,
    UNIQUE (article_id, tag_id)
);

COMMENT ON TABLE article_tag IS '文章标签关联表';
COMMENT ON COLUMN article_tag.id IS '关联ID';
COMMENT ON COLUMN article_tag.article_id IS '文章ID';
COMMENT ON COLUMN article_tag.tag_id IS '标签ID';
COMMENT ON COLUMN article_tag.created_at IS '创建时间';

-- 创建评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGSERIAL PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    website VARCHAR(255),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ip_address VARCHAR(50),
    user_agent TEXT,
    article_id BIGINT NOT NULL,
    parent_id BIGINT,
    reply_to_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_reply_to FOREIGN KEY (reply_to_id) REFERENCES comment(id) ON DELETE SET NULL
);

COMMENT ON TABLE comment IS '评论表';
COMMENT ON COLUMN comment.id IS '评论ID';
COMMENT ON COLUMN comment.nickname IS '评论者昵称';
COMMENT ON COLUMN comment.email IS '评论者邮箱';
COMMENT ON COLUMN comment.website IS '评论者网站';
COMMENT ON COLUMN comment.content IS '评论内容';
COMMENT ON COLUMN comment.status IS '状态（PENDING/APPROVED/REJECTED）';
COMMENT ON COLUMN comment.ip_address IS 'IP地址';
COMMENT ON COLUMN comment.user_agent IS '用户代理';
COMMENT ON COLUMN comment.article_id IS '文章ID';
COMMENT ON COLUMN comment.parent_id IS '父评论ID（用于多级回复）';
COMMENT ON COLUMN comment.reply_to_id IS '回复的评论ID';
COMMENT ON COLUMN comment.created_at IS '创建时间';
COMMENT ON COLUMN comment.updated_at IS '更新时间';

-- 创建网站配置表
CREATE TABLE IF NOT EXISTS site_setting (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE site_setting IS '网站配置表';
COMMENT ON COLUMN site_setting.id IS '配置ID';
COMMENT ON COLUMN site_setting.setting_key IS '配置键';
COMMENT ON COLUMN site_setting.setting_value IS '配置值';
COMMENT ON COLUMN site_setting.setting_type IS '配置类型（STRING/INTEGER/BOOLEAN/JSON）';
COMMENT ON COLUMN site_setting.description IS '配置描述';
COMMENT ON COLUMN site_setting.created_at IS '创建时间';
COMMENT ON COLUMN site_setting.updated_at IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_article_status ON article(status);
CREATE INDEX IF NOT EXISTS idx_article_created_at ON article(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_article_category_id ON article(category_id);
CREATE INDEX IF NOT EXISTS idx_article_author_id ON article(author_id);
CREATE INDEX IF NOT EXISTS idx_article_is_top ON article(is_top DESC);
CREATE INDEX IF NOT EXISTS idx_article_published_at ON article(published_at DESC);

CREATE INDEX IF NOT EXISTS idx_comment_status ON comment(status);
CREATE INDEX IF NOT EXISTS idx_comment_article_id ON comment(article_id);
CREATE INDEX IF NOT EXISTS idx_comment_created_at ON comment(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_comment_parent_id ON comment(parent_id);

CREATE INDEX IF NOT EXISTS idx_category_sort_order ON category(sort_order);
CREATE INDEX IF NOT EXISTS idx_category_slug ON category(slug);

CREATE INDEX IF NOT EXISTS idx_tag_slug ON tag(slug);

CREATE INDEX IF NOT EXISTS idx_article_tag_article_id ON article_tag(article_id);
CREATE INDEX IF NOT EXISTS idx_article_tag_tag_id ON article_tag(tag_id);

CREATE INDEX IF NOT EXISTS idx_site_setting_key ON site_setting(setting_key);

-- 插入默认管理员账号
-- 密码：admin123（使用 BCrypt 加密）
INSERT INTO blog_user (username, password, nickname, email, role, enabled)
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Administrator', 'admin@example.com', 'ROLE_ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM blog_user WHERE username = 'admin');

-- 插入默认网站配置
INSERT INTO site_setting (setting_key, setting_value, setting_type, description)
SELECT 'site_name', 'My Blog', 'STRING', '网站名称'
WHERE NOT EXISTS (SELECT 1 FROM site_setting WHERE setting_key = 'site_name');

INSERT INTO site_setting (setting_key, setting_value, setting_type, description)
SELECT 'site_description', 'A personal blog built with Spring Boot', 'STRING', '网站描述'
WHERE NOT EXISTS (SELECT 1 FROM site_setting WHERE setting_key = 'site_description');

INSERT INTO site_setting (setting_key, setting_value, setting_type, description)
SELECT 'site_keywords', 'blog, spring boot, java', 'STRING', '网站关键词'
WHERE NOT EXISTS (SELECT 1 FROM site_setting WHERE setting_key = 'site_keywords');

INSERT INTO site_setting (setting_key, setting_value, setting_type, description)
SELECT 'footer_text', 'Powered by Spring Boot', 'STRING', '页脚文字'
WHERE NOT EXISTS (SELECT 1 FROM site_setting WHERE setting_key = 'footer_text');

INSERT INTO site_setting (setting_key, setting_value, setting_type, description)
SELECT 'posts_per_page', '10', 'INTEGER', '每页文章数'
WHERE NOT EXISTS (SELECT 1 FROM site_setting WHERE setting_key = 'posts_per_page');

-- 创建更新时间自动更新触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为各表创建触发器
DROP TRIGGER IF EXISTS update_blog_user_updated_at ON blog_user;
CREATE TRIGGER update_blog_user_updated_at
    BEFORE UPDATE ON blog_user
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_category_updated_at ON category;
CREATE TRIGGER update_category_updated_at
    BEFORE UPDATE ON category
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_tag_updated_at ON tag;
CREATE TRIGGER update_tag_updated_at
    BEFORE UPDATE ON tag
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_article_updated_at ON article;
CREATE TRIGGER update_article_updated_at
    BEFORE UPDATE ON article
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_comment_updated_at ON comment;
CREATE TRIGGER update_comment_updated_at
    BEFORE UPDATE ON comment
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_site_setting_updated_at ON site_setting;
CREATE TRIGGER update_site_setting_updated_at
    BEFORE UPDATE ON site_setting
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- 初始化完成
-- =====================================================