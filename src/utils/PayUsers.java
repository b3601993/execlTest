package utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * 付费用户的公共方法
 *
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2016年8月11日上午10:23:40
 */
public class PayUsers {
	
	private static DB db = MongodbConnection.getCollection();
	//支付那边衍生出来的表,当一个用户由试用变为付费时，数据不会删除，而是会新增一条数据
	private static DBCollection payUser = db.getCollection("t_paySys_user_permi");
	private static DBCollection useropRecord = db.getCollection("userop_record");
	
	
	/**
	 * 获得当前付费用户set集合
	 * 
	 * @author yutao
	 * @return 
	 * @date 2016年8月11日上午10:25:27
	 */
	public static Set<String> getCurrentPayUsers(){
		//找出付费用户来
		BasicDBObject payQuery = new BasicDBObject();
		payQuery.append("isPay", 1);//付费为1，试用为0
		payQuery.append("open_date", new BasicDBObject("$lte",DateUtil.getBeginOfDay()));//有些付费用户开始时间会大于今天，这种暂时不算
		payQuery.append("end_date", new BasicDBObject("$gt", DateUtil.getBeginOfDay()));//其中end_date等于今天都是失效的数据
		Set<String> paySet = new HashSet<String>();
		DBCursor payCursor = payUser.find(payQuery);
		while(payCursor.hasNext()){
			DBObject o = payCursor.next();
			String accountId = o.get("user_id").toString();
			paySet.add(accountId);
		}
		payCursor.close();
		return paySet;
	}
	
	
	/**
	 * 获取指定月份的付费用户集合
	 * @param date
	 * @return
	 * @author yutao
	 * @date 2017年1月9日下午4:16:59
	 */
	public static Set<String> getMonthPayUsers(Date date){
		//找出付费用户来
		BasicDBObject payQuery = new BasicDBObject();
		payQuery.append("isPay", 1);//付费为1，试用为0
		payQuery.append("open_date", new BasicDBObject("$lte",date));//有些付费用户开始时间会大于今天，这种暂时不算
		payQuery.append("end_date", new BasicDBObject("$gt", date));//其中end_date等于今天都是失效的数据
		Set<String> paySet = new HashSet<String>();
		DBCursor payCursor = payUser.find(payQuery);
		while(payCursor.hasNext()){
			DBObject o = payCursor.next();
			String accountId = o.get("user_id").toString();
			paySet.add(accountId);
		}
		payCursor.close();
		return paySet;
	}
	
	/**
	 * 获取全年的付费用户
	 * @param date
	 * @return
	 * @author yutao
	 * @date 2017年1月10日上午11:07:27
	 */
	public static Set<String> getYearPayUsers(Date startDate, Date endDate){
		//找出付费用户来
		BasicDBObject payQuery = new BasicDBObject();
		payQuery.append("isPay", 1);//付费为1，试用为0
		payQuery.append("open_date", new BasicDBObject("$lte",endDate));//有些付费用户开始时间会大于今天，这种暂时不算
		payQuery.append("end_date", new BasicDBObject("$gt", startDate));//其中end_date等于今天都是失效的数据
		Set<String> paySet = new HashSet<String>();
		DBCursor payCursor = payUser.find(payQuery);
		while(payCursor.hasNext()){
			DBObject o = payCursor.next();
			String accountId = o.get("user_id").toString();
			paySet.add(accountId);
		}
		payCursor.close();
		return paySet;
	}
	
	/**
	 * 获得当前试用用户set集合
	 * @return
	 * @author yutao
	 * @date 2016年8月11日上午11:05:51
	 */
	public static Set<String> getTryUsers(){
		BasicDBObject tryQuery = new BasicDBObject();
		tryQuery.append("isPay", 0);
		tryQuery.append("open_date", new BasicDBObject("$lte", DateUtil.getBeginOfDay()));
		tryQuery.append("end_date", new BasicDBObject("$gt", DateUtil.getBeginOfDay()));
		Set<String> trySet = new HashSet<String>();
		DBCursor tryCursor = payUser.find(tryQuery);
		while(tryCursor.hasNext()){
			DBObject o = tryCursor.next();
			String accountId = o.get("user_id").toString();
			trySet.add(accountId);
		}
		tryCursor.close();
		//试用用户里面可能有付费用户 情景：假如 ，今天衍生试用的用户，他从过去到现在是试用的。然后他的到期日期是大于今天的。
		//第二天有人给他下了个付费单子，就变成付费的。
		Set<String> paySet = getCurrentPayUsers();
		trySet.removeAll(paySet);
		return trySet;
	}
	
