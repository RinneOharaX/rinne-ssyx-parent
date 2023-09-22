package com.rinneohara.ssyx.common.auth;

import com.rinneohara.ssyx.common.config.RedisConst;
import com.rinneohara.ssyx.vo.user.UserLoginVo;
import com.rinneohara.utils.JwtHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/7 15:06
 */

public class UserInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;

    public UserInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)){
            return  false;
        }else {
            Long userId = JwtHelper.getUserId(token);
            UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);
            if (userLoginVo!=null){
                ThreadLocalUtils.setUserId(userLoginVo.getUserId());
                ThreadLocalUtils.setWareId(userLoginVo.getWareId());
            }
            return true;
        }
    }
}
