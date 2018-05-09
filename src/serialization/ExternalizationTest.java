package serialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ExternalizationTest {

	
	public static void main(String[] args) {
        String fileName = "person.ser";
        Person person = new Person();
        person.setId(1);
        person.setName("Pankaj");
        person.setGender("Male");
        //序列化
        try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(person);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        //反序列化
        try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Person p = (Person)ois.readObject();
			ois.close();
			System.out.println("Person Object Read="+p);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}
}
