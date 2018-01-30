package sshTest.kex;

public interface HostKeyRepository {

	int CHANGED = 2;
	int NOT_INCLUDED = 1;
	int OK = 0;

	int check(String chost, byte[] k_S);

}
