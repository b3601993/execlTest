package sshTest.tran;


public class Packet {

	Buffer buffer;

	public Packet(Buffer buffer) {
		this.buffer = buffer;
	}

	public void reset() {
		buffer.index = 5;
	}
	
	
}
