package com.rinneohara.ssyx.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.mapper.ActivityInfoMapper;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.service.ActivityInfoService;
import com.rinneohara.ssyx.service.ActivityRuleService;
import com.rinneohara.ssyx.vo.activity.ActivityRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@RestController
@RequestMapping("/admin/activity/activityInfo")
@Api(tags = "优惠劵信息接口")
@CrossOrigin
public class ActivityInfoController {
    @Autowired
    ActivityInfoService activityInfoService;

    @Autowired
    ActivityRuleService activityRuleService;
    // getPageList(page, limit) {
    //    return request({
    //      url: `${api_name}/${page}/${limit}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("优惠劵分页信息")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit){
        IPage<ActivityInfo> pageRule=new Page<>(page,limit);
        IPage<ActivityInfo> pageList = activityInfoService.page(pageRule);
        return Result.ok(pageList);
    }
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    //
    @ApiOperation("根据id查询对应优惠劵")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        ActivityInfo activity = activityInfoService.getById(id);
        return Result.ok(activity);
    }
    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    //
    @ApiOperation("保存优惠劵信息")
    @PostMapping("/save")
    public Result save(@RequestBody ActivityInfo activityInfo){
        activityInfoService.save(activityInfo);
        return Result.ok("保存成功");
    }
    //  updateById(role) {
    //    return request({
    //      url: `${api_name}/update`,
    //      method: 'put',
    //      data: role
    //    })
    //  },
    @ApiOperation("修改优惠劵信息")
    @PutMapping("/update")
    public Result update(@RequestBody ActivityInfo activityInfo){
        activityInfoService.updateById(activityInfo);
        return Result.ok("修改成功");
    }
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("根据id删除优惠劵信息")
    @DeleteMapping("/remove/{id}")
    public  Result removeById(@PathVariable Long id){
        activityInfoService.removeById(id);
        return Result.ok("删除成功");
    }
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //    })
    //  },
    @ApiOperation("根据IdList批量删除优惠劵信息")
    @DeleteMapping("/batchRemove")
    public Result removeRows(@RequestBody List<Long> idList){
        if (!CollectionUtils.isEmpty(idList)){
            activityInfoService.getBaseMapper().deleteBatchIds(idList);
        }else {
            throw new RuntimeException("不存在idList，请选中想要删除的id");
        }
        return Result.ok("批量删除成功");
    }

    //  findActivityRuleList(id) {
    //    return request({
    //      url: `${api_name}/findActivityRuleList/${id}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("根据优惠劵id查找对应优惠规则")
    @GetMapping("/findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable(value = "id") Long activityId){
        ActivityRule activityRule = activityRuleService.getOne(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        return Result.ok(activityRule);
    }
    //  saveActivityRule(rule) {
    //    return request({
    //      url: `${api_name}/saveActivityRule`,
    //      method: 'post',
    //      data: rule
    //    })
    //  },
    @ApiOperation("保存优惠劵规则")
    @PostMapping("/saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo){
        activityInfoService.saveActivityRule(activityRuleVo);
        return Result.ok("保存优惠劵规则成功");
    }
    //  findSkuInfoByKeyword(keyword) {
    //    return request({
    //      url: `${api_name}/findSkuInfoByKeyword/${keyword}`,
    //      method: 'get'
    //    })
    @ApiOperation("根据关键词查找sku信息")
    @GetMapping("/findSkuInfoByKeyword/{keyword}")
    public Result  findSkuInfoByKeyword(@PathVariable String keyword){
        return Result.ok(activityInfoService.findSkuInfoByKeyword(keyword));
    }
}

