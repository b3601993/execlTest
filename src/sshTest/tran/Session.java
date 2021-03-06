package sshTest.tran;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sshTest.JSchException;
import sshTest.Jsch;
import sshTest.kex.HostKey;
import sshTest.kex.HostKeyRepository;
import sshTest.kex.KeyExchange;
import sshTest.kex.KnownHosts;
import sshTest.kex.Signature;
import sshTest.utils.Utils;

public class Session {
	
	static final Logger logger = LogManager.getLogger(Session.class.getName());
	
	static final int SSH_MSG_DISCONNECT = 1;
	static final int SSH_MSG_IGNORE = 2;// ignore
	static final int SSH_MSG_UNIMPLEMENTED = 3;// unimplemented
	static final int SSH_MSG_DEBUG = 4;// debug
	static final int SSH_MSG_SERVICE_REQUEST = 5;// service_request
	static final int SSH_MSG_SERVICE_ACCEPT = 6;// service_accept
	// kexinit
	static final int SSH_MSG_KEXINIT = 20;
	// packet_max_size
	private static final int PACKET_MAX_SIZE = 256 * 1024;
	
	
	private byte[] V_S; // server version
	//客户端 -- 标识字串 
	private static byte[] V_C = Utils.str2Byte("SSH-2.0-YUTAO-1.0.0");
	// the payload of the client's SSH_MSG_KEXINIT
	private byte[] I_C; 
	private byte[] I_S; // the payload of the server's SSH_MSG_KEXINIT
	
	private int seqi = 0;
	
	String[] guess = null;
	
	private volatile boolean isConnected = false;
	private boolean isAuthed = false;
	
	private IO io;
	private String hostKeyAlias = null;
	
	
	//用于生成强随机数
	static Random random;

	Buffer buf;
	private UserInfo userinfo;
	
	private HostKeyRepository hostkeyRepository = null;
	Jsch jsch;
	
	String host = "127.0.0.1";
	String org_host = "127.0.0.1";
	int port = 22;
	
	private long kex_start_time = 0L;
	
