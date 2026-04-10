-- 优化仓库名称,使其更具辨识度
-- 方案:使用"城市+区域+类型"的命名规范

-- 备份原有数据(可选)
CREATE TABLE IF NOT EXISTS `storage_backup_20260329` AS SELECT * FROM `storage`;

-- 更新仓库名称,使其更具体
UPDATE `storage` SET `name` = '北京朝阳主仓', `place` = '北京市朝阳区' WHERE `name` = '主仓库';
UPDATE `storage` SET `name` = '上海浦东分仓', `place` = '上海市浦东新区' WHERE `name` = '分仓库';
UPDATE `storage` SET `name` = '广州天河备用仓', `place` = '广州市天河区' WHERE `name` = '备用仓库';

-- 查看优化后的结果
SELECT id, name, place, remark FROM `storage`;
