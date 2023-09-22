package com.rinneohara.ssyx.serviceImpl;

import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.common.config.RedisConst;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.enums.SkuType;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/20 15:48
 */

@Service
public class CartServiceImp implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;
    @Override
    public void addToCart(Long userId, Long skuId, Integer skuNum) {
        //从Redis里查询这个key，看看是否已经存在于购物车
        String cartKey=RedisConst.USER_KEY_PREFIX + userId;
        BoundHashOperations<String,String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo=null;
        if (boundHashOperations.hasKey(skuId.toString())){
            cartInfo = boundHashOperations.get(skuId.toString());
            int currentNum=cartInfo.getSkuNum()+skuNum;
            if (currentNum<1){
                return;
            }
            cartInfo.setSkuNum(currentNum);
            cartInfo.setCurrentBuyNum(currentNum);
            if (currentNum>=cartInfo.getPerLimit()){
                throw new MyException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        }else {
            skuNum=1;
            cartInfo=new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (null==skuInfo){
                throw new MyException(ResultCodeEnum.DATA_ERROR);
            }
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }
        // 更新缓存
        boundHashOperations.put(skuId.toString(), cartInfo);
        // 设置过期时间
        this.setCartKeyExpire(cartKey);
        }

    @Override
    public void deleteCart(Long skuId, Long userId) {
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId);
        if (boundHashOperations.hasKey(skuId.toString())){
            boundHashOperations.delete(skuId.toString());
        }
    }

    @Override
    public void deleteAllCart(Long userId) {
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId);
        boundHashOperations.values().forEach(item->{
            boundHashOperations.delete(item.getSkuId().toString());
        });
    }

    @Override
    public void batchDeleteCart(List<Long> skuIdList, Long userId) {
        //获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId);
        if (!CollectionUtils.isEmpty(skuIdList)) {
            skuIdList.forEach(skuId -> {
                hashOperations.delete(skuId.toString());
            });
        }
    }

    @Override
    public List<CartInfo> getCartList(Long userId) {
        List<CartInfo> cartInfoList=new ArrayList<>();
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(RedisConst.USER_KEY_PREFIX + userId);
        if (StringUtils.isEmpty(userId)) return cartInfoList;
        cartInfoList=boundHashOperations.values();
        if (null != cartInfoList){
                cartInfoList.sort(new Comparator<CartInfo>() {
                    @Override
                    public int compare(CartInfo o1, CartInfo o2) {
                        return o2.getCreateTime().compareTo(o1.getCreateTime());
                    }
                });
        }
        return cartInfoList;
    }

    public void setCartKeyExpire(String cartKey){
            redisTemplate.expire(cartKey,RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
        }
}
