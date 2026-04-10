-- 为 goods 表添加 barcode 字段
ALTER TABLE `goods` ADD COLUMN `barcode` VARCHAR(50) COMMENT '商品条码' AFTER `remark`;
