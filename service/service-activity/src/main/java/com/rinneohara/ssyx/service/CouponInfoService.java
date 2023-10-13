package com.rinneohara.ssyx.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.vo.activity.CouponRuleVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public interface CouponInfoService extends IService<CouponInfo> {

    Map<String,Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);
    List<CouponInfo> findCouponByKeyword(String keyword);

    List<CouponInfo> findCouponInfo(Long skuId, Long userId);

    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);

    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

   Result updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);
}
