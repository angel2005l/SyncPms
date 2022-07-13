package com.pms.sync.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.pms.sync.entity.User;

@Mapper
public interface UserMapper {

	@Select({ "SELECT USER_ID from CM_USERS_TE  where JOB_NUMBER = #{jobNumber,jdbcType=VARCHAR}" })
	public String selUserIdByJobNumber(@Param("jobNumber") String jobNumber) throws Exception;

	@Update({
			"INSERT INTO  CM_USERS_TE(WID,USER_ID,USER_NAME,ACTUAL_NAME,PASSWORD,SEX,EMAIL,DISABLED,IS_DOMAIN,JOB_NUMBER,DEPART_NAME,VP,ONE_DEPARTMENT,TWO_DEPARTMENT,GRADE_LEVEL,UPT_FLAG)",
			"VALUES(#{wid},#{userId},#{userName},#{actualName},#{password},#{sex},#{email},#{disabled},#{isDomain},#{jobNumber},#{departName,jdbcType=VARCHAR},#{vp,jdbcType=VARCHAR},#{oneDepartment,jdbcType=VARCHAR},#{twoDepartment,jdbcType=VARCHAR},#{gradeLevel,jdbcType=INTEGER},'Y')" })
	public Integer insUser(User user) throws Exception;

	@Update({
			"UPDATE CM_USERS_TE SET USER_NAME = #{userName} ,ACTUAL_NAME = #{actualName}, SEX = #{sex},EMAIL = #{email},DISABLED = #{disabled},DEPART_NAME=#{departName,jdbcType=VARCHAR},VP = #{vp,jdbcType=VARCHAR},ONE_DEPARTMENT = #{oneDepartment,jdbcType=VARCHAR},TWO_DEPARTMENT = #{twoDepartment,jdbcType=VARCHAR},GRADE_LEVEL = #{gradeLevel,jdbcType=INTEGER},UPT_FLAG = 'Y' WHERE USER_ID = #{userId,jdbcType=VARCHAR}" })
	public Integer uptUser(User user) throws Exception;

	@Select({ "SELECT COUNT(1) FROM CM_USERS_TE WHERE JOB_NUMBER = #{jobNumber,jdbcType=VARCHAR}" })
	public Integer selUserNumByJobNumber(@Param("jobNumber") String jobNumber) throws Exception;

	@Update("INSERT INTO CM_DEPTUSER_TE(USER_ID,DEPT_ID) VALUES(#{userId,jdbcType=VARCHAR},#{deptNo,jdbcType=VARCHAR})")
	public Integer insDeptUser(@Param("userId") String userId, @Param("deptNo") String deptNo) throws Exception;
//
//	@Insert("INSERT INTO CO_MSG_SUBS_TE (USER_ID,MODULE_ID,SUBS_TYPE_SMS) values(#{userId},'INSTANT_NOTIFY','Y')")
//	public int insMsgSubs(@Param("userId") String userId) throws Exception;
	
	@Delete("DELETE FROM CM_DEPTUSER_TE WHERE USER_ID=#{userId,jdbcType=VARCHAR}")
	public Integer delDeptUser(@Param("userId")String userId) throws Exception;
}
