package sshTest.tran;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IO {

	private InputStream in;
	
	private OutputStream out;
	
	private OutputStream out_ext;
	
	
	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public OutputStream getOut_ext() {
		return out_ext;
	}

	public void setOut_ext(OutputStream out_ext) {
		this.out_ext = out_ext;
	}
	
	/**
	 * 读取指定长度的字节到指定的字节数组中
	 * @param array
	 * @param begin
	 * @param length
	 * @throws IOException
	 */
	public void getByte(byte[] array, int begin, int length) throws IOException {
		do{
			int read = in.read(array, begin, length);
			if(read < 0){
				throw new IOException("End of IO Stream Read");
			}
			begin += read;
			length -= read;
		}while(length > 0);
	}
	
	
}
