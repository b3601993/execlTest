package sshTest;

import java.util.concurrent.ConcurrentHashMap;

public class Jsch {

	static int SSH_MSG_KEXINIT=20;
	static public ConcurrentHashMap<String,  String> config = new ConcurrentHashMap<>();
	
	{
		//serverToClient
		config.put("cipher.s2c", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
		//clientToServer
		config.put("cipher.c2s", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
	}
	
}
