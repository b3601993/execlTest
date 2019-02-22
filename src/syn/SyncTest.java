package syn;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2019年1月9日下午3:42:27
 */
public class SyncTest {

	public static void main(String[] args) {
		
		Sync syncThread = new Sync();
		
		/*Thread A_thread1 = new Thread(syncThread, "A_thread1");
		Thread A_thread2 = new Thread(syncThread, "A_thread2");
		Thread B_thread1 = new Thread(syncThread, "B_thread1");
		Thread B_thread2 = new Thread(syncThread, "B_thread2");
		Thread C_thread1 = new Thread(syncThread, "C_thread1");
		Thread C_thread2 = new Thread(syncThread, "C_thread2");*/
		
		Thread A_thread1 = new Thread(new Sync(), "A_thread1");
		Thread A_thread2 = new Thread(new Sync(), "A_thread2");
		Thread B_thread1 = new Thread(new Sync(), "B_thread1");
		Thread B_thread2 = new Thread(new Sync(), "B_thread2");
		Thread C_thread1 = new Thread(new Sync(), "C_thread1");
		Thread C_thread2 = new Thread(new Sync(), "C_thread2");
		
		A_thread1.start();
		A_thread2.start();
		B_thread1.start();
		B_thread2.start();
		C_thread1.start();
		C_thread2.start();
		
	}
	
}
