package com.star.mobile.video.util;

import java.security.MessageDigest;


public class EncryptUtils {
	
	public static String md5Encryption(String t,String p) {
		StringBuffer sb = new StringBuffer();
		sb.append(t).append(":").append(p);
		MessageDigest md5 = null;
		try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
           throw new IllegalStateException("MD5 instance error.");  
        }  
        char[] charArray = sb.toString().toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString(); 
	}
	
	/**
	 * 转换
	 * @param inStr
	 * @return
	 */
	private static String convert(String inStr){  
		  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  
    }
	
	private static String getPwd(String mdd) {
		int startIndex = mdd.indexOf(":");
		String pwd = mdd.substring(startIndex+1, mdd.length());
		return pwd;
	}
	
	/**
	 * 解密后的密码
	 * @param encryptionPwd
	 * @return
	 */
	public static String decrypt(String encryptionPwd) {
		return getPwd(convert(convert(encryptionPwd)));
	}
}
