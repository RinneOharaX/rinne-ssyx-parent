package com.rinneohara.ssyx.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.activity.CouponUse;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 优惠券领用表 Mapper 接口
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Mapper
public interface CouponUseMapper extends BaseMapper<CouponUse> {

}
