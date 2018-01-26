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

	public void getByte(byte[] array, int begin, int length) throws IOException {
		/*do{
			int read = in.read(array, begin, length);
			begin += read;
			length -= read;
		}while(length > 0);*/
		int read = in.read(array, begin, length);
		while(read > 0){
			begin += read;
			length -= read;
			read = in.read(array, begin, length);
		}
	}
	
	
}
