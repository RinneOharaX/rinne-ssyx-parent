package com.rinneohara.ssyx.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Mapper
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    List<CouponInfo> selectCouponInfoList(Long id, Long categoryId, Long userId);

    List<CouponInfo> selectCarCouponInfoList(@Param("userId") Long userId);
}
