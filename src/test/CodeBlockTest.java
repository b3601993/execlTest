package test;

public class CodeBlockTest {

	
	public CodeBlockTest() {
		System.out.println("我是构造函数");
	}
	
	
	public static void main(String[] args) {
		System.out.println("普通语句");
		{
			System.out.println("我是普通代码块");
		}
		new CodeBlockTest();
		String.valueOf(1);
		
	}
	
	
	static {
		System.out.println("我是静态代码块");
	}
	//主调类的静态代码块、父类静态代码 
	
}
