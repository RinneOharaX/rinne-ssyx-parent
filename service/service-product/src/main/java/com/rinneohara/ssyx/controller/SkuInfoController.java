package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.base.BaseEntity;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.FileUploadService;
import com.rinneohara.ssyx.service.SkuImageService;
import com.rinneohara.ssyx.service.SkuInfoService;
import com.rinneohara.ssyx.vo.product.SkuInfoQueryVo;
import com.rinneohara.ssyx.vo.product.SkuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.rinneohara.ssyx.repository.*;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 16:22
 */


@Slf4j
@RestController
@Api("SKU管理")
@RequestMapping("/admin/product/skuInfo")
@CrossOrigin
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private  SkuRepository skuRepository;


    // getPageList(page, limit, searchObj) {
    //    return request({
    //      url: `${api_name}/${page}/${limit}`,
    //      method: 'get',
    //      params: searchObj
    //    })
    //  },
    @ApiOperation("sku条件查询分页")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable Long page,
                              @PathVariable Long limit,
                              SkuInfoQueryVo skuInfoQueryVo){
        IPage<SkuInfo> pageRule =new Page<>(page,limit);
        String keyword = skuInfoQueryVo.getKeyword();
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        //封装条件
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(keyword)) {
            wrapper.like(SkuInfo::getSkuName,keyword);
        }
        if(!StringUtils.isEmpty(skuType)) {
            wrapper.eq(SkuInfo::getSkuType,skuType);

        }
        if(!StringUtils.isEmpty(categoryId)) {
            wrapper.eq(SkuInfo::getCategoryId,categoryId);
        }
        //调用方法查询
        IPage<SkuInfo> skuInfoPage = skuInfoService.getBaseMapper().selectPage(pageRule, wrapper);

        return Result.ok(skuInfoPage);
    }
    //  getById(id) {
    //    return request({
    //      url: `${api_name}/get/${id}`,
    //      method: 'get'
    //    })
    //  },
    //
    @ApiOperation("根据id查询sku")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable Long id){
        SkuInfo sku = skuInfoService.getById(id);
        return Result.ok(sku);
    }

    //  save(role) {
    //    return request({
    //      url: `${api_name}/save`,
    //      method: 'post',
    //      data: role
    //    })
    //  },
    //
    @ApiOperation("新增sku")
    @PostMapping("/save")
    public Result save(@RequestBody SkuInfo skuInfo){
        skuInfoService.save(skuInfo);
        return Result.ok("保存成功");
    }
    //  updateById(role) {
    //    return request({
    //      url: `${api_name}/update`,
    //      method: 'put',
    //      data: role
    //    })
    //  },
    @ApiOperation("根据Id修改sku")
    @PutMapping("/update")
    public  Result updateById(@RequestBody SkuInfoVo skuInfoVo){
        skuInfoService.updateSkuInfoVo(skuInfoVo);
        return Result.ok("修改成功");
    }
    //  removeById(id) {
    //    return request({
    //      url: `${api_name}/remove/${id}`,
    //      method: 'delete'
    //    })
    //  },
    @ApiOperation("根据Id删除sku")
    @DeleteMapping("/remove/{id}")
    public Result removeById(@PathVariable Long id){
        skuInfoService.removeById(id);
        skuRepository.deleteById(id);
        return Result.ok("删除成功");
    }
    //  removeRows(idList) {
    //    return request({
    //      url: `${api_name}/batchRemove`,
    //      method: 'delete',
    //      data: idList
    //    })
    //  },
    @ApiOperation("根据IdList批量删除sku")
    @DeleteMapping("/batchRemove")
    public Result removeRows(@RequestBody List<Long> idList){
        skuInfoService.removeByIds(idList);
        idList.forEach(id->{
            skuRepository.deleteById(id);
        });
        return Result.ok("批量删除成功");
    }
    //  //商品上架
    //  publish(id, status) {
    //    return request({
    //      url: `${api_name}/publish/${id}/${status}`,
    //      method: 'get'
    //    })
    //  },
    //
    @ApiOperation("商品上架")
    @GetMapping("/publish/{id}/{status}")
    public Result publish(@PathVariable Long id,
                          @PathVariable Long status){
        skuInfoService.publish(id,status);
        return Result.ok("商品上架成功");
    }
    //  //商品审核
    //  check(id, status) {
    //    return request({
    //      url: `${api_name}/check/${id}/${status}`,
    //      method: 'get'
    //    })
    //  },
    //
    @ApiOperation("商品审核")
    @GetMapping("/check/{id}/{status}")
    public Result check(@PathVariable Long id,
                          @PathVariable Long status){
        skuInfoService.update(new LambdaUpdateWrapper<SkuInfo>().set(SkuInfo::getCheckStatus,status).eq(BaseEntity::getId,id));
        return Result.ok("商品审核成功");
    }
    //  //新人专享
    //  isNewPerson(id, status) {
    //    return request({
    //      url: `${api_name}/isNewPerson/${id}/${status}`,
    //      method: 'get'
    //    })
    //  },
    @ApiOperation("新人专享")
    @GetMapping("/isNewPerson/{id}/{status}")
    public Result isNewPerson(@PathVariable Long id,
                        @PathVariable Long status){
        skuInfoService.update(new LambdaUpdateWrapper<SkuInfo>().set(SkuInfo::getIsNewPerson,status).eq(BaseEntity::getId,id));
        return Result.ok("新人专享");
    }
}
