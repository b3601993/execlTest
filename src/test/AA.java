package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class AA {

	
	public static void main(String[] args) {
		
		List<Map<String, Object>> result = new ArrayList<>();
		
//		result = result.subList(0, 100);
//		System.out.println(result);
		
		
		Set<String> ss = new HashSet<>();
		System.out.println(ss);
		System.out.println(ss.contains(null));
		
		//<p>1、目前看反弹低预期，而A股下一次反弹的窗口可能要等2019年2-3月（新时代证券）<a href=\\\"#1\\\" target=\\\"_self\\\">#1</a></p><p>
//		String content = "<p>1、目前看反弹低预期，而A股下一次反弹的窗口可能要等2019年2-3月（新时代证券）<a href=\\\"#1\\\" target=\\\"_self\\\">#1</a></p><p>2、干货解读！读透中央经济工作会议，深挖哪些超预期的投资机会（长江证券）<a href=\\\"#2\\\" target=\\\"_self\\\">#2</a></p><p>3、新能源汽车销售火爆下，看好下游六氟磷酸锂（招商证券）<a href=\\\"#3\\\" target=\\\"_self\\\">#3</a></p><p>4、天孚通信：公司进入新一轮成长周期，布局5G优质成长企业（招商证券）<a href=\\\"#4\\\" target=\\\"_self\\\">#4</a></p><p>5、动力电池回收产业“春色欲来”，政府出政策企业忙布局（朝阳财经研究院）<a href=\\\"#5\\\" target=\\\"_self\\\">#5</a></p><p></p><br/>";
		String content = "2、干货解读！读透中央经济工作会议，深挖哪些超预期的投资机会";
		System.out.println(content);
		content = content.replaceAll("<p>", "");
		content = content.replaceAll("</p>", "<br/>");
//		content = content.replaceAll("<br/>", "");
		
//		Pattern pattern = Pattern.compile("((.*?)(<a.*?>.*?</a>)?)?((.*?)<a.*?>.*?</a>)?((.*?)<a.*?>.*?</a>)?((.*?)<a.*?>.*?</a>)?((.*?)<a.*?>.*?</a>)?");
		Pattern pattern = Pattern.compile("<a[^<]*?>#.{1}</a>");
		
		Matcher matcher2 = pattern.matcher(content);
		
		String replaceAll = matcher2.replaceAll("");
		System.out.println(replaceAll);
//		replaceAll = replaceAll.replaceAll("</a>", "");
//		System.out.println(replaceAll.replaceAll("", "")/*.replaceAll("(^1\\d、)", "<br/>$1")*/);
		String str  = "";
		String[] split = replaceAll.split("<br/>");
		int length = split.length;
		for(int i =0; i<length; i++) {
			if (i == length-1) {
				str += split[i] ;
			}else {
				str += split[i]+ "<br/>";
			}
		}
		System.out.println(str);
		
		
/*		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			for(int i=1;i<=matcher.groupCount();i++){
				
				String group = matcher.group(i);
				if (group == null) {
					continue;
				}
				if(!group.contains("href")) {
					if(StringUtils.isBlank(str)) {
						str += group;
					}else {
						str += "<br/>" + group;
					}
					
				}
				System.out.println("group "+i+":"+matcher.group(i));
			}
		}
		System.out.println(str);
*/	}
}
