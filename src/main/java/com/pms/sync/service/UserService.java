package com.pms.sync.service;

import java.util.List;
import java.util.Map;

import javax.sound.midi.SysexMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pms.sync.entity.Depart;
import com.pms.sync.entity.Result;
import com.pms.sync.entity.User;
import com.pms.sync.mapper.DepartMapper;
import com.pms.sync.mapper.PositionMapper;
import com.pms.sync.mapper.UserMapper;
import com.pms.sync.util.PasswordUtil;
import com.pms.sync.util.UUIDUtil;

@Service
public class UserService {
	private static Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private DepartMapper departMapper;

	/**
	 * 
	 * @Description:人员同步
	 * @param users
	 * @return
	 * @throws Exception
	 * @author: huanggya
	 * @date: 2022年5月24日下午4:26:37
	 * @version:版本
	 */
	public Result<Object> syncUser(List<Map<String, String>> users) throws Exception {
		if (null == users || users.isEmpty()) {
			return new Result<Object>(Result.ERROR, "用户信息处理数据为空！");
		}
		// 防止父类组织新增导致更新失效
		for (Map<String, String> addUser : users) {
			if (!checkUserExists(addUser.get("jobNumber"))) {
				if("离职".equals(addUser.get("disabled")))
					continue;
				addUser(addUser);
			}
		}
		// 统一完成更新组织
		for (Map<String, String> uptUser : users) {
			uptUser(uptUser);
		}

		// 完成后生成PATH
		return new Result<Object>(Result.SUCCESS, "员工信息更新完成，共计处理员工数据" + users.size());
	}

	/**
	 * 
	 * @Description:新增用户逻辑
	 * @param user
	 * @author: huanggya
	 * @date: 2022年5月23日上午10:28:01
	 * @version:版本
	 */
	public void addUser(Map<String, String> user) {
		try {
			User newUser = new User();
			// 新增用户信息
			// 初始化用户信息
			String wid = UUIDUtil.genUUID();
			String userId = UUIDUtil.genUUID();
			newUser.setWid(wid);
			newUser.setUserId(userId);
			newUser.setUserName(user.get("userName"));
			newUser.setActualName(user.get("actualName"));
			newUser.setPassword(PasswordUtil.desEncode("asd123##"));
			newUser.setSex("女".equals(user.get("sex")) ? "1" : "0");
			newUser.setEmail(user.get("email"));
			newUser.setDisabled(0);
			newUser.setIsDomain("0");
			newUser.setJobNumber(user.get("jobNumber"));
			// 搞定职位
			String positionCode = user.get("departName");
			String departNo = positionMapper.selParentPositionNoByPositionCode(positionCode);
			newUser.setDepartName(getDeptNoPath(departNo));
			// 需要进行更新的 人员工号
			String vp = user.get("vp");
			String oneDepartment = user.get("oneDepartment");
			String twoDepartment = user.get("twoDepartment");
			String vpUserId = userMapper.selUserIdByJobNumber(vp);
			String oneDepartmentUserId = userMapper.selUserIdByJobNumber(oneDepartment);
			String twoDepartmentUserId = userMapper.selUserIdByJobNumber(twoDepartment);
			newUser.setVp(vpUserId);
			newUser.setOneDepartment(oneDepartmentUserId);
			newUser.setTwoDepartment(twoDepartmentUserId);
			newUser.setGradeLevel(user.get("gradeLevel"));
			// 插入用户信息
			userMapper.insUser(newUser);
			userMapper.insDeptUser(userId, departNo);
			userMapper.insMsgSubs(userId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}
	}

	/**
	 * 
	 * @Description:用户信息更新
	 * @param user
	 * @author: huanggya
	 * @date: 2022年5月24日下午4:01:40
	 * @version:版本
	 */
	public void uptUser(Map<String, String> user) {
		try {
			// 根据工号更新
			User uptUser = new User();
			
			uptUser.setUserName(user.get("userName"));
			uptUser.setActualName(user.get("actualName"));
			uptUser.setSex("女".equals(user.get("sex")) ? "1" : "0");
			uptUser.setEmail(user.get("email"));
			uptUser.setDisabled("离职".equals(user.get("disabled")) ? 1 : 0);
			uptUser.setJobNumber(user.get("jobNumber"));
			
			String positionCode = user.get("departName");
			String departNo = positionMapper.selParentPositionNoByPositionCode(positionCode);
			uptUser.setDepartName(getDeptNoPath(departNo));
			// 需要进行更新的 人员工号
			String vp = user.get("vp");
			String oneDepartment = user.get("oneDepartment");
			String twoDepartment = user.get("twoDepartment");
			String vpUserId = userMapper.selUserIdByJobNumber(vp);
			String oneDepartmentUserId = userMapper.selUserIdByJobNumber(oneDepartment);
			String twoDepartmentUserId = userMapper.selUserIdByJobNumber(twoDepartment);
			String userId = userMapper.selUserIdByJobNumber(uptUser.getJobNumber());
			uptUser.setUserId(userId);
			uptUser.setVp(vpUserId);
			uptUser.setOneDepartment(oneDepartmentUserId);
			uptUser.setTwoDepartment(twoDepartmentUserId);
			uptUser.setGradeLevel(user.get("gradeLevel"));
			System.err.println(uptUser);
			// 更新用户信息
			userMapper.uptUser(uptUser);
			userMapper.delDeptUser(userId);
			userMapper.insDeptUser(userId, departNo);
		} catch (Exception e) {
			log.error(e.toString());
		}

	}

	/**
	 * 
	 * @Description:递归获取组织PathName
	 * @param deptNo
	 * @return
	 * @author: huanggya
	 * @date: 2022年7月9日上午11:41:33
	 * @version:版本
	 */
	public String getDeptNoPath(String deptNo) {
		String pathName = "";
		try {
			Depart dapart = departMapper.selDepartByDeptNo(deptNo);
			String dapartName = dapart.getDeptName();
			if (null != dapart) {
				String parentDeptNo = dapart.getParentDeptNo();
				if (!StringUtils.isEmpty(parentDeptNo) && !"0".equals(parentDeptNo)) {

					pathName = getDeptNoPath(parentDeptNo) + "/" + dapartName;
				} else {
					pathName = dapartName;
				}
			}

		} catch (Exception e) {
			log.error(e.toString());
		}
		return pathName;
	}

	// 校验是否可以来取
	private boolean checkUserExists(String jobNumber) throws Exception {
		// 根据deptCode查询是否存在 组织
		int userCot = userMapper.selUserNumByJobNumber(jobNumber);
		if (userCot > 0) {
			return true;
		}
		return false;
	}

}
