-- =============================================
-- 仓库管理系统 - 完整数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS warehouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE warehouse;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(50) NOT NULL COMMENT '密码',
  `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 仓库表
-- =============================================
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
  `place` VARCHAR(200) COMMENT '仓库地址',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- =============================================
-- 3. 商品表
-- =============================================
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `storage` INT COMMENT '仓库ID',
  `type` INT DEFAULT 0 COMMENT '库存数量',
  `remark` VARCHAR(500) COMMENT '备注/仓库名称',
  `barcode` VARCHAR(100) COMMENT '商品条码',
  PRIMARY KEY (`id`),
  INDEX `idx_storage` (`storage`),
  INDEX `idx_barcode` (`barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- =============================================
-- 4. 出入库记录表
-- =============================================
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `goods` VARCHAR(100) COMMENT '商品名称',
  `goods_id` INT COMMENT '商品ID',
  `user` VARCHAR(50) COMMENT '操作用户',
  `count` INT COMMENT '数量(正数入库/负数出库)',
  `storage` INT COMMENT '仓库ID',
  `time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  INDEX `idx_goods_id` (`goods_id`),
  INDEX `idx_storage` (`storage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库记录表';

-- =============================================
-- 5. 库存预警表
-- =============================================
DROP TABLE IF EXISTS `alert`;
CREATE TABLE `alert` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '预警ID',
  `type` VARCHAR(50) NOT NULL COMMENT '预警类型',
  `goods_name` VARCHAR(100) COMMENT '商品名称',
  `goods_id` INT COMMENT '商品ID',
  `current_count` INT COMMENT '当前库存',
  `threshold` INT COMMENT '预警阈值',
  `storage_id` INT COMMENT '仓库ID',
  `status` VARCHAR(20) DEFAULT 'unread' COMMENT '状态: unread/read',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` DATETIME COMMENT '阅读时间',
  INDEX `idx_status` (`status`),
  INDEX `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警表';

-- =============================================
-- 6. 插入初始数据
-- =============================================

-- 用户数据
INSERT INTO `user` (`username`, `password`, `role`) VALUES
('admin', '123456', 'admin'),
('user1', '123456', 'user');

-- 仓库数据
INSERT INTO `storage` (`name`, `place`, `remark`) VALUES
('北京朝阳主仓', '北京市朝阳区', '主要存储商品'),
('上海浦东分仓', '上海市浦东新区', '临时存储商品'),
('广州天河备用仓', '广州市天河区', '应急存储商品');

-- 商品数据
INSERT INTO `goods` (`name`, `storage`, `type`, `remark`, `barcode`) VALUES
('电脑', 1, 50, '北京朝阳主仓', 'PC001'),
('手机', 1, 100, '北京朝阳主仓', 'PH001'),
('鼠标', 2, 200, '上海浦东分仓', 'MS001'),
('键盘', 2, 150, '上海浦东分仓', 'KB001'),
('显示器', 3, 80, '广州天河备用仓', 'MN001');
