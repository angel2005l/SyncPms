package com.pms.userUptAuto.userAuto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pms.sync.service.DepartService;

@SpringBootTest
class UserAutoApplicationTests {

	@Autowired
	private  DepartService service;
	
	@Test
	void contextLoads() {
		
		String deptNoPath = service.getDeptNoPath("7C0F084329DC4EA69213ED41306DC613");
		System.err.println(deptNoPath);
	}

}
