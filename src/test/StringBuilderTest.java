package test;

public class StringBuilderTest {

	public static void main(String[] args) {
		String result = "bb";
		for(int i=0; i<20;i++){
			result += "a";
			new StringBuilder().append(result);
		}
		System.out.println(result);
	}
}
