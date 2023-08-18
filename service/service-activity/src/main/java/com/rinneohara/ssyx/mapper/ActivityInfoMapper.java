package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Mapper
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

}
