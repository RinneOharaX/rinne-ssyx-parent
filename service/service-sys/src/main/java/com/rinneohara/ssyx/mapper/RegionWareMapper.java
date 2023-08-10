package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.rinneohara.ssyx.model.sys.RegionWare;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface RegionWareMapper extends BaseMapper<RegionWare> {


    void updateState(Long id, Long status);

}
