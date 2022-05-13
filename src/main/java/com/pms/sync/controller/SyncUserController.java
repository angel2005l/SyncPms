package com.pms.sync.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pms.sync.entity.Result;
import com.pms.sync.mue.PmsUserInfoEnum;
import com.pms.sync.service.PmsAutoUptUserInfoService;
import com.pms.sync.util.POIUtil;

@RestController
@RequestMapping("/admin")
public class SyncUserController {
	
	@Autowired
	private PmsAutoUptUserInfoService  pmsService;
	
	@PostMapping("index")
	public Result<List<Map<String, String>>>  updateUserToExcel(@RequestParam(value = "filename") MultipartFile excelFile,
			HttpServletRequest request, HttpServletResponse response){
		try {
			List<Map<String, String>> pmsUserInfo = POIUtil.readExcel(excelFile, true, 4, true, PmsUserInfoEnum.class);
			return pmsService.cleanUserInfo(pmsUserInfo);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result<>("500", e.toString());
		}
		
	}
	
	
}
