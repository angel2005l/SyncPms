package com.pms.sync.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pms.sync.entity.Position;
import com.pms.sync.entity.Result;
import com.pms.sync.mapper.PositionMapper;
import com.pms.sync.mapper.UserMapper;
import com.pms.sync.util.UUIDUtil;

@Service
public class PositionService {

	private static Logger log = LoggerFactory.getLogger(PositionService.class);

	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private UserMapper userMapper;

	/**
	 * 
	 * @Description:职位同步
	 * @param positions
	 * @return
	 * @throws Exception
	 * @author: huanggya
	 * @date: 2022年7月9日上午9:24:11
	 * @version:版本
	 */
	public Result<Object> syncPosition(List<Map<String, String>> positions) throws Exception {
		// 校验处理数据集
		if (positions == null || positions.isEmpty()) {
			return new Result<>(Result.ERROR, "职位架构处理数据为空！");
		}
		// 防止父类组织新增导致更新失效
		for (Map<String, String> addPosition : positions) {
			if (!checkPositionExists(addPosition.get("positionCode"))) {
				addPositionInfo(addPosition);
			}
		}
		// 统一完成更新组织
		for (Map<String, String> uptPosition : positions) {
			uptPositionInfo(uptPosition);
		}
		// 完成后生成PATH
		uptPositionNoPath();
		return new Result<Object>(Result.SUCCESS, "职位更新完成，共计处理职位" + positions.size());
	}

	public void addPosition(Map<String, String> position) {
		try {
			Position newPosition = new Position();
			String wid = UUIDUtil.genUUID();
			String positionNo = UUIDUtil.genUUID();
			newPosition.setWid(wid);
			newPosition.setPositionNo(positionNo);
			newPosition.setPositionCode(position.get("positionCode"));
			newPosition.setPositionName(position.get("positionName"));

		} catch (Exception e) {
			log.error(e.toString());
		}

	}

	/**
	 * 
	 * @Description:当职位信息更新后，批量修正PATH路径
	 * @author: huanggya
	 * @date: 2022年7月9日上午9:08:42
	 * @version:版本
	 */
	public void uptPositionNoPath() {
		try {
			List<Position> positions = positionMapper.selPositionWithUpt();

			if (null == positions || positions.isEmpty()) {
				return;
			}
			for (Position position : positions) {

				String positionNo = getPositionNoPath(position.getPositionNo());
				positionMapper.uptPositionForPositionNoPath(position.getWid(), positionNo);
			}
		} catch (Exception e) {
			log.error(e.toString());
		}

	}

	/**
	 * 
	 * @Description:新增职位
	 * @param position
	 * @author: huanggya
	 * @date: 2022年7月8日下午1:48:34
	 * @version:版本
	 */
	public void addPositionInfo(Map<String, String> position) {
		try {
			Position newPosition = new Position();
			// 获得WID，positionNo
			String wid = UUIDUtil.genUUID();
			String positionNo = UUIDUtil.genUUID();
			newPosition.setWid(wid);
			newPosition.setPositionNo(positionNo);
			// 設置name
			newPosition.setPositionCode(position.get("positionCode"));
			newPosition.setPositionName(position.get("positionName"));

			// 执行保存
			positionMapper.insPosition(newPosition);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * 
	 * @Description:更新部门
	 * @param position
	 * @author: huanggya
	 * @date: 2022年7月9日上午9:26:52
	 * @version:版本
	 */
	public void uptPositionInfo(Map<String, String> position) {
		try {
			// 根据父类Code 获得父类positionno excel存值只有code
			String parentPositionCode = position.get("parentPositionNo");
			String parentPositionNo = positionMapper.selPositionNoByPositionCode(parentPositionCode);
			if (!StringUtils.isEmpty(parentPositionNo)) {
				position.put("parentPositionNo", parentPositionNo);
			} else {
				position.put("parentPositionNo", "");
			}
			// 根据VP工号，获得VP的userId
			String userVp = position.get("userVp");
			String userVpId = userMapper.selUserIdByJobNumber(userVp);
			if (!StringUtils.isEmpty(userVpId)) {
				position.put("userVp", userVpId);
			} else {
				position.put("userVp", userVp);
			}
			// 执行更新
			positionMapper.uptPosition(position);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * 
	 * @Description:递归获取职位Path
	 * @param positionNo
	 * @return
	 * @author: huanggya
	 * @date: 2022年7月8日下午1:52:58
	 * @version:版本
	 */
	public String getPositionNoPath(String positionNo) {
		String path = "";
		try {
			Position position = positionMapper.selDepartByDeptNo(positionNo);
			if (null != position) {
				String parentPositionNo = position.getParentPositionNo();
				if (!StringUtils.isEmpty(parentPositionNo) && !"0".equals(parentPositionNo)) {

					path = getPositionNoPath(parentPositionNo) + "," + positionNo;
				} else {
					path = positionNo;
				}
			}

		} catch (Exception e) {
			log.error(e.toString());
		}
		return path;
	}

	/**
	 * 
	 * @Description:校验职位是否存在
	 * @param positionCode
	 * @return
	 * @throws Exception
	 * @author: huanggya
	 * @date: 2022年7月9日上午9:02:00
	 * @version:版本
	 */
	private boolean checkPositionExists(String positionCode) throws Exception {
		// 根据positionCode查询是否存在 组织
		int positionCot = positionMapper.selPositionNumByPositionCode(positionCode);
		if (positionCot > 0) {
			return true;
		}
		return false;
	}

}
