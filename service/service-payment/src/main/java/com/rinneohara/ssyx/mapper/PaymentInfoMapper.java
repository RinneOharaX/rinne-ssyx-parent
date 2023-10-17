package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}
