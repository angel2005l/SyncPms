package com.pms.userUptAuto.userAuto.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PmsAutoUptUserInfoMapper {

	@Select("SELECT * FROM CM_USERS WHERE USER_NAME = 'huanggya'")
	public List<Map<String,Object>> selectPmsUser();
	
}
