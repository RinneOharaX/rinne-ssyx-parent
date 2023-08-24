package com.rinneohara.ssyx.impl;

;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.mapper.ActivityRuleMapper;
import com.rinneohara.ssyx.mapper.ActivitySkuMapper;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.ActivityRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠规则 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public class ActivityRuleServiceImpl extends ServiceImpl<ActivityRuleMapper, ActivityRule> implements ActivityRuleService {
    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ActivitySkuMapper activitySkuMapper;
    @Override
    public Map<String, Object> findActivityRuleList(Long activityId) {
        Map<String,Object> resultMap=new HashMap<>();

        List<Long> skuIdList=new ArrayList<>();
        //1.根据活动id查询，查询规则列表activity_rule表
        List<ActivityRule> activityRules = baseMapper.selectList(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        //2.根据活动id查询，查询使用规则商品skuid列表
        List<ActivitySku> activitySkus = activitySkuMapper.selectList(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityId));

        activitySkus.forEach(item->{
            skuIdList.add(item.getSkuId());
        });
        List<SkuInfo> skuInfoList = productFeignClient.getSkuInfoList(skuIdList);
        resultMap.put("activityRuleList",activityRules);
        resultMap.put("skuInfoList",skuInfoList);
        return resultMap;

    }
}
