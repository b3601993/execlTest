package test;

public class test1 {
	public static void main(String[] args) {
		
		String classCode = "ab";
		
		if(classCode.length()==2){
			System.out.println(classCode.length());
		}
		
		String[] codeSplit = classCode.split("&");
		for(String code: codeSplit){
			System.out.println(code);
		}
	}
}
