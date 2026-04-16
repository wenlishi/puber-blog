# 数据库脚本目录说明

## 目录结构

```
db/
├── init/              # 数据库初始化脚本
│   └── init.sql       # 系统初始化SQL（建表、初始数据）
│
└── migrations/        # 数据库迁移脚本
    ├── demo_table.sql                 # 演示表建表脚本
    ├── migration_merge_demo_fields.sql # 字段合并迁移（三字段→单字段）
    └── optimize_indexes.sql           # 索引优化脚本
```

## 使用方法

### 初始化数据库（首次部署）

```bash
# 方式1：手动执行
psql -h localhost -p 5432 -U postgres -d puber_blog -f db/init/init.sql

# 方式2：Docker自动执行
# docker-compose.yml已配置自动执行init.sql
docker-compose up -d
```

### 执行迁移脚本

```bash
# 演示表字段合并迁移
psql -h localhost -p 5432 -U postgres -d puber_blog -f db/migrations/migration_merge_demo_fields.sql

# 索引优化
psql -h localhost -p 5432 -U postgres -d puber_blog -f db/migrations/optimize_indexes.sql
```

### Docker部署说明

**docker-compose.yml配置**：
```yaml
services:
  postgres:
    volumes:
      - ./db/init/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
```

首次启动Docker容器时，PostgreSQL会自动执行`init.sql`。

**注意**：迁移脚本需要手动执行（不自动执行）。

## 脚本说明

### init.sql（系统初始化）

**包含内容**：
- 创建所有基础表（article、user、category、tag等）
- 创建索引
- 创建触发器
- 插入初始数据（管理员账户、默认设置等）

**执行时机**：首次部署，数据库为空时

### migration_merge_demo_fields.sql（字段合并迁移）

**目的**：将演示表的三字段设计合并为单字段设计

**变更内容**：
- 添加`full_html_content`字段
- 合并现有数据（html_content + css_content + js_content → full_html_content）
- 删除旧字段（html_content、css_content、js_content）

**执行时机**：已在生产环境执行（2026-04-16）

### optimize_indexes.sql（索引优化）

**目的**：优化高频查询性能

**包含内容**：
- 添加复合索引（status + published_at）
- 添加全文搜索索引（article content）
- 添加外键索引优化

**执行时机**：性能优化阶段

## 迁移管理建议

**未来迁移脚本命名规范**：
```
YYYYMMDD_description.sql

示例：
20260416_add_comment_table.sql
20260420_add_user_avatar_field.sql
20260425_optimize_article_indexes.sql
```

**迁移脚本模板**：
```sql
-- =====================================================
-- 迁移脚本：添加评论表
-- 版本：v1.1
-- 执行时间：2026-04-16
-- =====================================================

BEGIN;

-- 创建评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    article_id BIGINT NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES blog_user(id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_comment_article ON comment(article_id);
CREATE INDEX idx_comment_user ON comment(user_id);

COMMIT;
```

## 备份策略

**定期备份**：
```bash
# 每天备份
0 2 * * * pg_dump -h localhost -U postgres puber_blog > backup/db-$(date +\%Y\%m\%d).sql
```

**迁移前备份**：
```bash
# 执行迁移前先备份
pg_dump puber_blog > backup/pre-migration-$(date +\%Y\%m\%d).sql

# 执行迁移
psql -d puber_blog -f db/migrations/new_migration.sql
```

---

**维护建议**：迁移脚本执行后，在脚本头部添加已执行标记