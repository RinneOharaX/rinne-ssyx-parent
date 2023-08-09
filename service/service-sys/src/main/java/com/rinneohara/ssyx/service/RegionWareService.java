package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.sys.RegionWare;
import org.springframework.stereotype.Service;


@Service
public interface RegionWareService extends IService<RegionWare> {
    void updateState(Long id,Long status);

    void saveRegionWare(RegionWare regionWare);
}
