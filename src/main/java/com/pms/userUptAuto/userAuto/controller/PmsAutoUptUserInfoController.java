package com.pms.userUptAuto.userAuto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pms.userUptAuto.userAuto.service.PmsAutoUptUserInfoService;

@RestController
@RequestMapping("/admin")
public class PmsAutoUptUserInfoController {
	
	@Autowired
	private PmsAutoUptUserInfoService  pmsService;
	
	@PostMapping("index")
	public void findUser() {
		pmsService.findUserInfo();
	}
	
	
}
