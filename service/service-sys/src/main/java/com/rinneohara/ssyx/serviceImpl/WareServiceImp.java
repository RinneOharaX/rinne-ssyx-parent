package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.WareMapper;
import com.rinneohara.ssyx.model.sys.Ware;
import com.rinneohara.ssyx.service.WareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/8 14:34
 */
@Service
public class WareServiceImp extends ServiceImpl<WareMapper, Ware> implements WareService {
}
