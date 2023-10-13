package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.vo.activity.ActivityRuleVo;
import com.rinneohara.ssyx.vo.order.CartInfoVo;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public interface ActivityInfoService extends IService<ActivityInfo> {

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    List<ActivityRule> findActivityRule(Long skuId);

    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);

//    Map<Long, List<String>>  findActivityNameBySkuList(List<Long> skuIdList);
}
