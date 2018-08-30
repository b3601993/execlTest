package mutilThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest extends Thread {

	@Override
	public void run() {
		super.run();
	}

	
//	ExecutorService
	public static void main(String[] args) {
		
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		
		
		
//		threadPool.execute(command);
	}
}
