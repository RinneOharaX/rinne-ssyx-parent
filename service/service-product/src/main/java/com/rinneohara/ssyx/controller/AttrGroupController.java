package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.product.AttrGroup;
import com.rinneohara.ssyx.service.AttrGroupService;
import com.rinneohara.ssyx.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 15:33
 */

@RestController
@Slf4j
@RequestMapping("/admin/product/attrGroup")
@ApiOperation("平台组管理")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    // getPageList(page, limit, searchObj) {
    //    return request({
    //      url: `${api_name}/${page}/${limit}`,
    //      method: 'get',
    //      params: searchObj
    //    })
    //  },
    @GetMapping("/{page}/{limit}")
    @ApiOperation("条件分页查询平台组")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit,
                              AttrGroupQueryVo attrGroupQueryVo){
        IPage<AttrGroup> attrGroupIPage=new Page<>(page,limit);
        String groupName = attrGroupQueryVo.getName();
        IPage<AttrGroup> pageList;
        if (!StringUtils.isEmpty(groupName)){
           pageList = attrGroupService.getBaseMapper().selectPage(attrGroupIPage,
                    new LambdaQueryWrapper<AttrGroup>()
                            .like(AttrGroup::getName,  groupName));
        }else {
          pageList = attrGroupService.getBaseMapper().selectPage(attrGroupIPage,null);
        }
        return Result.ok(pageList);
    }
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    //
    @ApiOperation("根据id获取平台组")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        AttrGroup attGroup = attrGroupService.getById(id);
        return Result.ok(attGroup);
    }
    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    //
    @ApiOperation("新增平台组")
    @PostMapping("/save")
    public Result save(@RequestBody AttrGroup attrGroup){
        attrGroupService.save(attrGroup);
        return Result.ok("保存成功");
    }
    //  updateById(role) {
    //    return request({
    //      url: `${api_name}/update`,
    //      method: 'put',
    //      data: role
    //    })
    //  },
    @ApiOperation("更新平台组")
    @PutMapping("/update")
    public Result updateById(@RequestBody AttrGroup attrGroup){
        attrGroupService.updateById(attrGroup);
        return Result.ok("更新成功");
    }
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("根据Id删除平台组")
    @DeleteMapping("/remove/{id}")
    public Result removeById(@PathVariable Long id){
        attrGroupService.removeById(id);
        return Result.ok("删除成功");
    }
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //    })
    //  },

    @ApiOperation("根据Id批量删除平台组")
    @DeleteMapping("/batchRemove")
    public Result removeById(@RequestBody List<Long> idList){
        attrGroupService.removeByIds(idList);
        return Result.ok("批量删除成功");
    }
    //  findAllList() {
    //    return request({
    //      url: `${api_name}/findAllList`,
    //      method: 'get'
    //    })
    //  }
    @ApiOperation("获取所有平台组")
    @GetMapping("/findAllList")
    public Result findAllList(){
        List<AttrGroup> attrGroups = attrGroupService.getBaseMapper().selectList(null);
        return Result.ok(attrGroups);
    }

}
