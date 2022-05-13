package com.pms.sync.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pms.sync.entity.Depart;
import com.pms.sync.entity.Result;
import com.pms.sync.mapper.DepartMapper;
import com.pms.sync.mapper.UserMapper;
import com.pms.sync.util.UUIDUtil;

@Service
public class DepartService {

	private static Logger log = LoggerFactory.getLogger(DepartService.class);

	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private UserMapper userMapper;

	public Result<Object> syncDepart(List<Map<String, String>> departs) throws Exception {
		// 校验处理数据集
		if (departs == null || departs.isEmpty()) {
			return new Result<>(Result.ERROR, "组织架构处理数据为空！");
		}
//		for (Map<String, String> addDepart : departs) {
//			if (!checkDepartExists(addDepart.get("deptCode"))) {
//				addDepartInfo(addDepart);
//			}
//		}
//		for (Map<String, String> uptDepart : departs) {
//			uptDepartInfo(uptDepart);
//		}
		// 完成后生成PATH
		uptDeptNoPath();
		return new Result<Object>(Result.SUCCESS, "组织更新完成，共计处理组织" + departs.size());
	}

	/**
	 * 
	 * @Description:当组织信息更新后，批量修正PATH路径
	 * @author: huanggya
	 * @date: 2022年5月12日上午10:16:10
	 * @version:1.0
	 */
	public void uptDeptNoPath() {
		try {
			List<Depart> departs = departMapper.selDepartWithUpt();

			if (null == departs || departs.isEmpty()) {
				return;
			}
			for (Depart depart : departs) {

				String deptNoPath = getDeptNoPath(depart.getDeptNo());
				departMapper.uptDepartForDeptNoPath(depart.getWid(), deptNoPath);
			}
		} catch (Exception e) {
			log.error(e.toString());
		}

	}

	/**
	 * 
	 * @Description:新增组织
	 * @param newDepart
	 * @author: huanggya
	 * @date: 2022年5月11日下午2:08:18
	 * @version:1.0
	 * @throws Exception
	 */
	public void addDepartInfo(Map<String, String> depart) {
		try {
			Depart newDepart = new Depart();
			// 获得WID，deptNo
			String wid = UUIDUtil.genUUID();
			String deptNo = UUIDUtil.genUUID();
			newDepart.setWid(wid);
			newDepart.setDeptNo(deptNo);
			// 設置name
			newDepart.setDeptCode(depart.get("deptCode"));
			newDepart.setDeptName(depart.get("deptName"));

			// 根据父类Code 获得父类deptno excel存值只有code
//			String parentDepartCode = newDepart.getParentDeptNo();
//			String parentDepartCode = depart.get("parentDeptNo");
//			
//			String parentDepartNo = departMapper.selDeptNoByDepartCode(parentDepartCode);
//			if (!StringUtils.isEmpty(parentDepartNo)) {
//				newDepart.setParentDeptNo(parentDepartNo);
//			}
//			// 根据VP工号，获得VP的userId
//			String userVp = newDepart.getUserVp();
//			depart.get("userVp");
//			String userVpId = userMapper.selUserIdByJobNum(userVp);
//			if (!StringUtils.isEmpty(userVpId)) {
//				newDepart.setUserVp(userVpId);
//			}
			// 执行保存
			departMapper.insDepart(newDepart);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * 
	 * @Description:更新部门
	 * @param depart
	 * @author: huanggya
	 * @date: 2022年5月11日下午4:02:57
	 * @version:1.0
	 */
	public void uptDepartInfo(Map<String, String> depart) {
		try {
			// 根据父类Code 获得父类deptno excel存值只有code
			String parentDepartCode = depart.get("parentDeptNo");
			String parentDepartNo = departMapper.selDeptNoByDepartCode(parentDepartCode);
			if (!StringUtils.isEmpty(parentDepartNo)) {
				depart.put("parentDeptNo", parentDepartNo);
			} else {
				depart.put("parentDeptNo", "");
			}
			// 根据VP工号，获得VP的userId
			String userVp = depart.get("userVp");
			String userVpId = userMapper.selUserIdByJobNum(userVp);
			if (!StringUtils.isEmpty(userVpId)) {
				depart.put("userVp", userVpId);
			} else {
				depart.put("userVp", "");
			}
			// 执行更新
			departMapper.uptDepart(depart);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * 
	 * @Description:递归获取职位Path
	 * @param deptNo
	 * @return
	 * @author: huanggya
	 * @date: 2022年5月12日上午9:35:36
	 * @version:1.0
	 */
	public String getDeptNoPath(String deptNo) {
		String path = "";
		try {
			Depart dapart = departMapper.selDepartByDeptNo(deptNo);
			if (null != dapart) {
				String parentDeptNo = dapart.getParentDeptNo();
				if (!StringUtils.isEmpty(parentDeptNo) && !"0".equals(parentDeptNo)) {

					path = getDeptNoPath(parentDeptNo) + "," + deptNo;
				} else {
					path = deptNo;
				}
			}

		} catch (Exception e) {
			log.error(e.toString());
		}
		return path;
	}

	/**
	 * 
	 * @Description:校验组织是否存在，
	 * @param deptCode
	 * @return
	 * @throws Exception
	 * @author: huanggya
	 * @date: 2022年5月11日下午2:05:30
	 * @version:1.0
	 */
	public boolean checkDepartExists(String deptCode) throws Exception {
		// 根据deptCode查询是否存在 组织
		int departCot = departMapper.selDepartNumByDeptCode(deptCode);
		System.err.println(departCot);
		if (departCot > 0) {
			return true;
		}
		return false;
	}

}
