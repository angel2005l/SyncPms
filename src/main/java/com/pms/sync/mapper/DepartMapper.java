package com.pms.sync.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.pms.sync.entity.Depart;

@Mapper
public interface DepartMapper {

	@Select("SELECT COUNT(1) FROM CM_DEPART_TE WHERE  NODE_TYPE = '1' AND  DEPT_CODE = #{deptCode,jdbcType=VARCHAR}")
	public Integer selDepartNumByDeptCode(String deptCode) throws Exception;

	@Select("SELECT DEPT_NO FROM CM_DEPART_TE WHERE NODE_TYPE = '1' AND  DEPT_CODE = #{deptCode,jdbcType=VARCHAR} ")
	public String selDeptNoByDepartCode(String deptCode) throws Exception;

	@Select("INSERT INTO CM_DEPART_TE(WID,DEPT_NO,DEPT_CODE,DEPT_NAME,PARENT_DEPT_NO,DEPT_TYPE,NODE_TYPE,LIMIT_NUM,UPT_FLAG) VALUES(#{wid},#{deptNo},#{deptCode,jdbcType=VARCHAR},#{deptName,jdbcType=VARCHAR},#{deptCode,jdbcType=VARCHAR},'SYS_DEPART','1',0,'Y')")
	public Integer insDepart(Depart depart) throws Exception;

	@Select("UPDATE CM_DEPART_TE SET DEPT_NAME=#{deptName},PARENT_DEPT_NO=#{parentDeptNo,jdbcType=VARCHAR},USER_VP=#{userVp,jdbcType=VARCHAR},UPT_FLAG='Y' where DEPT_CODE =#{deptCode,jdbcType=VARCHAR} AND NODE_TYPE = '1'")
	public Integer uptDepart(Map<String, String> depart) throws Exception;

	@Delete("DELETE CM_DEPART_TE WHERE  DEPT_CODE =#{deptCode} AND NODE_TYPE = '1'")
	public Integer delDepart(String deptCode) throws Exception;

	@Select("SELECT * FROM CM_DEPART_TE WHERE  NODE_TYPE = '1' AND  DEPT_NO = #{deptNo}")
	@Results(id = "deptMap", value = { @Result(column = "WID", property = "wid"),
			@Result(column = "DEPT_NO", property = "deptNo"), @Result(column = "DEPT_CODE", property = "deptCode"),
			@Result(column = "DEPT_NAME", property = "deptName"),
			@Result(column = "PARENT_DEPT_NO", property = "parentDeptNo"),
			@Result(column = "REMARK", property = "remark"), @Result(column = "DEPT_TYPE", property = "deptType"),
			@Result(column = "NODE_TYPE", property = "nodeType"), @Result(column = "LIMIT_NUM", property = "limitNum"),
			@Result(column = "USER_VP", property = "userVp"), @Result(column = "UPT_FLAG", property = "uptFlag") })
	public Depart selDepartByDeptNo(@Param("deptNo") String deptNo) throws Exception;

	@Select("SELECT * FROM CM_DEPART_TE WHERE UPT_FLAG = 'Y' AND NODE_TYPE = '1'")
	@ResultMap(value = "deptMap")
	public List<Depart> selDepartWithUpt() throws Exception;

	@Update("UPDATE CM_DEPART_TE SET DEPT_NO_PATH = #{deptNoPath},UPT_FLAG='N' where WID = #{wid} AND NODE_TYPE = '1'")
	public Integer uptDepartForDeptNoPath(@Param("wid") String wid, @Param("deptNoPath") String deptNoPath)
			throws Exception;

}
