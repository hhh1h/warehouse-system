package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("`alert`")
public class Alert {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String type;
    private String goodsName;
    private Integer goodsId;
    private Integer currentCount;
    private Integer threshold;
    private Integer storageId;
    private String status;
    private Date createTime;
    private Date readTime;
}
