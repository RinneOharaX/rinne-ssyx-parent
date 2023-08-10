package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.product.AttrGroup;
import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.service.CategoryService;
import com.rinneohara.ssyx.vo.product.AttrGroupQueryVo;
import com.rinneohara.ssyx.vo.product.CategoryQueryVo;
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
 * @DATE: 2023/8/10 16:04
 */

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/admin/product/category")
@ApiOperation("商品分类管理")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
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
                              CategoryQueryVo categoryQueryVo){
        IPage<Category> categoryIPage=new Page<>(page,limit);
        String categoryName = categoryQueryVo.getName();
        IPage<Category> pageList;
        if (!StringUtils.isEmpty(categoryName)){
            pageList = categoryService.getBaseMapper().selectPage(categoryIPage,
                    new LambdaQueryWrapper<Category>()
                            .like(Category::getName,categoryName));
        }else {
            pageList = categoryService.getBaseMapper().selectPage(categoryIPage,null);
        }
        return Result.ok(pageList);
    }
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("根据id获取分类")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        Category category = categoryService.getById(id);
        return Result.ok(category);
    }
    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    @ApiOperation("新增分类")
    @PostMapping("/save")
    public Result save(@RequestBody Category category){
        categoryService.save(category);
        return Result.ok("保存成功");
    }
    //  updateById(role) {
    //    return request({
    //      url: `${api_name}/update`,
    //      method: 'put',
    //      data: role
    //    })
    //  },
    @ApiOperation("更新分类")
    @PutMapping("/update")
    public Result updateById(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.ok("更新成功");
    }
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("根据Id删除分类")
    @DeleteMapping("/remove/{id}")
    public Result removeById(@PathVariable Long id){
        categoryService.removeById(id);
        return Result.ok("删除成功");
    }
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //    })
    //  },
    @ApiOperation("根据Id批量删除分类")
    @DeleteMapping("/batchRemove")
    public Result removeById(@RequestBody List<Long> idList){
        categoryService.removeByIds(idList);
        return Result.ok("批量删除成功");
    }
    //  findAllList() {
    //    return request({
    //      url: `${api_name}/findAllList`,
    //      method: 'get'
    //    })
    //  }
    @ApiOperation("获取所有分类")
    @GetMapping("/findAllList")
    public Result findAllList(){
        List<Category> categoryList =categoryService.getBaseMapper().selectList(null);
        return Result.ok(categoryList);
    }
}
