package execlTest;

/**
 * 
 * 实体类
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc. All rights reserved.
 * @date 2016年5月16日 上午11:31:13
 */
public class XlsDto {

	/**
     * 选课号
     */
    private Integer xkh;
    /**
     * 学号
     */
    private String xh;
    /**
     * 姓名
     */
    private String xm;
    /**
     * 学院
     */
    private String yxsmc;
    /**
     * 课程号
     */
    private Integer kch;
    /**
     * 课程名
     */
    private String kcm;
    /**
     * 成绩
     */
    private float cj;
	
	public Integer getXkh() {
		return xkh;
	}
	
	public void setXkh(Integer xkh) {
		this.xkh = xkh;
	}
	
	public String getXh() {
		return xh;
	}
	
	public void setXh(String xh) {
		this.xh = xh;
	}
	
	public String getXm() {
		return xm;
	}
	
	public void setXm(String xm) {
		this.xm = xm;
	}
	
	public String getYxsmc() {
		return yxsmc;
	}
	
	public void setYxsmc(String yxsmc) {
		this.yxsmc = yxsmc;
	}
	
	public Integer getKch() {
		return kch;
	}
	
	public void setKch(Integer kch) {
		this.kch = kch;
	}
	
	public String getKcm() {
		return kcm;
	}
	
	public void setKcm(String kcm) {
		this.kcm = kcm;
	}
	
	public float getCj() {
		return cj;
	}
	
	public void setCj(float cj) {
		this.cj = cj;
	}
}
