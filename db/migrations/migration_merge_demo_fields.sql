-- =====================================================
-- 演示表字段合并迁移脚本
-- =====================================================
-- 版本：v2.0.0
-- 执行时间：2026-04-16
-- 说明：将html_content、css_content、js_content三个字段合并为full_html_content单一字段
-- =====================================================

-- 步骤1：添加新字段full_html_content
ALTER TABLE demo ADD COLUMN full_html_content TEXT;

COMMENT ON COLUMN demo.full_html_content IS '完整HTML内容（包含HTML/CSS/JS）';

-- 步骤2：合并现有数据到新字段
-- 将三部分内容合并为一个完整的HTML文件
UPDATE demo SET full_html_content =
    '<!DOCTYPE html>' || E'\n' ||
    '<html>' || E'\n' ||
    '<head>' || E'\n' ||
    '    <meta charset="UTF-8">' || E'\n' ||
    '    <style>' || E'\n' ||
    COALESCE(css_content, '') || E'\n' ||
    '    </style>' || E'\n' ||
    '</head>' || E'\n' ||
    '<body>' || E'\n' ||
    COALESCE(html_content, '') || E'\n' ||
    '    <script>' || E'\n' ||
    COALESCE(js_content, '') || E'\n' ||
    '    </script>' || E'\n' ||
    '</body>' || E'\n' ||
    '</html>'
WHERE full_html_content IS NULL;

-- 步骤3：删除旧字段
ALTER TABLE demo DROP COLUMN html_content;
ALTER TABLE demo DROP COLUMN css_content;
ALTER TABLE demo DROP COLUMN js_content;

-- 步骤4：设置新字段为非空约束
ALTER TABLE demo ALTER COLUMN full_html_content SET NOT NULL;

-- =====================================================
-- 迁移完成
-- =====================================================