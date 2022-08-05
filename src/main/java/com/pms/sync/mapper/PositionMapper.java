package com.pms.sync.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.pms.sync.entity.Position;

@Mapper
public interface PositionMapper {

	@Select("SELECT COUNT(1) FROM CM_DEPART WHERE  NODE_TYPE = '0' AND  DEPT_CODE = #{positionCode}")
	public Integer selPositionNumByPositionCode(String positionCode) throws Exception;
	
	@Select("SELECT DEPT_NO FROM CM_DEPART WHERE NODE_TYPE = '1' AND  DEPT_CODE = #{positionCode} ")
	public String selPositionNoByPositionCode(String positionCode) throws Exception;
	
	@Select("SELECT PARENT_DEPT_NO FROM CM_DEPART WHERE NODE_TYPE = '0' AND  DEPT_CODE = #{positionCode} ")
	public String selParentPositionNoByPositionCode(String positionCode) throws Exception;
	
	@Select("INSERT INTO CM_DEPART(WID,DEPT_NO,DEPT_CODE,DEPT_NAME,PARENT_DEPT_NO,DEPT_TYPE,NODE_TYPE,LIMIT_NUM,UPT_FLAG) VALUES(#{wid},#{positionNo},#{positionCode},#{positionName},#{positionCode},'SYS_DEPART','0',0,'Y')")
	public Integer insPosition(Position position) throws Exception; 
	
	@Update("UPDATE CM_DEPART SET DEPT_NAME=#{positionName},PARENT_DEPT_NO=#{parentPositionNo},USER_VP=#{userVp},UPT_FLAG='Y' where DEPT_CODE =#{positionCode} AND NODE_TYPE = '0' for update")
	public Integer uptPosition(Map<String,String> position) throws Exception;

	@Select("SELECT * FROM CM_DEPART WHERE  NODE_TYPE = '0' AND  DEPT_NO = #{positionNo,jdbcType=VARCHAR}" )
	@Results(id="positionMap",value= {
			@Result(column="WID",property="wid"),
			@Result(column="DEPT_NO",property="positionNo"),
			@Result(column="DEPT_CODE",property="positionCode"),
			@Result(column="DEPT_NAME",property="positionName"),
			@Result(column="PARENT_DEPT_NO",property="parentPositionNo"),
			@Result(column="REMARK",property="remark"),
			@Result(column="DEPT_TYPE",property="positionType"),
			@Result(column="NODE_TYPE",property="nodeType"),
			@Result(column="LIMIT_NUM",property="limitNum"),
			@Result(column="USER_VP",property="userVp"),
			@Result(column="UPT_FLAG",property="uptFlag")
	})
	public Position selPositionByPositionNo(@Param("positionNo") String positionNo) throws Exception;
	
	@Select("SELECT * FROM CM_DEPART WHERE  DEPT_NO = #{deptNo,jdbcType=VARCHAR}" )
	@ResultMap("positionMap")
	public Position selDepartByDeptNo(@Param("deptNo") String deptNo) throws Exception;
	
	@Select("SELECT * FROM CM_DEPART WHERE UPT_FLAG = 'Y' AND  NODE_TYPE = '0'")
	@ResultMap(value="positionMap")
	public List<Position> selPositionWithUpt() throws Exception;
	
	@Update("UPDATE CM_DEPART SET DEPT_NO_PATH = #{positionNoPath,jdbcType=VARCHAR},UPT_FLAG='N' where WID = #{wid} AND NODE_TYPE = '0' for update")
	public Integer uptPositionForPositionNoPath(@Param("wid") String wid, @Param("positionNoPath") String positionNoPath) throws Exception;
	
	
}
