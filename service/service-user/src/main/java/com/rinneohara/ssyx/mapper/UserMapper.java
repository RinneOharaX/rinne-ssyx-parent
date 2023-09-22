package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.user.User;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import com.rinneohara.ssyx.vo.user.UserLoginVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/5 16:27
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    UserLoginVo getUserLoginVo(Long id);

    LeaderAddressVo getLeaderAddressVoByUserId(Long id) ;
}
