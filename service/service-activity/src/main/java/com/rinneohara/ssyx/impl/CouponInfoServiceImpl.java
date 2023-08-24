package com.rinneohara.ssyx.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.enums.CouponRangeType;
import com.rinneohara.ssyx.mapper.CouponInfoMapper;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.model.activity.CouponRange;
import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.CouponInfoService;
import com.rinneohara.ssyx.service.CouponRangeService;
import com.rinneohara.ssyx.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.ranges.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
