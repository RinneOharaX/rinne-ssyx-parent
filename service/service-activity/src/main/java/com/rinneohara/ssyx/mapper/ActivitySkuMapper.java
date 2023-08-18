package com.rinneohara.ssyx.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 活动参与商品 Mapper 接口
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Mapper
public interface ActivitySkuMapper extends BaseMapper<ActivitySku> {

}
