package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Resource
    RabbitService rabbitService;




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
}
