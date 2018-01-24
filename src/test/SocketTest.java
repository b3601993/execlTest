package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTest {

	
	public static void main(String[] args) throws UnknownHostException, IOException {
		String host = "192.168.0.110";
		int port = 22;
		//不只是创建了一个对象，还会尝试连接远程主机socket
		//如果能从流中读取到服务器的ssh2的版本信息，就说明可以进行ssh2连接
		Socket socket = new Socket(host, port);
		
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		String str = "nihao\n";
		byte[] bytes = str.getBytes("UTF-8");
		
		out.write(bytes);
		
		byte[] bt = new byte[1024*2*10];
		int i =0;
		while(true){
			int read = in.read();
			bt[i] = (byte) read;
			if(read == 10){
				break;
			}
			i++;
		}
		System.out.println(new String(bt));
	}
	
	
	
	
}
