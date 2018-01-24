package test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

public class ModifierTest {

	static Hashtable<String,  String> config = new Hashtable<>();
	
	
	public static void main(String[] args) {
		Class<?> clazz = ModifierTest.class;
		
		Field[] fields = clazz.getDeclaredFields();
		for(Field s : fields){
			System.out.println(s.getName() + "->" + Modifier.toString(s.getModifiers()));
		}
	}
}
