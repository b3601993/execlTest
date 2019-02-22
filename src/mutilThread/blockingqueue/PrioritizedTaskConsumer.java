package mutilThread.blockingqueue;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PrioritizedTaskConsumer implements Runnable {

	private PriorityBlockingQueue<Runnable> queue;
	
	public PrioritizedTaskConsumer(PriorityBlockingQueue<Runnable> queue) {
		super();
		this.queue = queue;
	}
	
	@Override
	public void run() {

		try {
			while(!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(1000);
				queue.take().run();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished PrioritizedTaskConsumer.");
	}

}
