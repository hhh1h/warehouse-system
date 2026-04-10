package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("`record`")
public class Record {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String goods;
    private Integer goodsId;
    private String user;
    private Integer count;
    private Date time;
    private Integer storage;
}
