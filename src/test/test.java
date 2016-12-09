package test;
public class test {

	public static void main(String[] args) {
		String xx = "a1anda2anda11anda21<10anda111a";
		String aa = "a1=10andb==10andc<=2andd>=10ande=12andf2!=10";
		
		String x = "01_03_sfd2ef_10";
//		String reg = "a(\\d\\D)";
		String a1 = "a1(\\D)";
		String a11 = "a11(\\D)";
		String a111 = "a111(\\D)";
		String reg = "(\\w|\\d)=(\\d)";
		
//		System.out.println (xx.replaceAll (reg, "a0"+"$1"));
		System.out.println (xx.replaceAll (a1, "a1<5"+"$1"));
		System.out.println (xx.replaceAll (a11, "a11>12"+"$1"));
		System.out.println (xx.replaceAll (a111, "a111>10"+"$1"));
		System.out.println (aa.replaceAll (reg, "$1"+"=="+"$2"));
		
	}

}
