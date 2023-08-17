package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.vo.product.SkuInfoVo;
import org.springframework.stereotype.Service;

@Service
public interface SkuInfoService extends IService<SkuInfo> {
    void updateSkuInfoVo(SkuInfoVo skuInfoVo);

    void publish(Long id, Long status);
}
