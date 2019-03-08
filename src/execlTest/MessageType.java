package execlTest;


public class MessageType {
	
	
	/**
	 * 消息类型
	 */
	private String mq_type;
	
	/**
	 * 消息子类型
	 */
	private String mq_sub_type;
	
	/**
	 * 消息所属类型
	 */
	private String user_type;

	/**
	 * 权限
	 */
	private String pro_type;


	public String getPro_type() {
		return pro_type;
	}

	public void setPro_type(String pro_type) {
		this.pro_type = pro_type;
	}

	public String getMq_type() {
		return mq_type;
	}

	public void setMq_type(String mq_type) {
		this.mq_type = mq_type;
	}

	public String getMq_sub_type() {
		return mq_sub_type;
	}

	public void setMq_sub_type(String mq_sub_type) {
		this.mq_sub_type = mq_sub_type;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	
	/**
	 * 哈希码值
	 */
	private volatile int hasCode = 1;

	public MessageType() {}

	@Override
	public int hashCode() {
		
		int result = hasCode;
		if(result == 1){
			result = 31 * result + ((mq_sub_type == null) ? 0 : mq_sub_type.hashCode());
			result = 31 * result + ((mq_type == null) ? 0 : mq_type.hashCode());
			hasCode = result;
		}
		return result;
	}
	
	public String getMessageFlag(){
		return "mq_type=" + mq_type + "|mq_sub_type=" + mq_sub_type;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this){
			return true;
		}else if(obj == null){
			return false;
		}else if(!(obj instanceof MessageType)){
			return false;
		}
		MessageType mo = (MessageType)obj;
		return this.getMessageFlag().equals(mo.getMessageFlag());
		
	}
	
}
