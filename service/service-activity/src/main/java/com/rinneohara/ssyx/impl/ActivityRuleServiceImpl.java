package com.rinneohara.ssyx.impl;

;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.ActivityRuleMapper;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.service.ActivityRuleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠规则 服务实现类
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@Service
public class ActivityRuleServiceImpl extends ServiceImpl<ActivityRuleMapper, ActivityRule> implements ActivityRuleService {

}
