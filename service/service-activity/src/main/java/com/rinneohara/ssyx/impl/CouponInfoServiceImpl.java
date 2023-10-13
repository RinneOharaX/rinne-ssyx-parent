package com.rinneohara.ssyx.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.enums.CouponRangeType;
import com.rinneohara.ssyx.enums.CouponStatus;
import com.rinneohara.ssyx.mapper.CouponInfoMapper;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.model.activity.CouponRange;
import com.rinneohara.ssyx.model.activity.CouponUse;
import com.rinneohara.ssyx.model.base.BaseEntity;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.model.order.OrderItem;
import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.CouponInfoService;
import com.rinneohara.ssyx.service.CouponRangeService;
import com.rinneohara.ssyx.service.CouponUseService;
import com.rinneohara.ssyx.vo.activity.CouponRuleVo;
import com.sun.org.apache.bcel.internal.generic.LMUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.ranges.Range;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponRangeService couponRangeService;
    @Autowired
    private  CouponInfoMapper couponInfoMapper;
    @Autowired
    private CouponUseService  couponUseService;
    @Autowired
    ProductFeignClient productFeignClient;
    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        Map<String,Object> result=new HashMap<>();
        //通过id找到对应的优惠劵信息
        CouponInfo couponInfo = baseMapper.selectById(id);
        //通过优惠劵id找到优惠劵range_id
        List<CouponRange> couponRanges = couponRangeService.getBaseMapper().selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));

        List<Long> RangeIds = couponRanges.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        //根据rangetype进行分类，如果rangetype是sku，那么range_id就是skuid
        if (!CollectionUtils.isEmpty(couponRanges)){
            if (couponInfo.getRangeType()==CouponRangeType.SKU){
                List<SkuInfo> skuInfoList = productFeignClient.getSkuInfoList(RangeIds);
                result.put("skuInfoList",skuInfoList);
            }
            //如果rangeType是分类，那么range_id就是分类id
            if (couponInfo.getRangeType()==CouponRangeType.CATEGORY){
                List<Category> categoryList=productFeignClient.getCategoryList(RangeIds);
                result.put("categoryList",categoryList);
            }
            else {
             //通用
            }
        }

        return result;
    }

    //新增优惠券规则
    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        /*
        优惠券couponInfo 与 couponRange 要一起操作：先删除couponRange ，更新couponInfo ，再新增couponRange ！
         */
        QueryWrapper<CouponRange> couponRangeQueryWrapper = new QueryWrapper<>();
        couponRangeQueryWrapper.eq("coupon_id",couponRuleVo.getCouponId());
        couponRangeService.getBaseMapper().delete(couponRangeQueryWrapper);

        //  更新数据
        CouponInfo couponInfo = this.getById(couponRuleVo.getCouponId());
        // couponInfo.setCouponType();
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        couponInfoMapper.updateById(couponInfo);

        //  插入优惠券的规则 couponRangeList
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange : couponRangeList) {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            //  插入数据
            couponRangeService.getBaseMapper().insert(couponRange);
        }
    }
    //根据关键字获取sku列表，活动使用

    public List<CouponInfo> findCouponByKeyword(String keyword) {
        //  模糊查询
        QueryWrapper<CouponInfo> couponInfoQueryWrapper = new QueryWrapper<>();
        couponInfoQueryWrapper.like("coupon_name",keyword);
        return couponInfoMapper.selectList(couponInfoQueryWrapper);
    }

    @Override
    public List<CouponInfo> findCouponInfo(Long id, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(id);
        if(null == skuInfo) return new ArrayList<>();
        return couponInfoMapper.selectCouponInfoList(skuInfo.getId(), skuInfo.getCategoryId(), userId);
    }

    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {

        //根据用户id得到所有的优惠劵信息
        List<CouponInfo> userAllCouponInfoList = baseMapper.selectCarCouponInfoList(userId);
        if (CollectionUtils.isEmpty(userAllCouponInfoList)) {
            return new ArrayList<CouponInfo>();
        }
        //所有用户的优惠劵id集合
        List<Long> couponIdList = userAllCouponInfoList.stream().map(CouponInfo::getId).collect(Collectors.toList());

        //查询优惠劵对应的范围
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CouponRange::getCouponId, couponIdList);
        List<CouponRange> couponRangeList = couponRangeService.getBaseMapper().selectList(wrapper);
        //获取优惠劵id对应的skuId列表
        //根据优惠劵id进行分组，得到map集合
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        BigDecimal reduceAmount = new BigDecimal(0);
        CouponInfo optimalCouponInfo = null;
        for (CouponInfo couponInfo : userAllCouponInfoList) {
            //全场通用
            if (couponInfo.getRangeType() == CouponRangeType.ALL) {
                BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                } else {
                    //优惠劵Id获取对应的sku列表
                    List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                    //满足使用范围的购物项
                    List<CartInfo> currentCartInfoList = cartInfoList.stream().filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                    BigDecimal totalAmounts = computeTotalAmount(currentCartInfoList);
                    if (totalAmounts.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                        couponInfo.setIsSelect(1);
                    }
                }
                if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                    reduceAmount = couponInfo.getAmount();
                    optimalCouponInfo = couponInfo;
                }
            }
            if (null != optimalCouponInfo) {
                optimalCouponInfo.setIsOptimal(1);
            }
        }
        return userAllCouponInfoList;
    }

    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        //根据优惠劵id查询优惠劵对象
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (null==couponInfo){
            return null;
        }
        List<CouponRange> couponRangeList = couponRangeService.getBaseMapper().selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId,couponId));

        //range就是对应的sku信息
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        //遍历这个map，将所有的优惠劵对应的商品list封装如CouponInfo;
        for (Map.Entry<Long,List<Long>> entry:couponIdToSkuIdMap.entrySet()){
            Long  currentCouponId= entry.getKey();
            List<Long> skuIdList=entry.getValue();
            couponInfo.setSkuIdList(skuIdList);
        }
        return couponInfo;
    }

    @Override
    public Result updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {
        CouponUse couponUse = couponUseService.getBaseMapper().selectOne(new LambdaQueryWrapper<CouponUse>().
                eq(CouponUse::getCouponId,couponId).
                eq((CouponUse::getUserId),userId).
                eq(CouponUse::getOrderId,orderId));

        couponUse.setCouponStatus(CouponStatus.USED);

        couponUseService.getBaseMapper().updateById(couponUse);
        return Result.ok("成功");
    }

    private Map<Long,List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList, List<CouponRange> couponRangeList ) {
        Map<Long,List<Long>> couponIdToSkuIdMap=new HashMap<>();
        //根据优惠劵id分组，key为优惠劵id，value为对应的分组信息
        Map<Long, List<CouponRange>> couponRangeToRangeListmap = couponRangeList.stream().collect(Collectors.groupingBy(CouponRange::getCouponId));

        for (Map.Entry<Long,List<CouponRange>> entry:couponRangeToRangeListmap.entrySet()){
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();
            Set<Long> skuIdSet=new HashSet<>();
            for (CartInfo cartInfo:cartInfoList){
                for (CouponRange couponRange:rangeList){
                    if (couponRange.getRangeType()==CouponRangeType.SKU&&
                    couponRange.getRangeId().longValue()==cartInfo.getSkuId().longValue()){
                        skuIdSet.add(cartInfo.getSkuId());
                    }else if (couponRange.getRangeType()==CouponRangeType.CATEGORY &&
                            couponRange.getRangeId().longValue()==cartInfo.getCategoryId().longValue()){
                        skuIdSet.add(cartInfo.getSkuId());
                    }
                }
            }
            couponIdToSkuIdMap.put(couponId,new ArrayList<>(skuIdSet));
        }
        return couponIdToSkuIdMap;
    }
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }
}
