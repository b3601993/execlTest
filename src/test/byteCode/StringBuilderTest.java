package test.byteCode;

public class StringBuilderTest {

	public static void main(String[] args) {
		/*String result = "bb";
		for(int i=0; i<20;i++){
			result += "a";
			new StringBuilder().append(result);
		}
		System.out.println(result);*/
		
		String v1 = "abc";
		String v2 = "a";
		String v3 = "bc";
		
		System.out.println(v1 == (v2+v3));
	}
}
