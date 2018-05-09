package serialization;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Data implements Serializable {

	private static final long serialVersionUID = 7432678381028990496L;
	
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public Data(String d){
		data = d;
	}

	@Override
	public String toString() {
		return "Data{data="+data+"}";
	}
	
	//保留主类的状态
	private static class DataProxy implements Serializable{
		
		private static final long serialVersionUID = 5127543494922934921L;
		
		private String dataProxy;
		private static final String prefix = "ABC";
		private static final String suffix = "DEFG";
		
		public DataProxy(Data d){
			this.dataProxy = prefix + d.data + suffix;
		}
		
		private Object readResolve() throws InvalidObjectException {
			if(dataProxy.startsWith(prefix) && dataProxy.endsWith(suffix)){
				return new Data(dataProxy.substring(3, dataProxy.length()-4));
			}else{
				throw new InvalidObjectException("data corrupted");
			}
		}
	}
	//
	private Object writeReplace(){
		return new DataProxy(this);
	}
	
	private void readObject(ObjectInputStream ois) throws InvalidObjectException{
		throw new InvalidObjectException("Proxy is not used, something fishy");
	}
}
