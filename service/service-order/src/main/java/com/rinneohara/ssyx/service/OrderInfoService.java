package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import com.rinneohara.ssyx.vo.order.OrderSubmitVo;
import com.rinneohara.ssyx.vo.order.OrderUserQueryVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author rinne
 * @since 2023-10-07
 */
@Service
public interface OrderInfoService extends IService<OrderInfo> {

    OrderConfirmVo confirmOrder();

    Long submitOrder(OrderSubmitVo orderParamVo);

    OrderInfo getOrderInfoById(Long orderId);

    void orderPay(String orderNo);
    OrderInfo getOrderInfoByOrderNo(String orderNo);

    IPage<OrderInfo> findUserOrderPage(Page<OrderInfo> pageParam, OrderUserQueryVo orderUserQueryVo);
}
