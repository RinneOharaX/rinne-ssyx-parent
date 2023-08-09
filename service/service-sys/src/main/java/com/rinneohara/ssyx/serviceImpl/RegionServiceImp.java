package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.RegionMapper;
import com.rinneohara.ssyx.model.sys.Region;
import com.rinneohara.ssyx.service.RegionService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/8 14:31
 */
@Service
public class RegionServiceImp extends ServiceImpl<RegionMapper, Region> implements RegionService {
}
