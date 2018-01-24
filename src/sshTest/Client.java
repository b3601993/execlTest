package sshTest;

import sshTest.tran.Session;

public class Client {

	
	public static void main(String[] args) {
		
		Session session = new Session();
		session.connect();
	}
}
