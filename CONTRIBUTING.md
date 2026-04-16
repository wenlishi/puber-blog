# Git 提交规范

## 📋 基本要求

- **作者署名**：所有 commit 作者必须设置为 `wenlishi`（仅此一人）
- **提交格式**：遵循 Conventional Commits 规范，格式为 `<type>(<scope>): <简短描述>`

---

## 类型要求

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(用户): 添加邮箱验证功能` |
| `fix` | 错误修复 | `fix(登录): 修复空令牌导致的崩溃` |
| `chore` | 杂项（构建流程、工具等） | `chore(依赖): 更新 axios 到 v2 版本` |
| `docs` | 文档更新 | `docs(README): 更新安装说明` |
| `refactor` | 重构（不修复错误也不添加功能） | `refactor(工具): 简化日期处理逻辑` |
| `test` | 测试相关 | `test(登录): 添加边界条件测试` |

---

## 描述要求

- ✅ 使用**中文**描述
- ✅ 语言**精简**，直接说明改动内容
- ✅ 禁止使用任何 emoji
- ✅ 避免冗长，主语通常省略

---

## 示例

### ✅ 正确的提交信息

```bash
git commit -m "fix(登录): 修复空令牌导致的崩溃"
git commit -m "chore(依赖): 更新 axios 到 v2 版本"
git commit -m "feat(用户): 添加邮箱验证功能"
git commit -m "docs(README): 更新安装说明"
git commit -m "refactor(工具): 简化日期处理逻辑"
git commit -m "test(登录): 添加边界条件测试"
```

### ❌ 错误的提交信息

```bash
# 禁止使用 emoji
git commit -m "修复了一个超级严重的 bug 🐛，这个 bug 会让系统崩溃"

# 禁止使用主语
git commit -m "我修复了空指针异常的问题"

# 禁止冗长
git commit -m "我今天花了一下午时间修复了一个问题，这个问题是因为空指针导致的，现在终于修好了"

# 禁止使用英文（除非专业术语）
git commit -m "fix: fix the login bug"
```

---

## Git 配置

### 设置作者信息

```bash
# 全局设置（推荐）
git config --global user.name "wenlishi"
git config --global user.email "your-email@example.com"

# 或仅在当前项目设置
cd puber-blog
git config user.name "wenlishi"
git config user.email "your-email@example.com"
```

### 验证配置

```bash
# 查看当前配置
git config user.name
git config user.email
```

---

## 快速参考

```bash
# 1. 确保在正确的分支
git checkout dev

# 2. 查看改动
git status
git diff

# 3. 添加文件
git add .

# 4. 提交（遵循规范）
git commit -m "type(scope): 描述"

# 5. 推送
git push origin dev
```

---

## 常见 Scope 参考

| Scope | 说明 |
|-------|------|
| `登录` | 登录/注册相关 |
| `用户` | 用户管理相关 |
| `文章` | 文章发布/编辑相关 |
| `分类` | 分类/标签管理 |
| `评论` | 评论系统相关 |
| `后台` | 后台管理界面 |
| `前台` | 前台展示页面 |
| `依赖` | 依赖包更新 |
| `配置` | 配置文件修改 |
| `工具` | 工具类/辅助函数 |
| `数据库` | 数据库相关 |
| `API` | 接口相关 |

---

_请严格遵守此规范，保持提交历史的专业性和可读性_
