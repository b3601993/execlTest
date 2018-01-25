package sshTest.tran;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sshTest.JSchException;
import sshTest.Jsch;
import sshTest.utils.Utils;

public class Session {
	
	static final Logger logger = LogManager.getLogger(Session.class.getName());

	//客户端 -- 标识字串 
	private static byte[] V_C = Utils.str2Byte("SSH-2.0-YUTAO-1.0.0");
	static byte[] buffer = new byte[1024*10*2];
	
	public void connect(){
		
		try {
			//建立连接时，服务器已经将版本号返回啦！
			Socket socket = new Socket("192.168.0.110", 22);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			//这时紧接着，就需要把客户端的标识字串发送给服务器端
			byte[] cbyte = new byte[V_C.length + 1];
			System.arraycopy(V_C, 0, cbyte, 0, V_C.length);
			//单引号是char类型
			cbyte[V_C.length] = (byte)'\n';
			System.out.println(new String(cbyte));
			out.write(cbyte);
			
			int read=0;
			while(true){
				 read = in.read(buffer);
				if(read > 0){
					break;
				}
			}
			System.out.println(new String(buffer, 0, read));
			
			//密钥交换将在发送标识字串后，立即开始
			//密钥交换和算法协商
			send_kexinit();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 密钥交换和算法协商
	 * 
	 * @author yutao
	 * @throws Exception 
	 * @date 2018年1月25日上午11:54:33
	 */
	private void send_kexinit() throws Exception {
		
		//这里不明白为什么要用两个，而不是cipherc2s一个就够了
		String cipherc2s = getConfig("cipher.c2s");
		String ciphers2c = getConfig("cipher.s2c");
		
		//不可使用的算法
		String[] notAvailableCiphers = checkCiphers(cipherc2s);
		if(notAvailableCiphers != null && notAvailableCiphers.length>0){
			//将不可用的密钥算法从字符串中移除掉
			cipherc2s = Utils.diffString(cipherc2s, notAvailableCiphers);
			ciphers2c = Utils.diffString(ciphers2c, notAvailableCiphers);
			if(cipherc2s == null || cipherc2s == null){
				throw new JSchException("There are not any available ciphers.");
			}
		}
	}

	/**
	 * 检查密钥，返回没有实现类的密钥
	 * 猜测可能是模拟服务器返回的密钥，所以要验证下是否支持该密钥
	 * @param cipherc2s
	 * @author yutao
	 * @date 2018年1月25日下午2:12:04
	 */
	private String[] checkCiphers(String ciphers) {
		if(StringUtils.isBlank(ciphers)){
			return null;
		}
		logger.info("CheckCiphers: " + ciphers);
		
		String cipherc2s = getConfig("cipher.c2s");
		//我注释掉的，感觉没什么用
//		String ciphers2c = getConfig("cipher.s2c");
		
		CopyOnWriteArrayList<String> result = new CopyOnWriteArrayList<>();
		//原版是重写了split，我觉得没必要
//		String[] _ciphers = Utils.split(ciphers, ",");
		
		String[] _ciphers = ciphers.split(",");
		for(String s : _ciphers){
			
			if(/*ciphers2c.indexOf(s) == -1 &&*/ cipherc2s.indexOf(s) == -1){
				continue;
			}
			//能执行到这一步说明，可能支持该算法
			if (!checkCipher(getConfig(s))) {
				result.add(s);
			}
		}
		//之所以使用数组，是因为数组比集合轻；
		//List<String>这里面存的是对象，而数组里面存的是值
		String[] foo = new String[result.size()];
		//result.toArray() 不使用这个的话，result里面存的都是对象
		System.arraycopy(result.toArray(), 0, foo, 0, result.size());
		for(String s : foo){
			logger.info(s+" is not available.");
		}
		return foo;
	}

	/**
	 * 验证该加密算法是否有具体的实现类
	 * @param config
	 * @return
	 * @author yutao
	 * @date 2018年1月25日下午5:21:02
	 */
	private boolean checkCipher(String cipher) {
		
		try {
			Class<?> c = Class.forName(cipher);
//			Cipher _c = (Cipher)c.newInstance();
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 获取指定key的配置信息
	 * @param string
	 * @author yutao
	 * @return 
	 * @date 2018年1月25日下午2:07:02
	 */
	private String getConfig(String key) {
		return Jsch.config.get(key);
	}
	
}
