# 部署指南 · 自有服务器(2C4G)

> 单机部署拓扑:一台 2C4G 服务器跑 Nginx(门户 + 反代)+ yudao 后端 + MySQL + Redis。
> 内存紧张时给系统加 2G swap 保底。

---

## 一、部署拓扑

```
                        ┌────────────────────────────── 服务器 (2C4G) ──┐
浏览器 ──HTTPS──▶ Nginx :443                                     │
                     ├── /               → 门户静态文件(本仓库)     │
                     ├── /yudao-api/*    → 反代 yudao-server:48080 │
                     ├── /admin/*        → 反代 yudao-ui-admin 构建 │
                     └── /healthz        → 静态 ok(门户存活探针)    │
                     yudao-server(JVM):48080 ──▶ MySQL :3306 / Redis :6379
```

---

## 二、服务器准备

```bash
# 2C4G 建议加 swap 保底
fallocate -l 2G /swapfile && chmod 600 /swapfile
mkswap /swapfile && swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab

# 安装基础环境
apt install -y nginx openjdk-17-jdk mysql-server redis-server maven
```

MySQL / Redis 用默认安装即可;为 yudao 建库建账号:

```sql
CREATE DATABASE yudao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'yudao'@'localhost' IDENTIFIED BY '换一个强密码';
GRANT ALL PRIVILEGES ON yudao.* TO 'yudao'@'localhost';
FLUSH PRIVILEGES;
```

---

## 三、部署 yudao 后端(systemd 守护)

```bash
# 1. 克隆/上传 yudao 工程(裁剪版),配置 application-local.yaml
#    spring.datasource → 上面的 yudao 库/账号
#    spring.data.redis → 本机 redis

# 2. 构建(跳过测试)
mvn clean package -Dmaven.test.skip=true

# 3. systemd 服务
cat > /etc/systemd/system/yudao.service <<'EOF'
[Unit]
Description=yudao club backend
After=network.target mysql.service redis-server.service

[Service]
User=root
WorkingDirectory=/opt/yudao
ExecStart=/usr/bin/java -Xms256m -Xmx768m -jar /opt/yudao/yudao-server.jar --spring.profiles.active=local
Restart=always
RestartSec=3
EOF

systemctl daemon-reload && systemctl enable --now yudao
systemctl status yudao          # 确认 active
curl http://127.0.0.1:48080/actuator/health   # yudao 自带健康检查
```

> yudao-boot 默认端口 48080。JVM 内存 `-Xmx768m` 为 2C4G 保守值,如需更大可到 1g。

---

## 四、构建并部署 yudao 管理后台前端

```bash
cd yudao-ui-admin
npm install
# .env.production 中 VITE_BASE_URL=/yudao-api(与门户同域反代)
npm run build:prod        # 产物在 dist/
cp -r dist /var/www/admin
```

---

## 五、部署门户(本仓库)

```bash
# 服务器上
mkdir -p /var/www/portal
rsync -av --exclude '.git' --exclude 'node_modules' \
  ./  root@服务器:/var/www/portal/
```

---

## 六、Nginx 完整配置

```nginx
server {
    listen 443 ssl http2;
    server_name club.example.com;          # 换成实际域名

    ssl_certificate     /etc/letsencrypt/live/club.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/club.example.com/privkey.pem;

    # ── 门户静态站点 ──
    root /var/www/portal;
    index index.html;
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源长缓存
    location ~* \.(css|js|png|jpg|svg|ico|woff2?)$ {
        expires 7d;
        add_header Cache-Control "public";
    }

    # 内容文件不缓存(改 content/ 立即可见)
    location ~ ^/content/ {
        add_header Cache-Control "no-cache";
    }

    # ── yudao API 反代 ──
    location /yudao-api/ {
        proxy_pass http://127.0.0.1:48080/admin-api/;   # 去掉 /yudao-api 前缀
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # ── yudao 管理后台 ──
    location /admin/ {
        alias /var/www/admin/;
        try_files $uri $uri/ /admin/index.html;
    }

    # ── 健康检查 ──
    location = /healthz { return 200 "ok\n"; }
}

# HTTP → HTTPS
server {
    listen 80;
    server_name club.example.com;
    return 301 https://$host$request_uri;
}
```

> 门户文件里 `site.json` 的 `apiBase` 保持 `/yudao-api` 即可,反代自动对接后端。CORS 无需配置(同域)。

---

## 七、备份策略(学生维护,越简单越好)

```bash
# 每天凌晨 2 点:数据库 + 门户文件 + 后台配置
0 2 * * * mysqldump -uyudao -p'密码' yudao | gzip > /backup/yudao_$(date +%F).sql.gz
0 2 * * * tar czf /backup/portal_$(date +%F).tar.gz -C /var/www portal
# 保留最近 14 天
0 3 * * * find /backup -name '*.gz' -mtime +14 -delete
```

恢复:解压门户文件回 `/var/www/portal`;数据库 `gunzip < backup | mysql -uyudao -p yudao`。

---

## 八、问题定位(按症状排查)

| 症状 | 定位路径 |
|------|----------|
| 门户打不开 | `nginx -t` → `systemctl status nginx` → `/var/log/nginx/error.log` |
| 报名/反馈失败 | 浏览器 F12 看 `/yudao-api/...` 响应;`journalctl -u yudao -f` 看后端日志 |
| 后台登录不了 | Redis 是否存活(`redis-cli ping`);后端是否启动(`curl 127.0.0.1:48080/actuator/health`) |
| 门户改了内容没生效 | `content/` 目录 no-cache 已配置;确认文件真的同步到了服务器 |
| 内存不足 OOM | `free -m` 确认 swap;JVM `-Xmx` 是否过大;`dmesg | grep -i oom` |

---

## 九、上线前清单

- [ ] 域名 + HTTPS 证书(Let's Encrypt 免费)
- [ ] MySQL/Redis 设强密码,不对外网开放端口(仅 127.0.0.1)
- [ ] yudao 后台默认 admin 密码必须改
- [ ] 报名/反馈接口已加 `@PermitAll` 且联调通过
- [ ] `/healthz` 可访问,便于外部监控
- [ ] 备份 cron 已配置,并手动跑一次验证
