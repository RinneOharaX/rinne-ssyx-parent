package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.service.FileUploadService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/11 15:29
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/admin/product")
@ApiOperation("文件上传接口")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    //文件上传
    @PostMapping("/fileUpload")
    public Result fileUpload(MultipartFile file) throws Exception{
        return Result.ok(fileUploadService.fileUpload(file));
    }
}
