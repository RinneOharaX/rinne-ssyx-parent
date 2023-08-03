package com.rinneohara.ssyx.acl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.acl.Admin;
import com.rinneohara.ssyx.vo.acl.AdminLoginVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/1 13:07
 */

@Mapper
public interface IndexMapper extends BaseMapper<Admin> {
}
