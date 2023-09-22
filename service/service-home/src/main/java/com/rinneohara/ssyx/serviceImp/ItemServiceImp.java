package com.rinneohara.ssyx.serviceImp;

import com.rinneohara.ssyx.client.ActivityFeignClient;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.client.SearchFeignClient;
import com.rinneohara.ssyx.service.ItemService;
import com.rinneohara.ssyx.vo.product.SkuInfoVo;
import io.netty.util.concurrent.CompleteFuture;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/17 11:28
 */
@Service
public class ItemServiceImp implements ItemService {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;

    @Override
    public Map<String, Object> item(Long id, Long userId) {
        Map<String,Object> resultMap=new HashMap<>();

        //线程一
        CompletableFuture<SkuInfoVo> thread1 = CompletableFuture.supplyAsync(() -> {
            //远程调用获取sku对应数据
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(id);
            resultMap.put("skuInfoVo",skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);

        //线程二
        CompletableFuture<Object> thread2 = CompletableFuture.supplyAsync(() -> {
            //远程调用获取当前商品参加的活动优惠信息
            Map<String, Object> activityAndCouponMap = activityFeignClient.findActivityAndCoupon(id, userId);
            resultMap.putAll(activityAndCouponMap);
            return activityAndCouponMap;
        }, threadPoolExecutor);

        //线程三
        CompletableFuture<Object> thread3 = CompletableFuture.supplyAsync(() -> {
            searchFeignClient.incrHotScore(id);
            return null;
        }, threadPoolExecutor);

        CompletableFuture.allOf(thread1, thread2,thread3).join();

        return resultMap;
    }
}
