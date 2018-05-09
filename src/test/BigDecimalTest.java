package test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigDecimalTest {

	
	public static void main(String[] args) {
		double d1 = 12341231.124125124D;//11225453.12D
		double dd = 0.124125124D;
		int d2 = 123;
		Double d3 = 12.20D, d17=-12.20D;
		double d4 = 5D, d16=-5D;
		double d5 = 56D, d15= -56D;
		double d6 = 110, d14=-110;
		double d7 = 12.123, d8=1.234, d9=1.1, d10=0.1231, d11=123124125D, d12=0000.000, d13=-1.1;
		
		double[] dList = {12341231.124125124D, 123,0.124125124D,
						  12.20D, -12.20D, 5D, -5D, 56D, -56D, 110, -110,
						  12.123, 1.234, 1.1, 0.1231, 123124125D, 0000.000, -1.1};
		/*for(Double d : dList){
			System.out.println(validDivi(d, 10000, 2));
		}*/
		
		String aa = "10000.00";
//		System.out.println(new BigDecimal(aa));
		
		String bigStr = new BigDecimal("-0.12123").toString();
		System.out.println(bigStr);
		
//		System.out.println(validDivi(d1, 10000, 2));
		
		/*validDivi2(d1);
		validDivi2(d2);
		validDivi2(d3);
		validDivi2(d4);
		validDivi2(d5);
		validDivi2(d6);
		validDivi2(d7);
		validDivi2(d8);
		validDivi2(d9);
		validDivi2(d10);
		validDivi2(d11);
		validDivi2(d13);
		validDivi2(d14);
		validDivi2(d15);
		validDivi2(d16);
		validDivi2(d17);*/
		
		Double ta = null;
		if(ta != null && ta > 0){
			System.out.println("不报错");
		}
	}
	
	
	public static void validDivi2(double v1){
		
		BigDecimal big = new BigDecimal(String.valueOf(v1));
		
		Pattern p = Pattern.compile("-?(\\d+)(\\.*)(\\d*)");
		String ss = big.toString();
		
		Matcher m = p.matcher(ss);
		if(m.matches()){
			System.out.println(ss);
			System.out.println(m.group(1));
			System.out.println(m.group(3));
		}
	}
	/**
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
	public static BigDecimal validDivi(double v1, double v2, int precision){
		
		BigDecimal big = new BigDecimal(String.valueOf(v1));
		BigDecimal b2 = new BigDecimal(String.valueOf(v2));
		
		Pattern p = Pattern.compile("-?(\\d+)(\\.*)(\\d*)");
		BigDecimal divide = big.divide(b2);
		Matcher m = p.matcher(divide.toString());
//		System.out.println(divide);
		if(m.matches()){
			Long ll = Long.valueOf(m.group(1));
			//正数位为0，保留指定的有效位
			if(ll == 0){
				MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
				//保留指定的有效位
				return big.divide(b2, mc);
//				return divide.divide(BigDecimal.ONE, mc);
			}
		}
		//保留指定小数位
		return big.divide(b2, precision, BigDecimal.ROUND_HALF_UP);
//		return divide.setScale(precision, BigDecimal.ROUND_HALF_UP);
	}
	
	
	
	
}
