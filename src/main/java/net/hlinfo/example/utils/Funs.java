package net.hlinfo.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.hutool.json.JSONObject;
import net.hlinfo.example.entity.BaseEntity;

/**
 * 一些静态的工具方法
 */
public class Funs {
	/**
	 * 密码加密，使用spring security
	 * @param pwd
	 * @return
	 */
	public static String passwdEncoder(String pwd) {
		BCryptPasswordEncoder en = new BCryptPasswordEncoder();
		return en.encode(pwd);
	}
	/**
	 * 验证密码，使用spring security
	 * @param pwd
	 * @param hash
	 * @return
	 */
	public static boolean passwdMatches(String pwd,String hash) {
		BCryptPasswordEncoder en = new BCryptPasswordEncoder();
		return en.matches(pwd, hash);
	}
	
    /**
     * 时间戳转换成字符串
     */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
    public final static String getYear(){
		DateFormat df = new SimpleDateFormat("yyyy");
		String rzDate = df.format(new Date());
		return rzDate;
	}
	
	
	/**
	 * 时间添+秒s
	 * @param d
	 * @param s
	 * @return
	 */
	public  static Date dateAddSecond(Date d, int s) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.SECOND, s);
		return c.getTime();
	}
	/**
	 * 时间添+秒s
	 * @param d
	 * @param s
	 * @return
	 */
	public  static Date dateAddSecond(Date d, long s) {
		return dateAddSecond(d, (int)s);
	}
	/**
	 * 6342 s 6342 / 60= 105 6342 % 60= 42
	 * 105 / 60= 1 105%60=45 
	 * 1:45:42
	 * 根据日期秒计算多少小时多少分多少秒
	 * @param time
	 * @return 
	 */
	public static String surplusTimerCalc(long allSeconds) {
		long min = allSeconds / 60; long seconds = allSeconds % 60;
		long hource = min / 60; min = min % 60;
		StringBuffer sb = new StringBuffer();
		if(hource > 0) {
			sb.append(hource + "小时");
		}
		if(min > 0) {
			sb.append(min + "分钟");
		}
		sb.append(seconds + "秒");
		return sb.toString();
	}
	
	public static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 我国常用三个坐标系，WGS84,、北京54及西安80，而WGS84椭球的长半轴就为6378137.0，
	 */
	public static final double EARTH_RADIUS = 6378.137;
	
	/**
	 * 验证手机号
	 *
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone) {
		 String CM_NUM = "^((13[4-9])|(14[7-8])|(15[0-2,7-9])|(165)|(178)|(18[2-4,7-8])|(19[5,8]))\\d{8}|(170[3,5,6])\\d{7}$";
	        String CU_NUM = "^((13[0-2])|(14[5,6])|(15[5-6])|(16[6-7])|(17[1,5,6])|(18[5,6]))\\d{8}|(170[4,7-9])\\d{7}$";
	        String CT_NUM = "^((133)|(149)|(153)|(162)|(17[3,7])|(18[0,1,9])|(19[1,3,9]))\\d{8}|((170[0-2])|(174[0-5]))\\d{7}$";
	        Pattern P_CM_NUM = Pattern.compile(CM_NUM);
	        Matcher M_CM_NUM = P_CM_NUM.matcher(phone);
	        Pattern P_CU_NUM = Pattern.compile(CU_NUM);
	        Matcher M_CU_NUM = P_CU_NUM.matcher(phone);
	        Pattern P_CT_NUM = Pattern.compile(CT_NUM);
	        Matcher M_CT_NUM = P_CT_NUM.matcher(phone);
	        if (M_CM_NUM.matches()) {
	            return true;
	        }
	        if (M_CU_NUM.matches()) {
	            return true;
	        }
	        return M_CT_NUM.matches();
	}

	/**
	 * 中国国家商用密码杂凑算法SM3
	 * 
	 * @author 呐喊
	 * @param str
	 * @return
	 */
	public static String sm3(String str) {
		str = (str == null) ? "" : str;
		byte[] pmdata = string2byte(str);
		SM3Digest sm3 = new SM3Digest();
		sm3.update(pmdata, 0, pmdata.length);
		byte[] hash = new byte[sm3.getDigestSize()];
		sm3.doFinal(hash, 0);
		String rsSM3 = ByteUtils.toHexString(hash);
		return rsSM3;
	}

	/**
	 * 中国国家商用密码杂凑算法SM3 HMAC消息认证
	 * 
	 * @author 呐喊
	 * @param str
	 * @return
	 */
	public static String sm3_hmac(String key, String str) {
		key = (key == null) ? "" : key;
		str = (str == null) ? "" : str;
		KeyParameter kp = new KeyParameter(string2byte(key));
		byte[] pmdata = string2byte(str);
		SM3Digest sm3 = new SM3Digest();
		HMac hmac = new HMac(sm3);
		hmac.init(kp);
		hmac.update(pmdata, 0, pmdata.length);
		byte[] hash = new byte[hmac.getMacSize()];
		hmac.doFinal(hash, 0);
		String rsSM3 = ByteUtils.toHexString(hash);
		return rsSM3;
	}

	public static String sha3(String str) {
		if (str == null) {
			str = "";
		}
		byte[] pmdata = string2byte(str);
		SHA3Digest sha3 = new SHA3Digest();
		sha3.update(pmdata, 0, pmdata.length);
		byte[] hash = new byte[sha3.getDigestSize()];
		sha3.doFinal(hash, 0);
		String resultSha3 = ByteUtils.toHexString(hash);
		return resultSha3;
	}

	public static byte[] string2byte(String s) {
		byte[] pmdata = null;
		try {
			pmdata = s.getBytes("utf-8");
			return pmdata;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pmdata;
	}

	/**
	 * 密码加密
	 * 
	 * @param sourcePwd 原密码
	 * @param salt      加密盐
	 * @return
	 */
	public static String pwdEncry(String sourcePwd, String salt) {
		return sm3_hmac("CN%JdC#/@100001000$", sourcePwd + salt);
	}

	public static String getRandom(int length) {
		Random random = new Random();
		StringBuilder rs = new StringBuilder();
		for (int i = 0; i < length; i++) {
			rs.append(random.nextInt(10));
		}
		return rs.toString();
	}

	/**
	 * 生成15位数ID
	 * 
	 * @return
	 */
	public static String getId() {
		long millis = System.currentTimeMillis();
		Random random = new Random();
		int end2 = random.nextInt(99);
		String string = millis + String.format("%02d", end2);
		return string;
	}
	public static String createAccount() {
		String account = Times.format("yy", new Date()) + "0" + LongUUID();
		return account.substring(0, 13);
	}
	/**
	 * 过滤掉手机端输入的表情字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {
		if (source == null) {
			return source;
		}
		Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher emojiMatcher = emoji.matcher(source);
		if (emojiMatcher.find()) {
			source = emojiMatcher.replaceAll("*");
			return source;
		}
		return source;
	}


	public static String UUID() {
		String s = java.util.UUID.randomUUID().toString();
		return s.replaceAll("-", "");
	}
	public static String UUID36() {
		String s = java.util.UUID.randomUUID().toString();
		return s;
	}
	
	/**
	 * 数字类型的UUID
	 * 
	 * @return
	 */
	public static long LongUUID() {
		StringBuffer shortBuffer = new StringBuffer();
		Random random = new Random();
		shortBuffer.append((random.nextInt(9) + 1));
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			// shortBuffer.append(chars[x % 0x3E]);
			x = x % 0x3E;
			String xStr = "";
			if (x < 10) {
				xStr =  "0" + x;
			}
			xStr = x+"";
			shortBuffer.append(xStr);
		}
		return Long.parseLong(shortBuffer.toString());
	}
	public static long LongUUID(String uuid) {
		StringBuffer shortBuffer = new StringBuffer();
		Random random = new Random();
		shortBuffer.append((random.nextInt(9) + 1));
		uuid = uuid.replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			// shortBuffer.append(chars[x % 0x3E]);
			x = x % 0x3E;
			String xStr = "";
			if (x < 10) {
				xStr =  "0" + x;
			}
			xStr = x+"";
			shortBuffer.append(xStr);
		}
		return Long.parseLong(shortBuffer.toString());
	}
	
	public static long longUuidByUuid(String uuid) {
		return Funs.LongUUID(uuid);
	}
	
	/*
	 * 中文转unicode编码
	 */
	public static String gbEncoding(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int i = 0; i < utfBytes.length; i++) {
			String hexB = Integer.toHexString(utfBytes[i]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		return unicodeBytes;
	}

	/*
	 * unicode编码转中文
	 */
	public static String decodeUnicode(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			end = dataStr.indexOf("\\u", start + 2);
			String charStr = "";
			if (end == -1) {
				charStr = dataStr.substring(start + 2, dataStr.length());
			} else {
				charStr = dataStr.substring(start + 2, end);
			}
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			start = end;
		}
		return buffer.toString();
	}

	public final static String getNowFullTimeNum() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String rzDate = df.format(new Date());
		return rzDate;
	}

	/**
	 * 获取多少分钟之后的时间
	 * 
	 * @param min 分钟
	 * @return
	 */
	public final static String getNowFullTimeByXAfter(int min) {
		if (min == 0) {
			min = 60;
		}
		long time = System.currentTimeMillis();
		time += min * 1000 * 60;
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String rzDate = df.format(time);
		return rzDate;
	}

	public final static String getNowFullTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String rzDate = df.format(new Date());
		return rzDate;
	}
	public final static String getNowDate() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String rzDate = df.format(new Date());
		return rzDate;
	}
	// 返回时间格式如：2020-02-17 00:00:00
	public static Date getStartOfDay(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
	// 返回时间格式如：2020-02-19 23:59:59
    public static Date getEndOfDay(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
	public static long dec2Value(BigDecimal bd1) {
		BigDecimal bd2 = bd1.multiply(new BigDecimal(100));
		long total_fee = bd2.longValue();
		return total_fee;
	}

	/**
	 * 去除文本中的html
	 * 
	 * @param htmlStr
	 * @return
	 */
	public static String delHTMLTag(String htmlStr) {
		if(Funs.isBlank(htmlStr)) {
			return "";
		}
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签
		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签
		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签
		return htmlStr.replaceAll("\\s*|\t|\r|\n", "").trim(); // 返回文本字符串
	}

	/**
	 * 在html中获取图片地址
	 * 
	 * @param html
	 * @return
	 */
	public static List<String> getImgByHtml(String html) {
		List<String> srcList = new ArrayList<String>(); // 用来存储获取到的图片地址
		Pattern p = Pattern.compile("<(img|IMG)(.*?)(>|></img>|/>)");// 匹配字符串中的img标签
		Matcher matcher = p.matcher(html);
		boolean hasPic = matcher.find();
		if (hasPic == true)// 判断是否含有图片
		{
			while (hasPic) // 如果含有图片，那么持续进行查找，直到匹配不到
			{
				String group = matcher.group(2);// 获取第二个分组的内容，也就是 (.*?)匹配到的
				Pattern srcText = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");// 匹配图片的地址
				Matcher matcher2 = srcText.matcher(group);
				if (matcher2.find()) {
					srcList.add(matcher2.group(3));// 把获取到的图片地址添加到列表中
				}
				hasPic = matcher.find();// 判断是否还有img标签
			}

		}
		return srcList;
	}

	/**
	 * 将size B转换为KB, MB等等
	 * 
	 * @param size
	 * @return
	 */
	public static String getPrintSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + " B";
		} else {
			size = size / 1024;
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + " KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			// 因为如果以MB为单位的话，要保留最后1位小数，
			// 因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + " MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + " GB";
		}
	}

	public static String getText(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find())
			return matcher.group(1);
		return null;
	}

	public static int string2int(String s) {
		if (s == null)
			return 0;
		s = getText(s, "(\\d+)");
		if ("".equals(s) || s == null)
			return 0;
		return Integer.parseInt(s);
	}
	public static int string2int(Object s) {
		if (s == null) {return 0;}
		String str = s.toString();
		str = getText(str, "(\\d+)");
		if ("".equals(str) || str == null){return 0;}
		return Integer.parseInt(str);
	}

	public static Long string2Long(String s) {
		if (s == null)
			return 0l;
		s = getText(s, "(\\d+)");
		if ("".equals(s) || s == null)
			return 0l;
		return Long.parseLong(s);
	}
	
	public static float string2Float(String s) {
		if (s == null)
			return 0l;
		s = getText(s, "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
		if ("".equals(s) || s == null)
			return 0l;
		return Float.parseFloat(s);
	}

	public static long obj2int(Object obj) {
		if (obj == null)
			return 0;
		try {
			return Long.parseLong(obj.toString());
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			System.out.println("error=");
			return 0;
		}
	}

	public final static String getNowDate4yyyyMM() {
		DateFormat df = new SimpleDateFormat("yyyyMM");
		String rzDate = df.format(new Date());
		return rzDate;
	}

	/**
	 * 获取当前网络ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("X-Real-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
	
	/**
	 * 
	 * @param real_ip
	 * @return
	 */
	public static Map<String, Object> getIpInfo(String real_ip){
		Map<String, Object> map = new HashMap<String, Object>();
		HttpURLConnection connection ;
        String api = "http://ip.taobao.com/service/getIpInfo.php";
        String connect_symbol = "?";
        String front_name = "ip=";
        URL url ;
        BufferedReader bf ;
        StringBuffer sb = new StringBuffer();
        try {
            url = new URL(api+connect_symbol+front_name+real_ip);
            connection = (HttpURLConnection)url.openConnection();
            bf = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String line = "";
            while(null!=(line = bf.readLine())){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject dataObject = new JSONObject(sb.toString());
        JSONObject jsonObject= dataObject.getJSONObject("data");
        map.put("region", jsonObject.get("region"));//省
        map.put("city", jsonObject.get("city")); //省会
        map.put("area", jsonObject.get("area")); //地级市
        return map;
//        System.out.println(jsonObject.toString());
//        System.out.println("ip:"+jsonObject.get("ip"));
//        System.out.println("country:"+jsonObject.get("country"));
//        System.out.println("area:"+jsonObject.get("area"));
//        System.out.println("region:"+jsonObject.get("region"));
//        System.out.println("county:"+jsonObject.get("county"));
//        System.out.println("isp:"+jsonObject.get("isp"));
//        System.out.println("city:"+jsonObject.get("city"));
//        System.out.println("country_id:"+jsonObject.get("country_id"));
//        System.out.println("area_id:"+jsonObject.get("area_id"));
//        System.out.println("region_id:"+jsonObject.get("region_id"));
//        System.out.println("city_id:"+jsonObject.get("city_id"));
//        System.out.println("county_id:"+jsonObject.get("county_id"));
//        System.out.println("isp_id:"+jsonObject.get("isp_id"));
	}
	/**
	 * 生成文件名，用时间年月日时分秒
	 * @return
	 */
	public final static String getFileNameByFullTime() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String rzDate = df.format(new Date());
		return rzDate + "_" + new Random().nextInt(10000);
	}
	/**
	 * 生成文件名,sm3加密（时间戳和UUID）生成
	 * @return
	 */
	public final static String getFileNameBySm3() {
		String fileName = Funs.sm3(new Date().getTime() + new Random().nextInt(100000) + Funs.UUID());
		return fileName;
	}
	public final static String getNowDate4yyyyMMDD() {
		DateFormat df = new SimpleDateFormat("yyyyMMDD");
		String rzDate = df.format(new Date());
		return rzDate;
	}
	/**
	 * 格式化日期
	 * @param dateStr 日期字符串
	 * @param dtf DateTimeFormatter
	 * @return
	 */
	public static String parseDate(String dateStr,DateTimeFormatter dtf){
		try {
			if(dateStr==null || "".equals(dateStr)) {return "";}
			LocalDate date = LocalDate.parse(dateStr,dtf);
			return date.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 判断BigDecimal类型是否为空或者0
	 * @param bd
	 * @return 为null或者0返回true,否则返回false
	 */
	public static boolean BigDecimalIsEmpty(BigDecimal bd) {
		if(bd==null) {return true;}
		if(bd.compareTo(BigDecimal.ZERO)==0) {return true;}
		return false;
	}
	/**
	 * 设置下载文件的Header信息
	 * @param request
	 * @param response
	 * @param fileName 文件名
	 * @return
	 */
	public static boolean setDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            String browser = request.getHeader("User-Agent");
            if (-1 < browser.indexOf("MSIE 6.0") || -1 < browser.indexOf("MSIE 7.0")) {
                // IE6, IE7 浏览器
                response.addHeader("content-disposition", "attachment;filename="
                        + new String(fileName.getBytes(), "ISO8859-1"));
            } else if (-1 < browser.indexOf("MSIE 8.0")) {
                // IE8
                response.addHeader("content-disposition", "attachment;filename="
                        + URLEncoder.encode(fileName, "UTF-8"));
            } else if (-1 < browser.indexOf("MSIE 9.0")) {
                // IE9
                response.addHeader("content-disposition", "attachment;filename="
                        + URLEncoder.encode(fileName, "UTF-8"));
            } else if (-1 < browser.indexOf("Chrome")) {
                // 谷歌
                response.addHeader("content-disposition",
                        "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
            } else if (-1 < browser.indexOf("Safari")) {
                // 苹果
                response.addHeader("content-disposition", "attachment;filename="
                        + new String(fileName.getBytes(), "ISO8859-1"));
            } else {
                // 火狐或者其他的浏览器
                response.addHeader("content-disposition",
                        "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
            }
            return true;
        } catch (Exception e) {
            //log.error(e.getMessage());
            return false;
        }
    }
	
	/**
	 * 求职状态
	 * @param state
	 * @return
	 */
	public static  String getJobState(int state) {
		String js="";
		switch (state) {
		case 1:
			js = "离职";
			break;
		case 2:
			js = "在职，可一个月到岗";
			break;
		case 3:
			js = "在职，考虑换工作";
			break;
		case 4:
			js = "在职，应届毕业生";
			break;
		default:
			break;
		}
		return js;
	}
	
	public static String getSalary(long min,long max) {
		if(min==-1 && max==-1) {
			return "面议";
		}
		if(min==-1) {
			String maxsalary = max+"";
			if(max>1000) {maxsalary = max/1000 + "K";}
			return maxsalary+"以下";
		}
		if(max==-1) {
			String minsalary = min+"";
			if(min>1000) {minsalary = min/1000 + "K";}
			return minsalary+"以上";
		}
		String minsalary = min+"";
		String maxsalary = max+"";
		if(max>1000) {maxsalary = max/1000 + "K";}
		if(min>1000) {minsalary = min/1000 + "K";}
		return minsalary+"-"+maxsalary;
	}
	
	public static String getWorkExpSalary(BigDecimal min,BigDecimal max) {
		if(min==null) {
			min = new BigDecimal("0");
		}
		if(max==null) {
			max = new BigDecimal("0");
		}
		if(min.compareTo(new BigDecimal("-1")) == 0) {
			String maxsalary = max+"";
			if(max.compareTo(new BigDecimal("1000"))==1) {maxsalary = max.divide(new BigDecimal("1000"),0,BigDecimal.ROUND_HALF_UP) + "K";}
			return maxsalary+"以下";
		}
		if(max.compareTo(new BigDecimal("-1")) == 0) {
			String minsalary = min+"";
			if(min.compareTo(new BigDecimal("1000"))==1) {minsalary = min.divide(new BigDecimal("1000"),0,BigDecimal.ROUND_HALF_UP) + "K";}
			return minsalary+"以上";
		}
		String minsalary = min+"";
		String maxsalary = max+"";
		if(max.compareTo(new BigDecimal("1000"))==1) {maxsalary = max.divide(new BigDecimal("1000"),0,BigDecimal.ROUND_HALF_UP) + "K";}
		if(min.compareTo(new BigDecimal("1000"))==1) {minsalary = min.divide(new BigDecimal("1000"),0,BigDecimal.ROUND_HALF_UP) + "K";}
		return minsalary+"-"+maxsalary;
	}
	/**
	 * 获取学历
	 * @param edu
	 * @return
	 */
	public static String getEducation(int edu) {
		String edutxt = "无";
		if(edu==0) {return "无";}
		String[] eduarr = {"无","小学","中学","初中","高中","专科","中专","大专","本科","硕士","博士","博士后"};
		String[] edumode = {"","以下","以上"};
		int a = edu%10;//对10取余，可得到个位数
		int b = edu/10;//除10得到十位数，由于b为整形，小数位会自动省略
		edutxt = eduarr[b]+edumode[a];
		return edutxt;
	}
	
	/**
	 * 判断实体类中每个属性是否都为空
	 * @param o
	 * @return
	 */
	public static boolean allFieldIsNULL(Object o){
		if(o==null) {return true;}
		try {
			for (Field field : o.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(o);
				if (value instanceof CharSequence) {
					if (!org.springframework.util.ObjectUtils.isEmpty(value)) {
						return false;
					}
				} else {
					if (null != value) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("判断对象属性为空异常" + e);
		}
		return true;
	}
	/**
	 * 将实体类中为空字符串的属性设为null
	 * @param obj
	 * @return 处理后的对象
	 */
	public static <T> T setFieldIsEmptyToNull(T obj){
		if(obj==null) {return obj;}
		try {
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(obj);
				if (value instanceof CharSequence) {
					if (org.springframework.util.ObjectUtils.isEmpty(value)) {
						field.set(obj, null);
					}
				}
			}
			return obj;
		} catch (Exception e) {
			System.out.println("判断对象属性为空异常" + e);
			return obj;
		}
		
	}
	
	
	
	
	
	/**
	 * map转obj
	 * @param <T>
	 * @param cls
	 * @param map
	 * @return
	 */
	public static <T> T map2obj(Class<T> cls, Map map){
		T object = null;
		try {
			object = cls.newInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//检查是否空值
		if(map == null || map.isEmpty()){
			return null;
		}
		//获取Object属性数组
		Field[] fields = object.getClass().getDeclaredFields();
		//遍历数组
		for(Field field : fields){
			if(!field.isAccessible()){
				field.setAccessible(true);
			}
			//赋值
			try {
				String type = field.getType().getSimpleName();
				if("int".equals(type)) {
					//field.setInt(object, Funs.string2int(map.get(field.getName())+""));
				}else if("double".equals(type)){
					//field.setDouble(object, map.get(field.getName()));
				}else if("float".equals(type)){
					//field.setFloat(object, map.get(field.getName()));
				}else {
					field.set(object, map.get(field.getName()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("map->obj:" + e.getMessage());
			}
		}
		return object;
	}
	
	/**
	 * 将list转为map
	 * list=[{age:1, name:'n1', addr:"a1"}, {age:2,name:"n2", addr:"a3"}] key=name
	 * 返回的map={
	 * 	n1: {age:1, name:'n1', addr:"a1"},
	 *  n2: {age:2,name:"n2", addr:"a3"}
	 * }
	 * @param <T>
	 * @param list 需要转为Map的list
	 * @param key 作为map的key值，为list中item的其中一个字段的值
	 * 			，不能是数字字段，必须为字符串且不重复
	 * @return
	 */
	public static <T> Map list2map(List<T> list, String key) {
		if(list == null || list.size() <= 0 || Funs.isBlank(key)) {
			System.out.println("Funs.list2map -> 所传参数为空");
			return null;
		}
		T t = list.get(0);
		try {
			Field field = t.getClass().getDeclaredField(key);
			if(field == null) {
				System.out.println("Funs.list2map -> 所传key不是list项中的字段");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		Map<String, T> map = new HashMap<>();
		for(T item:list) {
			try {
				Field field = t.getClass().getDeclaredField(key);
				field.setAccessible(true);
				Method me = item.getClass().getMethod("get" + Funs.upperFirst(key));
				map.put((String)me.invoke(item), item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 字符串类型日期转换成Date类型
	 * @param date
	 * @return
	 */
	public static Date string2Date(String date) {
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date mydate = myFormatter.parse("1992-03-29");
			return mydate;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Date();
	}
	/**
	 * 根据出生年月日获取年龄
	 * @param dateOfBirth
	 * @return
	 */
	public static int getAge(Date birthDay) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (birthDay != null) {
            now.setTime(new Date());
            born.setTime(birthDay);
            if (born.after(now)) {
                throw new IllegalArgumentException("年龄不能超过当前日期");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
            int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
            //System.out.println("nowDayOfYear:" + nowDayOfYear + " bornDayOfYear:" + bornDayOfYear);
            if (nowDayOfYear < bornDayOfYear) {
                age -= 1;
            }
        }
        return age;
    }
	/**
	 * 根据出生年月日获取年龄
	 * @param dateOfBirth
	 * @return
	 */
	public static int getAge(String birthDay) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (Funs.isNotBlank(birthDay)) {
            now.setTime(new Date());
            born.setTime(Funs.string2Date(birthDay));
            if (born.after(now)) {
                throw new IllegalArgumentException("年龄不能超过当前日期");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
            int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
            //System.out.println("nowDayOfYear:" + nowDayOfYear + " bornDayOfYear:" + bornDayOfYear);
            if (nowDayOfYear < bornDayOfYear) {
                age -= 1;
            }
        }
        return age;
    }
	
	/**
     * 如果此字符串为 null 或者全为空白字符(包含：空格、tab 键、换行符)，则返回 true
     *
     * @param cs
     *            字符串
     * @return 如果此字符串为 null 或者全为空白字符，则返回 true
     */
	public static boolean isBlank(CharSequence cs) {
        if (null == cs)
            return true;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!(Character.isWhitespace(cs.charAt(i))))
                return false;
        }
        return true;
    }
	/**
     * 如果此字符串不为 null 或者全为空白字符(包含：空格、tab 键、换行符)，则返回 true
     *
     * @param cs
     *            字符串
     * @return 如果此字符串不为 null 或者全为空白字符，则返回 true
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }
    /**
     * 检查两个字符串是否相等.
     *
     * @param s1
     *            字符串A
     * @param s2
     *            字符串B
     * @return true 如果两个字符串相等,且两个字符串均不为null
     */
    public static boolean equals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }
    /**
     * 检查两个字符串是否不相等.
     *
     * @param s1
     *            字符串A
     * @param s2
     *            字符串B
     * @return 如果两个字符串不相等，返回true
     */
    public static boolean notequals(String s1, String s2) {
        return !equals(s1,s2);
    }
    /**
     * 检测URL地址是否能正常连接
     * @param url 需要测试的URL地址
     * @return 能正常连接返回true，否则返回false
     */
    public static boolean testURLConn(String url){
		int status = 404;
		try {
			URL conn = new URL(url);
			HttpURLConnection openConn = (HttpURLConnection) conn.openConnection();
			openConn.setUseCaches(false);
			openConn.setConnectTimeout(3000); //设置超时时间
		   status = openConn.getResponseCode();//获取请求状态
		   if (200 == status) {
		    return true;
		   }
		} catch (Exception e) {
		   //e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return false;
	}
    
    /**
     * 实体对象转Map
     * @param object
     * @return
     */
    public static Map<String, Object> entity2Map(Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(object==null) {return map;}
	    for (Field field : object.getClass().getDeclaredFields()){
	        try {
	        	boolean flag = field.isAccessible();
	            field.setAccessible(true);
	            Object o = field.get(object);
	            map.put(field.getName(), o);
	            field.setAccessible(flag);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return map;
	}
    
    private static boolean isemojicharacter(char codepoint) {
        return (codepoint == 0x0) || (codepoint == 0x9) || (codepoint == 0xa)
                || (codepoint == 0xd)
                || ((codepoint >= 0x20) && (codepoint <= 0xd7ff))
                || ((codepoint >= 0xe000) && (codepoint <= 0xfffd))
                || ((codepoint >= 0x10000) && (codepoint <= 0x10ffff));
    }
  
    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filteremoji(String source) {
        if (Funs.isBlank(source)) {
            return source;
        }
        StringBuilder buf = null;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codepoint = source.charAt(i);
            if (isemojicharacter(codepoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codepoint);
            }
        }
        if (buf == null) {
            return source;
        } else {
            if (buf.length() == len) {
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }
    
    
    /**
     * 通过身份证号获取生日
     * @param identifyNumber
     * @return
     */
    public static String getBirthByIdcard(String identifyNumber){
        String dateOfBirth = "";
        //通过身份证获取性别和生日
        if(identifyNumber!=null&&!"".equals(identifyNumber)){
            if(identifyNumber.length()==15){
                dateOfBirth  = "19"+identifyNumber.substring(6, 8)+"-"+identifyNumber.substring(8, 10)+"-"+identifyNumber.substring(10, 12);
            }else if (identifyNumber.length()==18){
                dateOfBirth = identifyNumber.substring(6, 10)+"-"+identifyNumber.substring(10, 12)+"-"+identifyNumber.substring(12, 14);
            }
        }
        return dateOfBirth;
    }
    /**
     * 通过身份证号获取生日和性别
     * @param identifyNumber
     * @return 1:男  2:女
     */
    public static int getSexByIdcard(String identifyNumber){
        int gender = 1;
        //通过身份证获取性别和生日
        if(identifyNumber!=null&&!"".equals(identifyNumber)){
            if(identifyNumber.length()==15){
                /*基数为男 偶数为女*/
                if(Integer.parseInt(identifyNumber.substring(14, 15)) % 2 == 0){
                    gender = 2;
                }else{
                    gender = 1;
                }
            }else if (identifyNumber.length()==18){
                /*基数为男 偶数为女*/
                if(Integer.parseInt(identifyNumber.substring(16, 17)) % 2 == 0){
                    gender = 2;
                }else{
                    gender = 1;
                }
            }
        }
        return gender;
    }
    /**
     * 将字符串首字母大写
     *
     * @param s
     *            字符串
     * @return 首字母大写后的新字符串
     */
    public static String upperFirst(CharSequence s) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == 0)
            return "";
        char c = s.charAt(0);
        if (Character.isUpperCase(c))
            return s.toString();
        return new StringBuilder(len).append(Character.toUpperCase(c))
                                     .append(s.subSequence(1, len))
                                     .toString();
    }
    
    public static String getNation(){
		String s = new String("{nation:\"汉族\"},"
				+"{nation:\"苗族\"},"
				+"{nation:\"布依族\"},"
				+"{nation:\"侗族\"},"
				+"{nation:\"土家族\"},"
				+"{nation:\"彝族\"},"
				+"{nation:\"仡佬族\"},"
				+"{nation:\"水族\"},"
				+"{nation:\"回族\"},"
				+"{nation:\"瑶族\"},"
				+"{nation:\"壮族\"},"
				+"{nation:\"畲族\"},"
				+"{nation:\"白族\"},"
				+"{nation:\"羌族\"},"
				+"{nation:\"蒙古族\"},"
				+"{nation:\"仫佬族\"},"
				+"{nation:\"毛南族\"},"
				+"{nation:\"满族\"},"
				+"{nation:\"藏族\"},"
				+"{nation:\"裕固族\"},"
				+"{nation:\"锡伯族\"},"
				+"{nation:\"乌孜别克族\"},"
				+"{nation:\"维吾尔族\"},"
				+"{nation:\"佤族\"},"
				+"{nation:\"土族\"},"
				+"{nation:\"塔塔尔族\"},"
				+"{nation:\"塔吉克族\"},"
				+"{nation:\"撒拉族\"},"
				+"{nation:\"普米族\"},"
				+"{nation:\"门巴族\"},"
				+"{nation:\"怒族\"},"
				+"{nation:\"纳西族\"},"
				+"{nation:\"珞巴族\"},"
				+"{nation:\"拉祜族\"},"
				+"{nation:\"僳僳族\"},"
				+"{nation:\"黎族\"},"
				+"{nation:\"柯尔克孜族\"},"
				+"{nation:\"景颇族\"},"
				+"{nation:\"京族\"},"
				+"{nation:\"基诺族\"},"
				+"{nation:\"赫哲族\"},"
				+"{nation:\"高山族\"},"
				+"{nation:\"高山族\"},"
				+"{nation:\"哈萨克族\"},"
				+"{nation:\"哈尼族\"},"
				+"{nation:\"鄂温克族\"},"
				+"{nation:\"俄罗斯族\"},"
				+"{nation:\"鄂伦春族\"},"
				+"{nation:\"独龙族\"},"
				+"{nation:\"德昂族\"},"
				+"{nation:\"傣族\"},"
				+"{nation:\"达斡尔族\"},"
				+"{nation:\"朝鲜族\"},"
				+"{nation:\"布朗族\"},"
				+"{nation:\"保安族\"},"
				+"{nation:\"东乡族\"},"
				+"{nation:\"阿昌族\"},"
				+"{nation:\"穿青人\"},"
				+"{nation:\"其他\"}");
		return "["+s+"]";
	}
	public static String getPolicy(){
		String s=new String("{policy:\"群众\"},"
				+"{policy:\"中共党员\"},"
				+"{policy:\"中共预备党员\"},"
				+"{policy:\"共青团员\"},"
				+"{policy:\"民革党员\"},"
				+"{policy:\"民盟盟员\"},"
				+"{policy:\"民建会员\"},"
				+"{policy:\"民进会员\"},"
				+"{policy:\"农工党党员\"},"
				+"{policy:\"致公党党员\"},"
				+"{policy:\"九三学社社员\"},"
				+"{policy:\"台盟盟员\"},"
				+"{policy:\"无党派民主人士\"}");
		return "["+s+"]";
	}
	/**
	 * 截取末尾几位
	 * @param s
	 * @param len
	 * @return
	 */
	public static String laststr(String s,int len) {
		if(s==null || "".equals(s)) {return "";}
		return s.trim().substring(s.trim().length()-len);
		
	}
	/**
	 * 过滤字符串中的非数字
	 * @param text
	 * @return
	 */
	public static String getIntStr(String text) {
		Pattern pattern = Pattern.compile("[^0-9]");
		Matcher matcher = pattern.matcher(text);
		System.out.println(matcher.replaceAll(""));
		return matcher.replaceAll("");
	}
	
	
	/**
	 * 将实体对象的属性转为mysql查询语句的select 和from中的字段
	 * prex.user_name as fieldPrexuserName
	 * @param cls 实体class
	 * @param prex 数据字段前缀
	 * @param fieldPrex 字段前缀
	 * @return
	 */
	public static String vo2mysqlField(Class<?> cls
			, String prex
			, String fieldPrex) {
		prex = Funs.isBlank(prex)?"":prex+".";
		fieldPrex = Funs.isBlank(fieldPrex)?"":fieldPrex;
		List<Field> fs = new ArrayList<>();
		Field[] cfs = cls.getDeclaredFields();
		fs.addAll(Arrays.asList(cfs));
		Field[] pfs = {};
		if(cls.getSuperclass().getName() == BaseEntity.class.getName()) {
			pfs = cls.getSuperclass().getDeclaredFields();
		}
		fs.addAll(Arrays.asList(pfs));
		if(fs.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(int i = 0; i < fs.size(); i ++) {
			JsonIgnore jsonIgnore = fs.get(i).getAnnotation(JsonIgnore.class);
			if(jsonIgnore != null) {
				continue;
			}
			Column cd = fs.get(i).getDeclaredAnnotation(Column.class);
			String fieldType = fs.get(i).getType().toString();
			if(cd != null) {
				if(Funs.isNotBlank(cd.value())) {
					if(index > 0) {
						sb.append(",");
					}
					if(fieldType.endsWith("Date")) {
						sb.append("DATE_FORMAT("+prex+cd.value()+",'%Y-%m-%d %H:%i:%s') as " + fieldPrex + fs.get(i).getName());
					}else {
						sb.append(prex+cd.value() + " as " + fieldPrex + fs.get(i).getName());
					}
					index ++;
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 将实体对象的属性转为Pgsql查询语句的select 和from中的字段
	 * prex.user_name as fieldPrexuserName
	 * @param cls 实体class
	 * @param prex 表名或表的别名
	 * @param fieldPrex 字段前缀
	 * @param isExtends 是否有继承
	 * @return
	 */
	public static String vo2PgsqlField(Class<?> cls
			, String prex
			, String fieldPrex,boolean isExtends) {
		prex = Funs.isBlank(prex)?"":prex+".";
		fieldPrex = Funs.isBlank(fieldPrex)?"":fieldPrex;
		
		List<Field> fs = new ArrayList<>();
		if(isExtends) {
			do {
				Field[] pfs = cls.getDeclaredFields();
				fs.addAll(Arrays.asList(pfs));
					cls = cls.getSuperclass();
			}while(!Strings.equals(cls.getName(), Object.class.getName()));
		}else {
			Field[] pfs = cls.getDeclaredFields();
			fs.addAll(Arrays.asList(pfs));
		}
		if(fs.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(int i = 0; i < fs.size(); i ++) {
			JsonIgnore jsonIgnore = fs.get(i).getAnnotation(JsonIgnore.class);
			if(jsonIgnore != null) {
				continue;
			}
			Column cd = fs.get(i).getDeclaredAnnotation(Column.class);
			String fieldType = fs.get(i).getType().toString();
			if(cd != null) {
				if(Funs.isNotBlank(cd.value())) {
					if(index > 0) {
						sb.append(",");
					}
					if(fieldType.endsWith("Date")) {
						sb.append("to_char("+prex+cd.value()+",'yyyy-MM-DD HH24:MI:SS') as " + fieldPrex + fs.get(i).getName());
					}else {
						sb.append(prex+cd.value() + " as \"" + fieldPrex + fs.get(i).getName()+"\"");
					}
					index ++;
				}
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * 将实体对象的属性转为Pgsql查询语句的select 和from中的字段
	 * prex.user_name as fieldPrexuserName
	 * @param cls 实体class
	 * @param alias 表的别名或表名
	 * @param fieldPrex 字段前缀
	 * @param filterField 需要过滤的字段，格式为pojo实体名,多个用竖线|分隔，如：userName|userPasswd
	 * @param allowField 只允许的字段，格式为pojo实体名,多个用竖线|分隔，如：userName|userPasswd
	 * @param isExtends 是否存在继承的类
	 * @return
	 */
	public static String vo2PgsqlField(Class<?> cls
			, String alias
			, String fieldPrex
			,String filterField
			,String allowField
			,boolean isExtends) {
		alias = Funs.isBlank(alias) ? "" : alias+".";
		fieldPrex = Funs.isBlank(fieldPrex)?"":fieldPrex;
		List<Field> fs = new ArrayList<>();
		
		if(isExtends) {
			do {
				Field[] dfs = cls.getDeclaredFields();
				fs.addAll(Arrays.asList(dfs));
					cls = cls.getSuperclass();
			}while(!equals(cls.getName(), Object.class.getName()));
		}else {
			Field[] dfs = cls.getDeclaredFields();
			fs.addAll(Arrays.asList(dfs));
		}
		
		if(fs.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(int i = 0; i < fs.size(); i ++) {
			JsonIgnore jsonIgnore = fs.get(i).getAnnotation(JsonIgnore.class);
			if(jsonIgnore != null) {
				continue;
			}
			if(Funs.isNotBlank(filterField)) {
				String[] filterFields = filterField.split("\\|");
				if(Arrays.asList(filterFields).contains(fs.get(i).getName())) {
					continue;
				}
			}
			if(Funs.isNotBlank(allowField)) {
				String[] allowFields = allowField.split("\\|");
				if(!Arrays.asList(allowFields).contains(fs.get(i).getName())) {
					continue;
				}
			}
			Column cd = fs.get(i).getDeclaredAnnotation(Column.class);
			String fieldType = fs.get(i).getType().toString();
			if(cd != null) {
				if(Funs.isNotBlank(cd.value())) {
					if(index > 0) {
						sb.append(",");
					}
					if(fieldType.endsWith("Date")) {
						sb.append("to_char("+alias+cd.value()+",'yyyy-MM-DD HH24:MI:SS') as " + fieldPrex + fs.get(i).getName());
					}else {
						sb.append(alias+cd.value() + " as \"" + fieldPrex + fs.get(i).getName()+"\"");
					}
					index ++;
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获得最近几个月
	 * @param size
	 * @param asc
	 * @return
	 */
	public static List<String> getLastMonths(int size,boolean asc) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        List<String> list = new ArrayList<String>(size);
        for (int i=0;i<size;i++) {
            c.setTime(new Date());
            c.add(Calendar.MONTH, -i);
            Date m = c.getTime();
            list.add(sdf.format(m));
        }
       if(asc) {
        	Collections.reverse(list);
        }
        return list;

    }
	 public static Date timeParse(String time) {
		if(Strings.isBlank(time)) {
			System.out.println("时间为空");
			return new Date();
		}
		time = time.replaceAll("/", "-").trim();
		try {
			return Times.parse("yyyy-MM-dd", time);
		}catch(Exception e) {}
		
		try {
			return Times.parse("yyyy-MM-dd HHmm", time);
		}catch(Exception e) {}
		
		try {
			return Times.parse("yyyy-MM-dd HH:mm:ss", time);
		}catch(Exception e) {}
		try {
			return Times.parse("yyyyMMddHHmmss", time);
		}catch(Exception e) {}
		
		try {
			return Times.parse("yyyy-MM-dd HH:mm", time);
		}catch(Exception e) {}
		
		try {
			return Times.parse("yyyy-MM-dd HH", time);
		}catch(Exception e) {}
		
		try {
			return Times.parse("yyyy-MM", time);
		}catch(Exception e) {}
		
		try {
			return Times.parse("MM-dd", time);
		}catch(Exception e) {}
		
		System.out.println("时间["+time+"]解析错误,直接取日期得了");
		time = time.substring(0,10);
		try {
			return Times.parse("yyyy-MM-dd", time);
		}catch(Exception e) {}
		
		System.out.println("时间["+time+"]解析不了了，直接使用当前的日期喽");
		return null;
	}
	
	 public static int getYearDiffNowDate(String date) {
		 Calendar c1 = Calendar.getInstance();
		 Calendar c2 = Calendar.getInstance();
		 c1.setTime(Funs.timeParse(date));
		 int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		 return Math.abs(year);
	 }
	 
	 public final static String getNowLocalDate() {
			LocalDate ld = LocalDate.now();
			return ld.format(DateTimeFormatter.BASIC_ISO_DATE);
		}
	 
}
