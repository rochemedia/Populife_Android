package com.populstay.populife.util.string;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * 判断字符串是否为空
	 *
	 * @param str 字符串内容
	 * @return true 空，false非空
	 */
	public static boolean isBlank(String str) {
		boolean b = false;
		if (null == str || "".equals(str) || "null".equals(str) || "NULL".equals(str) || "".equals(str.trim())) {
			b = true;
		} else {
			b = false;
		}
		return b;
	}

	/**
	 * 判断密码格式是否正确
	 * 格式要求：8-16位，必须包含大小写字母、数字（特殊符号不限制）
	 *
	 * @param pwd 密码
	 * @return true 正确，false 错误
	 */
	public static boolean isPwdValid(String pwd) {
//		String str = "/^.*(?=.{6,})(?=.*\\d)(?=.*[A-Za-z]).*$/";
		String str = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*.]{8,16}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(pwd);
		return m.matches();
	}

	/**
	 * 验证是否是邮箱
	 *
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		// 邮箱地址为空
		if (TextUtils.isEmpty(email)) {
			return false;
		}
		if (!email.contains("@")) {
			return false;
		}
		// 邮箱地址长度超过50个字符
		if (email.length() > 50) {
			return false;
		}
		// 邮箱地址最后一位是"."
		if (email.endsWith(".")) {
			return false;
		}
		// 邮箱地址中包含有一个以上的连续的"."，此校验主要是为了防止用户多输入一个"."
		if (email.contains("..")) {
			return false;
		}

		// 前面是优化逻辑，做初步判断，毕竟复杂的正则比较耗时
		String strPattern = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 获取关键帧图片
	 *
	 * @param video_url
	 * @param count
	 * @return
	 */
	public static String getCIFImageUrl(String video_url, int count) {
		if (video_url == null)
			return null;
		// getbasepath
		int index = video_url.lastIndexOf("/");
		String basepath = video_url.substring(0, index + 1);
		// getbasefilename
		String tmp = video_url.substring(index + 1);
		tmp = tmp.substring(0, tmp.lastIndexOf("."));

		if (count > 3)
			count = 0;
		return basepath + tmp + "_cif_" + count + ".jpg";
	}

	/**
	 * 替换字符
	 *
	 * @param text
	 * @param value 要替换成的字符
	 * @return
	 */
	public static String replace(String text, String value) {
		String[] lines = text.split("&&P&&");
		Pattern pattern = Pattern.compile("\\*\\d+\\*");

		Map<String, String> maps = new HashMap<String, String>();
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				if (!maps.containsKey(matcher.group())) {
					String key = matcher.group();
					int len = Integer.parseInt(key.replace("*", ""));
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < len; i++) {
						sb.append(value);
					}
					maps.put(key, sb.toString());
				}
			}
		}
		for (String key : maps.keySet()) {
			text = text.replace(key, maps.get(key));
		}
		return text;
	}

	/**
	 * 处理空字符串
	 *
	 * @param str
	 * @return String
	 */
	public static String doEmpty(String str) {
		return doEmpty(str, "");
	}

	/**
	 * 处理空字符串
	 *
	 * @param str
	 * @param defaultValue
	 * @return String
	 */
	public static String doEmpty(String str, String defaultValue) {
		if (str == null || str.equalsIgnoreCase("null") || str.trim().equals("") || str.trim().equals("－请选择－")) {
			str = defaultValue;
		} else if (str.startsWith("null")) {
			str = str.substring(4, str.length());
		}
		return str.trim();
	}

	public static boolean isPhoneNumberValid(String number) {
		boolean isValid = false;
		if (number == null || number.length() <= 0) {
			return false;
		}
		Pattern PHONE = Pattern.compile( // sdd = space, dot, or dash
				"(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
						+ "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
						+ "([0-9][0-9\\-  0-9\\+ \\.][0-9\\- 0-9\\+ \\.]+[0-9])");
		Matcher matcher = PHONE.matcher(number);
		isValid = matcher.matches();
		return isValid;
	}

	/**
	 * 判断一个字符串是否为手机号码
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {
		if (TextUtils.isEmpty(mobiles)){
			return false;
		}
		int len = mobiles.length();
		if (len > 11 || len <11){
			return false;
		}

		Pattern p = Pattern.compile("^((16[0-9])|(19[0-9])|(13[0-9])|(14[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 判断一个字符串的内容是否为数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		try {
			new BigDecimal(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressLint("DefaultLocale")
	public static String toLowerCase(String str) {
		return str.toLowerCase();
	}

	/**
	 * 求一个字符串的md5值
	 *
	 * @param target 字符串
	 * @return md5 value
	 */
	public static String md5(String target) {
		return MD5.getMD5Str(target);
	}

	/**
	 * 获取 url 中指定 name 的 value
	 *
	 * @param url  链接
	 * @param name 参数名
	 * @return 参数值
	 */
	public static String getValueByName(String url, String name) {
		String result = "";
		int index = url.indexOf("?");
		String temp = url.substring(index + 1);
		String[] keyValue = temp.split("&");
		for (String str : keyValue) {
			if (str.contains(name)) {
				result = str.replace(name + "=", "");
				break;
			}
		}
		return result;
	}

	/**
	 * 随机生成字符串
	 *
	 * @param length 字符串长度（大小写字母、数字）
	 */
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 判断用户名格式是否正确
	 * 格式要求：只能含有大小写字母、数字、下划线
	 *
	 * @param userName 用户名
	 * @return true 正确，false 错误
	 */
	public static boolean isUserNameValid(String userName) {
		String str = "^[0-9a-zA-Z_]{3,16}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(userName);
		return m.matches();
	}
}
