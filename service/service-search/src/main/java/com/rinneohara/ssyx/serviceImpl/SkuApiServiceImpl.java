package com.rinneohara.ssyx.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.rinneohara.ssyx.client.ActivityFeignClient;
import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.enums.SkuType;
import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.model.search.SkuEs;
import com.rinneohara.ssyx.repository.SkuRepository;
import com.rinneohara.ssyx.service.SkuApiService;
import com.rinneohara.ssyx.vo.search.SkuEsQueryVo;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/16 12:14
 */
@Service
public class SkuApiServiceImpl implements SkuApiService {
    @Autowired
    ActivityFeignClient activityFeignClient;
    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    private SkuRepository skuEsRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public void upperSku(Long skuId) {
        SkuEs skuEs=new SkuEs();
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(null == skuInfo) return;
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName()+","+skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if(skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }

        SkuEs save = skuEsRepository.save(skuEs);

    }

    @Override
    public void lowerGoods(Long skuId) {;
        skuEsRepository.deleteById(skuId);
    }

    @Override
    public List<SkuEs> findHotSkuList() {
        Pageable pageable = PageRequest.of(0, 10);
        return skuEsRepository.findByOrderByHotScoreDesc(pageable).getContent();
    }

    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        //1.在拦截器的本地线程中存储过当前用户的仓库地址，将仓库地址注入SkuEsQueryVo中
        skuEsQueryVo.setWareId(ThreadLocalUtils.getWareId());
        Page<SkuEs> pageModel=null;
        //2.根据Spring Data的命名模式，通过Es查询到对应的商品
        String keyword = skuEsQueryVo.getKeyword();
        //先判断存不存在查询关键词，如果存在查询关键词，那就根据仓库id+关键词 查询
        if(StringUtils.isEmpty(keyword)){
            pageModel = skuEsRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(), skuEsQueryVo.getWareId(), pageable);
        } else {
            pageModel = skuEsRepository.findByKeywordAndWareId(skuEsQueryVo.getKeyword(), skuEsQueryVo.getWareId(), pageable);
        }
        //3.查询出来的商品，想要知道它参加了哪些优惠活动
        List<SkuEs> content = pageModel.getContent();
        if (!CollectionUtils.isEmpty(content)){
            //遍历查到的商品集合，得到所有的商品id
            List<Long> collect = content.stream().map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            //远程调用商品的接口，得到优惠的信息
            Map<Long, List<String>> activity = activityFeignClient.findActivity(collect);
            content.forEach(item->{
                item.setRuleList(activity.get(item.getId()));
            });
        }
        return pageModel;
    }

    @Override
    public void incrHotScore(Long skuId) {
        // 定义key
        String hotKey = "hotScore";
        // 保存数据
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId:" + skuId, 1);
        if (hotScore%10==0){
            // 更新es
            Optional<SkuEs> optional = skuEsRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuEsRepository.save(skuEs);
        }
    }

    //获取爆品商品

}
