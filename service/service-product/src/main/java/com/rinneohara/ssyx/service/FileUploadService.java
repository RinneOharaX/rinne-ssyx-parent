package com.rinneohara.ssyx.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/11 15:30
 */
@Service
public interface FileUploadService {
    String fileUpload(MultipartFile file) throws Exception;
}
