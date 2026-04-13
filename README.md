# puber-blog

基于 Spring Boot + Thymeleaf + PostgreSQL 开发的个人博客系统。

## 项目介绍

puber-blog 是一个功能完善的个人博客系统，支持文章发布、分类标签管理、评论系统等功能。项目采用前后端分离的设计思路，前台使用 Thymeleaf 模板引擎渲染，后台提供完整的文章管理界面。

## 功能概览

### 用户模块
- 管理员登录/退出
- 个人信息修改（头像、昵称、简介）
- 修改密码

### 文章模块
- 文章列表（分页展示）
- 文章发布（支持 Markdown 编辑器）
- 文章编辑/删除
- 文章详情页
- 文章置顶功能
- 草稿保存
- 浏览量统计

### 分类模块
- 分类的增删改查
- 按分类筛选文章

### 标签模块
- 标签的增删改查
- 按标签筛选文章

### 评论模块
- 游客评论（昵称 + 邮箱）
- 评论审核（后台管理）
- 评论回复

### 前台展示
- 首页（最新文章列表 + 侧边栏）
- 文章详情页
- 分类页/标签页
- 归档页（按时间归档）
- 关于我页面
- 搜索功能

### 后台管理
- 仪表盘（文章数、评论数、访问量统计）
- 文章管理
- 分类/标签管理
- 评论管理
- 系统设置（博客名称、个人信息等）

## 技术栈

### 后端
- **Spring Boot 3.2.1** - 应用框架
- **Spring Data JPA** - ORM 框架
- **Spring Security** - 安全框架
- **Spring Validation** - 数据验证
- **PostgreSQL 15.x** - 关系型数据库
- **Lombok** - 简化代码

### 前端
- **Thymeleaf** - 模板引擎
- **Bootstrap 5** - CSS 框架
- **CommonMark** - Markdown 解析器

### 构建工具
- **Maven 3.8+** - 项目构建管理
- **JDK 17** - Java 开发环境

## 环境搭建

### 前置要求
- JDK 17（两端必须版本一致）
- Maven 3.8+
- PostgreSQL 15.x
- Git

### Windows 本地环境搭建

#### 1. 安装 JDK 17

下载并安装 JDK 17：
```bash
# 下载地址
https://www.oracle.com/java/technologies/downloads/#java17

# 或使用 OpenJDK
https://adoptium.net/

# 安装后配置环境变量
JAVA_HOME=C:\Program Files\Java\jdk-17
Path=%JAVA_HOME%\bin

# 验证安装
java -version
javac -version
```

#### 2. 安装 Maven

下载并安装 Maven 3.8+：
```bash
# 下载地址
https://maven.apache.org/download.cgi

# 解压到目录，配置环境变量
MAVEN_HOME=C:\Program Files\Apache\maven-3.8.x
Path=%MAVEN_HOME%\bin

# 验证安装
mvn -version
```

#### 3. 安装 PostgreSQL

下载并安装 PostgreSQL 15.x：
```bash
# 下载地址
https://www.postgresql.org/download/windows/

# 安装时记住设置的密码（默认用户名：postgres）
# 默认端口：5432

# 验证安装
psql -U postgres -h localhost
```

#### 4. 创建数据库

使用 pgAdmin 或命令行创建数据库：
```bash
# 命令行方式
psql -U postgres
CREATE DATABASE puber_blog;
\q

# 或使用 pgAdmin 图形界面创建数据库 puber_blog
```

#### 5. 初始化数据库

执行 init.sql 脚本：
```bash
# 方式一：使用 pgAdmin 图形界面
# 打开 Query Tool，复制 init.sql 内容执行

# 方式二：命令行执行
psql -U postgres -d puber_blog -f init.sql

# 或在 psql 交互界面执行
psql -U postgres -d puber_blog
\i init.sql
```

#### 6. 配置 application-dev.yml

复制配置模板：
```bash
cd src/main/resources
cp application-dev.yml.example application-dev.yml
```

修改 application-dev.yml 配置：
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/puber_blog
    username: postgres
    password: your_actual_password  # 修改为你的数据库密码
    driver-class-name: org.postgresql.Driver

