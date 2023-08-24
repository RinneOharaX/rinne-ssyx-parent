package com.rinneohara.ssyx.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.mapper.ActivityInfoMapper;
import com.rinneohara.ssyx.mapper.ActivityRuleMapper;
import com.rinneohara.ssyx.mapper.ActivitySkuMapper;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import com.rinneohara.ssyx.model.base.BaseEntity;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.ActivityInfoService;
import com.rinneohara.ssyx.service.ActivityRuleService;
import com.rinneohara.ssyx.service.ActivitySkuService;
import com.rinneohara.ssyx.vo.activity.ActivityRuleVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {
    @Autowired
    private ActivityRuleService activityRuleService;

    @Autowired
    private ActivitySkuService activitySkuService;

    @Autowired
    private  ActivityInfoService activityInfoService;

    @Autowired
    private ProductFeignClient productFeignClient;
    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        //根据活动id，删除之前的所有活动规则，再新增规则
        Long activityId = activityRuleVo.getActivityId();
        activityRuleService.remove(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId,activityId));
        //新增规则
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        activityRuleList.forEach(item->{
            item.setActivityId(activityId);
            item.setActivityType(activityInfoService.getById(activityId).getActivityType());
        });
        activityRuleService.saveBatch(activityRuleList);

        //根据活动id，删除之前所有活动期限内的商品，再新增新的活动商品
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        activitySkuList.forEach(item->{
            item.setActivityId(activityId);
        });

        activitySkuService.remove(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId,activityId));
        activitySkuService.saveBatch(activitySkuList);


    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        //实现添加商品的模糊查询
        //根据商品名字查询，但下拉框中需要排除那些已经在活动的商品避免重复添加
        List<SkuInfo> skuInfoListByKeyword = productFeignClient.getSkuInfoListByKeyword(keyword);
        List<Long> skuIdList = skuInfoListByKeyword.stream().map(SkuInfo::getId).collect(Collectors.toList());

        List<Long> existedSkuId=baseMapper.selectExistedSkuId(skuIdList);

        if (!CollectionUtils.isEmpty(existedSkuId)){
            List<SkuInfo> collectedSku = skuInfoListByKeyword.stream().filter(num -> !existedSkuId.contains(num.getId())).collect(Collectors.toList());
            return collectedSku;
        }
        return skuInfoListByKeyword;
    }
}
