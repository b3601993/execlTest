package mutilThread.blockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {

	private static int counter = 1;
	private final int priority;
	private Random random = new Random(47);
	private final int id=counter++;
	public static List<PrioritizedTask> sequence = new ArrayList<>();
	
	public PrioritizedTask(int priority) {
		super();
		this.priority = priority;
	}
	
	
	@Override
	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(random.nextInt(250));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(this);
	}

	
	@Override
	public int compareTo(PrioritizedTask o) {
		
		int val = this.priority - o.priority;
		
		return val<0?1:(val>0?-1:0);
	}


	@Override
	public String toString() {
		return String.format("p=[%1$-3d]", priority) + ", ID=" + id;
	}
	
	
	public static class EndFlagTask extends PrioritizedTask{

		private ExecutorService exec;
		
		public EndFlagTask(ExecutorService exec) {
			super(-1);
			this.exec = exec;
		}

		@Override
		public void run() {
			System.out.println(this + " calling shutdownNow()");
			exec.shutdownNow();
		}
	}
	
}
