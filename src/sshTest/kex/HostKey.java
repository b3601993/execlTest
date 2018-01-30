package sshTest.kex;

import sshTest.JSchException;

public class HostKey {

	public static final int GUESS = 0;
	
	public static final int SSHDSS = 1;
	public static final int SSHRSA = 2;
	public static final int ECDSA256 = 3;
	public static final int ECDSA384 = 4;
	public static final int ECDSA521 = 5;
	static final int UNKNOWN = 6;
	
	protected String marker;
	protected String host;
	protected int type;
	protected byte[] key;
	protected String comment;
	
	
	public HostKey(String host, byte[] key) throws JSchException {
		this(host, GUESS, key);
	}

	public HostKey(String host, int type, byte[] key) throws JSchException {
		this(host, type, key, null);
	}

	public HostKey(String host, int type, byte[] key, String comment) throws JSchException {
		this("", host, type, key, comment);
	}

	public HostKey(String marker, String host, int type, byte[] key, String comment) throws JSchException {
		this.marker = marker;
		this.host = host;
		if (type == GUESS) {
			if (key[8] == 'd') {
				this.type = SSHDSS;
			} else if (key[8] == 'r') {
				this.type = SSHRSA;
			} else if (key[8] == 'a' && key[20] == '2') {
				this.type = ECDSA256;
			} else if (key[8] == 'a' && key[20] == '3') {
				this.type = ECDSA384;
			} else if (key[8] == 'a' && key[20] == '5') {
				this.type = ECDSA521;
			} else {
				throw new JSchException("invalid key type");
			}
		} else {
			this.type = type;
		}
		this.key = key;
		this.comment = comment;
	}
	
}
