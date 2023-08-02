package com.rinneohara.ssyx.vo.acl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "管理员登录信息")
@TableName("admin")
public class AdminLoginVo implements Serializable {

	private static final long serialVersionUID = 1L;
	@TableField("id")
	@ApiModelProperty(value = "管理员id")
	private Long adminId;


	@ApiModelProperty(value = "姓名")
	private String name;

	@ApiModelProperty(value = "仓库id")
	private Long wareId;

}
