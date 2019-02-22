package test;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class regexTest2 {

	public static void main(String[] args) {
		String xx = "a1anda2anda11anda21<10anda111a";
		String aa = "a1=10andb==10andc<=2andd>=10ande=12andf2!=10";
		
		
		Pattern app = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
		
		
	}
	
}
