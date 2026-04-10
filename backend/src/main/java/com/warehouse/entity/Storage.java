package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`storage`")
public class Storage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String place;
    private String remark;
}
