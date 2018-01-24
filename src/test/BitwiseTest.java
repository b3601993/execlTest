package test;

import java.io.OutputStream;
import java.nio.ByteOrder;

/**
 * 按位操作
 *
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2018年1月18日上午10:25:38
 */
public class BitwiseTest {

	public static void main(String[] args) {
		byte b =-1;
		int a = b;
		System.out.println(Integer.toBinaryString(a));
		a = b&0xff;
		
		System.out.println(Integer.toBinaryString(a));
		
		System.out.println(Integer.MIN_VALUE);
		
		writeFixedInt(326);
		
		System.out.println(ByteOrder.nativeOrder()); 
	}
	
	
	public static void writeFixedInt(int i) {
		
	    System.out.println(((byte) (i >>> 24)));
	    System.out.println(((byte) (i >>> 16)));
	    System.out.println(((byte) (i >>> 8)));
	    System.out.println(((byte) (i)));
	}
	
}
