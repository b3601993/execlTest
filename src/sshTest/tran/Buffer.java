package sshTest.tran;

public class Buffer {

	final byte[] tmp = new byte[4];
	
	byte[] buffer;
	
	int index;
	
	int s;
	
	public Buffer(int size){
		buffer = new byte[size];
		index=0;
		s=0;
	}
	
	public Buffer(){
		this(1024 * 10 * 2);
	}

	public void putByte(byte foo) {
		buffer[index++] = foo;
	}

	public void skip(int n) {
		index += n;
	}
	
	/**
	 * 保存字符串字节数组
	 * @param foo
	 * @author yutao
	 * @date 2018年1月26日下午4:25:07
	 */
	public void putString(byte[] foo) {
		putString(foo, 0, foo.length);
	}
	
	/**
	 * 保存字符串字节数组时，要先保存数组长度，再保留具体数据
	 * @param foo
	 * @param begin
	 * @param length
	 * @author yutao
	 * @date 2018年1月26日下午4:23:01
	 */
	private void putString(byte[] foo, int begin, int length) {
		putInt(length);
		putByte(foo, begin, length);
	}

	/**
	 * 
	 * @param foo
	 * @param begin
	 * @param length
	 * @author yutao
	 * @date 2018年1月26日下午4:17:18
	 */
	private void putByte(byte[] foo, int begin, int length) {
		System.arraycopy(foo, begin, buffer, index, length);
		index += length;
	}

	/**
	 * 把数据处理成大端字节序
	 * @param n
	 * @author yutao
	 * @date 2018年1月26日下午4:12:48
	 */
	public void putInt(int n) {
		tmp[0] = (byte) (n >>> 24);
		tmp[1] = (byte) (n >>> 16);
		tmp[2] = (byte) (n >>> 8);
		tmp[3] = (byte) (n);
		
		System.arraycopy(tmp, 0, buffer, index, 4);
		index += 4;
	}

	public void setOffSet(int n) {
		s = n;
	}
	
	/**
	 * 获取有效数据长度
	 * @return
	 * @author yutao
	 * @date 2018年1月26日下午4:37:26
	 */
	public int getLength() {
		return index - s;
	}

	/**
	 * 重置--数组起始和已使用的最后的位置
	 * 
	 * @author yutao
	 * @date 2018年1月26日下午4:37:22
	 */
	public void reset() {
		index = 0;
		s = 0;
	}
}
