package sshTest.tran;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Session {

	static byte[] buffer = new byte[1024*10*2];
	
	public void connect(){
		
		try {
			Socket socket = new Socket("192.168.0.110", 22);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			int read=0;
			while(true){
				 read = in.read(buffer);
				if(read > 0){
					break;
				}
			}
			System.out.println(new String(buffer, 0, read));
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
