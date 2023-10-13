package com.rinneohara.ssyx.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
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
        //按照规则分组，有对应活动规则的商品与没有活动规则的商品
        List<CartInfoVo> cartActivityList = this.findCartActivityList(cartInfoList);
        //2.计算参加活动后优惠的金额
        //筛选出有活动的购物车商品与规则
        BigDecimal reduceAmount = cartActivityList.stream().filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //3.获取购物车可以使用优惠劵列表
        List<CouponInfo> couponInfoList=couponInfoService.findCartCouponInfo(cartInfoList,userId);

        //4. 计算商品使用优惠劵之后的优惠金额，一次只能使用一张优惠劵
        BigDecimal couponReduce= new BigDecimal(0);
        if (!CollectionUtils.isEmpty(couponInfoList)){
            couponReduce = couponInfoList.stream().filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1).map(CouponInfo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
            //5. 计算商品没有参加活动的金额
            BigDecimal originalTotalAmount = cartInfoList.stream().filter(cartInfo -> cartInfo.getIsChecked() == 1)
                    .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


        //6.参加活动，使用优惠劵的总金额
        BigDecimal totalAmount = originalTotalAmount.subtract(reduceAmount).subtract(couponReduce);

        //7. 封装需要的数据到OrderConfirmVo
        OrderConfirmVo orderConfirmVo=new OrderConfirmVo();

        orderConfirmVo.setActivityReduceAmount(reduceAmount);
        orderConfirmVo.setCouponReduceAmount(couponReduce);
        orderConfirmVo.setTotalAmount(totalAmount);
        orderConfirmVo.setOriginalTotalAmount(originalTotalAmount);
        orderConfirmVo.setCarInfoVoList(cartActivityList);
        orderConfirmVo.setCouponInfoList(couponInfoList);
        return orderConfirmVo;
    }

    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
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
                for (Map.Entry<Long,Set<Long>> entry:activityIdToSkuIdListMap.entrySet()){
                    Long activityId = entry.getKey();
                    Set<Long> currentActivitySkuIdSet = entry.getValue();
                    //所有购物车内参加活动的商品集合
                    List<CartInfo> cartInfoInActivityList = cartInfoList.stream().filter(cartInfo -> currentActivitySkuIdSet.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                    //计算购物项的总金额
                    BigDecimal activityTotalAmount = this.computeTotalAmount(cartInfoInActivityList);
                    //选中商品的数量
                    int activityTotalNum = this.computeCatNum(cartInfoInActivityList);
                    //根据activityId获取活动对应规则
                    List<ActivityRule> currentActivityRuleList = activityIdToActivityRuleListMap.get(activityId);
                    //由于只有两种活动类型，所以直接get0随便取一个else取反
                    ActivityType activityType = currentActivityRuleList.get(0).getActivityType();

                    ActivityRule activityRule=null;
                    //满减折扣
                    if (activityType==ActivityType.FULL_REDUCTION){
                        activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                    }
                    //满量折扣
                    else {
                      activityRule=this.computeFullDiscount(activityTotalNum,activityTotalAmount,currentActivityRuleList);
                    }
                    CartInfoVo cartInfoVo=new CartInfoVo();
                    cartInfoVo.setActivityRule(activityRule);
                    cartInfoVo.setCartInfoList(cartInfoInActivityList);
                    cartInfoVoList.add(cartInfoVo);

                    activitySkuIdSet.addAll(currentActivitySkuIdSet);
                }
                //找到没有参加活动的商品
                skuIdList.removeAll(activitySkuIdSet);
                if (!CollectionUtils.isEmpty(skuIdList)){
                    Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
                    List<CartInfo>  cartInfos=new ArrayList<>();
                    for (Long skuId:skuIdList){
                        CartInfoVo cartInfoVo=new CartInfoVo();
                        cartInfoVo.setActivityRule(null);
                        CartInfo cartInfo = skuIdCartInfoMap.get(skuId);
                        cartInfos.add(cartInfo);
                        cartInfoVo.setCartInfoList(cartInfos);
                        cartInfoVoList.add(cartInfoVo);
                    }
                }
            }
                return cartInfoVoList;
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

    private BigDecimal computeTotalAmount (List<CartInfo> cartInfoList){
        BigDecimal total=new BigDecimal("0");
        for (CartInfo cartInfo:cartInfoList){
            if (cartInfo.getIsChecked() ==1){
                BigDecimal itemtotal=cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total=total.add(itemtotal);
            }
        }
        return total;
    }

    private  int computeCatNum(List<CartInfo> cartInfoList){
        int totalNum=0;
        for (CartInfo cartInfo:cartInfoList){
            if (cartInfo.getIsChecked()==1){
                int num=cartInfo.getSkuNum();
                totalNum+=totalNum+num;
            }
        }
        return totalNum;
    }

    /**
     * 计算满量打折最优规则
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal toalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = toalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = toalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum-optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }
}
