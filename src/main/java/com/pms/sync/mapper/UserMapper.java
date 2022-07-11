package com.pms.sync.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.pms.sync.entity.User;

@Mapper
public interface UserMapper {

	@Select({ "SELECT USER_ID from CM_USERS_TE  where JOB_NUMBER = #{jobNumber}" })
	public String selUserIdByJobNumber(@Param("jobNumber") String jobNumber) throws Exception;

	@Insert({
			"INSERT INTO  CM_USERS_TE(WID,USER_ID,USER_NAME,ACTUAL_NAME,PASSWORD,SEX,EMAIL,DISABLED,IS_DOMAIN,JOB_NUMBER,DEPART_NAME,VP,ONE_DEPARTMENT,TWO_DEPARTMENT,GRADE_LEVEL,UPT_FLAG)",
			"VALUES(#{wid},#{userId},#{userName},#{actualName},#{password},#{sex},#{email},#{disabled},#{isDimain},#{jobNumber},#{departName},#{vp},#{oneDepartment},#{twoDepartment},#{gradeLevel},'Y')" })
	public int insUser(User user) throws Exception;

	@Update({
			"UPDATE CM_USERS_TE SET USER_NAME = #{userName} ,ACTUAL_NAME = #{actualName}, SEX = #{sex},EMAIL = #{email},DISABLED = #{disabled},DEPART_NAME=#{departName},VP = #{vp},ONE_DEPARTMENT = #{oneDepartment},TWO_DEPARTMENT = #{twoDepartment},GRADE_LEVEL = #{gradeLevel},UPT_FLAG = 'Y' WHERE USER_ID = #{userId}" })
	public int uptUser(User user) throws Exception;

	@Select({ "SELECT COUNT(1) FROM CM_USERS_TE WHERE JOB_NUMBER = #{jobNumber}" })
	public int selUserNumByJobNumber(@Param("jobNumber") String jobNumber) throws Exception;

	@Insert("INSERT INTO CM_DEPTUSER(USER_ID,DEPT_ID) VALUES(#{USERId},#{deptNo})")
	public int insDeptUser(@Param("userId") String userId, @Param("deptNo") String deptNo) throws Exception;

	@Insert("INSERT INTO CO_MSG_SUBS (USER_ID,MODULE_ID,SUBS_TYPE_SMS) values(#{userId},'INSTANT_NOTIFY','Y')")
	public int insMsgSubs(@Param("userId") String userId) throws Exception;
	
	@Delete("DELETE FROM CM_DEPTUSER WHERE USER_ID=#{userId}")
	public int delDeptUser(@Param("userId")String userId) throws Exception;
}
