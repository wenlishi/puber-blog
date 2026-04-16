# Docker部署完整学习文档

## 目录
1. [Docker架构设计原理](#docker架构设计原理)
2. [部署问题排查复盘](#部署问题排查复盘)
3. [云服务器部署指南](#云服务器部署指南)

---

## Docker架构设计原理

### 为什么使用三个容器镜像？

#### 微服务架构优势

**传统单体部署**：
```
一台服务器 → 安装Java、PostgreSQL、Nginx → 所有服务混在一起
```
- ❌ 更新困难：改一个功能要重启所有服务
- ❌ 数据不安全：应用崩溃可能导致数据库损坏
- ❌ 扩展受限：无法单独扩容某个服务

**Docker微服务架构**：
```
puber-blog-app容器（Java应用）
puber-blog-db容器（PostgreSQL数据库）
puber-blog-nginx容器（Nginx反向代理）
```

#### 三层分离架构详解

##### 1. **nginx:alpine容器** - 反向代理层
```
职责：流量入口、负载均衡、静态资源缓存
位置：面向用户，暴露80/443端口
优势：
  - 专业处理HTTP请求
  - gzip压缩优化传输
  - SSL/HTTPS支持
  - 安全headers防护（XSS、Frame injection）
  - 静态资源缓存减轻应用压力
```

**为什么不是应用内置？**
- Nginx在处理高并发静态文件时性能远超Tomcat
- 应用崩溃时nginx仍可返回友好错误页面
- 可以扩展多个app实例实现负载均衡

##### 2. **puber-blog:latest容器** - 应用业务层
```
职责：业务逻辑处理、数据库操作
位置：仅内部通信（8080端口不对外）
优势：
  - 专注业务处理，不受其他服务干扰
  - 可以启动多个实例（横向扩展）
  - 更新时数据库不受影响
```

**为什么需要独立容器？**
- Java应用内存占用大，独立容器方便调整资源
- 更新频繁，独立容器重启快
- 可以部署不同版本测试（蓝绿部署）

##### 3. **postgres:15-alpine容器** - 数据持久层
```
职责：数据存储、查询优化
位置：最底层，完全隔离（5432端口不对外）
优势：
  - 数据卷持久化，容器重启数据不丢失
  - 专业数据库性能优化
  - 独立备份和恢复
```

**为什么不能和应用一起？**
- 数据库启动慢（30秒），应用启动快（5秒）
- 数据库崩溃时应用可继续运行缓存数据
- 数据库升级（PostgreSQL 14→15）不影响应用

#### Docker网络通信原理

```
用户请求流程：
用户浏览器 → nginx(80) → app(8080内部网络) → db(5432内部网络)

内部通信：
- nginx通过Docker网络访问app容器的8080端口
- app通过Docker网络访问db容器的5432端口
- 外部无法直接访问app和db（安全隔离）
```

**docker-compose.yml网络配置**：
```yaml
networks:
  puber-blog-network:
    driver: bridge  # 创建隔离的桥接网络

services:
  app:
    networks:
      - puber-blog-network  # 加入网络，可访问db

  db:
    networks:
      - puber-blog-network  # 加入网络，可被app访问

  nginx:
    networks:
      - puber-blog-network  # 加入网络，可访问app
    ports:
      - "80:80"  # 只有nginx对外暴露
```

---

## 部署问题排查复盘

### 问题1：密码登录失败

#### 问题现象
- 前台登录admin/admin123显示"用户名或密码错误"

#### 排查过程
```bash
# 1. 检查数据库中的密码hash
docker exec puber-blog-db psql -U postgres -d puber_blog -c \
  "SELECT username, password FROM blog_user WHERE username='admin';"

# 发现：密码hash格式异常
```

#### 根本原因
- **治标问题**：数据库中的密码hash不正确
- **治本问题**：初始化脚本中的BCrypt hash错误

#### 修复方案
```bash
# 临时修复（治标）：
# 运行PasswordTest生成正确的BCrypt hash
mvn test -Dtest=PasswordTest

# 输出正确hash：$2a$10$qXTZUpgXjlplImwSvh/Kku5xlIldtbqL4CHxQSBoeyTZIxyA1CVR2

# 更新数据库
docker exec puber-blog-db psql -U postgres -d puber_blog -c \
  "UPDATE blog_user SET password = '正确hash' WHERE username = 'admin';"

# 根本修复（治本）：
# 更新初始化脚本db/init/init.sql中的密码hash
# 确保下次部署不会重复问题
```

#### 学习要点
- ✅ 修复问题要从根源入手（治本比治标更重要）
- ✅ Docker初始化脚本只在首次启动执行，修改后需重建数据库

---

### 问题2：评论提交失败（UTF-8编码）

#### 问题现象
- 前台提交中文评论返回500错误
- 浏览器控制台显示"Invalid UTF-8 start byte"

#### 排查过程
```bash
# 1. 查看应用日志
docker logs puber-blog-app --tail 100 | grep -i "utf"

# 发现：JSON解析错误，UTF-8编码问题

# 2. 测试纯英文评论（成功）
curl -X POST http://localhost/api/public/comments \
  -H "Content-Type: application/json" \
  -d '{"nickname":"test","content":"test"}'

# 结论：英文评论正常，中文编码有问题
```

#### 根本原因
- Spring Boot缺少UTF-8编码配置
- application.yml中未强制指定UTF-8编码

#### 修复方案
```yaml
# application.yml添加UTF-8配置
spring:
  http:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

server:
  servlet:
    encoding:
      charset: UTF-8
      force: true
      force-response: true
```

```javascript
// JavaScript添加charset声明
fetch(API.POST_COMMENT, {
    headers: {
        'Content-Type': 'application/json; charset=UTF-8'
    }
})
```

#### 学习要点
- ✅ Spring Boot 3.x需要显式配置UTF-8编码
- ✅ 测试时要区分英文和中文数据

---

### 问题3：JavaScript文件400错误（最关键）

#### 问题现象
- 浏览器控制台：`GET /js/article-comments.js 400 (Bad Request)`
- 前台评论一直转圈无法显示

#### 排查过程（层层深入）

**第1层：检查文件是否存在**
```bash
ls -la src/main/resources/static/js/article-comments.js
# 文件存在 ✓

jar -tf target/blog-0.0.1-SNAPSHOT.jar | grep article-comments.js
# jar包中有文件 ✓
```

**第2层：检查静态资源配置**
```bash
curl http://localhost/js/article-comments.js
# 返回400 ✗

curl http://localhost:8080/js/article-comments.js
# 返回400 ✗（应用端口也失败）
```

**第3层：容器内部测试（关键突破）**
```bash
# 从nginx容器访问app服务
docker exec puber-blog-nginx curl http://app:8080/js/article-comments.js
# 文件正常返回 ✓✓✓

# 结论：应用本身没问题，问题在nginx！
```

**第4层：检查nginx配置**
```nginx
# 原配置（错误）：
location ~* \.(js|css)$ {
    proxy_pass http://puber_blog_app;
    # 缺少proxy headers配置 ✗
}

# 修复后（正确）：
location ~* \.(js|css)$ {
    proxy_pass http://puber_blog_app;
    proxy_set_header Host $host;          # 添加 ✓
    proxy_set_header X-Real-IP $remote_addr;  # 添加 ✓
}
```

#### 根本原因
- nginx静态资源location缺少proxy headers
- 导致转发到Spring Boot时Host等关键信息丢失
- Spring Boot无法正确处理请求返回400

#### 修复方案
```nginx
# nginx.conf添加完整的proxy headers
location ~* \.(jpg|jpeg|png|gif|ico|css|js|woff|woff2|ttf|svg)$ {
    proxy_pass http://puber_blog_app;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

```bash
# 重启nginx应用新配置
docker-compose restart nginx

# 验证修复
curl http://localhost/js/article-comments.js | head -15
# JavaScript文件正常返回 ✓
```

#### 学习要点（最重要）
- ✅ **排查Docker问题要层层深入**：文件存在→配置正确→容器内部测试→定位nginx
- ✅ **容器内部测试是关键技巧**：绕过外部网络直接验证应用本身
- ✅ **nginx反向代理需要完整的headers配置**：缺少headers会导致后端应用异常

---

### 问题4：Docker Hub镜像下载TLS错误

#### 问题现象
- `docker-compose pull postgres nginx`失败
- 错误：`tls: certificate valid for o.kaikozlov.com, not auth.docker.io`

#### 根本原因
- v2rayN代理的TUN模式拦截HTTPS流量
- 导致TLS证书验证失败

#### 修复方案
```json
// daemon.json配置Docker使用HTTP代理端口（不使用TUN）
{
  "proxies": {
    "default": {
      "httpProxy": "http://127.0.0.1:10808",
      "httpsProxy": "http://127.0.0.1:10808"
    }
  }
}
```

#### 学习要点
- ✅ Docker配置HTTP代理避免TLS拦截
- ✅ 重启Docker Desktop使代理配置生效

---

## 云服务器部署指南

### 方案一：直接部署（推荐新手）

#### 步骤1：购买云服务器
```
推荐配置：
- CPU：2核
- 内存：4GB（应用2GB + 数据库1GB + 系统1GB）
- 磁盘：50GB（系统20GB + 数据库20GB + 上传文件10GB）
- 带宽：5Mbps（可升级）

云服务商选择：
- 阿里云：国内稳定，备案方便
- 腾讯云：性价比高
- 华为云：政企用户
```

#### 步骤2：服务器环境准备

**安装Docker和Docker Compose**：
```bash
# CentOS/RHEL系统
yum update -y
yum install -y docker docker-compose
systemctl start docker
systemctl enable docker

# Ubuntu系统
apt update
apt install -y docker.io docker-compose
systemctl start docker
systemctl enable docker

# 验证安装
docker --version
docker-compose --version
```

**配置防火墙**：
```bash
# 开放HTTP和HTTPS端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --reload

# 或Ubuntu系统
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

#### 步骤3：上传代码到服务器

**方式1：Git克隆（推荐）**
```bash
# 在服务器上
cd /opt
git clone https://github.com/你的用户名/puber-blog.git
cd puber-blog
```

**方式2：本地打包上传**
```bash
# 在本地Windows
# 排除本地配置文件
tar -czf puber-blog.tar.gz \
  --exclude=.env \
  --exclude=target \
  --exclude=.idea \
  --exclude=logs \
  .

# 上传到服务器
scp puber-blog.tar.gz root@你的服务器IP:/opt/

# 在服务器解压
ssh root@你的服务器IP
cd /opt
tar -xzf puber-blog.tar.gz
```

#### 步骤4：修改生产配置

**创建.env文件**：
```bash
cd /opt/puber-blog
vim .env

# 输入配置：
DB_PASSWORD=你的强密码_here
JAVA_OPTS=-Xms1024m -Xmx2048m -XX:+UseG1GC
SPRING_PROFILES_ACTIVE=prod
```

**修改docker-compose.yml**：
```yaml
services:
  app:
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/puber_blog
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}

  postgres:
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    # 不暴露5432端口（保持安全）
```

**创建application-prod.yml**：
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/puber_blog
    username: postgres
    password: ${DB_PASSWORD}

server:
  port: 8080

logging:
  level:
    com.puber.blog: info
```

#### 步骤5：编译并部署

**本地编译（避免服务器安装Maven）**：
```bash
# 在本地Windows
mvn clean package -DskipTests

# 上传jar包到服务器
scp target/blog-0.0.1-SNAPSHOT.jar root@服务器IP:/opt/puber-blog/target/
```

**服务器启动**：
```bash
cd /opt/puber-blog

# 启动容器（首次会初始化数据库）
docker-compose up -d

# 查看启动日志
docker-compose logs -f app

# 等待看到 "Started PuberBlogApplication" ✓
```

#### 步骤6：配置域名和SSL（可选）

**配置Nginx SSL**：
```bash
# 申请免费SSL证书（Let's Encrypt）
apt install -y certbot

# 获取证书
certbot certonly --standalone -d yourdomain.com

# 证书路径：
/etc/letsencrypt/live/yourdomain.com/fullchain.pem
/etc/letsencrypt/live/yourdomain.com/privkey.pem
```

**修改nginx.conf启用HTTPS**：
```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    location / {
        proxy_pass http://puber_blog_app;
        # 其他配置同上
    }
}

# HTTP重定向到HTTPS
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

**挂载SSL证书到nginx容器**：
```yaml
# docker-compose.yml
nginx:
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf:ro
    - /etc/letsencrypt:/etc/nginx/ssl:ro
```

```bash
# 重启nginx
docker-compose restart nginx
```

#### 步骤7：配置数据库定时备份

```bash
# 创建备份脚本
vim /opt/puber-blog/backup.sh

#!/bin/bash
BACKUP_DIR="/opt/puber-blog/backups"
DATE=$(date +%Y%m%d_%H%M%S)

docker exec puber-blog-db pg_dump -U postgres puber_blog > \
  $BACKUP_DIR/puber_blog_$DATE.sql

# 保留最近7天的备份
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete

# 添加执行权限
chmod +x backup.sh

# 配置定时任务（每天凌晨2点备份）
crontab -e
# 输入：
0 2 * * * /opt/puber-blog/backup.sh
```

---

### 方案二：CI/CD自动化部署（推荐团队）

#### GitHub Actions自动部署

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [master]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout代码
      uses: actions/checkout@v3

    - name: 构建应用
      run: mvn clean package -DskipTests

    - name: 构建Docker镜像
      run: docker build -t puber-blog:${{ github.sha }} .

    - name: 推送到Docker Hub
      run: |
        docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
        docker push puber-blog:${{ github.sha }}

    - name: SSH部署到服务器
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_PRIVATE_KEY }}
        script: |
          cd /opt/puber-blog
          docker-compose pull app
          docker-compose up -d --no-deps app
          docker-compose restart nginx
