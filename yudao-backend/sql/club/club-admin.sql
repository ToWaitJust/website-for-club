-- ============================================
-- AI+CLUB 管理员账号
-- 用户名: clubadmin
-- 密  码: admin123
-- 角  色: 超级管理员(super_admin)
-- 使用方式: 在 Navicat 中打开 ruoyi-vue-pro 数据库,
--           直接运行以下两条 INSERT 语句即可。
-- ============================================

-- 1. 创建用户(密码 hash 与 yudao 默认 admin 相同,即 admin123;id 自增,不指定)
INSERT INTO `system_users` (
    `id`, `username`, `password`, `nickname`, `remark`,
    `dept_id`, `post_ids`, `email`, `mobile`, `sex`, `avatar`,
    `status`, `login_ip`, `login_date`, `creator`, `create_time`,
    `updater`, `update_time`, `deleted`, `tenant_id`
) VALUES (
    NULL, 'clubadmin',
    '$2a$04$.vd8nPeLwxt6hnSzmAoAyul8BOLX7Cib6QhcxRe30rfvrIPQHH1OG',
    '社团管理员', 'AI+CLUB 运营管理',
    103, '[1]', '', '', 0, '',
    0, '', NULL, '1', NOW(),
    '1', NOW(), b'0', 1
);

-- 2. 关联超级管理员角色(role_id=1;user_id 取自刚插入的 clubadmin)
INSERT INTO `system_user_role` (
    `id`, `user_id`, `role_id`, `creator`, `create_time`,
    `updater`, `update_time`, `deleted`, `tenant_id`
)
SELECT NULL, id, 1, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_users`
WHERE username = 'clubadmin' AND deleted = b'0'
LIMIT 1;