package mutilThread.blockingqueue;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class PrioritizedTaskProducer implements Runnable{

	private Queue<Runnable> queue;
	private ExecutorService exec;
	
	public PrioritizedTaskProducer(Queue<Runnable> queue, ExecutorService exec) {
		this.queue = queue;
		this.exec = exec;
	}

	@Override
	public void run() {
		try {
			
			for(int i=0; i<6; i++){
				queue.add(new PrioritizedTask(i));
			}
			
			
			for(int i=0; i<6; i++){
				TimeUnit.MILLISECONDS.sleep(250);
				queue.add(new PrioritizedTask(9));
			}
			
			queue.add(new PrioritizedTask(0));
			queue.add(new PrioritizedTask(0));
			
			queue.add(new PrioritizedTask.EndFlagTask(exec));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished PrioritizedTaskProducer.");
	}

	
	
	
}
