package com.pms.sync.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pms.sync.entity.Result;
import com.pms.sync.mapper.PmsAutoUptUserInfoMapper;

@Service
public class PmsAutoUptUserInfoService {
	@Autowired
	private PmsAutoUptUserInfoMapper pauMapper ;

	
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
			
		
		return new Result<List<Map<String, String>>>("200", "执行完成",pmsUsers);
	}
	
	
}