	/**
	 * 获取每天的付费用户id集合（因为付费用户每天都可能在变，比如8月6号的付费用户与8月11号付费用户可能不一样）
	 * 
	 * @param openDate 
	 * @param isPay 1是付费用户 0是试用用户
	 * @return 最后得到了每天的付费用户id集合
	 * @author yutao
	 * @date 2016年8月11日上午11:24:01
	 */
	public static Map<String, Set<String>> getpayUsers(Date openDate,int isPay){
		Date minDate = DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd");
		BasicDBObject payQuery = new BasicDBObject();
		//先找出付费用户open_date mindate
		payQuery.append("isPay", isPay);
		//结束日期要大于8月5号，才是我需要的有效数据
		payQuery.append("end_date", new BasicDBObject("$gt", minDate));
		DBCursor cursor = payUser.find(payQuery).sort(new BasicDBObject("open_date",-1));
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		while(cursor.hasNext()){
			DBObject o = cursor.next();
			Object startDate = o.get("open_date");
			if(startDate != null){
				minDate = (Date)startDate;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("account_id", o.get("user_id"));
				map.put("isPay", o.get("isPay"));
				map.put("open_date", startDate);
				map.put("end_date", o.get("end_date"));
				result.add(map);
			}
		}
		cursor.close();
		//说明目前还没有付费用户
		if(result.isEmpty()){
			return null;
		}
		
		List<Date> lDate = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();  
         
        
		if(openDate != null){
			lDate.add(openDate);
			// 使用给定的 Date 设置此 Calendar 的时间  
			cal.setTime(openDate); 
		}else{
			// 把开始时间加入集合  
			lDate.add(minDate);
			// 使用给定的 Date 设置此 Calendar 的时间  
			cal.setTime(minDate); 
		}
		
        
        boolean bContinue = true;  
        //只统计到昨天数据
        Date endDate = DateUtil.getYesterday();
        while (bContinue) {  
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量  
            cal.add(Calendar.DAY_OF_MONTH, 1);  
            // 测试此日期是否在指定日期之后 
            
            if (endDate.after(cal.getTime())) {  
                lDate.add(cal.getTime());  
            } else {  
                break;  
            }  
        }  
        // 把结束时间加入集合
        lDate.add(endDate);
        
        Map<String, Set<String>> getpayUsers = null;
        if(isPay == 0){
        	//这里使用的是递归
        	getpayUsers = getpayUsers(openDate, 1);
        }
        
		Map<String, Set<String>> resultSet = new HashMap<String, Set<String>>();
		for(Date d : lDate){
			String dateString = DateUtil.dateToString(d, "yyyy-MM-dd");
			for(Map<String, Object> m : result){
				Date startDate = (Date)m.get("open_date");
				Date endDate2 = (Date)m.get("end_date");
				String accountId = m.get("account_id").toString();
				if(DateUtil.compare(d, startDate)>=0 && DateUtil.compare(d, endDate2)<0){
					//说明该用户该天在有效区间内
					Set<String> accountSet = resultSet.get(dateString);
					if(accountSet == null){
						Set<String> set = new HashSet<String>();
						set.add(accountId);
						resultSet.put(dateString, set);
					}else{
						accountSet.add(accountId);
					}
				}
			}
			if(isPay == 0 && getpayUsers != null){//若为试用用户还要去除付费用户
				Set<String> set = getpayUsers.get(dateString);//得到该天付费用户id集合
				Set<String> set2 = resultSet.get(dateString);//得到该天试用用户id集合
				set2.removeAll(set);//将付费用户id集合中与试用用户集合交集去除
			}
		}
		return resultSet;
	}
	
	/**
	 * 得到每天登陆过gogoal3.0终端的用户id集合（得到的是付费用户、试用用户、过期用户的总和）
	 * 
	 * @author yutao
	 * @return 
	 * @date 2016年8月23日上午10:09:26
	 */
	public static Map<String, Set<String>> getLoginGogalAccountId(Date maxDate){
		//由于S3_06不能作为判断用户是否登录的标准（因为假设该人登录后好几天没关闭）
		BasicDBList list = new BasicDBList();
		list.add("S3_01");
		list.add("S3_01_00");
		list.add("S3_02");
		list.add("S3_03");
		list.add("S3_03_00");
		list.add("S3_04");
		list.add("S3_05");
		list.add("S3_06");
		list.add("S3_06_01");
		list.add("S3_06_02");
		list.add("S3_06_00");
		Date minDate = DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd");
		BasicDBObject useropQuery = new BasicDBObject();
		useropQuery.append("status", 1);
		useropQuery.append("org_id", new BasicDBObject("$ne", 4));
		useropQuery.append("type", 3);
		useropQuery.append("code", new BasicDBObject("$nin", list));
		if(maxDate != null){
			minDate = maxDate;
		}
		useropQuery.append("createtime", new BasicDBObject("$gte", minDate).append("$lt", DateUtil.getBeginOfDay()));
		DBCursor cursor = useropRecord.find(useropQuery);
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		while(cursor.hasNext()){
			DBObject o = cursor.next();
			Object createtimeObject = o.get("createtime");
			if(createtimeObject != null){
				String dateString = DateUtil.dateToString((Date)createtimeObject, "yyyy-MM-dd");
				String accountId = o.get("account_id").toString();
				Set<String> setAccountId = result.get(dateString);
				if(setAccountId == null || setAccountId.isEmpty()){
					Set<String> set = new HashSet<String>();
					set.add(accountId);
					result.put(dateString, set);
				}else{
					setAccountId.add(accountId);
				}
			}
		}
		cursor.close();
		return result;
	}
}
