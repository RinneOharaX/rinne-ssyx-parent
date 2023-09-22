package com.rinneohara.ssyx.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.enums.ActivityType;
import com.rinneohara.ssyx.mapper.ActivityInfoMapper;
import com.rinneohara.ssyx.mapper.ActivityRuleMapper;
import com.rinneohara.ssyx.mapper.ActivitySkuMapper;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.model.base.BaseEntity;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.ActivityInfoService;
import com.rinneohara.ssyx.service.ActivityRuleService;
import com.rinneohara.ssyx.service.ActivitySkuService;
import com.rinneohara.ssyx.service.CouponInfoService;
import com.rinneohara.ssyx.vo.activity.ActivityRuleVo;
import com.rinneohara.ssyx.vo.order.CartInfoVo;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
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
public class  ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {
    @Autowired
    private ActivityRuleService activityRuleService;

    @Autowired
    private ActivitySkuService activitySkuService;

    @Autowired
    private  ActivityInfoService activityInfoService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
    private ActivityRuleMapper activityRuleMapper;
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

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();
        //skuIdList遍历，得到每个skuId
        skuIdList.forEach(skuId -> {
            //根据skuId进行查询，查询sku对应活动里面规则列表
            List<ActivityRule> activityRuleList =
                    baseMapper.selectActivityRuleList(skuId);
            //数据封装，规则名称
            if(!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                //把规则名称处理
                for (ActivityRule activityRule:activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                result.put(skuId,ruleList);
            }
        });
        return result;
    }

    @Override
    public Map<String, Object> findActivityAndCoupon(Long id, Long userId) {
        //一个sku只能有一个促销活动，一个活动有多个活动规则（如满赠，满100送10，满500送50）
        List<ActivityRule> activityRuleList = this.findActivityRule(id);
        //获取优惠券信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfo(id, userId);
        Map<String, Object> map = new HashMap<>();
        map.put("activityRuleList", activityRuleList);
        map.put("couponInfoList", couponInfoList);
        return map;
    }

    @Override
    public List<ActivityRule> findActivityRule(Long skuId) {
        List<ActivityRule> activityRuleBySkuId = baseMapper.findActivityRuleBySkuId(skuId);
        activityRuleBySkuId.forEach(item->{
            item.setRuleDesc(this.getRuleDesc(item));
        });
        return activityRuleBySkuId;
    }

    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        List<CartInfoVo> cartInfoVoList=this.findCartActivityList(cartInfoList);
        List<Long> skuIdList=new ArrayList<>();
        Set<Long> skuSet = new HashSet<>();
        // 活动id与购物车内参加该活动商品的映射map
        Map<Long, Set<Long>> activitySkuIdMap=new HashMap<>();
        cartInfoList.forEach(cartInfo -> {
            Long skuId = cartInfo.getSkuId();
            skuIdList.add(skuId);
        });
        skuIdList.forEach(skuId->{
            ActivitySku activitySku = activitySkuService.getBaseMapper().selectOne(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getSkuId, skuId));
            if (null!=activitySku) {
                Long activityId = activitySku.getActivityId();
                if (!activitySkuIdMap.containsKey(activityId)){
                    skuSet.clear();
                    skuSet.add(skuId);
                    activitySkuIdMap.put(activityId,skuSet);
                }else {
                    skuSet.add(skuId);
                    activitySkuIdMap.put(activityId,skuSet);
                }
            }
        });
        //活动id与活动规则的映射map
        Map<Long,List<ActivityRule>> activityIdAndRuleMap=new HashMap<>();
        activitySkuIdMap.forEach((k,v)->{
            Map<String, Object> activityRuleListMap = activityRuleService.findActivityRuleList(k);
            activityIdAndRuleMap.put(k, (List<ActivityRule>) activityRuleListMap.get("activityRuleList"));

        });


        //1.获取购物车，每个购物项参加活动，根据活动规则进行分组

        //一个规则对应多个商品


        //2.计算参加活动后的金额


        //3.获取购物车可以使用优惠劵列表


        //4. 计算商品使用优惠劵之后金额，一次只能使用一张优惠劵



        //5. 计算商品没有参加活动的金额


        //6.参加活动，使用优惠劵的总金额


        //7. 封装需要的数据到OrderConfirmVo
    }

    private List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        //创建结果集合
        List<CartInfoVo> cartInfoVoList=new ArrayList<>();
        //获取所有skuId
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        //根据获得的所有SkuId列表获取参加活动列表
        List<ActivitySku> activitySkuList=baseMapper.selectCartActivityList(skuIdList);

//        List<Long> activityList = activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toList());

//        HashMap<Long,Set<Long>> activityToSkuIds=new HashMap<>();
//
//        activityList.forEach(item->{
//            Set<Long> skuIds = baseMapper.selectSkuIdListByAcitivityId(item);
//            activityToSkuIds.put(item,skuIds);
//        });
        //key是活动id，value是活动对应的商品id
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream().collect(Collectors.groupingBy(ActivitySku::getActivityId, Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())));


        Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();

        //所有活动的id
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toSet());

        if(!CollectionUtils.isEmpty(activityIdSet)){
            LambdaQueryWrapper<ActivityRule> activityRuleLambdaQueryWrapper=new LambdaQueryWrapper<>();
            activityRuleLambdaQueryWrapper.orderByDesc(ActivityRule::getConditionAmount,ActivityRule::getConditionNum);
            activityRuleLambdaQueryWrapper.in(ActivityRule::getActivityId,activityIdSet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(activityRuleLambdaQueryWrapper);
            activityIdToActivityRuleListMap = activityRuleList.stream().collect(Collectors.groupingBy(ActivityRule::getActivityId));
        }
             //从购物车列表的skuId，找到参加了活动的商品
            Set<Long> activitySkuIdSet=new HashSet<>();
            if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)){
                activityIdToSkuIdListMap.forEach((k,v)->{
                    //skuIds是当前指定k活动的商品列表
                    Set<Long> skuIds = activityIdToSkuIdListMap.get(k);

                });
            }
            //找到没有参加活动的商品
    }

    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}