	public void connect() throws Exception{
		if(isConnected){
			throw new JSchException("session is already connected");
		}
		
		io = new IO();
		try {
			int i = 0, j;
			//建立连接时，服务器已经将版本号返回啦！
			Socket socket = new Socket("192.168.0.110", 22);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			io.setIn(in);
			io.setOut(out);
			
			//这时紧接着，就需要把客户端的标识字串发送给服务器端
			byte[] cbyte = new byte[V_C.length + 1];
			System.arraycopy(V_C, 0, cbyte, 0, V_C.length);
			//单引号是char类型
			cbyte[V_C.length] = (byte)'\n';
			System.out.println(new String(cbyte));
			out.write(cbyte);
			
			int read=0;
			while(true){
				 read = in.read(buf.buffer);
				if(read > 0){
					break;
				}
			}
			System.out.println(new String(buf.buffer, 0, read));
			
			V_S = new byte[i];
			//将远程主机ssh2版本信息赋值到v_s变量
			System.arraycopy(buf.buffer, 0, V_S, 0, i);
			
			//密钥交换将在发送标识字串后，立即开始
			//密钥交换和算法协商
			send_kexinit();
			//客户端已经发送完了，SSH_MSG_KEXINIT数据包
			//接下来开始接收服务器端返回的SSH_MSG_KEXINIT数据包
			buf = read(buf);
			if(buf.getCommand() != SSH_MSG_KEXINIT){
				//说明密钥交互失败，需要再次执行
				in_kex = false;
				throw new JSchException("invalid protocol: " + buf.getCommand());
			}
			logger.info("SSH_MSG_KEXINIT received");
			
			//调用这个方法之后，就确定了使用哪个算法
			KeyExchange kex = recieive_kexinit(buf);
			while(true){
				buf = read(buf);
				if(kex.getState() == buf.getCommand()){
					kex_start_time = System.currentTimeMillis();
					boolean result = kex.next(buf);
					if(!result){
						in_kex = false;
						throw new JSchException("verify: " + result);
					}
				}else{
					in_kex = false;
					throw new JSchException("invalid protocol(kex): " + buf.getCommand());
				}
				if(kex.getState() == KeyExchange.STATE_END){
					break;
				}
			}
			//能执行到这里说明，上面已经验证完毕；
			//即密钥完成了交换，并且已经确定了会话的ID:K,hash值H也生成好了
			
			long tmp = System.currentTimeMillis();
			in_prompt = true;
			checkHost(host, port, kex);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HostKey hostkey = null;
	
	private void checkHost(String chost, int port, KeyExchange kex) throws JSchException {
		String shkc = getConfig("StrictHostKeyChecking");
		if (hostKeyAlias != null) {
			chost = hostKeyAlias;
		}
		//服务器的公钥
		byte[] K_S = kex.getHostKey();
		//算法类型
		String key_type = kex.getKeyType();
		//将hash后的值，再进行
		String key_fprint = kex.getFingerPrint();
		
		if(hostKeyAlias == null && port != 22){
			chost = ("[" + chost + "]:" + port);
		}
		
		//获取已知的服务器的公钥类
		HostKeyRepository hkr = getHostKeyRepository();
		//默认是：no
		String hkh = getConfig("HashKnownHosts");
		if (hkh.equals("yes") && (hkr instanceof KnownHosts)) {
			hostkey = ((KnownHosts) hkr).createHashedHostKey(chost, K_S);
		} else {
			//根据服务端的主机地址和公钥创建一个hostkey类
			hostkey = new HostKey(chost, K_S);
		}
		
		int i = 0;
		synchronized (hkr) {
			//检查该主机是否之前已经连接过了
			//个人猜测，可能是验证服务器的公钥是否正确
			i = hkr.check(chost, K_S);
		}
		
		
		boolean insert = false;
		if ((shkc.equals("ask") || shkc.equals("yes")) && i == HostKeyRepository.CHANGED) {
			String file = null;
			synchronized (hkr) {
//				file = hkr.getKnownHostsRepositoryID();
			}
			if (file == null) {
				file = "known_hosts";
			}

			boolean b = false;

			/*if (userinfo != null) {
				String message = "WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!\n"
						+ "IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!\n"
						+ "Someone could be eavesdropping on you right now (man-in-the-middle attack)!\n"
						+ "It is also possible that the " + key_type + " host key has just been changed.\n"
						+ "The fingerprint for the " + key_type + " key sent by the remote host " + chost + " is\n"
						+ key_fprint + ".\n" + "Please contact your system administrator.\n"
						+ "Add correct host key in " + file + " to get rid of this message.";

				if (shkc.equals("ask")) {
					b = userinfo.promptYesNo(message + "\nDo you want to delete the old key and insert the new key?");
				} else { // shkc.equals("yes")
					userinfo.showMessage(message);
				}
			}*/

			if (!b) {
				throw new JSchException("HostKey has been changed: " + chost);
			}

			/*synchronized (hkr) {
				hkr.remove(chost, kex.getKeyAlgorithName(), null);
				insert = true;
			}*/
		}
		
		
		//下面就是为了提示 认证主机的真实性，目前无法识别，问你是否需要继续连接
		if ((shkc.equals("ask") || shkc.equals("yes")) && (i != HostKeyRepository.OK) && !insert) {
			if (shkc.equals("yes")) {
				throw new JSchException("reject HostKey: " + host);
			}
			// System.err.println("finger-print: "+key_fprint);
			if (userinfo != null) {
				boolean foo = userinfo.promptYesNo("The authenticity of host '" + host + "' can't be established.\n"
						+ key_type + " key fingerprint is " + key_fprint + ".\n"
						+ "Are you sure you want to continue connecting?");
				if (!foo) {
					throw new JSchException("reject HostKey: " + host);
				}
				insert = true;
			} else {
				if (i == HostKeyRepository.NOT_INCLUDED)
					throw new JSchException(
							"UnknownHostKey: " + host + ". " + key_type + " key fingerprint is " + key_fprint);
				else
					throw new JSchException("HostKey has been changed: " + host);
			}
		}
		
		if (shkc.equals("no") && HostKeyRepository.NOT_INCLUDED == i) {
			insert = true;
		}
		
		//远程主机的密钥已经被标记为撤销
		//从源码的意思中看，视乎是host包含在k_S中就等于该host_key是无效的
		/*if (i == HostKeyRepository.OK) {
			HostKey[] keys = hkr.getHostKey(chost, kex.getKeyAlgorithName());
			String _key = Utils.byte2str(Utils.toBase64(K_S, 0, K_S.length));
			for (int j = 0; j < keys.length; j++) {
				if (keys[i].getKey().equals(_key) && keys[j].getMarker().equals("@revoked")) {
					if (userinfo != null) {
						userinfo.showMessage("The " + key_type + " host key for " + host + " is marked as revoked.\n"
								+ "This could mean that a stolen key is being used to " + "impersonate this host.");
					}
					if (JSch.getLogger().isEnabled(Logger.INFO)) {
						JSch.getLogger().log(Logger.INFO, "Host '" + host + "' has provided revoked key.");
					}
					throw new JSchException("revoked HostKey: " + host);
				}
			}
		}*/
		
		/*if (i == HostKeyRepository.OK && JSch.getLogger().isEnabled(Logger.INFO)) {
			JSch.getLogger().log(Logger.INFO, "Host '" + host + "' is known and matches the " + key_type + " host key");
		}*/
		
	}

	/**
	 * Gets the hostkeyRepository. If this.hostkeyRepository is
	 * <code>null</code>, JSch#getHostKeyRepository() will be invoked.
	 *
	 * @see JSch#getHostKeyRepository()
	 */
	public HostKeyRepository getHostKeyRepository() {
		if (hostkeyRepository == null)
			return jsch.getHostKeyRepository();
		return hostkeyRepository;
	}

	/**
	 * 解析来自服务器的数据
	 * @param buf
	 * @return 
	 * @throws Exception 
	 */
	private KeyExchange recieive_kexinit(Buffer buf) throws Exception {
		int j = buf.getInt();
		//如果数据包的长度和实际的字节长度不一致说明数据被压缩啦！
		if(j != buf.getLength()){
			buf.getByte();
			//压缩后的payload字节大小
			I_S = new byte[buf.index - 5];
		}else{
			//没有压缩的payload字节大小
			I_S = new byte[j - 1 - buf.getByte()];
		}
		//I_S里面装的就是payload字节
		System.arraycopy(buf.buffer, buf.s, I_S, 0, I_S.length);
		//接收到的不是SSH_MSG_KEXINIT 需要重新发送
		if(!in_kex){
			send_kexinit();
		}
		//协商或者猜测 出了服务端和客户端都支持的算法
		//代码里是以客户端为外层进行循环比较的
		guess = KeyExchange.guess(I_S, I_C);
		if(guess == null){
			throw new JSchException("Algorithm negotiation fail");
		}
		
		if(!isAuthed && (guess[KeyExchange.PROPOSAL_ENC_ALGS_CTOS].equals("none") || 
				         guess[KeyExchange.PROPOSAL_ENC_ALGS_STOC].equals("none"))){
			throw new JSchException("NONE Cipher should not be chosen before authentification is successed.");
		}
		
		KeyExchange kex = null;
		
		try {
			Class<?> c = Class.forName(getConfig(guess[KeyExchange.PROPOSAL_KEX_ALGS]));
			kex = (KeyExchange)c.newInstance();
		} catch (Exception e) {
			throw new JSchException(e.toString(), e);
		}
		
		kex.init(this, V_S, V_C, I_S, I_C);
		return kex;
	}


	private int s2ccipher_size = 8;
	private int c2scipher_size = 8;
	
	private volatile boolean in_kex = false;
	private volatile boolean in_prompt = false;
	
	
	/**
	 * 
	 * @param buf
	 * @return
	 * @author yutao
	 * @throws Exception 
	 * @date 2018年1月26日下午4:34:26
	 */
	private Buffer read(Buffer buf) throws Exception {
		int j = 0;
		while(true){
			buf.reset();
			//读取流中的数据到buffer中，只读取8个字节
			io.getByte(buf.buffer, buf.index, s2ccipher_size);
			buf.index += s2ccipher_size;
			/*if (s2ccipher != null) {
				s2ccipher.update(buf.buffer, 0, s2ccipher_size, buf.buffer, 0);
			}*/
			//手动拼接成 32位 即 4个字节
			//因为一个int型占4个字节，而包的开头就是该数据包的总长度
			//所以需要先转成int型，下面就是手动去拼接成一个4字节 即int
			j = ((buf.buffer[0]<<24)&0xff000000) | (buf.buffer[1]<<16)&0x00ff0000
					| (buf.buffer[2]<<8) & 0x0000ff00 | (buf.buffer[3]) & 0x000000ff;
			
			if(j<5 || j> PACKET_MAX_SIZE){
				//数据包发送丢失或者过大的情况 
				//pack_length占4个字节，padding_length占1个字节
				//小于5就说明真正的有效数据发生丢失啦！
//				start_discard(buf, s2ccipher, s2cmac, j, PACKET_MAX_SIZE);
			}
			//j表示的是包的总长度 + 4 就完全包含前面的字节，再减8就是需要解码的数据包的长度
			//need就是需要解密的数据包的长度
			//RFC 4253 6 Binary Packet Protocol 最后一句话中有说明
			int need = j + 4 - s2ccipher_size;
			//如果现有的数组装不下，就要进行扩容
			if((buf.index + need)> buf.buffer.length){
				byte[] foo = new byte[buf.index + need];
				System.arraycopy(buf.buffer, 0, foo, 0, buf.index);
				buf.buffer = foo;
			}
			//因为包都是8的整数倍
			if(need % s2ccipher_size != 0){
				String message = "Bad packet length " + need;
				logger.fatal(message);
//				start_discard(buf, s2ccipher, s2cmac, j, PACKET_MAX_SIZE);
			}
			
			if(need > 0){
				//开始真正的去读取有效数据
				io.getByte(buf.buffer, buf.index, need);
				buf.index += need;
				/*if (s2ccipher != null) {
					s2ccipher.update(buf.buffer, s2ccipher_size, need, buf.buffer, s2ccipher_size);
				}*/
			}
			
			/*if (s2cmac != null) {
				s2cmac.update(seqi);
				s2cmac.update(buf.buffer, 0, buf.index);

				s2cmac.doFinal(s2cmac_result1, 0);
				io.getByte(s2cmac_result2, 0, s2cmac_result2.length);
				if (!java.util.Arrays.equals(s2cmac_result1, s2cmac_result2)) {
					if (need > PACKET_MAX_SIZE) {
						throw new IOException("MAC Error");
					}
					start_discard(buf, s2ccipher, s2cmac, j, PACKET_MAX_SIZE - need);
					continue;
				}
			}*/
			//???
			seqi++;
			
			//解压
			/*if (inflater != null) {
				// inflater.uncompress(buf);
				int pad = buf.buffer[4];
				uncompress_len[0] = buf.index - 5 - pad;
				byte[] foo = inflater.uncompress(buf.buffer, 5, uncompress_len);
				if (foo != null) {
					buf.buffer = foo;
					buf.index = 5 + uncompress_len[0];
				} else {
					System.err.println("fail in inflater");
					break;
				}
			}*/
			
			int type = buf.getCommand() & 0xff;
			if(type == SSH_MSG_DISCONNECT){
				buf.rewind();
				buf.getInt();
				buf.getShort();
			}
			break;
		}
		
		buf.rewind();
		return buf;
	}

	/**
	 * 密钥交换和算法协商
	 * 
	 * @author yutao
	 * @throws Exception 
	 * @date 2018年1月25日上午11:54:33
	 */
	private void send_kexinit() throws Exception {
		
		if(in_kex){
			return;
		}
		
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
		
		String kex = getConfig("kex");
		String[] not_available_kexes = checkKexes(getConfig("CheckKexes"));
		if(not_available_kexes != null && not_available_kexes.length>0){
			kex = Utils.diffString(kex, not_available_kexes);
			if (kex == null) {
				throw new JSchException("There are not any available kexes.");
			}
		}
		
		String server_host_key = getConfig("server_host_key");
		String[] not_available_shks = checkSignatures(getConfig("CheckSignatures"));
		if(not_available_shks != null && not_available_shks.length > 0){
			server_host_key = Utils.diffString(server_host_key, not_available_shks);
			if (server_host_key == null) {
				throw new JSchException("There are not any available sig algorithm.");
			}
		}
		
		in_kex = true;
		kex_start_time = System.currentTimeMillis();
		
		//以后需要发送的数据都会转成字节存到这个对象的属性字节中
		Buffer buf = new Buffer();
		//将需要发送的字节数组数据，封装成数据包的形式
		Packet packet = new Packet(buf);
		packet.reset();
		//将请求类型存入字节数组
		buf.putByte((byte) SSH_MSG_KEXINIT);
		//cookie
		synchronized (random) {
			random.fill(buf.buffer, buf.index, 16);
			buf.skip(16);
		}
		//密钥交换算法，第一个是首选的算法
		buf.putString(Utils.str2Byte(kex));
		//受支持的为服务器主机密钥服务的算法的名称列表
		buf.putString(Utils.str2Byte(server_host_key));
		//可接受的对称加密算法（也称为加密器）的名称列表
		buf.putString(Utils.str2Byte(cipherc2s));
		buf.putString(Utils.str2Byte(ciphers2c));
		//可接受的MAC算法的名称列表，按优先级排序
		buf.putString(Utils.str2Byte(getConfig("mac.c2s")));//这里之所以要写两套，我觉得是因为服务器和客户端要使用同一套加密算法
		buf.putString(Utils.str2Byte(getConfig("mac.s2c")));
		//可接受的压缩算法的名称列表，按优先级排序
		buf.putString(Utils.str2Byte(getConfig("compression.c2s")));
		buf.putString(Utils.str2Byte(getConfig("compression.s2c")));
		//语言标志的名称列表，按优先级排序
		buf.putString(Utils.str2Byte(getConfig("lang.c2s")));
		buf.putString(Utils.str2Byte(getConfig("lang.s2c")));
		//表明是否有一个猜测的密钥交换数据包跟随
		buf.putByte((byte)0);//false
		//为将来扩展预留的
		buf.putInt(0);
		
		//将字节数组起始位置标识定位到5
		buf.setOffSet(5);
		//得到客户端SSH_MSG_KEX_INIT的有效载荷
		I_C = new byte[buf.getLength()];
		
		//开始发送数据
		write(packet);
		
		logger.info("SSH_MSG_KEXINIT sent");
	}

	/**
	 * 
	 * @param packet
	 * @author yutao
	 * @date 2018年1月26日下午4:09:20
	 */
	private void write(Packet packet) {
		
	}

	/**
	 * 检查签名
	 * @param sign
	 * @author yutao
	 * @date 2018年1月26日上午10:14:24
	 */
	private String[] checkSignatures(String sign) {
		if(sign == null || sign.length() == 0){
			return null;
		}
		logger.info("CheckSignatures: " + sign);
		
		CopyOnWriteArrayList<String> result = new CopyOnWriteArrayList<>();
		
		String[] _sign = sign.split(",");
		for(String s : _sign){
			try {
				Class<?> c = Class.forName(getConfig(s));
				Signature sig = (Signature)(c.newInstance());
				sig.init();
			} catch (Exception e) {
				result.add(s);
			}
		}
		if(result.size()==0){
			return null;
		}
		
		String[] foo = new String[result.size()];
		System.arraycopy(result.toArray(), 0, foo, 0, result.size());
		for(String f : foo){
			logger.info(f + " is not available.");
		}
		return foo;
	}

	/**
	 * ???
	 * @param kexes
	 * @author yutao
	 * @date 2018年1月26日上午9:43:39
	 */
	private String[] checkKexes(String kexes) {
		if(kexes == null || kexes.length() == 0){
			return null;
		}
		logger.info("CheckKexes: " + kexes);
		
		CopyOnWriteArrayList<String> result = new CopyOnWriteArrayList<>();
		String[] _kexes = kexes.split(",");
		for(String s : _kexes){
			if(!checkKex(this, s)){
				result.add(s);
			}
		}
		
		if(result.size() == 0){
			return null;
		}
		
		String[] foo = new String[result.size()];
		System.arraycopy(result.toArray(), 0, foo, 0, result.size());
		for(String f : foo){
			logger.info(f + " is not available.");
		}
		
		return foo;
	}

	/**
	 * 
	 * @param session
	 * @param kex
	 * @return
	 * @author yutao
	 * @date 2018年1月26日上午9:53:25
	 */
	private boolean checkKex(Session session, String kex) {
		
		try {
			Class<?> c = Class.forName(kex);
			KeyExchange _c = (KeyExchange)c.newInstance();
			_c.init(session,null,null,null,null);
			return true;
		} catch (Exception e) {
			return false;
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
		if(ciphers ==null || ciphers.length()==0){
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
	public String getConfig(String key) {
		return Jsch.config.get(key);
	}
	
}
