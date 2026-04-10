package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    void inbound(@Param("goodsId") Integer goodsId, @Param("count") Integer count);

    boolean outbound(@Param("goodsId") Integer goodsId, @Param("count") Integer count);

    int getCount(@Param("id") Integer id);
}
