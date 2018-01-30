package sshTest.utils;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CopyOnWriteArrayList;

import sshTest.kex.HASH;


public class Utils {

	/**
	 * 将字符串转成字节数组（默认是utf-8编码)
	 * @param str
	 * @return
	 * @author yutao
	 * @date 2018年1月25日上午9:49:31
	 */
	public static byte[] str2Byte(String str){
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return str.getBytes();
		}
	}
	
	/**
	 * 根据spilt对字符串ciphers进行分割
	 * 这里之所以重写是因为该jar包并不依赖任何包
	 * @param str
	 * @param split
	 * @return
	 * @author yutao
	 * @date 2018年1月25日下午2:33:54
	 */
	public static String[] split(String str, String split) {
		if(str == null)
			return null;
		//先将字符串转成字节数组
		byte[] buf = Utils.str2Byte(str);
		CopyOnWriteArrayList<String> bar = new CopyOnWriteArrayList<String>();
		int start = 0, index;
		while(true){
			index = str.indexOf(split, start);
			if(index > 0){
				bar.add(byte2Str(buf, start, index - start));
				start = index + 1;
				continue;
			}
			bar.add(byte2Str(buf, start, buf.length-start));
			break;
		}
		String[] ss = new String[bar.size()];
		for(int i=0; i<bar.size(); i++){
			ss[i] = bar.get(i);
		}
		return ss;
	}
	
	/**
	 * 将字节数组转成字符串
	 * @param buf
	 * @param start
	 * @param len
	 * @author yutao
	 * @return 
	 * @date 2018年1月25日下午2:48:52
	 */
	private static String byte2Str(byte[] buf, int start, int len) {
		return byte2Str(buf, start, len, "UTF-8");
	}

	/**
	 * 将字节数组转成字符串
	 * @param buf
	 * @param start
	 * @param len
	 * @param encoding
	 * @return
	 * @author yutao
	 * @date 2018年1月25日下午2:50:39
	 */
	private static String byte2Str(byte[] buf, int start, int len, String encoding) {
		
		try {
			return new String(buf, start, len, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(buf, start, len);
		}
	}

	/**
	 * 将cipherc2s中包含不可用的算法移除掉
	 * @param cipherc2s
	 * @param notAvailableCiphers
	 * @return
	 * @author yutao
	 * @date 2018年1月25日下午6:08:46
	 */
	public static String diffString(String cipherc2s, String[] notAvailableCiphers) {
		
		String[] foo = cipherc2s.split(",");
		String result = null;
		// yutao这个其实就是个标识，下面是标识了一个外层循环
		// 名称随便取的，为了控制在continue时，调到外层循环（针对下面业务需求）
		yutao:for(int i=0; i< foo.length; i++){
			for(int j=0;j<notAvailableCiphers.length; j++){
				if(foo[i].equals(notAvailableCiphers[j])){
					continue yutao;
				}
			}
			if(result == null){
				result = foo[i];
			}else{
				result += "," + foo[i];
			}
		}
		return result;
	}
	/**
	 * 字节转字符串
	 * @param cp
	 * @param s
	 * @param l
	 * @return
	 */
	public static String byte2str(byte[] cp, int s, int l) {
		return byte2str(cp, s, l, "UTF-8");
	}
	
	/**
	 * 字节转成字符串，可以指定编码
	 * @param cp
	 * @param s
	 * @param l
	 * @param encoding
	 * @return
	 * @author yutao
	 * @date 2018年1月30日上午11:36:23
	 */
	private static String byte2str(byte[] cp, int s, int l, String encoding) {
		try {
			return new String(cp, s, l, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(cp, s, l);
		}
	}
	
	private static String[] chars = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	public static String getFingerPrint(HASH hash, byte[] data) {
		try {
			hash.init();
			hash.update(data, 0, data.length);
			//digest方法完成hash计算，foo是hash后的结果
			byte[] foo = hash.digest();
			StringBuffer sb = new StringBuffer();
			int bar;
			for (int i = 0; i < foo.length; i++) {
				bar = foo[i] & 0xff;
				//再将hash后的结果，转成16进制的字符表示 即：指纹
				sb.append(chars[(bar >>> 4) & 0xf]);
				sb.append(chars[(bar) & 0xf]);
				if (i + 1 < foo.length)
					sb.append(":");
			}
			return sb.toString();
		} catch (Exception e) {
			return "???";
		}
	}
}
