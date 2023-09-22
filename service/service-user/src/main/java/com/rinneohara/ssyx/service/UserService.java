package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.user.User;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import com.rinneohara.ssyx.vo.user.UserLoginVo;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/5 16:21
 */
@Service
public interface UserService extends IService<User> {
     LeaderAddressVo getLeaderAddressVoByUserId(Long id) ;

    UserLoginVo getUserLoginVo(Long id);

    User getByOpenId(String openId);

}
