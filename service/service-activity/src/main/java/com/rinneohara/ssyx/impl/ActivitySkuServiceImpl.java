package com.rinneohara.ssyx.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.ActivitySkuMapper;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import com.rinneohara.ssyx.service.ActivitySkuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动参与商品 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public class ActivitySkuServiceImpl extends ServiceImpl<ActivitySkuMapper, ActivitySku> implements ActivitySkuService {

}
