package utils;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * 获取用户的工具对象
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yaomy
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc. All rights reserved.
 * @date 2016年11月2日 上午11:04:31
 */
public class ToolsUtil {

	/**
	 * 
	 * 方法描述 获取list数组对象
	 *
	 * @return
	 * 
	 * @author yaomy
	 * @date 2016年11月2日 上午11:04:58
	 */
	public static List<Object> getListObj(){
		List<Object> list = new ArrayList<Object>();
		return list;
	}
	/**
	 * 
	 * 方法描述 将原始值转换成想要的精度值
	 *
	 * @param scale 精度
	 * @param original 原始值
	 * @param divisor 除数
	 * @return
	 * 
	 * @author yaomy
	 * @date 2016年11月11日 上午10:36:10
	 */
	public static BigDecimal div(Double original, Double divisor,int scale){
		 if (scale < 0) {  
			 throw new IllegalArgumentException("The scale must be a positive integer or zero");  
		 }  
		 BigDecimal o =new BigDecimal(Double.toString(original));
		 BigDecimal d =new BigDecimal(Double.toString(divisor));
		 return o.divide(d, scale, BigDecimal.ROUND_HALF_UP);
	}
	/** 
	* 提供精确的乘法运算。 
	* @param v1 被乘数 
	* @param v2 乘数 
	* @return 两个参数的积 
	*/  
	public static BigDecimal mul(double v1, double v2, int scale) {  
	   BigDecimal b1 = new BigDecimal(Double.toString(v1));  
	   BigDecimal b2 = new BigDecimal(Double.toString(v2));  
//	   Double b3 = b1.multiply(b2).doubleValue();
//	   BigDecimal b4 = new BigDecimal(String.valueOf(b3));
	   BigDecimal multiply = b1.multiply(b2);
	   return multiply.setScale(scale, BigDecimal.ROUND_HALF_UP);  
	}  
	/**
	 * 
	 * 方法描述 取数据小数点后几位 （eg:0.00002 -- 得到的是0.00）
	 *
	 * @param v1 数据
	 * @param scale 精度
	 * @return
	 * 
	 * @author yaomy
	 * @date 2016年11月11日 下午1:30:19
	 */
	public static BigDecimal scal(double v1, int scale){
		 BigDecimal b1 = new BigDecimal(Double.toString(v1));  
		 return b1.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 保留有效位（eg:0.00002 -- 得到的是0.000020）
	 * 正数部分为0，保留有效位，否则保留小数位
	 * 
	 * @author yutao
	 * @return 
	 * @date 2016年11月14日下午1:27:28
	 */
	public static BigDecimal validScale(double v1, int scale){
		if (scale < 0) {  
			 throw new IllegalArgumentException("The scale must be a positive integer or zero");  
		}
		BigDecimal b = new BigDecimal(String.valueOf(v1));  
		/*BigDecimal divisor = BigDecimal.ONE;  
		MathContext mc = new MathContext(scale);
		b.divide(divisor, mc)*/
		return validOrFraction(b, scale);
	}
	
	/**
	 * 除法 （保留的是有效数字，而非有效的小数点）
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 * @author yutao
	 * @date 2016年11月14日下午2:01:31
	 */
	public static BigDecimal validDivi(double v1, double v2 , int scale){
		if (scale < 0) {  
			 throw new IllegalArgumentException("The scale must be a positive integer or zero");  
		}
		BigDecimal b1 = new BigDecimal(String.valueOf(v1));
		BigDecimal b2 = new BigDecimal(String.valueOf(v2));
		MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);
		
		return b1.divide(b2, mc);
	}
	
