package execlTest;

import java.util.Comparator;

/**
 *
 * Message 类型的比较器
 * @version 1.0
 * @since JDK1.7
 * @author 喻涛
 * @company 上海朝阳永续信息技术股份有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2019年3月8日上午11:57:52
 */
public class MqComparable implements Comparator<Message> {

	@Override
	public int compare(Message o1, Message o2) {
		
		int hashCode1 = o1.hashCode();
		int hashCode2 = o2.hashCode();
		if (o1.equals(o2) && hashCode1 == hashCode2) {
			return 0;
		}else if (hashCode1 > hashCode2) {
			return 1;
		}else {
			return -1;
		}
	}
}
