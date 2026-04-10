package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`goods`")
public class Goods {
    @TableId(type = IdType.INPUT)
    private Integer id;
    private String name;
    private Integer storage;
    private Integer type;
    private String remark;
    private String barcode;
}
