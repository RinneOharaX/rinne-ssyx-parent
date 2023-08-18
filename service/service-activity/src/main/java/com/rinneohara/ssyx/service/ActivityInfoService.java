package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.vo.activity.ActivityRuleVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public interface ActivityInfoService extends IService<ActivityInfo> {

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    Object findSkuInfoByKeyword(String keyword);
}
