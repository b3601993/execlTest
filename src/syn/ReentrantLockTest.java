package syn;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author 喻涛
 * @company 上海朝阳永续信息技术股份有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2019年2月26日下午3:41:38
 */
public class ReentrantLockTest extends Thread {
	
	
	public static ReentrantLock lock = new ReentrantLock();
	
	public static int i = 0;
	
	/**
	 * 
	 */
	public ReentrantLockTest(String name) {
		super.setName(name);
	}

	@Override
	public void run() {
		
		for (int i = 0; i < 10000000; i++) {
			lock.lock();
			
			System.out.println(this.getName() + " " + i);
			i++;
			lock.unlock();
		}
		
	}
	
	
	public static void main(String[] args) {
		
		ReentrantLockTest test1 = new ReentrantLockTest("thread1");
		ReentrantLockTest test2 = new ReentrantLockTest("thread2");
		
		
		
		
	}

}
