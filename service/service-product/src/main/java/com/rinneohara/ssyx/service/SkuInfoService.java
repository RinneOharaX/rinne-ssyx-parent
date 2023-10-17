package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.vo.product.SkuInfoVo;
import com.rinneohara.ssyx.vo.product.SkuStockLockVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SkuInfoService extends IService<SkuInfo> {
    void updateSkuInfoVo(SkuInfoVo skuInfoVo);

    void publish(Long id, Long status);

    List<SkuInfo> findNewPersonList();

    SkuInfoVo getSkuInfoVo(Long skuId);

    Result checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo);

    void minusStock(String orderNo);
}