```

#### 学习要点
- ✅ 推送代码自动触发部署
- ✅ 每次部署使用唯一镜像tag（git sha）
- ✅ 敏感信息存储在GitHub Secrets

---

### 云服务器运维监控

#### 监控容器状态

```bash
# 实时查看容器状态
docker-compose ps

# 查看容器资源占用
docker stats puber-blog-app puber-blog-db puber-blog-nginx

# 查看应用日志
docker-compose logs -f --tail=100 app

# 查看nginx访问日志
docker exec puber-blog-nginx tail -f /var/log/nginx/access.log
```

#### 配置监控告警（可选）

```bash
# 安装Prometheus和Grafana监控
docker run -d \
  --name=prometheus \
  -p 9090:9090 \
  prom/prometheus

docker run -d \
  --name=grafana \
  -p 3000:3000 \
  grafana/grafana

# 配置监控指标：CPU、内存、磁盘、网络
```

#### 常见运维操作

**重启应用**：
```bash
docker-compose restart app
```

**更新应用代码**：
```bash
git pull
mvn clean package -DskipTests
docker-compose build app
docker-compose up -d --no-deps app
```

**扩容应用实例**：
```yaml
# docker-compose.yml
app:
  deploy:
    replicas: 3  # 启动3个实例
```

```bash
docker-compose up -d --scale app=3
# nginx自动负载均衡到3个实例
```

**数据库迁移**：
```bash
# 本地开发新SQL脚本
vim db/migrations/v2_add_new_table.sql

