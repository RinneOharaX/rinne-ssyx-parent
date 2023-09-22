package com.rinneohara.ssyx.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.LeaderMapper;
import com.rinneohara.ssyx.mapper.UserDeliverMapper;
import com.rinneohara.ssyx.mapper.UserMapper;
import com.rinneohara.ssyx.model.user.Leader;
import com.rinneohara.ssyx.model.user.User;
import com.rinneohara.ssyx.model.user.UserDelivery;
import com.rinneohara.ssyx.service.UserService;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import com.rinneohara.ssyx.vo.user.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/5 16:28
 */
@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDeliverMapper userDeliveryMapper;

    @Resource
    private LeaderMapper leaderMapper;
    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(Long id) {
        LambdaQueryWrapper<UserDelivery> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDelivery::getUserId, id);
        queryWrapper.eq(UserDelivery::getIsDefault, 1);
        UserDelivery userDelivery = userDeliveryMapper.selectOne(queryWrapper);
        if(null == userDelivery) return null;

        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());

        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(id);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }


    @Override
    public UserLoginVo getUserLoginVo(Long id) {
        UserLoginVo userLoginVo = baseMapper.getUserLoginVo(id);
        return userLoginVo;
    }

    @Override
    public User getByOpenId(String openId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openId));
    }
}
