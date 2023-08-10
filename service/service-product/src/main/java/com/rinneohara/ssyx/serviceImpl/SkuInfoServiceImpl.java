package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.SkuInfoMapper;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.SkuInfoService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 14:05
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
}
