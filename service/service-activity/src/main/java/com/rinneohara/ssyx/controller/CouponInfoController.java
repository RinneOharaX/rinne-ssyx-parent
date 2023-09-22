package com.rinneohara.ssyx.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.service.CouponInfoService;
import com.rinneohara.ssyx.vo.activity.CouponRuleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author rinne
 * @since 2023-08-18
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {
    @Autowired
    private CouponInfoService couponInfoService;

    //  getPageList(page, limit) {
    //    return request({
    //      url: `${api_name}/${page}/${limit}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("优惠劵分页接口")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit){
        IPage<CouponInfo> pageRule=new Page<>(page,limit);
        IPage<CouponInfo> couponInfoIPage = couponInfoService.getBaseMapper().selectPage(pageRule, null);
        List<CouponInfo> records = couponInfoIPage.getRecords();
        records.forEach(item->{
            item.setCouponTypeString(item.getCouponType().getComment());
            if (item.getRangeType()!=null){
                item.setRangeTypeString(item.getRangeType().getComment());
            }
        });
        return Result.ok(couponInfoIPage);
    }
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("根据id获取优惠劵信息")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        CouponInfo couponInfo = couponInfoService.getById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType()!=null){
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return Result.ok(couponInfo);
    }
    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    @ApiOperation("保存优惠劵信息")
    @PostMapping("/save")
    public Result save(@RequestBody CouponInfo couponInfo){
        couponInfoService.save(couponInfo);
        return Result.ok("保存优惠劵成功");
    }
    //  updateById(role) {
    //    return request({
    //      url: `${api_name}/update`,
    //      method: 'put',
    //      data: role
    //    })
    //  },
    @ApiOperation("更新优惠劵信息")
    @PutMapping("/update")
    public Result updateById(@RequestBody CouponInfo couponInfo){
        couponInfoService.updateById(couponInfo);
        return Result.ok("更新成功");
    }
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("删除优惠劵信息")
    @DeleteMapping("/remove/{id}")
    public Result removeByid(@PathVariable Long id){
        couponInfoService.removeById(id);
        return Result.ok("删除成功");
    }
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //    })
    //  },
    @ApiOperation("批量删除优惠劵信息")
    @DeleteMapping("/batchRemove")
    public Result removeRows(@RequestBody List<Long> idList){
        couponInfoService.removeByIds(idList);
        return Result.ok("批量删除成功");
    }


    //  findCouponRuleList(id) {
    //    return request({
    //      url: `${api_name}/findCouponRuleList/${id}`,
    //      method: 'get'
    //    })
    //  },

    @ApiOperation("根据id查询优惠劵规则")
    @GetMapping("/findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id){
        Map<String,Object>  map= couponInfoService.findCouponRuleList(id);
        return Result.ok(map);
    }
    //  saveCouponRule(rule) {
    //    return request({
    //      url: `${api_name}/saveCouponRule`,
    //      method: 'post',
    //      data: rule
    //    })
    //  },
    @ApiOperation("保存优惠劵规则")
    @PostMapping("/saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo){
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok("保存成功");
    }
    //  findCouponByKeyword(keyword) {
    //    return request({
    //      url: `${api_name}/findCouponByKeyword/${keyword}`,
    //      method: 'get'
    //    })
    //  }
    @ApiOperation("根据关键词查询优惠规则")
    @PostMapping("/findCouponByKeyword/{keyword}")
    public Result saveCouponRule(@PathVariable String keyword){
        List<CouponInfo> couponByKeyword = couponInfoService.findCouponByKeyword(keyword);
        return Result.ok(couponByKeyword);
    }
}

