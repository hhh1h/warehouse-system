-- =============================================
-- 迁移: 添加唯一约束
-- 适用于已有数据库
-- =============================================

-- 检查并添加 username 唯一约束
ALTER TABLE `user` ADD UNIQUE IF NOT EXISTS (`username`);

-- 检查并添加 storage.name 唯一约束
ALTER TABLE `storage` ADD UNIQUE IF NOT EXISTS (`name`);
