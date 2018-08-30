package bitmap;

import java.util.BitSet;

/**
 * 处理海量数据的bitMap操作
 *
 *
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2018年6月8日下午4:44:25
 */
public class BitMapTest {

	
	public static void main(String[] args) {
		
		int [] array = new int [] {347209, 127427, 335968, 666258, 102251, 332123};
		
		BitSet bs = new BitSet();
		
		for(int i=0,size=array.length; i<size; i++){
			bs.set(array[i], true);
		}
		System.out.println(bs);
	}
}
