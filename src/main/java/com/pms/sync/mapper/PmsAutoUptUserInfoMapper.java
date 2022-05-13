package com.pms.sync.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PmsAutoUptUserInfoMapper {

	@Select("SELECT * FROM CM_USERS WHERE USER_NAME = 'huanggya'")
	public List<Map<String,Object>> selectPmsUser();
	
	@Update({
		"UPDATE CM_USERS SET DISABLED = '1' WHERE ACTUAL_NAME =#{actualName} AND  JOB_NUMBER = #{jobNumber}"
	})
	public int closeAccount(@Param("actualName") String actualName,@Param("jobNumber") String jobNumber) throws Exception;
	
	@Update({
		""
	})
	public int updatePmsUser() throws Exception;
	@Update({
		"UPDATE CM_USERS SET  GRADE_LEVEL= #{gradeLevel} WHERE ACTUAL_NAME = #{actualName} AND  JOB_NUMBER = #{jobNumber}"
	})
	public int updateGradeLevel(@Param("actualName") String actualName,@Param("jobNumber") String jobNumber,@Param("gradeLevel") int gradeLevel) throws Exception;
}
