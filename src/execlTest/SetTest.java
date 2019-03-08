package execlTest;

import java.util.HashSet;
import java.util.Set;
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

	
	public static void main(String[] args) {
		
		ConcurrentSkipListSet<Message> messageSet = null;
		
        if (messageSet == null) {
            messageSet = new ConcurrentSkipListSet<Message>();
        }
        
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
        
        Set<Message> ss = new HashSet<>();
        ss.add(m1);
        ss.add(m2);
        System.out.println(ss);
        messageSet.add(m2);
		
	}
}
