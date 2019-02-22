package jvm.loadclass;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2018年12月10日下午3:55:44
 */
public class ClassLoaderTest {

	public static void main(String[] args) {
		
		ClassLoader myLoader = new ClassLoader() {

			@Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
				try {
					
					String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
					
					InputStream is = getClass().getResourceAsStream(fileName);
					if(is == null) {
						return super.loadClass(name);
					}
					byte[] b = new byte[is.available()];
					is.read(b);
					return defineClass(name, b, 0, b.length);
					
				} catch (IOException e) {
					e.printStackTrace();
					throw new ClassNotFoundException(name);
				}
//				return super.loadClass(name);
			}
		};
		
		try {
			Object obj = myLoader.loadClass("jvm.loadclass.ClassLoaderTest").newInstance();
			System.out.println(obj.getClass());
			System.out.println(obj instanceof jvm.loadclass.ClassLoaderTest);
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
