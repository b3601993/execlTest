package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2018年12月6日上午11:51:48
 */
public class SetFinalTest {

	
	
	public static void main(String[] args) {
		String aa = "a";
		String b = "b";
		String c = "c";
		
		Map<String, Object> mm =new HashMap<>();
		Set<String> ss = mm.keySet();
		System.out.println(mm.keySet().getClass());
		ss.add(aa);
		ss.add(b);
		ss.add(c);
		System.out.println(ss);
	}
}
