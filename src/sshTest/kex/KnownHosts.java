package sshTest.kex;

import java.util.concurrent.CopyOnWriteArrayList;

import sshTest.JSchException;
import sshTest.Jsch;

public class KnownHosts implements HostKeyRepository {

	private static final String _known_hosts = "known_hosts";
	
	private Jsch jsch = null;
	private MAC hmacsha1 = null;
	private CopyOnWriteArrayList<String> pool = null;
	
	
	public KnownHosts(Jsch jsch) {
		super();
		this.jsch = jsch;
		this.hmacsha1 = getHMACSHA1();
		pool = new CopyOnWriteArrayList<>();
	}
	
	
	private MAC getHMACSHA1() {
		if (hmacsha1 == null) {
			try {
				Class<?> c = Class.forName(jsch.getConfig("hmac-sha1"));
				hmacsha1 = (MAC) (c.newInstance());
			} catch (Exception e) {
				System.err.println("hmacsha1: " + e);
			}
		}
		return hmacsha1;
	}


	public HostKey createHashedHostKey(String host, byte[] key) throws JSchException {
		HashedHostKey hhk = new HashedHostKey(host, key);
		hhk.hash();
		return hhk;
	}
	
	
	class HashedHostKey extends HostKey{
		
		
		HashedHostKey(String host, byte[] key) throws JSchException {
			this(host, GUESS, key);
		}

		HashedHostKey(String host, int type, byte[] key) throws JSchException {
			this("", host, type, key, null);
		}

		HashedHostKey(String marker, String host, int type, byte[] key, String comment) throws JSchException {
			super(marker, host, type, key, comment);
		}
		
		void hash() {

		}
	}


	@Override
	public int check(String chost, byte[] k_S) {
		return 0;
	}
}