file:
  upload:
    path: E:/uploads  # Windows 上传文件路径
```

### Linux VPS 环境搭建

#### 1. 安装 JDK 17

使用包管理器安装：
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# 验证安装
java -version
javac -version
```

#### 2. 安装 Maven

下载并安装 Maven：
```bash
cd /opt
sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo ln -s apache-maven-3.9.6 maven

# 配置环境变量
sudo nano /etc/profile.d/maven.sh

# 添加以下内容
export MAVEN_HOME=/opt/maven
export PATH=$MAVEN_HOME/bin:$PATH

# 使配置生效
source /etc/profile.d/maven.sh

# 验证安装
mvn -version
```

#### 3. 安装 PostgreSQL

使用包管理器安装：
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# CentOS/RHEL
sudo yum install postgresql-server postgresql-contrib
sudo postgresql-setup initdb
sudo systemctl start postgresql

# 启动服务
sudo systemctl start postgresql
sudo systemctl enable postgresql

# 切换到 postgres 用户
sudo -u postgres psql

# 创建数据库
CREATE DATABASE puber_blog;

# 设置密码
ALTER USER postgres WITH PASSWORD 'your_password';
\q
```

#### 4. 初始化数据库

执行 init.sql 脚本：
```bash
# 上传 init.sql 到服务器后执行
sudo -u postgres psql -d puber_blog -f init.sql

# 或在 psql 交互界面执行
sudo -u postgres psql -d puber_blog
\i /path/to/init.sql
```

#### 5. 配置 application-dev.yml

创建配置文件：
```bash
cd src/main/resources
cp application-dev.yml.example application-dev.yml
nano application-dev.yml
```

修改配置：
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/puber_blog
    username: postgres
    password: your_actual_password
    driver-class-name: org.postgresql.Driver

file:
  upload:
    path: /var/uploads  # Linux 上传文件路径
```

#### 6. 使用 screen/tmux 保持会话

推荐使用 screen 或 tmux 防止断连中断开发：
```bash
# 安装 screen
sudo apt install screen  # Ubuntu/Debian
sudo yum install screen  # CentOS/RHEL

# 创建会话
screen -S puber-blog

# 或使用 tmux
sudo apt install tmux
tmux new -s puber-blog

# 分离会话：Ctrl+A D (screen) 或 Ctrl+B D (tmux)
# 重新连接：
screen -r puber-blog
tmux attach -t puber-blog
```

## 项目启动

### 1. 克隆项目

```bash
git clone https://github.com/your-username/puber-blog.git
cd puber-blog
```

### 2. 编译项目

```bash
mvn clean install
```

### 3. 启动项目

开发模式启动：
```bash
mvn spring-boot:run
```

或打包后启动：
```bash
mvn clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

访问应用：
- 前台首页：http://localhost:8080
- 后台管理：http://localhost:8080/admin

默认管理员账号：
- 用户名：admin
- 密码：admin123

## Git 日常同步工作流

### 开始开发前

每次开始开发前，先拉取最新代码：
```bash
git pull origin main
```

### 结束开发后

提交并推送代码：
```bash
# 添加文件
git add .

# 提交（使用规范的 commit 信息）
git commit -m "feat: 新增文章发布功能"

# 推送
git push origin main
```

### Commit 信息规范

使用规范的 commit 信息格式：
- `feat:` - 新功能
- `fix:` - 修复 bug
- `style:` - 样式调整
- `refactor:` - 代码重构
- `docs:` - 文档更新
- `chore:` - 其他修改

示例：
```bash
git commit -m "feat: 新增文章发布功能"
git commit -m "fix: 修复评论审核 bug"
git commit -m "style: 调整首页样式"
git commit -m "refactor: 重构用户 Service"
git commit -m "docs: 更新 README"
git commit -m "chore: 更新依赖版本"
```

### 处理冲突

如果 pull 时出现冲突：
```bash
# 查看冲突文件
git status

# 手动编辑冲突文件，解决冲突后
git add <冲突文件>
git commit -m "fix: 解决合并冲突"
git push origin main
```

## 常见问题排查

### 1. 端口占用

如果 8080 端口被占用：
```bash
# Windows
netstat -ano | findstr 8080
taskkill /PID <进程ID> /F

