-- =====================================================
-- 交互式演示模块数据表
-- =====================================================
-- 版本：v1.0.0
-- 创建时间：2026-04-16
-- 说明：PostgreSQL 演示表，存储HTML/CSS/JS完整内容
-- =====================================================

CREATE TABLE IF NOT EXISTS demo (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    html_content TEXT NOT NULL,
    css_content TEXT,
    js_content TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    view_count BIGINT NOT NULL DEFAULT 0,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_demo_author FOREIGN KEY (author_id) REFERENCES blog_user(id) ON DELETE CASCADE
);

COMMENT ON TABLE demo IS '交互式演示表';
COMMENT ON COLUMN demo.id IS '演示ID';
COMMENT ON COLUMN demo.name IS '演示名称';
COMMENT ON COLUMN demo.slug IS '演示别名（URL友好，唯一）';
COMMENT ON COLUMN demo.description IS '演示描述';
COMMENT ON COLUMN demo.html_content IS 'HTML内容（TEXT类型，不经过XSS过滤）';
COMMENT ON COLUMN demo.css_content IS 'CSS样式内容';
COMMENT ON COLUMN demo.js_content IS 'JavaScript脚本内容';
COMMENT ON COLUMN demo.status IS '状态（PUBLISHED=已发布/DRAFT=草稿）';
COMMENT ON COLUMN demo.view_count IS '浏览次数';
COMMENT ON COLUMN demo.author_id IS '作者ID（关联用户表）';
COMMENT ON COLUMN demo.created_at IS '创建时间';
COMMENT ON COLUMN demo.updated_at IS '更新时间';
COMMENT ON COLUMN demo.published_at IS '发布时间';

-- 创建索引（优化高频查询）
CREATE INDEX IF NOT EXISTS idx_demo_status ON demo(status);
CREATE INDEX IF NOT EXISTS idx_demo_slug ON demo(slug);
CREATE INDEX IF NOT EXISTS idx_demo_created_at ON demo(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_demo_published_at ON demo(published_at DESC);

-- 创建复合索引（状态+发布时间，优化列表查询）
CREATE INDEX IF NOT EXISTS idx_demo_status_published ON demo(status, published_at DESC);

-- 创建触发器（自动更新updated_at）
DROP TRIGGER IF EXISTS update_demo_updated_at ON demo;
CREATE TRIGGER update_demo_updated_at
    BEFORE UPDATE ON demo
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- 表创建完成
-- =====================================================