package com.rinneohara.ssyx.service;

import com.rinneohara.ssyx.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import com.rinneohara.ssyx.vo.order.OrderSubmitVo;
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
}
