package com.pms.sync.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pms.sync.entity.Result;
import com.pms.sync.mue.UserEnum;
import com.pms.sync.service.UserService;
import com.pms.sync.util.POIUtil;

@RestController
@RequestMapping("/user")
public class SyncUserController {
	
	private static Logger log = LoggerFactory.getLogger(SyncUserController.class);

	@Autowired
	private UserService userService;

	@PostMapping("sync")
	public Result<Object> updateUserToExcel(@RequestParam(value = "filename") MultipartFile excelFile,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Map<String, String>> users = POIUtil.readExcel(excelFile, true, 11, true, UserEnum.class);
			return userService.syncUser(users);
		} catch (Exception e) {
			log.error(e.toString());
			return new Result<>(Result.ERROR, e.toString());
		}

	}
	
}
