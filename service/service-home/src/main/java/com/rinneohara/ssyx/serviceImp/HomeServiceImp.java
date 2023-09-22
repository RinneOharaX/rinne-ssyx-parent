package com.rinneohara.ssyx.serviceImp;

import com.rinneohara.ssyx.client.ActivityFeignClient;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.client.SearchFeignClient;
import com.rinneohara.ssyx.client.UserFeignClient;
import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.model.search.SkuEs;
import com.rinneohara.ssyx.repository.SkuRepository;
import com.rinneohara.ssyx.service.HomeService;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/7 16:39
 */
@Service
public class HomeServiceImp implements HomeService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private SearchFeignClient searchFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Override
    public Map<String, Object> getDataInFo(Long userId) {

        Map<String,Object> result=new HashMap<>();
        //获取分类信息
        List<Category> allCategoryList =  productFeignClient.findAllCategoryList();
        result.put("categoryList", allCategoryList);
        //获取新人专享商品
        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        result.put("newPersonSkuInfoList", newPersonSkuInfoList);
        //提货点地址信息
        LeaderAddressVo leaderAddressVo= userFeignClient.getUserAddressByUserId(userId);
        result.put("leaderAddressVo", leaderAddressVo);
        //获取爆品商品
        List<SkuEs> hotSkuList = searchFeignClient.findHotSkuList();
        if (!CollectionUtils.isEmpty(hotSkuList)){
            //获取爆款sku对应的促销活动标签
            List<Long> skuIdList = hotSkuList.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);
            if(null != skuIdToRuleListMap) {
                hotSkuList.forEach(skuEs -> {
                    skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId()));
                });
            }
        }
        result.put("hotSkuList", hotSkuList);
        return result;
    }
}

