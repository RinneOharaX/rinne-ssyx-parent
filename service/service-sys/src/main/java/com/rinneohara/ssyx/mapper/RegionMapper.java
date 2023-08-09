package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.sys.Region;
import org.apache.ibatis.annotations.Mapper;

import java.util.Base64;

@Mapper
public interface RegionMapper extends BaseMapper<Region> {
}