# Linux
lsof -i:8080
kill -9 <进程ID>

# 或修改 application.yml 中的端口
server:
  port: 8081
```

### 2. 数据库连接失败

检查 PostgreSQL 是否启动：
```bash
# Windows
# 检查服务是否运行

# Linux
sudo systemctl status postgresql
sudo systemctl start postgresql

# 测试连接
psql -U postgres -d puber_blog -h localhost
```

检查配置文件：
- 确认 application-dev.yml 已创建
- 确认数据库 URL、用户名、密码正确
- 确认 PostgreSQL 服务已启动

### 3. JDK 版本不一致

两端必须使用 JDK 17：
```bash
# 检查版本
java -version

# 如果版本不对，重新安装 JDK 17
# 并确保 JAVA_HOME 指向正确的 JDK 17 安装目录
```

### 4. 换行符问题

如果看到大量换行符差异：
```bash
# 确保 .gitattributes 文件存在
cat .gitattributes

# 重新规范化所有文件
git add --renormalize .
git status  # 查看是否还有换行符差异
```

### 5. Maven 依赖下载失败

清理并重新下载依赖：
```bash
mvn clean
mvn dependency:purge-local-repository
mvn install
```

检查 Maven 配置：
```bash
# 查看 Maven 仓库位置
mvn help:effective-settings

# 如果网络问题，可以配置国内镜像
# 编辑 ~/.m2/settings.xml
```

### 6. Lombok 不生效

如果 Lombok 生成的方法找不到：
- IDE 需要安装 Lombok 插件
- IntelliJ IDEA：Settings → Plugins → 安装 Lombok
- VS Code：安装 Lombok Annotations Support 扩展

### 7. Thymeleaf 缓存问题

开发模式下禁用缓存（已配置）：
```yaml
spring:
  thymeleaf:
    cache: false
```

如果修改模板后不生效，重启应用。

### 8. 文件上传失败

检查上传路径配置：
```yaml
file:
  upload:
    path: /var/uploads  # 确保路径存在且有写权限
```

Linux 创建上传目录：
```bash
sudo mkdir -p /var/uploads
sudo chmod 755 /var/uploads
```

Windows 创建上传目录：
```bash
mkdir E:\uploads
```

### 9. 中文乱码问题

确保数据库编码正确：
```sql
-- 查看数据库编码
SELECT pg_encoding_to_char(encoding) FROM pg_database WHERE datname = 'puber_blog';

-- 应该是 UTF8
```

确保 application.yml 配置了编码：
```yaml
server:
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true
```

### 10. Git 忽略文件不生效

如果 application-dev.yml 被追踪了：
```bash
# 从 Git 中移除（但保留本地文件）
git rm --cached src/main/resources/application-dev.yml
git commit -m "chore: 移除敏感配置文件追踪"

# 确保 .gitignore 包含
cat .gitignore | grep application-dev.yml
```

## 安全注意事项

1. **敏感文件不要提交到 Git**
   - application-dev.yml
   - application-prod.yml
   - 包含密码的任何文件

2. **数据库安全**
   - 定期备份数据库
   - 使用强密码
   - 如果密码泄露，立即修改

3. **GitHub 仓库安全**
   - 建议设置为 Private
   - 不要在公开仓库中提交敏感信息

4. **生产环境部署**
   - 使用 application-prod.yml
   - 修改默认管理员密码
   - 关闭开发模式特性（如 show-sql）

## 开发建议

1. **表结构变更**
   - 修改 init.sql 并注明版本
   - 在两端重新执行 init.sql
   - 使用 `CREATE TABLE IF NOT EXISTS` 保证幂等

2. **跨平台路径**
   - 使用 `/` 而不是 `\`
   - 或使用 `File.separator`
   - 不要硬编码路径

3. **代码规范**
   - 关键方法添加注释
   - 统一异常处理
   - 统一返回格式

4. **测试**
   - 编写单元测试
   - 测试关键业务逻辑
   - 在两端都测试

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或 Pull Request。