	/**
	 * 相除时保留2位
	 * 相除时，如果商的整数部分是0，则保留precision有效位
	 * 否则，就保留precision位小数位。<br>
	 * 例如：<table>
	 * <tr>
	 * <td>被除数</td>
	 * <td>除数</td>
	 * <td>结果</td>
	 * </tr>
	 * <tr><td>12341231.124125124D</td><td>10000</td><td>1234.12(保留小数)</td></tr>
	 * <tr><td>0.124125124D</td><td>10000</td><td>0.000012(有效位)</td></tr>
	 * </table>
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return
	 * @author yutao
	 * @date 2018年1月19日下午11:23:30
	 */
	public static BigDecimal diviValidOrFraction(double v1, double v2, int precision){
		if (precision < 0) {  
			 throw new IllegalArgumentException("The precision must be a positive integer or zero");  
		}
		BigDecimal b1 = new BigDecimal(String.valueOf(v1));
		BigDecimal b2 = new BigDecimal(String.valueOf(v2));
		//如果这里不写保留的小数位（eg:10）其默认就会保留一位小数
		//这样之后的判断就会出错
		BigDecimal divide = b1.divide(b2, 10, RoundingMode.HALF_UP);
		return validOrFraction(divide, precision);
	}
	
	
	/**
	 * 正数部分为0，保留有效位，否则保留小数位
	 * @param precision
	 * @param b1
	 * @param b2
	 * @return
	 * @author yutao
	 * @date 2018年1月19日下午1:28:29
	 */
	private static BigDecimal validOrFraction(BigDecimal divide, int precision) {
		
		Pattern p = Pattern.compile("-?(\\d+)(\\.*)(\\d*)");
		Matcher m = p.matcher(divide.toString());
		if(m.matches()){
			Long ll = Long.valueOf(m.group(1));
			//正数位为0，保留指定的有效位
			if(ll == 0){
				MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
				//保留指定的有效位
				return divide.divide(BigDecimal.ONE, mc);
			}
		}
		//保留指定小数位
		return divide.setScale(precision, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 相乘 正数部分为0，就保留有效位，否则保留小数位
	 * @param v1
	 * @param v2
	 * @param scale
	 * @author yutao
	 * @return 
	 * @date 2016年11月14日下午2:52:33
	 */
	public static BigDecimal validMuli(double v1, double v2, int scale){
		if (scale < 0) {  
			 throw new IllegalArgumentException("The scale must be a positive integer or zero");  
		}
		BigDecimal b1 = new BigDecimal(String.valueOf(v1));
		BigDecimal b2 = new BigDecimal(String.valueOf(v2));
		
		BigDecimal multiply = b1.multiply(b2);
		
//		MathContext mc = new MathContext(scale);
		return validOrFraction(multiply, scale);
	}
	
	/**
	 * 相加
	 * @param b1
	 * @param b2
	 * @return
	 * @author yutao
	 * @date 2017年4月28日下午5:41:15
	 */
	public static int addBig(Long v1, long v2){
		BigDecimal b1 = new BigDecimal(String.valueOf(v1));
		BigDecimal b2 = new BigDecimal(String.valueOf(v2));
		BigDecimal b3 = b1.add(b2);
		return b3.intValue();
	}
	
	
	
	/**
	 * 匹配是否包含数字
	 * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
	 * @return
	 * @author yutao
	 * @date 2016年11月14日下午7:41:22
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");// 该正则表达式可以匹配所有的数字
		String bigStr;
		try {
			bigStr = new BigDecimal(str).toString();
		} catch (Exception e) {
			return false;
		}

		Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
    /**
     * 将数字格式化为中间有逗号的形式 21024.00  -> 21,024.00
     * @param value 数字
     * @return
     * @author yutao
     * @date 2018年1月4日上午9:43:43
     */
    public static String numberOfCommaFormat(Double value, int digit){
    	if(value == null){
    		return null;
    	}
    	String style = "";
    	if(digit == 4){
    		style = "#,##0.00##";
    	}else{
    		style = "#,##0.00";
    	}
    	
    	return numberOfCommaFormat(style, value);
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
    	DecimalFormat df = new DecimalFormat();
    	df.applyPattern(style);
    	return df.format(value);
    }
}
