package test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class SignTest {

	// 每3位中间添加逗号的格式化显示
	public static String getCommaFormat(BigDecimal value) {
		return getFormat("##;-##.00", value);
	}

	// 自定义数字格式方法
	public static String getFormat(String style, BigDecimal value) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(style);// 将格式应用于格式化器
		return df.format(value.doubleValue());
	}

	public static void main(String[] args) {
//		System.out.println(getCommaFormat(new BigDecimal(-24113.668)));
//		System.out.println(numberOfCommaFormat("##;-##.00", -24113.668D));
		
		System.out.println(numberOfCommaFormat("#,-123123132##0.0#;#))))))))", 11D));
		System.out.println(numberOfCommaFormat("#,##0.0#;(#)））））））)", -11D));
		System.out.println(numberOfCommaFormat("#,##0.0#;#,##0.0#", -11D));
		System.out.println(numberOfCommaFormat("#,##0.0#;(#)", 11D));
		System.out.println(numberOfCommaFormat("#,##0.0#;(#,##0.0#)", 11D));
		//定义正负数模板,记得要用分号隔开   ==  == "##,####,####". 
		System.out.println(numberOfCommaFormat("#,##,###,####", 24112311233.668D));
		System.out.println(numberOfCommaFormat("######,####", 24112311233.668D));
		System.out.println(numberOfCommaFormat("##,####,###", 1124112311233.668D));
//		System.out.println(numberOfCommaFormat("0.0;'@'-#.0", 33D));
//		System.out.println(numberOfCommaFormat("0.0;'@'-#.0", -33D));
	     //-----------------------------------------------
	    
	    //综合运用，正负数的不同前后缀
	    String pattern="###,###.##正数后缀;-###,###.##负数后缀";
	    System.out.println(numberOfCommaFormat(pattern, -1223233.456));
	    System.out.println(numberOfCommaFormat("我是前缀##0.#####E0我是后缀", 12323123 ));
	    
	}

	
    /**
     * 将数字格式化为中间有逗号的形式
     * @param style 你要格式化的格式 比如 ,###.00#；21024.00  -> 21,024.00
     * @param value 数字
     * @return
     * @author yutao
     * @date 2018年1月4日上午9:43:49
     */
    public static String numberOfCommaFormat(String style, Double value){
    	if(value == null){
    		return null;
    	}
    	
		 /*NumberFormat f = NumberFormat.getInstance();
    	 if (f instanceof DecimalFormat) {
    		 DecimalFormat df = (DecimalFormat) f;
    		 df.applyPattern(style);
    	    	return df.format(value);
    	 }
    	return null;*/
    	DecimalFormat df = new DecimalFormat();
    	
    	df.applyPattern(style);
    	return df.format(value);
    }
    public static String numberOfCommaFormat(String style, int value){
    	DecimalFormat df = new DecimalFormat();
    	df.applyPattern(style);
    	return df.format(value);
    }
}
