package com.pms.userUptAuto.userAuto.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pms.userUptAuto.userAuto.mapper.PmsAutoUptUserInfoMapper;

@Service
public class PmsAutoUptUserInfoService {
	
	@Autowired
	private PmsAutoUptUserInfoMapper userMapper;
	
	public void findUserInfo() {
		List<Map<String, Object>> selectPmsUser = userMapper.selectPmsUser();
		System.err.println(selectPmsUser);
	}
}
