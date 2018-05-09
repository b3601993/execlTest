package serialization;

import utils.SerializationUtil;

public class SerializationTest {

	public static void main(String[] args) {
		String fileName = "employee.ser";
		SerializableBean sb = new SerializableBean();
		sb.setId(100);
		sb.setName("gogoal");
		sb.setSalary(5000);
		
		SerializationUtil.serialize(sb, fileName);
		
//		SerializableBean ssb = (SerializableBean)SerializationUtil.deserialize(fileName);
//		System.out.println(ssb);
	}
}
