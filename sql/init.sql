-- 创建数据库
CREATE DATABASE IF NOT EXISTS warehouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE warehouse;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `role` VARCHAR(20) DEFAULT 'user',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 仓库表
CREATE TABLE IF NOT EXISTS `storage` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `place` VARCHAR(200),
  `remark` VARCHAR(500),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品表
CREATE TABLE IF NOT EXISTS `goods` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `storage` INT,
  `type` INT DEFAULT 0,
  `remark` VARCHAR(500),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 记录表
CREATE TABLE IF NOT EXISTS `record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `goods` VARCHAR(100),
  `goods_id` INT,
  `user` VARCHAR(50),
  `count` INT,
  `storage` INT,
  `time` DATETIME,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入初始数据
INSERT INTO `user` (`username`, `password`, `role`) VALUES
('admin', '123456', 'admin'),
('user1', '123456', 'user');

INSERT INTO `storage` (`name`, `place`, `remark`) VALUES
('北京朝阳主仓', '北京市朝阳区', '主要存储商品'),
('上海浦东分仓', '上海市浦东新区', '临时存储商品'),
('广州天河备用仓', '广州市天河区', '应急存储商品');

INSERT INTO `goods` (`name`, `storage`, `type`, `remark`) VALUES
('电脑', 1, 50, '电子产品'),
('手机', 1, 100, '电子产品'),
('鼠标', 2, 200, '配件'),
('键盘', 2, 150, '配件'),
('显示器', 3, 80, '电子产品');
