package execlTest;

import org.apache.commons.lang3.StringUtils;

public class Message extends MessageType{

	/**
	 * 前端使用
	 */
	private String request_id;
	
	/**
	 * 用户账号
	 */
	private String account_id;
	/**
	 * 用户名
	 */
	private String account_name;
	
	/**
	 * 订阅状态 0:取消订阅  1：订阅  2：更新用户权限
	 */
	private String sub_status;
	
	/**
	 * 客户端标识
	 */
	private String client_flag;
	
	/**
	 * 权限
	 */
	private String pro_type;
	
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
	 * eg:v1/treasure/list_clue
	 */
	private String api_url;
	/**
	 * 接口参数
	 */
	private String params;
	
	/**
	 * 用户终端的版本号
	 */
	private String version;
	
	/**
	 * 产品线
	 */
	private String product_line;
	
	/**
	 * 哈希码值
	 */
	private volatile int hasCode = 1;
	
	public Message(){};
	public Message(String request_id, String account_id, String account_name, String sub_status, String client_flag,
			String pro_type, String mq_type, String mq_sub_type, String user_type, String api_url,
			String params, String version, String product_line, int hasCode) {
		super();
		this.request_id = request_id;
		this.account_id = account_id;
		this.account_name = account_name;
		this.sub_status = sub_status;
		this.client_flag = client_flag;
		this.pro_type = pro_type;
		this.mq_type = mq_type;
		this.mq_sub_type = mq_sub_type;
		this.user_type = user_type;
		this.api_url = api_url;
		this.params = params;
		this.version = version;
		this.product_line = product_line;
		this.hasCode = hasCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
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

	public String getApi_url() {
		return api_url==null?null:api_url.intern();
	}

	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getRequest_id() {
		return request_id==null?null:request_id.intern();
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getAccount_id() {
		return account_id.intern();
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getSub_status() {
		return sub_status;
	}

	public void setSub_status(String sub_status) {
		this.sub_status = sub_status;
	}

	public String getClient_flag() {
		return client_flag;
	}

	public void setClient_flag(String client_flag) {
		this.client_flag = client_flag;
	}

	public String getPro_type() {
		return pro_type;
	}

	public void setPro_type(String pro_type) {
		this.pro_type = pro_type;
	}

	public String getProduct_line() {
		return product_line;
	}

	public void setProduct_line(String product_line) {
		this.product_line = product_line;
	}
	
	/**
	 * 
	 * 校验
	 * @author yutao
	 * @throws Exception 
	 * @date 2018年11月1日下午3:28:27
	 */
	public void valid() throws Exception{
			
		if(StringUtils.isBlank(request_id)){
			throw new Exception("requestId 不能为空！");
		}
		if(StringUtils.isBlank(product_line)){
			throw new Exception("productLine 不能为空！");
		}
		if(StringUtils.isBlank(account_id)){
			throw new Exception("accountId 不能为空！");
		}
		if(StringUtils.isBlank(sub_status)){
			throw new Exception("subStatus 不能为空！");
		}
		if(StringUtils.isBlank(client_flag)){
			throw new Exception("clientFlag 不能为空！");
		}
		if(StringUtils.isBlank(mq_type)){
			throw new Exception("mqType 不能为空！");
		}
		if(StringUtils.isBlank(mq_sub_type)){
			throw new Exception("mqSubType 不能为空！");
		}
		
		if(StringUtils.isBlank(pro_type)){
			throw new Exception("proType 不能为空！");
		}
	}

	@Override
	public String toString() {
		
		return "request_id=" + request_id + ", product_line=" + product_line + ", account_id=" + account_id
				+ ", sub_status=" + sub_status + ", client_flag=" + client_flag + ", mq_type=" + mq_type + ", mq_sub_type="
				+ mq_sub_type + ", pro_type=" + pro_type + ", params=" + params + ", api_url=" + api_url;
	}
	
	@Override
	public int hashCode() {
		
		int result = hasCode;
		if(result == 1){
			result = 31 * result + ((mq_sub_type == null) ? 0 : mq_sub_type.hashCode());
			result = 31 * result + ((mq_type == null) ? 0 : mq_type.hashCode());
			result = 31 * result + ((request_id == null) ? 0 : request_id.hashCode());
			result = 31 * result + ((account_id == null) ? 0 : account_id.hashCode());
			result = 31 * result + ((product_line == null) ? 0 : product_line.hashCode());
			hasCode = result;
		}
		return result;
	}
	
	public String getMessageFlag(){
		return "mq_type=" + mq_type + "|mq_sub_type=" + mq_sub_type+ "|request_id=" + request_id+ "|account_id=" + account_id+"|product_line=" + product_line;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this){
			return true;
		}else if(obj == null){
			return false;
		}else if(!(obj instanceof Message)){
			return false;
		}
		Message mo = (Message)obj;
		return this.getMessageFlag().equals(mo.getMessageFlag());
		
	}
}
