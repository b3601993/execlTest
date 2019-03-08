package execlTest;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author 喻涛
 * @company 上海朝阳永续信息技术股份有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2019年3月8日上午9:58:50
 */
public class SetTest {

	
	public static void main(String[] args) throws InterruptedException {
		
		final ConcurrentSkipListSet<Message> messageSet = new ConcurrentSkipListSet<Message>(new MqComparable());
        
        
        	
		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				Message m1 = new Message();
				m1.setAccount_id("347209");
				m1.setApi_url("v1/goodcompany/get_user_list");
				m1.setMq_sub_type("list");
				m1.setMq_type("api");
				m1.setRequest_id("good");
				m1.setProduct_line("901");
				m1.setPro_type("8");
				m1.setParams("{'accountIds':'1'}");
				messageSet.add(m1);

				Message m2 = new Message();
				m2.setAccount_id("347209");
				m2.setApi_url("v1/goodcompany/get_user_list");
				m2.setMq_sub_type("list");
				m2.setMq_type("api");
				m2.setRequest_id("good");
				m2.setProduct_line("901");
				m2.setPro_type("8");
				m2.setParams("{'accountIds':'1'}");
				messageSet.add(m2);

				Message m3 = new Message();
				m3.setAccount_id("347211");
				m3.setApi_url("v1/goodcompany/get_user_list");
				m3.setMq_sub_type("list");
				m3.setMq_type("api");
				m3.setRequest_id("good");
				m3.setProduct_line("901");
				m3.setPro_type("8");
				m3.setParams("{'accountIds':'1'}");
				messageSet.add(m3);
				System.out.println("添加次数" + i);
			}
		}).start();
        
		
        new Thread(() -> {
        	for (int i = 0; i < 20; i++) {
        		 Message m1 = new Message();
     	        m1.setAccount_id("347209");
     	        m1.setApi_url("v1/goodcompany/get_user_list");
     	        m1.setMq_sub_type("list");
     	        m1.setMq_type("api");
     	        m1.setRequest_id("good");
     	        m1.setProduct_line("901");
     	        m1.setPro_type("8");
     	        m1.setParams("{'accountIds':'1'}");
 				Iterator<Message> iterator = messageSet.iterator();
     	        while (iterator.hasNext()) {
     				Message o = iterator.next();
     				String aId = o.getAccount_id();
     				String accountId = m1.getAccount_id();
     				String productLine = m1.getProduct_line();
                     if (accountId.equals(aId)&& productLine.equals(o.getProduct_line())) {
                    	 System.out.println("kais");
                     	iterator.remove();
                         /*if (messageSet.isEmpty()) {
                         	iterator.remove();
                         }*/
                         break;
                     }
     			}
    		}
		}).start() ;
        Thread.sleep(1000);
        System.out.println(messageSet.size() + "--" + messageSet);
		
	}
}
