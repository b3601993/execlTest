package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

	/**
	 * 去除HTML标签
	 *
	 * @param html
	 * @return
	 * 
	 * @author Albert
	 * @date 2014年8月6日 下午4:40:22
	 */
	public static String trimHtml(String html) {
    	if (html == null) {
    		return null;
    	}
    	String regExScript="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
    	String regExStyle="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
    	String regExHtml="<[^>]+>"; //定义HTML标签的正则表达式 
    	
    	Pattern pScript = Pattern.compile(regExScript,Pattern.CASE_INSENSITIVE); 
    	Matcher mScript = pScript.matcher(html); 
    	html = mScript.replaceAll(""); //过滤script标签 
    	
    	Pattern pStyle = Pattern.compile(regExStyle,Pattern.CASE_INSENSITIVE); 
    	Matcher mStyle = pStyle.matcher(html); 
    	html = mStyle.replaceAll(""); //过滤style标签 
    	
    	Pattern pHtml = Pattern.compile(regExHtml,Pattern.CASE_INSENSITIVE); 
    	Matcher mHtml = pHtml.matcher(html); 
    	html = mHtml.replaceAll(""); //过滤html标签 
    	html = html.replaceAll("&nbsp;", " "); //过滤html标签 
    	return html.trim();
    }
}
