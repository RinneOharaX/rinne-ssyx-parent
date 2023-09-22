package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.product.Attr;
import com.rinneohara.ssyx.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 14:58
 */

@Api("平台属性管理")
@RestController
@Slf4j
@RequestMapping("/admin/product/attr")
public class AttrController  {
    @Autowired
    AttrService attrService;
    //  getList(groupId) {
    //    return request({
    //      url: `${api_name}/${groupId}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("根据组id获取平台属性")
    @GetMapping("/{groupId}")
    public Result getList(@PathVariable Long groupId){
        List<Attr> attrs = attrService.getBaseMapper().selectList(new LambdaQueryWrapper<Attr>().eq(Attr::getAttrGroupId, groupId));
        return Result.ok(attrs);
    }
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    //
    @ApiOperation("根据id获取平台属性")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        return Result.ok(attrService.getById(id));
    }
    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    //
    @ApiOperation("新增平台属性")
    @PostMapping("/save")
    public Result save(@RequestBody Attr attr){
        attrService.save(attr);
        return Result.ok("保存成功");
    }
    //  updateById(role) {
    //    return request({
    //      url: `${api_name}/update`,
    //      method: 'put',
    //      data: role
    //    })
    //  },
    @ApiOperation("根据Id修改平台属性")
    @PutMapping("/update")
    public Result updateById(@RequestBody Attr attr){
        attrService.updateById(attr);
        return Result.ok("修改成功");
    }
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("根据Id删除平台属性")
    @DeleteMapping("/remove/{id}")
    public  Result removeById(@PathVariable Long id){
        attrService.removeById(id);
        return Result.ok("删除成功");
    }
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //    })
    //  }
    @ApiOperation("根据Id批量删除平台属性")
    @DeleteMapping("/batchRemove")
    public  Result removeRows(@RequestBody List<Long> idList){
        attrService.removeByIds(idList);
        return Result.ok("批量删除成功");
    }
}