# 云服务器执行迁移
scp db/migrations/v2_add_new_table.sql root@服务器IP:/opt/puber-blog/
docker exec -i puber-blog-db psql -U postgres -d puber_blog < \
  /opt/puber-blog/v2_add_new_table.sql
```

---

## 总结：Docker部署核心要点

### 1. **架构设计**
- ✅ 三层分离：nginx(流量入口) + app(业务逻辑) + db(数据持久化)
- ✅ 网络隔离：只暴露nginx端口，应用和数据库完全隔离
- ✅ 数据持久化：数据库数据卷独立，容器重启不丢失数据

### 2. **排查问题技巧**
- ✅ 分层排查：文件存在 → 配置正确 → 容器内部测试 → 定位组件
- ✅ 容器内部测试：绕过外部网络直接验证应用本身
- ✅ 查看日志：`docker logs 容器名 --tail 100 -f`

### 3. **云服务器部署**
- ✅ 环境准备：安装Docker、配置防火墙
- ✅ 生产配置：强密码、SSL证书、定时备份
- ✅ 监控运维：容器状态监控、资源占用监控

### 4. **安全要点**
- ✅ 数据库和应用端口不对外暴露
- ✅ .env文件不提交到Git（敏感信息）
- ✅ SSL证书配置HTTPS加密传输
- ✅ 数据库定时备份避免数据丢失

---

## 附录：常用Docker命令速查

```bash
# 启动所有容器
docker-compose up -d

# 查看容器状态
docker-compose ps

# 查看容器日志
docker-compose logs -f app

# 重启单个容器
docker-compose restart nginx

# 停止所有容器
docker-compose down

# 停止并删除数据卷（重新初始化数据库）
docker-compose down -v

# 进入容器内部
docker exec -it puber-blog-app sh

# 从容器复制文件到本地
docker cp puber-blog-app:/app/logs/app.log ./logs/

# 构建镜像
docker-compose build app

# 查看镜像列表
docker images

# 清理无用镜像（释放磁盘空间）
docker image prune -a
```

---

**文档版本**：v1.0  
**创建时间**：2026-04-16  
**适用场景**：Spring Boot + PostgreSQL + Nginx Docker部署  
**作者**：wenlishi

**相关文件**：
- docker-compose.yml（容器编排配置）
- Dockerfile（应用镜像构建）
- nginx.conf（反向代理配置）
- db/init/init.sql（数据库初始化）
- .env.example（环境变量模板）