package com.pms.sync.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

	@Select("SELECT USER_ID from cm_users  where JOB_NUMBER = #{jobNum}")
	public String selUserIdByJobNum(@Param("jobNum") String jobNum) throws Exception;
	
}
