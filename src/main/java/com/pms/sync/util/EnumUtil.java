package com.pms.sync.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public final class EnumUtil {
	private static final String EXCP_START = "E1000";// 异常编码起始
	private static final String EXCP_END = "E2000";// 异常编码终止

	/**
	 * @param ref
	 * @return
	 * @description 将枚举中所有实例转为map列表
	 * @author zikc
	 * @date 2016年9月24日 下午2:05:23
	 * @update 2016年9月24日 下午2:05:23
	 * @version V1.0
	 */
	public static <T> List<Map<String, String>> enumToListMap(Class<T> ref) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		if (ref.isEnum()) {
			T[] ts = ref.getEnumConstants(); // 得到Enum的所有实例
			for (T t : ts) {
				Enum<?> tempEnum = (Enum<?>) t;
				String code = getInvokeValue(t, "getCode");
				if (StringUtils.isEmpty(code)) {
					code = String.valueOf(tempEnum.ordinal());
				}
				String text = getInvokeValue(t, "getText");
				if (StringUtils.isEmpty(text)) {
					text = tempEnum.name();
				}
				map.put("value", code);
				map.put("text", text);
				maps.add(map);
			}
		}
		return maps;
	}


	/**
	 * @param t
	 * @param methodName
	 * @return
	 * @description 用反射的方法调用类的方法
	 * @author zikc
	 * @date 2017年2月20日 上午12:35:24
	 * @update 2017年2月20日 上午12:35:24
	 * @version V1.0
	 */
	private static <T> String getInvokeValue(T t, String methodName) {
		try {
			return (String) t.getClass().getMethod(methodName).invoke(t);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param ref
	 * @param code
	 * @return
	 * @description 根据枚举的Code值得到Text值
	 * @author zikc
	 * @date 2017年2月20日 上午12:34:03
	 * @update 2017年2月20日 上午12:34:03
	 * @version V1.0
	 */
	public static <T> String getTextFromCode(Class<T> ref, String code) {
		if (ref.isEnum()) {
			T[] ts = ref.getEnumConstants(); // 得到Enum的所有实例
			for (T t : ts) {
				String c = getInvokeValue(t, "getCode");
				if (c.equals(code))
					return getInvokeValue(t, "getText");
			}
		}
		return "";
	}

	/**
	 * @param ref
	 * @param text
	 * @return
	 * @description 根据枚举的Text值得到Code值
	 * @author zikc
	 * @date 2017年4月5日 下午11:11:08
	 * @update 2017年4月5日 下午11:11:08
	 * @version V1.0
	 */
	public static <T> String getCodeFromText(Class<T> ref, String text) {
		if (ref.isEnum()) {
			T[] ts = ref.getEnumConstants(); // 得到Enum的所有实例
			for (T t : ts) {
				String c = getInvokeValue(t, "getText");
				if (c.equals(text))
					return getInvokeValue(t, "getCode");
			}
		}
		return "";
	}

	/**
	 * @param ref
	 * @param code
	 * @return
	 * @description 判断给定枚举值是否属于指定枚举类中的枚举
	 * @author tann
	 * @date 2017年3月18日 下午12:06:41
	 * @update 2017年3月18日 下午12:06:41
	 * @version V1.0
	 */
	public static <T> boolean isInEnum(Class<T> ref, String code) {
		String text = getTextFromCode(ref, code);
		if ("".equals(text))
			return false;
		return true;
	}

	/**
	 * @param ref
	 * @return
	 * @description 得到枚举所有的code，以String数组形式返回，顺序与枚举中一致
	 * @author zikc
	 * @date 2017年4月6日 上午12:13:00
	 * @update 2017年4月6日 上午12:13:00
	 * @version V1.0
	 */
	public static <T> String[] getEnumCodeArray(Class<T> ref) {
		if (ref.isEnum()) {
			T[] ts = ref.getEnumConstants(); // 得到Enum的所有实例
			List<String> objs = new ArrayList<String>();
			for (T t : ts) {
				String c = getInvokeValue(t, "getCode");
				objs.add(c);
			}
			return objs.toArray(new String[] {});
		}
		return null;
	}

	/**
	 * @param ref
	 * @return
	 * @description 得到枚举所有的text，以String数组形式返回，顺序与枚举中一致
	 * @author zikc
	 * @date 2017年4月6日 上午12:14:02
	 * @update 2017年4月6日 上午12:14:02
	 * @version V1.0
	 */
	public static <T> String[] getEnumTextArray(Class<T> ref) {
		if (ref.isEnum()) {
			T[] ts = ref.getEnumConstants(); // 得到Enum的所有实例
			List<String> objs = new ArrayList<String>();
			for (T t : ts) {
				String c = getInvokeValue(t, "getText");
				objs.add(c);
			}
			return objs.toArray(new String[] {});
		}
		return null;
	}

	
	/**
	 * @param servExcpMsg
	 * @return
	 * @description 将服务层抛出的异常信息处理，只取:后面的部分，无：则不处理，直接返回原始完整的异常信息
	 * @author zikc
	 * @date 2016年11月3日 下午3:27:24
	 * @update 2016年11月3日 下午3:27:24
	 * @version V1.0
	 */
	public static String getExcpMsgFromService(String servExcpMsg) {
		int index = servExcpMsg.indexOf(":");
		if (index > 0)
			servExcpMsg = servExcpMsg.substring(index + 1).trim();
		return servExcpMsg;
	}

	/**
	 * @param excpCode
	 * @return
	 * @description 判断是否是枚举中的异常
	 * @author zikc
	 * @date 2016年11月3日 下午3:17:33
	 * @update 2016年11月3日 下午3:17:33
	 * @version V1.0
	 */
	public static boolean isExcpEnum(String excpCode) {
		return ((excpCode.compareTo(EXCP_START) > 0) && (excpCode.compareTo(EXCP_END) < 0));
	}

}
