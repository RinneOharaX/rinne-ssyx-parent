package com.rinneohara.ssyx.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.ActivityFeignClient;
import com.rinneohara.ssyx.client.CartFeignClient;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.client.UserFeignClient;
import com.rinneohara.ssyx.common.auth.RedisDateUtil;
import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.common.config.RedisConst;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.enums.*;
import com.rinneohara.ssyx.mapper.OrderInfoMapper;
import com.rinneohara.ssyx.mapper.OrderItemMapper;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.model.order.OrderInfo;
import com.rinneohara.ssyx.model.order.OrderItem;
import com.rinneohara.ssyx.service.OrderInfoService;
import com.rinneohara.ssyx.service.OrderItemService;
import com.rinneohara.ssyx.service.OrderSetService;
import com.rinneohara.ssyx.service.RabbitService;
import com.rinneohara.ssyx.vo.order.CartInfoVo;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import com.rinneohara.ssyx.vo.order.OrderSubmitVo;
import com.rinneohara.ssyx.vo.order.OrderUserQueryVo;
import com.rinneohara.ssyx.vo.product.SkuStockLockVo;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-10-07
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderSetService orderSetService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Override
    public OrderConfirmVo confirmOrder() {
        Long userId = ThreadLocalUtils.getUserId();
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        List<CartInfo> cartInfoList = cartFeignClient.getCartInfo(userId);

        List<CartInfo> isCheckedCartInfo =new ArrayList<>();
        cartInfoList.forEach(cartInfo -> {
            if (cartInfo.getIsChecked()==1){
                isCheckedCartInfo.add(cartInfo);
            }
        });
        List<CartInfoVo> isCheckedCartVoList = activityFeignClient.findCartActivityList(isCheckedCartInfo);
        String orderNum= String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT+orderNum,orderNum,24, TimeUnit.HOURS);
        OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(isCheckedCartInfo, userId);
        orderConfirmVo.setLeaderAddressVo(leaderAddressVo);
        orderConfirmVo.setCarInfoVoList(isCheckedCartVoList);
        orderConfirmVo.setOrderNo(orderNum);
        return orderConfirmVo;
    }

    @Override
    public Long submitOrder(OrderSubmitVo orderParamVo) {
        Long userId = ThreadLocalUtils.getUserId();
        Long orderId=null;
        orderParamVo.setUserId(userId);
        String orderNo = orderParamVo.getOrderNo();
        if (!StringUtils.isEmpty(orderNo)){
            String orderNum = (String) redisTemplate.opsForValue().get(RedisConst.ORDER_REPEAT+orderNo);
            if (StringUtils.isEmpty(orderNum)){
                throw new MyException(ResultCodeEnum.ILLEGAL_REQUEST);
            }
            //Lua脚本实现原子操作
            //如果redis中已经存在该订单号，证明该订单可以正常提交，提交后比你高且从redis里删除该key
            //如果redis里不存在该订单号，证明该订单号已经被删除，即为重复提交，抛出异常
            String script="if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
            Boolean flag = (Boolean) redisTemplate.execute(new DefaultRedisScript(script, Boolean.class), Arrays.asList(RedisConst.ORDER_REPEAT + orderNo), orderNo);
            if (!flag){
                throw new MyException(ResultCodeEnum.REPEAT_SUBMIT);
            }
            List<CartInfo> cartInfoList = cartFeignClient.getCartInfo(userId);
            List<CartInfo> isCheckedCartInfo =new ArrayList<>();
            cartInfoList.forEach(cartInfo -> {
                if (cartInfo.getIsChecked()==1){
                    isCheckedCartInfo.add(cartInfo);
                }
            });
            //得到所有被选中的普通商品类型
            List<CartInfo> commonSkuList = isCheckedCartInfo.stream().filter(cartInfo -> cartInfo.getSkuType() == SkuType.COMMON.getCode()).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(commonSkuList)){
                List<SkuStockLockVo> commonStockLockVoList = commonSkuList.stream().map(item -> {
                    SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                    skuStockLockVo.setSkuId(item.getSkuId());
                    skuStockLockVo.setSkuNum(item.getSkuNum());
                    return skuStockLockVo;
                }).collect(Collectors.toList());

                Result result = productFeignClient.checkAndLock(commonStockLockVoList, orderNo);
                if (result.getCode()!=200){
                    throw  new MyException(ResultCodeEnum.ORDER_STOCK_FALL);
                }
            }
            try {
                orderId =this.saveOrder(orderParamVo,isCheckedCartInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            rabbitService.sendMessage(RabbitMqConst.EXCHANGE_CANCEL_ORDER_DIRECT,RabbitMqConst.ROUTING_DELETE_CART,orderParamVo.getUserId());

        }
        return orderId;
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        List<OrderItem> orderItems = orderItemService.getBaseMapper().selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        orderInfo.setOrderItemList(orderItems);
        return orderInfo;
    }



    @Transactional(rollbackFor = {Exception.class})
    public Long saveOrder(OrderSubmitVo orderParamVo, List<CartInfo> isCheckedCartInfo) {
        Long userId=ThreadLocalUtils.getUserId();

        LeaderAddressVo userAddressByUserId = userFeignClient.getUserAddressByUserId(userId);
        if (userAddressByUserId==null){
            throw new MyException(ResultCodeEnum.DATA_ERROR);
        }
        //计算订单金额，包括活动和优惠劵
        //活动金额
        Map<String, BigDecimal> activitySplitAmount = this.computeActivitySplitAmount(isCheckedCartInfo);
        //优惠劵金额
        Map<String, BigDecimal>  couponInfoSplitAmount= this.computeCouponInfoSplitAmount(isCheckedCartInfo, orderParamVo.getCouponId());

        List<OrderItem> orderItemList=new ArrayList<>();
        for (CartInfo cartInfo : isCheckedCartInfo){
            OrderItem orderItem=new OrderItem();
            orderItem.setOrderId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if(cartInfo.getSkuType() == SkuType.COMMON.getCode()) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderParamVo.getLeaderId());
            //营销活动金额
            BigDecimal activityAmount = activitySplitAmount.get("activity:" + orderItem.getSkuId());
            if (activityAmount==null){
                activityAmount=new BigDecimal(0);
            }
            //优惠劵金额
            BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:" + orderItem.getOrderId());
            if (couponAmount==null){
                couponAmount=new BigDecimal(0);
            }
            //总金额
            BigDecimal amountInAll = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));

            BigDecimal currentAmount = amountInAll.subtract(activityAmount).subtract(couponAmount);
            orderItem.setSplitActivityAmount(activityAmount);
            orderItem.setSplitCouponAmount(couponAmount);
            orderItem.setSplitTotalAmount(currentAmount);
            orderItemList.add(orderItem);
        }
        //封装orderInfo数据
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setOrderNo(orderParamVo.getOrderNo());
        orderInfo.setOrderStatus(OrderStatus.UNPAID);//订单状态
        orderInfo.setLeaderId(orderParamVo.getLeaderId());
        orderInfo.setLeaderName(userAddressByUserId.getLeaderName());
        orderInfo.setLeaderPhone(userAddressByUserId.getLeaderPhone());
        orderInfo.setTakeName(userAddressByUserId.getTakeName());
        orderInfo.setReceiverName(orderParamVo.getReceiverName());
        orderInfo.setReceiverPhone(orderParamVo.getReceiverPhone());
        orderInfo.setReceiverProvince(userAddressByUserId.getProvince());
        orderInfo.setReceiverCity(userAddressByUserId.getCity());
        orderInfo.setReceiverDistrict(userAddressByUserId.getDistrict());
        orderInfo.setReceiverAddress(userAddressByUserId.getDetailAddress());
        orderInfo.setWareId(isCheckedCartInfo.get(0).getWareId());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.setCouponId(orderParamVo.getCouponId());


        //计算订单金额
        BigDecimal originalTotalAmount = this.computeTotalAmount(isCheckedCartInfo);
        BigDecimal activityAmount = activitySplitAmount.get("activity:total");
        if(null == activityAmount) activityAmount = new BigDecimal(0);
        BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:total");
        if(null == couponAmount) couponAmount = new BigDecimal(0);
        BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);
        //计算订单金额
        orderInfo.setOriginalTotalAmount(originalTotalAmount);
        orderInfo.setActivityAmount(activityAmount);
        orderInfo.setCouponAmount(couponAmount);
        orderInfo.setTotalAmount(totalAmount);

        //TODO
        //计算团长佣金
        BigDecimal profitRate = new BigDecimal(0);
        BigDecimal commissionAmount = orderInfo.getTotalAmount().multiply(profitRate);
        orderInfo.setCommissionAmount(commissionAmount);

        orderInfoService.getBaseMapper().insert(orderInfo);


        // 保存订单项
        for(OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
        }
        orderItemService.saveBatch(orderItemList);

        //更新优惠券使用状态
        if(null != orderInfo.getCouponId()) {
             activityFeignClient.updateCouponInfoUseStatus(orderInfo.getCouponId(), userId, orderInfo.getId());

        }

        //下单成功，记录用户商品购买个数
        String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderParamVo.getUserId();
        BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(orderSkuKey);
        isCheckedCartInfo.forEach(cartInfo -> {
            if(hashOperations.hasKey(cartInfo.getSkuId().toString())) {
                Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });
        redisTemplate.expire(orderSkuKey, RedisDateUtil.getCurrentExpireTimes(), TimeUnit.SECONDS);

        //发送消息
        return orderInfo.getId();
    }




    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }
    /**
     * 计算购物项分摊的优惠减少金额
     * 打折：按折扣分担
     * 现金：按比例分摊
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        //促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        //活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if(!CollectionUtils.isEmpty(cartInfoVoList)) {
            for(CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if(null != activityRule) {
                    //优惠金额， 按比例分摊
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if(cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:"+cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        //总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for(CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for(int i=0, len=cartInfoList.size(); i<len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if(i < len -1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    //sku分摊金额
                                    BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for(int i=0, len=cartInfoList.size(); i<len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if(i < len -1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    //sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:"+cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if(null == couponId) return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if(null != couponInfo) {
            //sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            //优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if(CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            //优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if(skuIdList.size() == 1) {
                //sku的优化金额
                couponInfoSplitAmountMap.put("coupon:"+skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                //总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for(int i=0, len=skuIdList.size(); i<len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if(i < len -1) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            //sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:"+cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:"+cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }

    public void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setProcessStatus(processStatus);
        orderInfo.setOrderStatus(processStatus.getOrderStatus());
        if(processStatus == ProcessStatus.WAITING_DELEVER) {
            orderInfo.setPaymentTime(new Date());
        } else if(processStatus == ProcessStatus.WAITING_LEADER_TAKE) {
            orderInfo.setDeliveryTime(new Date());
        } else if(processStatus == ProcessStatus.WAITING_USER_TAKE) {
            orderInfo.setTakeTime(new Date());
        }
        baseMapper.updateById(orderInfo);
    }

    @Override
    public void orderPay(String orderNo) {
        OrderInfo orderInfo = this.getOrderInfoByOrderNo(orderNo);
        if(null == orderInfo || orderInfo.getOrderStatus() != OrderStatus.UNPAID) return;

        //更改订单状态
        this.updateOrderStatus(orderInfo.getId(),  ProcessStatus.WAITING_DELEVER);

        //扣减库存
        rabbitService.sendMessage(RabbitMqConst.EXCHANGE_ORDER_DIRECT, RabbitMqConst.ROUTING_MINUS_STOCK, orderNo);
    }

    @Override
    public OrderInfo getOrderInfoByOrderNo(String orderNo) {
        OrderInfo orderInfo = baseMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        orderInfo.getParam().put("orderStatusName", orderInfo.getOrderStatus().getComment());
        List<OrderItem> orderItemList = orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderInfo.getId()));
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }

    @Override
    public IPage<OrderInfo> findUserOrderPage(Page<OrderInfo> pageParam,
                                                     OrderUserQueryVo orderUserQueryVo) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId,orderUserQueryVo.getUserId());
        wrapper.eq(OrderInfo::getOrderStatus,orderUserQueryVo.getOrderStatus());
        IPage<OrderInfo> pageModel = baseMapper.selectPage(pageParam, wrapper);

        //获取每个订单，把每个订单里面订单项查询封装
        List<OrderInfo> orderInfoList = pageModel.getRecords();
        for(OrderInfo orderInfo : orderInfoList) {
            //根据订单id查询里面所有订单项列表
            List<OrderItem> orderItemList = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, orderInfo.getId())
            );
            //把订单项集合封装到每个订单里面
            orderInfo.setOrderItemList(orderItemList);
            //封装订单状态名称
            orderInfo.getParam().put("orderStatusName",orderInfo.getOrderStatus().getComment());
        }
        return pageModel;
    }
}

