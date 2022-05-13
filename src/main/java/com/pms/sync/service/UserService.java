package com.pms.sync.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pms.sync.entity.Result;
import com.pms.sync.entity.User;
import com.pms.sync.mapper.PmsAutoUptUserInfoMapper;
import com.pms.sync.mapper.UserMapper;
import com.pms.sync.util.UUIDUtil;

@Service
public class UserService {
	@Autowired
	private PmsAutoUptUserInfoMapper pauMapper ;
	@Autowired
	private UserMapper userMapper;

	
	//清洗数据
	public Result<List<Map<String, String>>> cleanUserInfo(List<Map<String, String>> pmsUsers) throws Exception{
		//第一步，校验数据信息
		if(null== pmsUsers||pmsUsers.isEmpty()) 
			return new Result<>("500", "上传的数据集合为空");
		
		for (Map<String, String> map : pmsUsers) {
			
			if("离职".equals(map.get("emp_status"))) {
				//删除数据
				pauMapper.closeAccount(map.get("name"),map.get("job_number"));
				continue;
			}
			pauMapper.updateGradeLevel(map.get("name"),map.get("job_number"),Integer.parseInt(map.get("grade_level")));
			
		}
			
		
		return new Result<List<Map<String, String>>>(Result.SUCCESS, "执行完成",pmsUsers);
	}
	
	
	
	
	/**
	 * 施工点
	 */
	
	//批量数据
	public Result<Object> syncUser(List<Map<String,String>> users) throws Exception{
		if(null == users || users.isEmpty()) {
			return new Result<Object>(Result.ERROR,"用户信息处理数据为空！");
		}
		//新增
		
		
		//更新
		
		
		return null;
	}
	
	public void addUser(Map<String,String> user) {
		try {
			User newUser = new User();
			
			String wid = UUIDUtil.genUUID();
			String userId = UUIDUtil.genUUID();
			
			String salt = "850B1032922CDCDC";
		}catch(Exception e) {
			
		}
		
		
		
	}
	
	
	
	
}
