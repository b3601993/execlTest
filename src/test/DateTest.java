package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2019年1月2日下午2:22:22
 */
public class DateTest {

	
	public static void main(String[] args) {
		
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		String keyString = "2019-01-02 00:00:01";
		
		try {
			Date parse = sdf.parse(keyString);
			System.out.println(parse);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
