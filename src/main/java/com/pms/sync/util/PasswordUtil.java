package com.pms.sync.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {

	private static Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

	private static String CIPHER_DEFAULT_ALGORITHM = "DES";
	private static String CIPHER_DEFAULT_KEY = "850B1032922CDCDC";

	/**
	 * 
	 * @Description:加密逻辑
	 * @param keyOfbyte
	 * @param passwordOfbyte
	 * @return
	 * @throws Exception
	 * @author: huanggya
	 * @date: 2022年5月13日下午5:33:55
	 * @version:1.0
	 */
	private static byte[] getCipherMessage(byte[] keyOfbyte, byte[] passwordOfbyte) throws Exception {
		byte[] cipherMessage = null;
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyOfbyte, CIPHER_DEFAULT_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_DEFAULT_ALGORITHM);
		cipher.init(1, secretKeySpec);
		cipherMessage = cipher.doFinal(passwordOfbyte);
		return cipherMessage;
	}

	public static byte[] encode(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfbyte1, CIPHER_DEFAULT_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_DEFAULT_ALGORITHM);
		cipher.init(1, secretKeySpec);
		return cipher.doFinal(paramArrayOfbyte2);
	}

	public static String encode(byte[] paramArrayOfbyte, String paramString) throws Exception {
		return new String(encode(paramArrayOfbyte, paramString.getBytes()));
	}

	/**
	 * 
	 * @Description:解密逻辑
	 * @param paramArrayOfbyte1
	 * @param paramArrayOfbyte2
	 * @return
	 * @throws Exception
	 * @author: huanggya
	 * @date: 2022年5月13日下午5:34:17
	 * @version:1.0
	 */
	private static byte[] decode(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfbyte1, CIPHER_DEFAULT_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_DEFAULT_ALGORITHM);
		cipher.init(2, secretKeySpec);
		return cipher.doFinal(paramArrayOfbyte2);
	}

	/**
	 * 
	 * @Description:字符串转字节码
	 * @param arg
	 * @return
	 * @author: huanggya
	 * @date: 2022年5月13日下午5:34:36
	 * @version:1.0
	 */
	private static byte[] str2DesByte(String arg) {
		byte[] arrayOfByte = new byte[arg.length() / 2];
		for (byte b = 0; b < arrayOfByte.length; b++)
			arrayOfByte[b] = (byte) Integer.parseInt(arg.substring(2 * b, 2 * b + 2), 16);
		return arrayOfByte;
	}

	/**
	 * 
	 * @Description:字节码转字符串
	 * @param strOfbyte
	 * @return
	 * @author: huanggya
	 * @date: 2022年5月13日下午5:35:06
	 * @version:1.0
	 */
	private static String desByte2Str(byte[] strOfbyte) {
		StringBuffer stringBuffer = new StringBuffer();
		for (byte b = 0; b < strOfbyte.length; b++)
			stringBuffer.append(Integer.toHexString(256 + (strOfbyte[b] & 0xFF)).substring(1).toUpperCase());
		return stringBuffer.toString();
	}

	/**
	 * 
	 * @Description:加密
	 * @param password
	 * @return
	 * @author: huanggya
	 * @date: 2022年5月13日下午5:33:40
	 * @version:1.0
	 */
	public static String desEncode(String password) {
		byte[] arrayOfByte1 = str2DesByte(CIPHER_DEFAULT_KEY);
		byte[] arrayOfByte2 = null;
		try {
			arrayOfByte2 = encode(arrayOfByte1, password.getBytes());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return encodeByByte(arrayOfByte2);
	}

	private static String encodeByByte(byte[] paramArrayOfbyte) {
	    StringBuffer stringBuffer = new StringBuffer();
	    for (byte b = 0; b < paramArrayOfbyte.length; b++)
	      stringBuffer.append(Integer.toHexString(256 + (paramArrayOfbyte[b] & 0xFF)).substring(1).toUpperCase()); 
	    return stringBuffer.toString();
	  }
	/**
	 * 
	 * @Description:解密
	 * @param password
	 * @return
	 * @author: huanggya
	 * @date: 2022年5月13日下午5:33:18
	 * @version:1.0
	 */
	public static String desDecode(String password) {
		byte[] keyOfbyte = str2DesByte(CIPHER_DEFAULT_KEY);
		byte[] passwordOfbyte = str2DesByte(password);
		byte[] rtnEnCodeOfbyte = null;
		try {
			rtnEnCodeOfbyte = decode(keyOfbyte, passwordOfbyte);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new String(rtnEnCodeOfbyte);
	}

	public static void main(String[] arg) {
		String pass = "asd123##";
		String desDecode = PasswordUtil.desEncode(pass);
		System.err.println(desDecode);
	}
}
