package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.sys.Region;
import com.rinneohara.ssyx.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/8 14:55
 */
@RestController
@Api(tags = "区域管理")
@Slf4j
@CrossOrigin
@RequestMapping("/admin/sys/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    //  findRegionByKeyword(keyword) {
    //    return request({
    //      url: `${api_name}/findRegionByKeyword/${keyword}`,
    //      method: 'get'
    //    })
    //  },
    //
    //  findByParentId(parentId) {
    //    return request({
    //      url: `${api_name}/findByParentId/${parentId}`,
    //      method: 'get'
    //    })
    //  }
    @GetMapping("/findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable String keyword){
        List<Region> regions;
        if (!StringUtils.isEmpty(keyword)){
               regions = regionService.getBaseMapper().selectList(new LambdaQueryWrapper<Region>().like(Region::getName, keyword));
        }else {
               regions=regionService.getBaseMapper().selectList(null);
        }
        return Result.ok(regions);
    }
        @GetMapping("/findByParentId/{parentId}")
        public Result findByParentId(@PathVariable Long parentId){
            List<Region> regions;
            if (!StringUtils.isEmpty(parentId)){
                regions = regionService.getBaseMapper().selectList(new LambdaQueryWrapper<Region>().like(Region::getParentId, parentId));
            }else {
                regions=regionService.getBaseMapper().selectList(null);
            }
            return Result.ok(regions);
        }
}
