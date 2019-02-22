package syn;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2019年1月9日下午3:27:26
 */
public class Sync implements Runnable {

	
	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		
		if (StringUtils.startsWith(name, "A")) {
			async();
		}else if (StringUtils.startsWith(name, "B")) {
//			sync();
//			syncObj();
			syncClass();
		}else if (StringUtils.startsWith(name, "C")) {
			syncMethod();
		}
	}

	/**
	 * 
	 * @author yutao
	 * @date 2019年1月9日下午3:37:04
	 */
	private void async() {
		System.out.println(Thread.currentThread().getName() + "_Async_Start: " + new Date());
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println(Thread.currentThread().getName() + "_Async_End: " + new Date());
	}

	/**
	 * 
	 * @author yutao
	 * @date 2019年1月9日下午3:37:01
	 */
	private void sync() {
//		System.out.println(Thread.currentThread().getName() + "_Sync: " + new Date());
		synchronized (this) {
			try {
				System.out.println(Thread.currentThread().getName() + "_Sync_Start: " + new Date());
				Thread.sleep(2000);
				System.out.println(Thread.currentThread().getName() + "_Sync_End: " + new Date());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void syncObj() {
//		System.out.println(Thread.currentThread().getName() + "_Sync: " + new Date());
		synchronized (new Message().getMessageFlag().intern()) {
			try {
				System.out.println(Thread.currentThread().getName() + "_Sync_Start: " + new Date());
				Thread.sleep(2000);
				System.out.println(Thread.currentThread().getName() + "_Sync_End: " + new Date());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void syncClass() {
//		System.out.println(Thread.currentThread().getName() + "_Sync: " + new Date());
		synchronized (Sync.class) {
			try {
				System.out.println(Thread.currentThread().getName() + "_Sync_Start: " + new Date());
				Thread.sleep(2000);
				System.out.println(Thread.currentThread().getName() + "_Sync_End: " + new Date());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @author yutao
	 * @date 2019年1月9日下午3:36:55
	 */
	private synchronized static void syncMethod() {
//		System.out.println(Thread.currentThread().getName() + "_SyncMethod: " + new Date());
        try {
            System.out.println(Thread.currentThread().getName() + "_Sync_method_Start: " + new Date());
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + "_Sync_method_End: " + new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
