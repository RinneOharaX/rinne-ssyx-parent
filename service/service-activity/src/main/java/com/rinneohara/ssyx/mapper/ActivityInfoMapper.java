package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

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



    List<ActivityRule> selectActivityRuleList(@Param("skuId")Long skuId);

    List<Long> selectExistedSkuId(@Param("skuIdList") List<Long> skuIdList);

    List<ActivityRule> findActivityRuleBySkuId(@Param("id") Long skuId);

    List<ActivitySku> selectCartActivityList(@Param("skuIdList") List<Long> skuIdList);

    Set<Long> selectSkuIdListByAcitivityId(@Param("activityId") Long activityId);


//    List<String> findActivityNameBySkuList(@Param("skuIdList") List<Long> skuIdList);
}
