package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.sys.Region;
import com.rinneohara.ssyx.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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



}
