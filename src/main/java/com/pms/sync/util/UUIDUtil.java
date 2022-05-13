package com.pms.sync.util;

import java.util.UUID;

public class UUIDUtil {
	public static String genUUID() {
		String tmp = UUID.randomUUID().toString().toUpperCase();
		return tmp.replace("-", "");
	}
}
