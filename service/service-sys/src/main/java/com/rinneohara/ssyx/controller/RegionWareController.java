package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.sys.RegionWare;
import com.rinneohara.ssyx.service.RegionWareService;
import com.rinneohara.ssyx.vo.sys.RegionWareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/8 15:06
 */

@RestController
@Api(tags = "区域仓库管理")
@Slf4j
@CrossOrigin
@RequestMapping("/admin/sys/regionWare")
public class RegionWareController {
    @Resource
    private RegionWareService regionWareService;

    //  getPageList(page, limit,searchObj) {
    //    return request({
    //      url: `${api_name}/${page}/${limit}`,
    //      method: 'get',
    //      params: searchObj
    //    })
    //  },
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    //
    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    //
    //  updateStatus(id, status) {
    //    return request({
    //      url: `${api_name}/updateStatus/${id}/${status}`,
    //      method: 'post'
    //    })
    //  },
    //
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //
    @ApiOperation("区域仓库管理分页查询")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit,
                              RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> regionWareIPage = new Page<>(page, limit);
        LambdaQueryWrapper<RegionWare> like = new LambdaQueryWrapper<RegionWare>()
                .like(RegionWare::getRegionName, regionWareQueryVo.getKeyword()).or().like(RegionWare::getWareName,regionWareQueryVo.getKeyword());
        String keyword = regionWareQueryVo.getKeyword();
        IPage<RegionWare> pageList;
        if (!StringUtils.isEmpty(keyword)){
          pageList = regionWareService.getBaseMapper().selectPage(regionWareIPage,like);
        }else {
            pageList = regionWareService.getBaseMapper().selectPage(regionWareIPage,null);
        }
        return Result.ok(pageList);
    }

    @ApiOperation("根据ID获取区域仓库信息")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        RegionWare regionWare = regionWareService.getById(id);
        return Result.ok(regionWare);
    }

    @ApiOperation("新增区域仓库信息")
    @PostMapping("/save")
    public Result save(@RequestBody RegionWare regionWare){
        regionWareService.saveRegionWare(regionWare);
        return Result.ok("保存成功");
    }

    //updateStatus(id, status) {
    //    //    return request({
    //    //      url: `${api_name}/updateStatus/${id}/${status}`,
    //    //      method: 'post'
    //    //    })
    //    //  },
    @ApiOperation("更新仓库是否开通")
    @PostMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,@PathVariable Long status){
        regionWareService.updateState(id,status);
        return Result.ok("修改成功");
    }

    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("根据ID删除区域仓库信息")
    @DeleteMapping("/remove/{id}")
    public Result removeById(@PathVariable Long id){
        regionWareService.removeById(id);
        return Result.ok("删除成功");
    }

    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    @ApiOperation("根据IdList批量删除")
    @DeleteMapping("/batchRemove")
    public Result removeRows(@RequestParam List<Long> idList){
        regionWareService.removeByIds(idList);
        return Result.ok("批量删除成功");
    }
}
