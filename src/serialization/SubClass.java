package serialization;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SubClass extends SuperClass implements Serializable, ObjectInputValidation{

	private static final long serialVersionUID = -865568373672414368L;
	
	private String name;
	
	@Override
	public String toString() {
		return "SubClass{id="+getId()+",value="+getValue()+",name="+getName()+"}";
	}

	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException{
		ois.defaultReadObject();
		
		setId(ois.readInt());
		setValue(ois.readObject().toString());
	}
	
	
	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
		oos.writeInt(getId());
		oos.writeObject(getValue());
	}

	//校验数据
	@Override
	public void validateObject() throws InvalidObjectException {
		//校验对象
		if(name == null || "".equals(name)){
			throw new InvalidObjectException("name can't be null or empty");
		}
		if(getId() < 0){
			throw new InvalidObjectException("ID can't be negative or zero");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
