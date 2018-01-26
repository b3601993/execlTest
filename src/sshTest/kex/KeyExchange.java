package sshTest.kex;

import sshTest.tran.Session;

public abstract class KeyExchange {

	public abstract void init(Session session, byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception;

}
