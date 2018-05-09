package test;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class regexTest {

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
		
		byte a = -9;
		System.out.println(Integer.toHexString(a));
		
		
		Pattern app = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
		
		
        double taa = -19162431.1254;
        String ta = "-19162431.1254";
        String b = "-19162431a1254";
        String c = "中文";
        String ct="-00.";
        String cy = "1";
        String cyc = "1.1";
		System.out.println(isNumericzidai(Double.toString(taa)));
		System.out.println(isNumericzidai(ta));
		System.out.println(isNumericzidai(b));
		System.out.println(isNumericzidai(c));
		System.out.println(isNumericzidai(ct));
		System.out.println(isNumericzidai(cy));
		System.out.println(isNumericzidai(cyc));
		
		System.out.println(StringUtils.isNumeric("19162431.14"));
		System.out.println("=======");
		System.out.println(NumberUtils.isNumber(ta));
		System.out.println(NumberUtils.isNumber(b));
		System.out.println(NumberUtils.isNumber(c));
		System.out.println(NumberUtils.isNumber("-00."));
		System.out.println(NumberUtils.isNumber(cy));
		System.out.println(NumberUtils.isNumber(cyc));
	}
	
	public static boolean isNumericzidai(String str) {
		// 该正则表达式可以匹配所有的数字 包括负数
		Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
		String bigStr;
		try {
			bigStr = new BigDecimal(str).toString();
		} catch (Exception e) {
			return false;//异常 说明包含非数字。
		}

		Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
