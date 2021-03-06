package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	public static byte[] getIpByteArrayFromString(String ip) {
        byte[] ret = new byte[4];
        java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
        try {
            ret[0] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }
    
    public static void main(String args[]){
         byte[] a=getIpByteArrayFromString(args[0]);
          for(int i=0;i< a.length;i++)
                System.out.println(a[i]);
          System.out.println(getIpStringFromBytes(a)); 
    }
    /**
     * 对原始字符串进行编码转换，如果失败，返回原始的字符串
     * @param s 原始字符串
     * @param srcEncoding 源编码方式
     * @param destEncoding 目标编码方式
     * @return 转换编码后的字符串，失败返回原始字符串
     */
    public static String getString(String s, String srcEncoding, String destEncoding) {
        try {
            return new String(s.getBytes(srcEncoding), destEncoding);
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
    
    /**
     * 根据某种编码方式将字节数组转换成字符串
     * @param b 字节数组
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, String encoding) {
        try {
            return new String(b, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b);
        }
    }
    
    /**
     * 根据某种编码方式将字节数组转换成字符串
     * @param b 字节数组
     * @param offset 要转换的起始位置
     * @param len 要转换的长度
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }
    
    /**
     * @param ip ip的字节数组形式
     * @return 字符串形式的ip
     */
    public static String getIpStringFromBytes(byte[] ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(ip[0] & 0xFF);
        sb.append('.');       
        sb.append(ip[1] & 0xFF);
        sb.append('.');       
        sb.append(ip[2] & 0xFF);
        sb.append('.');       
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }
    
    /**
	 * 去掉字段中的时间
	 * @param html
	 * @return
	 * @author yutao
	 * @date 2017年5月4日下午5:22:56
	 */
	public static String replaceDate(String html){
		String regDateTime = "[\\d-:]"; //定义date的正则表达式 
		Pattern pattern = Pattern.compile(regDateTime);
		Matcher matcher = pattern.matcher(html);
		
		html = matcher.replaceAll("");
		return html.trim();
	}
	
	/**
	 * 是否包含特殊字符
	 * @param keyWord
	 * @return
	 * @author yutao
	 * @date 2017年2月24日上午9:43:19
	 */
	public static boolean isSpecialchar(String keyWord){
		if(StringUtils.isNotBlank(keyWord)){
			 String regEx = "[`~!@#^&*+=|{}':',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]";
			 Pattern pattern = Pattern.compile(regEx);
			 Matcher m = pattern.matcher(keyWord);
			 if(m.find()){
				 return true;
			 }
		 }
		return false;
	}
	
	/**
	 * 去除特殊字符
	 * @param keyWord
	 * @return
	 * @author yutao
	 * @date 2017年5月10日下午4:01:13
	 */
	public static String deleteSpecialchar(String keyWord){
		if(keyWord == null){
			return null;
		}
		String regEx = "[`~!@#^&*+=|{}':',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]";
		Pattern p = Pattern.compile(regEx);  
		Matcher m = p.matcher(keyWord);  
		keyWord = m.replaceAll("");
		return keyWord;
	}
    
	/**
	 * 得到用&好拼接后的URL
	 * 
	 * @param params
	 * @param incomingParams
	 * @return
	 * @author yutao
	 * @date 2018年1月9日下午5:31:28
	 */
	public static String getUrlStr(Map<String, String> params) {
		String incomingParams = "";
		if (params != null && !params.isEmpty()) {
			try {
				for (String s : params.keySet()) {
					incomingParams = incomingParams + s + "=" + URLEncoder.encode((String) params.get(s), "UTF-8") + "&";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			incomingParams = incomingParams.substring(0, incomingParams.length() - 1);
		}
//		String paramsStr = JSON.toJSONString(params);
//		incomingParams = paramsStr.replaceAll("[{}\"]", "").replaceAll(":", "=").replaceAll(",", "&");
		return incomingParams;
	}
	
	/**
	 * 将参数反向解析成map形式
	 * @param paramStr
	 * @return
	 * @author yutao
	 * @date 2018年1月12日下午4:01:23
	 */
	public static LinkedHashMap<String, String> getParamMap(String paramStr){
		
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		if(StringUtils.isNotBlank(paramStr)){
			String[] split = paramStr.split("&");
			for(String s : split){
				String[] ss = s.split("=");
				try {
					if(ss.length == 2){
						params.put(ss[0], URLDecoder.decode(ss[1], "UTF-8"));
					}else{
						params.put(ss[0], "");
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}
}
