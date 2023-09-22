package com.rinneohara.ssyx.controller;

import com.alibaba.fastjson.JSONObject;
import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.common.config.RedisConst;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.enums.UserType;
import com.rinneohara.ssyx.model.user.User;
import com.rinneohara.ssyx.model.user.UserDelivery;
import com.rinneohara.ssyx.service.UserDeliverService;
import com.rinneohara.ssyx.service.UserService;
import com.rinneohara.ssyx.utils.ConstantPropertiesUtil;
import com.rinneohara.ssyx.utils.HttpClientUtils;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import com.rinneohara.ssyx.vo.user.UserLoginVo;
import com.rinneohara.utils.JwtHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/5 15:27
 */

@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Autowired
    private UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private UserDeliverService  userDeliverService;

    @ApiOperation("更新用户信息")
    @PostMapping("/auth/updateUser")
    public  Result updateUser(@RequestBody User user){
        User user1 = userService.getById(ThreadLocalUtils.getUserId());
        //把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }

    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public Result callback(@PathVariable String code) throws Exception {
        System.out.println("从微信服务器回调得到openid和sessionid");
        if (StringUtils.isEmpty(code)){
            throw new MyException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        StringBuilder url=new StringBuilder()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(url.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);


        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new MyException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        System.out.println("使用code换取的access_token结果 = " + result);
        JSONObject resultJson = JSONObject.parseObject(result);
        if(resultJson.getString("errcode") != null){
            throw new MyException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = resultJson.getString("session_key");
        String openId = resultJson.getString("openid");

        User user=userService.getByOpenId(openId);
        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
        if(null == user){
            user = new User();
            user.setOpenId(openId);
            user.setNickName(openId);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
            UserDelivery userDelivery=new UserDelivery();
            userDelivery.setUserId(user.getId());
            userDelivery.setWareId(1L);
            userDelivery.setLeaderId(1L);
            userDelivery.setIsDefault(1);
            userDeliverService.save(userDelivery);

        }
        LeaderAddressVo leaderAddressVo = userService.getLeaderAddressVoByUserId(user.getId());
        Map<String, Object> map = new HashMap<>();
        String name = user.getNickName();
        map.put("user", user);
        map.put("leaderAddressVo", leaderAddressVo);
        String token = JwtHelper.createToken(user.getId(), name);
        map.put("token", token);
//        if(user.getUserType() == UserType.LEADER) {
//            Leader leader = leaderService.getLeader();
//            map.put("leader", leader);
//        }
        UserLoginVo userLoginVo = userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(), userLoginVo, RedisConst.USERKEY_TIMEOUT, TimeUnit.DAYS);
        ThreadLocalUtils.setUserId(user.getId());
        return Result.ok(map);
    }

}
