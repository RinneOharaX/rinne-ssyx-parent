package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.SkuStockHistoryMapper;
import com.rinneohara.ssyx.model.product.SkuStockHistory;
import com.rinneohara.ssyx.service.SkuStockHistoryService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 14:09
 */
@Service
public class SkuStockHistoryServiceImpl extends ServiceImpl<SkuStockHistoryMapper, SkuStockHistory> implements SkuStockHistoryService {
}
