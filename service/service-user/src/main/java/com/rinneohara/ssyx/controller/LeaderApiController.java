package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.service.UserService;
import com.rinneohara.ssyx.vo.user.LeaderAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/8 14:08
 */
@Api(tags = "团长接口")
@RestController
@RequestMapping("/api/user/leader")
public class LeaderApiController {


    @Resource
    private UserService userService;


    @ApiOperation("提货点地址信息")
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable(value = "userId") Long userId) {
        return userService.getLeaderAddressVoByUserId(userId);
    }
}
