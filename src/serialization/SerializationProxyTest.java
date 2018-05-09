package serialization;

import utils.SerializationUtil;

public class SerializationProxyTest {

	
	public static void main(String[] args) {
		String fileName = "data.ser";
		Data data = new Data("Pankaj");
		
		SerializationUtil.serialize(data, fileName);
		
		Data newData = (Data)SerializationUtil.deserialize(fileName);
		System.out.println(newData);
	}
}
