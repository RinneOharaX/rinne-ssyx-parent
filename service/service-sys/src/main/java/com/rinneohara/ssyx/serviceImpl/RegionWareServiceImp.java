package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.mapper.RegionWareMapper;
import com.rinneohara.ssyx.model.sys.RegionWare;
import com.rinneohara.ssyx.service.RegionService;
import com.rinneohara.ssyx.service.RegionWareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/8 14:37
 */
@Service
public class RegionWareServiceImp extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {
    @Resource
    RegionWareMapper regionWareMapper;
    @Override
    public void updateState(Long id, Long status) {
            regionWareMapper.updateState(id,status);

    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        Integer count = regionWareMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new MyException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }
}
