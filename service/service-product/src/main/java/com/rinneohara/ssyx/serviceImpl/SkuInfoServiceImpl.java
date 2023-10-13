package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.common.config.RedisConst;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.service.RabbitService;
import com.rinneohara.ssyx.mapper.SkuInfoMapper;
import com.rinneohara.ssyx.model.base.BaseEntity;
import com.rinneohara.ssyx.model.product.SkuAttrValue;
import com.rinneohara.ssyx.model.product.SkuImage;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.model.product.SkuPoster;
import com.rinneohara.ssyx.service.SkuAttrValueService;
import com.rinneohara.ssyx.service.SkuImageService;
import com.rinneohara.ssyx.service.SkuInfoService;
import com.rinneohara.ssyx.service.SkuPosterService;
import com.rinneohara.ssyx.vo.product.SkuInfoVo;
import com.rinneohara.ssyx.vo.product.SkuStockLockVo;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 14:05
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
    @Autowired
    SkuPosterService skuPosterService ;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Resource
    RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private RedissonClient redissonClient;




    @Override
    public void updateSkuInfoVo(SkuInfoVo skuInfoVo) {
        Long id = skuInfoVo.getId();
        //更新sku信息
        this.updateById(skuInfoVo);

        //保存sku详情
        skuPosterService.remove(new LambdaQueryWrapper<SkuPoster>().eq(SkuPoster::getSkuId, id));
        //保存sku海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if(!CollectionUtils.isEmpty(skuPosterList)) {
            int sort = 1;
            for(SkuPoster skuPoster : skuPosterList) {
                skuPoster.setImgName("poster_"+sort);
                skuPoster.setSkuId(id);
                sort++;
            }
            skuPosterService.saveBatch(skuPosterList);
        }

        //删除sku图片
        skuImageService.remove(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, id));
        //保存sku图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if(!CollectionUtils.isEmpty(skuImagesList)) {
            int sort = 1;
            for(SkuImage skuImages : skuImagesList) {
                skuImages.setImgName("picture_"+sort);
                skuImages.setSkuId(id);
                skuImages.setSort(sort);
                sort++;
            }
            skuImageService.saveBatch(skuImagesList);
        }

        //删除sku平台属性
        skuAttrValueService.remove(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId, id));
        //保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)) {
            int sort = 1;
            for(SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(id);
                skuAttrValue.setAttrName("Attr_"+sort);
                skuAttrValue.setSort(sort);
                sort++;
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    @Override
    public void publish(Long id, Long status) {
        baseMapper.update(null,new LambdaUpdateWrapper<SkuInfo>().eq(BaseEntity::getId,id).set(SkuInfo::getPublishStatus,status));
        if (status==1){
        rabbitService.sendMessage(RabbitMqConst.EXCHANGE_GOODS_DIRECT,
                                    RabbitMqConst.ROUTING_GOODS_UPPER,
                                    id);}
        if (status==0){
            rabbitService.sendMessage(RabbitMqConst.EXCHANGE_GOODS_DIRECT,
                                        RabbitMqConst.ROUTING_GOODS_LOWER,
                                        id);}
        }

    @Override
    public List<SkuInfo> findNewPersonList() {
        List<SkuInfo> skuInfoList = baseMapper.selectList(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getIsNewPerson, 1));

        return skuInfoList;


    }

    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        SkuInfoVo skuInfoVo =new SkuInfoVo();
        skuInfoVo.setCategoryId(skuInfo.getCategoryId());
        skuInfoVo.setAttrGroupId(skuInfo.getAttrGroupId());
        skuInfoVo.setSkuType(skuInfo.getSkuType());
        skuInfoVo.setSkuName(skuInfo.getSkuName());
        skuInfoVo.setImgUrl(skuInfo.getImgUrl());
        skuInfoVo.setPerLimit(skuInfo.getPerLimit());
        skuInfoVo.setPublishStatus(skuInfo.getPublishStatus());
        skuInfoVo.setCheckStatus(skuInfo.getCheckStatus());
        skuInfoVo.setIsNewPerson(skuInfo.getIsNewPerson());
        skuInfoVo.setSort(skuInfo.getSort());
        skuInfoVo.setSkuCode(skuInfo.getSkuCode());
        skuInfoVo.setPrice(skuInfo.getPrice());
        skuInfoVo.setMarketPrice(skuInfo.getMarketPrice());
        skuInfoVo.setStock(skuInfo.getStock());
        skuInfoVo.setLockStock(skuInfo.getLockStock());
        skuInfoVo.setLowStock(skuInfo.getLowStock());
        skuInfoVo.setSale(skuInfo.getSale());
        skuInfoVo.setWareId(skuInfo.getWareId());
        skuInfoVo.setId(skuInfo.getId());
        skuInfoVo.setCreateTime(skuInfo.getCreateTime());
        skuInfoVo.setUpdateTime(skuInfo.getUpdateTime());
        skuInfoVo.setIsDeleted(skuInfo.getIsDeleted());
        List<SkuPoster> skuPosterList = skuPosterService.getBaseMapper().selectList(new LambdaQueryWrapper<SkuPoster>().eq(SkuPoster::getSkuId, skuId));
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getBaseMapper().selectList(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId,skuId));
        List<SkuImage> skuImages = skuImageService.getBaseMapper().selectList(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        skuInfoVo.setSkuImagesList(skuImages);
        return skuInfoVo;
    }

    @Override
    public Result checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {
        if (CollectionUtils.isEmpty(skuStockLockVoList)){
            throw new MyException(ResultCodeEnum.DATA_ERROR);
        }

        // 遍历所有商品，验库存并锁库存，要具备原子性
        skuStockLockVoList.forEach(skuStockLockVo -> {
            checkLock(skuStockLockVo);
        });

        // 只要有一个商品锁定失败，所有锁定成功的商品要解锁库存
        if (skuStockLockVoList.stream().anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock())) {
            // 获取所有锁定成功的商品，遍历解锁库存
            skuStockLockVoList.stream().filter(SkuStockLockVo::getIsLock).forEach(skuStockLockVo -> {
           skuInfoMapper.unlockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            });
            // 响应锁定状态
            return Result.fail("失败");
        }

        // 如果所有商品都锁定成功的情况下，需要缓存锁定信息到redis。以方便将来解锁库存 或者 减库存
        // 以orderToken作为key，以lockVos锁定信息作为value
        this.redisTemplate.opsForValue().set(RedisConst.SROCK_INFO + orderNo, skuStockLockVoList);

//        // 锁定库存成功之后，定时解锁库存。
//        this.rabbitTemplate.convertAndSend("ORDER_EXCHANGE", "stock.ttl", orderToken);
        return Result.ok("成功");
    }

    private void checkLock(SkuStockLockVo skuStockLockVo){
        //公平锁，就是保证客户端获取锁的顺序，跟他们请求获取锁的顺序，是一样的。
        // 公平锁需要排队
        // ，谁先申请获取这把锁，
        // 谁就可以先获取到这把锁，是按照请求的先后顺序来的。
        RLock rLock = this.redissonClient
                .getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        rLock.lock();

        try {
            // 验库存：查询，返回的是满足要求的库存列表
            SkuInfo skuInfo = skuInfoMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            // 如果没有一个仓库满足要求，这里就验库存失败
            if (null == skuInfo) {
                skuStockLockVo.setIsLock(false);
                return;
            }

            // 锁库存：更新
            Integer row = skuInfoMapper.lockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (row == 1) {
                skuStockLockVo.setIsLock(true);
            }
        } finally {
            rLock.unlock();
        }
    }
}
