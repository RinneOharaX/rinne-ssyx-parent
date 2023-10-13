package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.product.SkuInfo;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.MatchesPattern;

@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void unlockStock(Long skuId, Integer skuNum);

    SkuInfo checkStock(Long skuId, Integer skuNum);

    Integer lockStock(Long skuId, Integer skuNum);
}
