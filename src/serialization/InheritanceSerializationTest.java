package serialization;

import utils.SerializationUtil;

public class InheritanceSerializationTest {
	//看一下我们是否能够从序列化的数据中获取父类的状态
	public static void main(String[] args) {
		String fileName = "subClass.ser";
		SubClass sub = new SubClass();
		sub.setId(10);
		sub.setName("yutao");
		sub.setValue("Data");
		
		SerializationUtil.serialize(sub, fileName);
		
		SubClass subNew = (SubClass)SerializationUtil.deserialize(fileName);
		System.out.println("SubClass read = "+subNew);
	}
}
