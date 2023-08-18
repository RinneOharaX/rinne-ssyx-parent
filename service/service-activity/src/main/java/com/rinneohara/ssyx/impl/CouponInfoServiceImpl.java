package com.rinneohara.ssyx.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.CouponInfoMapper;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.service.CouponInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
}
