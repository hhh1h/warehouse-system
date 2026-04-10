-- 创建预警表
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
