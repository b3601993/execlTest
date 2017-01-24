package gogoalExample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.BasicBSONDecoder;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
//import com.rabbitmq.client.AMQP.Basic;
//import com.sun.rowset.internal.Row;

import gogoalExample.common.ExcelPoiCommon;
import utils.DateUtil;
import utils.IPSeeker;
import utils.PayUsers;
/**
 * 用来跑测试数据的
 *
 *
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2016年8月17日上午9:59:05
 */
public class testExecl{

	//暂时用来跑导出
	public static void main(String[] args) {
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("106.75.51.20", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection useropRecord = db.getCollection("userop_record");
//			DBCollection loginRecord = db.getCollection("login_record");
			DBCollection accountrelation = db.getCollection("accountrelation");
			DBCollection payUser = db.getCollection("t_paySys_user_permi");
//			DBCollection userListStatistics = db.getCollection("user_list_statistics");
//			DBCollection collectionLog = db.getCollection("version_upgrade_log");
			

//			File file = new File("C:\\Users\\yutao\\Desktop\\2016年度终端用户数据.xls");
//			Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(1, 0, file);
			//现获取老用户
			BasicDBObject match = new BasicDBObject();
			match.put("status", 1);
			match.put("type", 3);//new BasicDBObject("$ne", 3)
			match.put("org_id", new BasicDBObject("$ne", 4));
			match.put("code", "S3_06");//S2_l09
			match.put("createtime", new BasicDBObject("$lte", DateUtil.getEndOfDay(DateUtil.stringToDate("2016-08-04", "yyyy-MM-dd"))));
			BasicDBObject group = new BasicDBObject();
			group.append("_id", null);
			group.append("account_name", new BasicDBObject("$addToSet", "$account_name"));
			
			
			AggregationOutput output = useropRecord.aggregate(new BasicDBObject("$match", match), new BasicDBObject("$group", group));
			Iterator<DBObject> iterator = output.results().iterator();
			List<String> list = null;
			//拿到了老用户
			while(iterator.hasNext()){
				DBObject o= iterator.next();
				list = (ArrayList<String>)o.get("account_name");
			}
			list.add(null);
			BasicDBObject useropQuery = new BasicDBObject();
			if(list != null){
				useropQuery.append("account_name", new BasicDBObject("$nin", list));
			}
			useropQuery.append("status", 1);
			useropQuery.append("code", "S3_06");//S2_l09
			useropQuery.append("org_id", new BasicDBObject("$ne", 4));
			useropQuery.append("type", 3);//new BasicDBObject("$ne", 3)
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd"));
			timeQuery.append("$lte", DateUtil.getEndOfDay(DateUtil.stringToDate("2016-12-31", "yyyy-MM-dd")));
			useropQuery.append("createtime", timeQuery);
			
			Map<String, Integer> loginMap = new HashMap<String, Integer>();
			Map<String, Integer> loginDateMap = new HashMap<String, Integer>();
			Map<String, Set<String>> salerMap = new HashMap<String, Set<String>>();
			Map<String, Set<String>> ipMap = new HashMap<String,Set<String>>();
			Map<String, String> userMap = new HashMap<String,String>();
			
			Set<String> loginSet = new HashSet<String>();
			DBCursor useropCursor = useropRecord.find(useropQuery);
			Set<String> accountSet = new HashSet<String>();
			Map<Integer, String> userM = new HashMap<Integer, String>(); 
			while(useropCursor.hasNext()){
				DBObject o = useropCursor.next();
				String accountName = o.get("account_name").toString();
				String dateToString = DateUtil.dateToString((Date)o.get("createtime"), "yyyy-MM-dd");
				String dateAccountName = dateToString + ";" + accountName;
				//统计的账号
				accountSet.add(accountName);
				userM.put(Integer.valueOf(o.get("account_id").toString()), accountName);
				
				//登录次数
				Integer loginInt = loginMap.get(accountName);
				if(loginInt == null){
					loginMap.put(accountName, Integer.valueOf(1));
				}else{
					loginMap.put(accountName, ++loginInt);
				}
				
				//登录天次
				if(!loginSet.contains(dateAccountName)){
					Integer loginDateInt = loginDateMap.get(accountName);
					if(loginDateInt == null){
						loginDateMap.put(accountName, Integer.valueOf(1));
					}else{
						loginDateMap.put(accountName, ++loginDateInt);
					}
					loginSet.add(dateAccountName);
				}
				
				//对应销售
				Set<String> set = salerMap.get(accountName);
				Object saler = o.get("saler_name");
				if(saler != null){
					if(set == null){
						set = new HashSet<String>();
						set.add(saler.toString());
						salerMap.put(accountName, set);
					}else{
						set.add(saler.toString());
					}
				}
				
				
				//ip地址
				Set<String> ipSet = ipMap.get(accountName);
				String ip = o.get("ip")==null?"":o.get("ip").toString();
				if(ipSet == null){
					ipSet = new HashSet<String>();
					ipSet.add(ip);
					ipMap.put(accountName, ipSet);
				}else{
					ipSet.add(ip);
				}
				
				//用户类型
				userMap.put(accountName, o.get("user_type").toString());
			}
			useropCursor.close();
			loginSet.clear();
			
			BasicDBObject accountQuery = new BasicDBObject();
			accountQuery.append("account_name", new BasicDBObject("$in", accountSet));
			DBCursor accountCursor = accountrelation.find(accountQuery);
			Map<String, String> mdoMap = new HashMap<String, String>();
			while(accountCursor.hasNext()){
				DBObject o = accountCursor.next();
				//账号
				String accountName = o.get("account_name").toString();
				
				//电话
				String mobile = o.get("mobile") == null ? "暂无电话" : o.get("mobile").toString();
				if(StringUtils.isBlank(mobile)){
					mobile="暂无电话";
				}
				//注册时间
				String dateToString = DateUtil.dateToString((Date)o.get("insert_date"), "yyyy-MM-dd");
				//机构名称
				String orgName = o.get("org_name") == null ? "普通用户" : o.get("org_name").toString();
				if(StringUtils.isBlank(orgName)){
					orgName="暂无机构名";
				}
				
				mdoMap.put(accountName, mobile + ";" + dateToString + ";" + orgName);
			}
			accountCursor.close();
			
			
			
			BasicDBObject payQuery = new BasicDBObject();
			payQuery.append("user_id", new BasicDBObject("$in", userM.keySet()));
			DBCursor payCursor = payUser.find(payQuery);
			Map<String, String> payUserMap = new HashMap<String, String>();
			Map<String, String> tryUserMap = new HashMap<String, String>();
			while(payCursor.hasNext()){
				DBObject o = payCursor.next();
				String isPay = o.get("isPay").toString();
				Integer userId = Integer.valueOf(o.get("user_id").toString());
				//开始时间
				String startDate = DateUtil.dateToString((Date)o.get("open_date"), "yyyy-MM-dd");
				//结束时间
				String endDate = DateUtil.dateToString((Date)o.get("end_date"), "yyyy-MM-dd");
				
				String se = startDate + ";" + endDate;
				String accountName = userM.get(userId);
				if("1".equals(isPay)){//付费的
					String str = payUserMap.get(accountName);
					if(str==null || StringUtils.isBlank(str)){
						payUserMap.put(accountName, se);
					}else{
						payUserMap.put(accountName, se + "and" + str);
					}
				}else{//试用
					String str = tryUserMap.get(accountName);
					if(str==null || StringUtils.isBlank(str)){
						tryUserMap.put(accountName, se);
					}else{
						tryUserMap.put(accountName, se + "and" + str);
					}
				}
			}
			payCursor.close();
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			String workbookStr = "2016年gg3.0用户情况";
			XSSFSheet sheet = workbook.createSheet(workbookStr);
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("账号");
			cell = row.createCell(1);
			cell.setCellValue("联系电话");
			cell = row.createCell(2);
			cell.setCellValue("注册时间");
			cell = row.createCell(3);
			cell.setCellValue("机构名称");
			cell = row.createCell(4);
			cell.setCellValue("用户类型");
			cell = row.createCell(5);
			cell.setCellValue("所属地区");
			cell = row.createCell(6);
			cell.setCellValue("付费权限时间范围");
			cell = row.createCell(7);
			cell.setCellValue("试用权限时间范围");
			cell = row.createCell(8);
			cell.setCellValue("总登录次数");
			cell = row.createCell(9);
			cell.setCellValue("总登录天次");
			cell = row.createCell(10);
			cell.setCellValue("对应销售");
			
			int rowUserop=1;
			
			
			for(Map.Entry<Integer, String> m : userM.entrySet()){
//				Integer accountId = m.getKey();//用户id
				String accountName = m.getValue();//账号
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);//账号
				cell.setCellValue(accountName);
				String valueStr = mdoMap.get(accountName);
				String mobile = "";
				String regDate = "";
				String orgName = "";
				if(valueStr != null){
					String[] split = valueStr.split(";");
					mobile = split[0];//电话
					regDate = split[1];//注册时间
					orgName = split[2];//机构名称
				}
				
				
				cell = row.createCell(1);
				cell.setCellValue(mobile);
				cell = row.createCell(2);
				cell.setCellValue(regDate);
				cell = row.createCell(3);
				cell.setCellValue(orgName);
				
				cell = row.createCell(4);//用户类型
				String type = userMap.get(accountName);
				cell.setCellValue(type);
				cell = row.createCell(5);//ip地址
				Set<String> ipStr = ipMap.get(accountName);
				Set<String> addSet = new HashSet<String>();
				for(String s : ipStr){
					Map<String, String> location = IPSeeker.getInstance().getLocation(s.trim());
					String province=location.get("province")==null?"":location.get("province");
					String city=location.get("city")==null?"":location.get("city");
					addSet.add(province+"-"+city);
//					map.put("user_area", province+city);
				}
				cell.setCellValue(addSet.toString());
				
				cell = row.createCell(6);//付费权限时间范围
				cell.setCellValue(payUserMap.get(accountName));
				
				cell = row.createCell(7);//试用权限时间范围
				cell.setCellValue(tryUserMap.get(accountName));
				
				cell = row.createCell(8);//总登录次数
				cell.setCellValue(loginMap.get(accountName));
				
				cell = row.createCell(9);//总登录天次
				cell.setCellValue(loginDateMap.get(accountName));
				
				cell = row.createCell(10);
				Set<String> set = salerMap.get(accountName);
				cell.setCellValue(set==null?"":set.toString());
				rowUserop++;
			}
			
		
			long time = System.currentTimeMillis();
			String fileName = workbookStr+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取活跃用户
	 * 
	 * @author yutao
	 * @date 2017年1月12日上午10:28:45
	 */
	public static void getUserActivator(){

		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("106.75.51.20", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection useropRecord = db.getCollection("userop_record");
//			DBCollection loginRecord = db.getCollection("login_record");
//			DBCollection accountrelation = db.getCollection("accountrelation");
//			DBCollection userListStatistics = db.getCollection("user_list_statistics");
//			DBCollection collectionLog = db.getCollection("version_upgrade_log");
			

//			File file = new File("C:\\Users\\yutao\\Desktop\\2016年度终端用户数据.xls");
//			Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(1, 0, file);
			Map<String, Set<String>> payUser = new HashMap<String, Set<String>>();
			/*Date date01 = DateUtil.stringToDate("2016-01-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date01, "yyyy-MM"), PayUsers.getMonthPayUsers(date01));
			Date date02 = DateUtil.stringToDate("2016-02-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date02, "yyyy-MM"), PayUsers.getMonthPayUsers(date02));
			Date date03 = DateUtil.stringToDate("2016-03-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date03, "yyyy-MM"), PayUsers.getMonthPayUsers(date03));
			Date date04 = DateUtil.stringToDate("2016-04-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date04, "yyyy-MM"), PayUsers.getMonthPayUsers(date04));
			Date date05 = DateUtil.stringToDate("2016-05-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date05, "yyyy-MM"), PayUsers.getMonthPayUsers(date05));
			Date date06 = DateUtil.stringToDate("2016-06-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date06, "yyyy-MM"), PayUsers.getMonthPayUsers(date06));*/
			/*Date date07 = DateUtil.stringToDate("2016-07-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date07, "yyyy-MM"), PayUsers.getMonthPayUsers(date07));
			Date date08 = DateUtil.stringToDate("2016-08-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date08, "yyyy-MM"), PayUsers.getMonthPayUsers(date08));
			Date date09 = DateUtil.stringToDate("2016-09-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date09, "yyyy-MM"), PayUsers.getMonthPayUsers(date09));
			Date date10 = DateUtil.stringToDate("2016-10-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date10, "yyyy-MM"), PayUsers.getMonthPayUsers(date10));
			Date date11 = DateUtil.stringToDate("2016-11-01", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date11, "yyyy-MM"), PayUsers.getMonthPayUsers(date11));*/
			Date date12 = DateUtil.stringToDate("2016-12-31", "yyyy-MM");
			payUser.put(DateUtil.dateToString(date12, "yyyy-MM"), PayUsers.getMonthPayUsers(date12));
//			Set<String> payUsers12 = PayUsers.getMonthPayUsers(date12);
			Set<String> yearPayUsers = PayUsers.getYearPayUsers(DateUtil.stringToDate("2016-01-01", "yyyy-MM-dd"), date12);
			
			BasicDBObject useropQuery = new BasicDBObject();
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.stringToDate("2016-10-01", "yyyy-MM-dd"));
			timeQuery.append("$lte", DateUtil.stringToDate("2016-12-31", "yyyy-MM-dd"));
			useropQuery.append("createtime", timeQuery);
			useropQuery.append("status", 1);
			useropQuery.append("org_id", new BasicDBObject("$ne", 4));
			useropQuery.append("account_name", new BasicDBObject("$ne", null));
			useropQuery.append("code", "S3_06");//S2_l09
			useropQuery.append("type", 3);
			Map<String, Integer> accountMap = new HashMap<String, Integer>();
			
			DBCursor useropCursor = useropRecord.find(useropQuery);
//			Map<String, Set<String>> accountCount = new HashMap<String, Set<String>>();
			Map<String, Integer> yearMap = new HashMap<String, Integer>();
			Map<String, Integer> payMap = new HashMap<String, Integer>();
			Set<String> quchong = new HashSet<String>();
			Set<String> quchong2 = new HashSet<String>();
			while(useropCursor.hasNext()){
				DBObject o = useropCursor.next();
				Object datetime = o.get("createtime");
				if(datetime != null){
//					String dateToString = DateUtil.dateToString((Date)datetime, "yyyy-MM");//日期
//					String accountName = o.get("account_name").toString();//账号
					String accountId = o.get("account_id").toString();
					
//					String dateAccount = dateToString + ";" + accountName + ";" + accountId;
					String dateAccount = accountId;
					
					Integer accountInt = accountMap.get(dateAccount);
					if(accountInt == null){
						accountMap.put(dateAccount, Integer.valueOf(1));
					}else{
						if(accountInt >= 15){//就是我要统计的全年用户数
							
							if(!quchong.contains(accountId)){
								if(yearPayUsers.contains(accountId)){
									Integer payInt = payMap.get("year");
									if(payInt == null){
										payMap.put("year", Integer.valueOf(1));
									}else{
										payMap.put("year", ++payInt);
									}
								}
								
								Integer yearInt = yearMap.get("year");
								if(yearInt == null){
									yearMap.put("year", Integer.valueOf(1));
								}else{
									yearMap.put("year", ++yearInt);
								}
								quchong.add(accountId);
							}
							continue;
						}
						accountMap.put(dateAccount, ++accountInt);
					}
					quchong2.add(accountId);
					/*Set<String> set = accountCount.get(dateToString);
					if(set==null){
						set = new HashSet<String>();
						set.add(accountName);
						accountCount.put(dateToString, set);
					}else{
						set.add(accountName);
					}*/
				}
			}
			useropCursor.close();
//			Map<String, Integer> payMap = new HashMap<String, Integer>();
/*			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			for(Map.Entry<String, Integer> m : accountMap.entrySet()){
				Integer value = m.getValue();//每个账号该登录的次数
				String key = m.getKey();
				String[] split = key.split(";");
				String date = split[0];
//				String accountName = split[1];
				String accountId = split[2];
				if(value >= 4){
					Integer dateInt = resultMap.get(date);
					if(dateInt == null){
						resultMap.put(date, Integer.valueOf(1));
					}else{
						resultMap.put(date, ++dateInt);
					}
					
					if(payUser.get(date).contains(accountId)){
						Integer payInt = payMap.get(date);
						if(payInt == null){
							payMap.put(date, Integer.valueOf(1));
						}else{
							payMap.put(date, ++payInt);
						}
					}
				}
			}*/
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("终端3.0每个月的活跃用户");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("日期");
			cell = row.createCell(1);
			cell.setCellValue("活跃总数");
			cell = row.createCell(2);
			cell.setCellValue("登录总数");
			cell = row.createCell(3);
			cell.setCellValue("付费用户数");
			
			int rowUserop=1;
			for(Map.Entry<String, Integer> m : yearMap.entrySet()){

				String key = m.getKey();//"year"
				Integer value = m.getValue();//次数
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue("2016年全年");
				
				cell = row.createCell(1);
				cell.setCellValue(value);
				
				cell = row.createCell(2);
				cell.setCellValue(quchong2.size());
				
				cell = row.createCell(3);
				cell.setCellValue(payMap.get(key));
				
				rowUserop++;
			}
			long time = System.currentTimeMillis();
			String fileName = "终端3.0每个月的活跃用户"+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		
		}catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	/**
	 * 指定相应账号 各模块统计
	 * @param useropRecord
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @author yutao
	 * @date 2017年1月3日下午2:36:27
	 */
	public static void getModuleCount(DBCollection useropRecord) throws FileNotFoundException, IOException {
		File file = new File("C:\\Users\\yutao\\Desktop\\2016年度终端用户数据.xls");
		
		Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(1, 0, file);
		
		BasicDBObject useropQuery = new BasicDBObject();
		useropQuery.append("account_name", new BasicDBObject("$in", accountNameSet));
		BasicDBObject timeQuery = new BasicDBObject();
		timeQuery.append("$gte", DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd"));
		timeQuery.append("$lte", DateUtil.stringToDate("2016-12-31", "yyyy-MM-dd"));
		useropQuery.append("createtime", timeQuery);
		useropQuery.append("status", 1);
		useropQuery.append("code", new BasicDBObject("$in", new Object[]{"S3_06", "G3_02","G3_03", "G3_04", "G3_05", "G3_06", "G3_07", "G3_08", "G3_09", "G3_10", "G3_11", "G3_13", "G3_16"}));
		
		Map<String, Integer> accountMap = new HashMap<String, Integer>();
		
		
		DBCursor useropCursor = useropRecord.find(useropQuery);
		List<String> accountList = new ArrayList<String>();
		while(useropCursor.hasNext()){
			DBObject o = useropCursor.next();
			String accountName = o.get("account_name").toString();
			String code = o.get("code").toString();
			
			ExcelPoiCommon.getAccountCount(accountMap, accountName+code);
		}
		useropCursor.close();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("终端3.0试用用户各模块点击次数");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("用户账号");
		cell = row.createCell(1);
		cell.setCellValue("登录次数");
		cell = row.createCell(2);
		cell.setCellValue("寻宝");
		cell = row.createCell(3);
		cell.setCellValue("事件");
		cell = row.createCell(4);
		cell.setCellValue("主题");
		cell = row.createCell(5);
		cell.setCellValue("诊股");
		cell = row.createCell(6);
		cell.setCellValue("研报");
		cell = row.createCell(7);
		cell.setCellValue("自选");
		cell = row.createCell(8);
		cell.setCellValue("个股");
		cell = row.createCell(9);
		cell.setCellValue("行情");
		cell = row.createCell(10);
		cell.setCellValue("数据");
		cell = row.createCell(11);
		cell.setCellValue("直播");
		cell = row.createCell(12);
		cell.setCellValue("业绩");
		cell = row.createCell(13);
		cell.setCellValue("社交");
		
		int rowUserop=1;
		for(String accountName : accountNameSet){
			String nameS3 = accountName + "S3_06";//登录
			String nameG2 = accountName + "G3_02";//寻宝
			String nameG3 = accountName + "G3_03";//时间
			String nameG4 = accountName + "G3_04";//主题
			String nameG5 = accountName + "G3_05";//诊股
			String nameG6 = accountName + "G3_06";//研报
			String nameG7 = accountName + "G3_07";//自选
			String nameG8 = accountName + "G3_08";//个股
			String nameG9 = accountName + "G3_09";//行情
			String nameG10 = accountName + "G3_10";//数据
			String nameG11 = accountName + "G3_11";//直播
			String nameG13 = accountName + "G3_13";//业绩
			String nameG16 = accountName + "G3_16";//社交
			accountList.add(nameS3);
			accountList.add(nameG2);
			accountList.add(nameG3);
			accountList.add(nameG4);
			accountList.add(nameG5);
			accountList.add(nameG6);
			accountList.add(nameG7);
			accountList.add(nameG8);
			accountList.add(nameG9);
			accountList.add(nameG10);
			accountList.add(nameG11);
			accountList.add(nameG13);
			accountList.add(nameG16);
			row = sheet.createRow(rowUserop);
			cell = row.createCell(0);
			cell.setCellValue(accountName);
//				cell = row.createCell(1);
//				cell.setCellValue(accountMap.get(nameS3));
			
			for(int i=0; i<accountList.size(); i++){
				cell = row.createCell(i+1);
				if(accountList.get(i) == null){
					cell.setCellValue(0);
				}else{
					cell.setCellValue(accountMap.get(accountList.get(i))==null?0:accountMap.get(accountList.get(i)));
				}
			}
			rowUserop++;
			accountList.clear();
		}
		long time = System.currentTimeMillis();
		String fileName = "终端3.0付试用用户各模块点击次数"+time+".xlsx";
		
		FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
		workbook.write(out);
		workbook.close();
	}

	
	/**
	 * 
	 * 
	 * @author yutao
	 * @date 2017年1月3日上午10:06:52
	 */
	public static void getlianghua() {
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("106.75.51.20", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection useropRecord = db.getCollection("userop_record");
//			DBCollection loginRecord = db.getCollection("login_record");
//			DBCollection accountrelation = db.getCollection("accountrelation");
//			DBCollection userListStatistics = db.getCollection("user_list_statistics");
//			DBCollection collectionLog = db.getCollection("version_upgrade_log");
			
			//用户点击统计排名前20的
//			XSSFWorkbook workbook = getClickNum(accountrelation, userListStatistics);
//			//得到登录次数，天数，开始时间，结束时间
//			File file = new File("C:\\Users\\yutao\\Desktop\\CMS产品类型：中国量化投资俱乐部名单2016-12-14.xls");
//			XSSFWorkbook workbook = getLoginStartLastTime(useropRecord, file);
			
			BasicDBObject query = new BasicDBObject();
			query.append("status", 1);
			query.append("code", "S3_03");
			query.append("type", 3);
			DBCursor cursor = useropRecord.find(query);
			Map<String, Integer> map = new HashMap<String, Integer>();
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				String dateToString = DateUtil.dateToString((Date)o.get("createtime"), "yyyy-MM-dd");
				Integer dateInt = map.get(dateToString);
				if(dateInt == null){
					map.put(dateToString, Integer.valueOf(1));
				}else{
					map.put(dateToString, ++dateInt);
				}
			}
			cursor.close();
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("终端3.0每天的下载量");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("日期");
			cell = row.createCell(1);
			cell.setCellValue("次数");
			int rowUserop=1;
			for(Map.Entry<String, Integer> m : map.entrySet()){
				Integer value = m.getValue();
				String key = m.getKey();
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(key);
				cell = row.createCell(1);
				cell.setCellValue(value);
				rowUserop++;
			}
			
			
		    
			long time = System.currentTimeMillis();
			String fileName = "终端3.0每天的下载量"+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到登录次数，天数，开始时间，结束时间
	 * @param useropRecord
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @author yutao
	 * @date 2016年12月16日上午10:54:20
	 */
	public static XSSFWorkbook getLoginStartLastTime(DBCollection useropRecord, File file)
			throws FileNotFoundException {
		Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(0, 2, file);
		
		BasicDBObject query = new BasicDBObject();
		query.append("status", 1);
		query.append("type", 3);//3new BasicDBObject("$in", new Object[]{0,1,2})
		query.append("code", "S3_06");//S3_06S2_l09
		query.append("createtime", new BasicDBObject("$gte", DateUtil.stringToDate("2016-01-01", "yyyy-MM-dd"))
				.append("$lte", DateUtil.stringToDate("2016-12-31", "yyyy-MM-dd")));
		query.append("account_name", new BasicDBObject("$in", accountNameSet));
		DBCursor cursor = useropRecord.find(query).sort(new BasicDBObject("createtime", 1));
		Map<String, Integer> map = new HashMap<String, Integer>();
		Map<String, String> firstMap = new HashMap<String, String>();
		Map<String, String> lastMap = new HashMap<String, String>();
		Map<String, Integer> dayMap = new HashMap<String, Integer>();
		Set<String> daySet = new HashSet<String>();
		while(cursor.hasNext()){
			DBObject o = cursor.next();
			String accountName = o.get("account_name").toString();
			String dateToString = DateUtil.dateToString((Date)o.get("createtime"), "yyyy-MM-dd");
			String dateAccountName = dateToString + ";" + accountName;
			if(firstMap.get(accountName)==null){
				firstMap.put(accountName, dateToString);
			}
			lastMap.put(accountName, dateToString);
			//次数
			Integer i = map.get(accountName);
			if(i == null){
				map.put(accountName, Integer.valueOf(1));
			}else{
				map.put(accountName, ++i);
			}
			//天次
			if(!daySet.contains(dateAccountName)){
				Integer dayInt = dayMap.get(accountName);
				if(dayInt == null){
					dayMap.put(accountName, Integer.valueOf(1));
				}else{
					dayMap.put(accountName, ++dayInt);
				}
				daySet.add(dateAccountName);
			}
			
		}
		cursor.close();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("gogoal账号登录情况");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("gogoal账号");
		cell = row.createCell(1);
		cell.setCellValue("登录次数");
		cell = row.createCell(2);
		cell.setCellValue("登录天次");
		cell = row.createCell(3);
		cell.setCellValue("首次登陆时间");
		cell = row.createCell(4);
		cell.setCellValue("最后登陆时间");
		int rowUserop=1;
		
		for(Map.Entry<String, Integer> m : map.entrySet()){
			String key = m.getKey();
			Integer value = m.getValue();
			row = sheet.createRow(rowUserop);
			cell = row.createCell(0);
			cell.setCellValue(key);
			cell = row.createCell(1);
			cell.setCellValue(value);
			cell = row.createCell(2);
			cell.setCellValue(dayMap.get(key));
			cell = row.createCell(3);
			cell.setCellValue(firstMap.get(key));
			cell = row.createCell(4);
			cell.setCellValue(lastMap.get(key));
			rowUserop++;
		}
		return workbook;
	}
	
	/**
	 * 用户点击前20的统计
	 * @param accountrelation
	 * @param userListStatistics
	 * @return
	 * @author yutao
	 * @date 2016年12月15日下午1:16:01
	 */
	public static XSSFWorkbook getClickNum(DBCollection accountrelation, DBCollection userListStatistics) {
		BasicDBObject match = new BasicDBObject();
		match.append("account_id", new BasicDBObject("$ne", "0"));
		List<String> list = new ArrayList<String>();
		list.add("付费用户");
		list.add("试用用户");
		match.append("user_type", new BasicDBObject("$in", list));
		
		BasicDBObject group = new BasicDBObject();
		group.append("_id", "$account_id");
		group.append("user_type", new BasicDBObject("$addToSet", "$user_type"));
		group.append("history_count",new BasicDBObject("$max", "$history_count"));
		
		BasicDBObject sort = new BasicDBObject();
		sort.append("history_count", -1);
		
		BasicDBObject limit = new BasicDBObject();
		limit.append("$limit", 20);
		
		AggregationOutput output = userListStatistics.aggregate(new BasicDBObject("$match", match), 
																new BasicDBObject("$group", group),
																new BasicDBObject("$sort", sort),
																limit);
		Iterator<DBObject> iterator = output.results().iterator();
		Map<String, Object> map = new HashMap<String, Object>();
		Set<Integer> accountSet = new HashSet<Integer>();
		while(iterator.hasNext()){
			DBObject o = iterator.next();
			String accountId = o.get("_id").toString();
			accountSet.add(Integer.valueOf(accountId));
			map.put(accountId, o.get("history_count"));
		}
		BasicDBObject accountQuery = new BasicDBObject();
		accountQuery.append("account_id", new BasicDBObject("$in", accountSet));
		DBCursor cursor = accountrelation.find(accountQuery);
//		Map<String, String>	 mm = new HashMap<String, String>();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("gogoal3.0登陆情况");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("gogoal账号");
		cell = row.createCell(1);
		cell.setCellValue("点击次数");
		cell = row.createCell(2);
		cell.setCellValue("机构名称");
		cell = row.createCell(3);
		cell.setCellValue("使用人名称");
		int rowUserop=1;
		
		while(cursor.hasNext()){
			DBObject o = cursor.next();
			
			String accountName = o.get("account_name") == null ? "暂时没有账号" : o.get("account_name").toString();
			String fullName = o.get("full_name") == null ? "暂时没有名称" : o.get("full_name").toString();
			String orgName = o.get("org_name") == null ? "暂时没有机构名称" : o.get("org_name").toString();
			row = sheet.createRow(rowUserop);
			cell = row.createCell(0);
			cell.setCellValue(accountName);
			cell = row.createCell(1);
			cell.setCellValue(map.get(o.get("account_id").toString()).toString());
			cell = row.createCell(2);
			cell.setCellValue(orgName);
			cell = row.createCell(3);
			cell.setCellValue(fullName);
			rowUserop++;
		}
		cursor.close();
		return workbook;
	}
	
	/**
	 * 具体某个机构访问情况
	 * 
	 * @author yutao
	 * @date 2016年9月14日下午5:08:42
	 */
	public void exportOrgAccess(){
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("42.62.50.226", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection accountRelation = db.getCollection("accountrelation");
			//先去获取账户信息
			BasicDBObject accountQuery = new BasicDBObject();
			accountQuery.append("org_name", new BasicDBObject("$regex", "申银万国"));
			DBCursor accountCursor = accountRelation.find(accountQuery);
			Map<String, String> accountMap = new HashMap<String, String>();
			while(accountCursor.hasNext()){
				DBObject o = accountCursor.next();
				String name = o.get("full_name") == null ? "暂无用户名" : o.get("full_name").toString();
				String department = o.get("department") == null ? "暂无部门名" : o.get("department").toString();
				String duty = o.get("duty") == null ? "暂无职责名" : o.get("duty").toString();
				String accountName = o.get("account_name") == null ? "暂时没有用户名" : o.get("account_name").toString();
				String account_id = o.get("account_id").toString();
				String orgName = o.get("org_name") == null ? "暂时没有机构名" : o.get("org_name").toString();
				String nameDD = name + ";" + department + ";" +duty + ";" +accountName + ";" + orgName;
				accountMap.put(account_id, nameDD);
			}
			accountCursor.close();
			DBCollection useropRecordCode = db.getCollection("userop_record_code");
			DBCursor userCursor = useropRecordCode.find();
			Map<String, String> codeMap= new HashMap<String, String>();
			while(userCursor.hasNext()){
				DBObject o = userCursor.next();
				String code = o.get("code").toString();
				String name = o.get("name").toString();
				codeMap.put(code, name);
			}
			userCursor.close();
			
			DBCollection useropRecord = db.getCollection("userop_record");
			
			BasicDBObject query = new BasicDBObject();
			query.append("status", 1);
//			query.append("type", 3);
			BasicDBList typeList = new BasicDBList();
			typeList.add(1);
			typeList.add(0);
			query.append("type", new BasicDBObject("$in", typeList));
			query.append("org_name", new BasicDBObject("$regex","申银万国上海"));
			
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.stringToDate("2015-11-01", "yyyy-MM-dd"));
			timeQuery.append("$lt", DateUtil.stringToDate("2016-09-14", "yyyy-MM-dd"));
			query.append("createtime", timeQuery);
			
			XSSFWorkbook workbook = new XSSFWorkbook(); //创建一个空白的工作薄
			XSSFSheet sheet = workbook.createSheet("申银万国上海");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("用户名");
			cell = row.createCell(1);
			cell.setCellValue("账号id");
			cell = row.createCell(2);
			cell.setCellValue("账号名");
			cell = row.createCell(3);
			cell.setCellValue("机构名称");
			cell = row.createCell(4);
			cell.setCellValue("部门");
			cell = row.createCell(5);
			cell.setCellValue("岗位");
			cell = row.createCell(6);
			cell.setCellValue("使用日期");
			cell = row.createCell(7);
			cell.setCellValue("功能模块");
			
			DBCursor cursor = useropRecord.find(query);
			
			Set<String> codeModule = new HashSet<String>();
			while(cursor.hasNext()){
				DBObject o = cursor.next();
//				String accountName = o.get("account_name") == null ? "暂时没有用户名" : o.get("account_name").toString();
//				String orgName = o.get("org_name") == null ? "暂时没有机构名" : o.get("org_name").toString();
				String accountId = o.get("account_id").toString();
				String code = o.get("code").toString();
//				String orgNameCode = orgName + ";" +code;
				String accountIdCode = accountId + ";" +code;
				Object createtime = o.get("createtime");
				if(createtime != null){
					String date = DateUtil.dateToString((Date)createtime, "yyyy-MM-dd");
					String acd = accountIdCode+";"+date;
					codeModule.add(acd);
				}
			}
			cursor.close();
			
			int rowUserop=1;
			for(String m : codeModule){
				
				String[] split = m.split(";");
				String accountId = split[0];
				String code = split[1];
				String date = split[2];
				String account = accountMap.get(accountId);
				String[] accountR = account.split(";");
				String name = accountR[0];
				String department = accountR[1];
				String duty = accountR[2];
				String accountName = accountR[3];
				String orgName = accountR[4];
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(name);
				cell = row.createCell(1);
				cell.setCellValue(accountId);
				cell = row.createCell(2);
				cell.setCellValue(accountName);
				cell = row.createCell(3);
				cell.setCellValue(orgName);
				cell = row.createCell(4);
				cell.setCellValue(department);
				cell = row.createCell(5);
				cell.setCellValue(duty);
				cell = row.createCell(6);
				cell.setCellValue(date);
				cell = row.createCell(7);
				cell.setCellValue(codeMap.get(code)==null ? code : codeMap.get(code));
				rowUserop++;
			}
			
			long time = System.currentTimeMillis();
			String fileName = "申银万国上海yyyyy"+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过读取execl文件得到的account_name去查询数据
	 * 
	 * @author yutao
	 * @date 2016年9月12日下午1:47:57
	 */
	public void export8monthAccess(){
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("42.62.50.226", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			
			DB db = mongoClient.getDB("gg_openapi");
			
			File file = new File("C:\\Users\\yutao\\Desktop\\统计使用情况.xlsx");
			FileInputStream in = new FileInputStream(file);  
			XSSFWorkbook wb = new XSSFWorkbook(in);
			XSSFSheet sheetAt = wb.getSheetAt(0);
		    int firstRowNum = sheetAt.getFirstRowNum();  
		    int lastRowNum = sheetAt.getLastRowNum(); 
		    BasicDBList columnList = new BasicDBList();
		    XSSFRow rowIn = null;
		    Cell cellIn = null;
		    for (int i = firstRowNum+1; i <= lastRowNum; i++){
		    	rowIn = sheetAt.getRow(i);          //取得第i行  
		    	cellIn = rowIn.getCell(0);        //取得i行的第一列  
		        String cellValue = cellIn.getStringCellValue().trim();  
		        System.out.println(cellValue);      
		        columnList.add(cellValue);  
		    }
			
			DBCollection useropRecord = db.getCollection("userop_record");
			
			BasicDBList list = new BasicDBList();
			list.add("G2_011");//自选股
			list.add("G2_012");//文字一分钟
			list.add("G2_005_01");//实务套表
			list.add("G2_004");//股票专家
			list.add("G2_006");//投资台历
			list.add("G2_013");//牛股头条
			
			//寻宝游戏
			list.add("G2_007_01");//寻宝线索
			list.add("G2_007_02");//我的股票书签
			list.add("G2_007_03");//我的寻宝池-研究报告
			
			//热点追踪
			list.add("G2_009_01");//个股报告
			list.add("G2_009_02");//行业报告
			
			list.add("G2_001");//研报中心
			
			list.add("G2_017");//VIP交流区
			
			Map<String, String> codeMap = new HashMap<String, String>();
			codeMap.put("G2_011", "自选股");
			codeMap.put("G2_012", "文字一分钟");
			codeMap.put("G2_005_01", "实务套表");
			codeMap.put("G2_004", "股票专家");
			codeMap.put("G2_006", "投资台历");
			codeMap.put("G2_013", "牛股头条");
			codeMap.put("G2_007_01", "寻宝线索");
			codeMap.put("G2_007_02", "我的股票书签");
			codeMap.put("G2_007_03", "我的寻宝池-研究报告");
			codeMap.put("G2_009_01", "个股报告");
			codeMap.put("G2_009_02", "行业报告");
			codeMap.put("G2_001", "研报中心");
			codeMap.put("G2_017", "VIP交流区");
			
			
			BasicDBObject query = new BasicDBObject();
			query.append("status", 1);
			BasicDBList typeList = new BasicDBList();
			typeList.add(0);
			typeList.add(1);
			query.append("type", new BasicDBObject("$in", typeList));
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.getBeginOfDay(DateUtil.stringToDate("2016-08-01", "yyyy-MM-dd")));
			timeQuery.append("$lte", DateUtil.getEndOfDay(DateUtil.stringToDate("2016-08-31", "yyyy-MM-dd")));
			
			
			query.append("createtime", timeQuery);
			query.append("code", new BasicDBObject("$in", list));
			query.append("account_name", new BasicDBObject("$in",columnList));
			Map<String, Integer> map = new HashMap<String, Integer>();
			
			DBCursor cursor = useropRecord.find(query);
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				String accountName = o.get("account_name") == null ? "暂时没有用户名" : o.get("account_name").toString();
				String code = o.get("code").toString();
				String accountNameCode = accountName + ";" +code;
				Integer count = map.get(accountNameCode);
				if(count == null){
					map.put(accountNameCode, 1);
				}else{
					count++;
					map.put(accountNameCode, count);
				}
			}
			cursor.close();
			
			
			XSSFWorkbook workbook = new XSSFWorkbook(); //创建一个空白的工作薄
			XSSFSheet sheet = workbook.createSheet("账户登录情况");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("用户名");
			cell = row.createCell(1);
			cell.setCellValue("模块名称");
			cell = row.createCell(2);
			cell.setCellValue("访问次数");
			
			int rowUserop=1;
			
			for(Map.Entry<String, Integer> m : map.entrySet()){
				Integer value = m.getValue();
				String key = m.getKey();
				String[] split = key.split(";");
				String accountName = split[0];
				String code = split[1];
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(accountName);
				cell = row.createCell(1);
				cell.setCellValue(codeMap.get(code));
				cell = row.createCell(2);
				cell.setCellValue(value);
				rowUserop++;
			}
			
			long time = System.currentTimeMillis();
			String fileName = "8月份访问情况"+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			wb.close();
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 付费用户留存量与活跃度
	 * 
	 * @author yutao
	 * @date 2016年9月9日下午4:14:48
	 */
	public void exportLogin(){
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("42.62.50.226", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection userSum = db.getCollection("user_sum");
			DBCollection payUsersActive = db.getCollection("pay_users_active");
			
			//找出登录过3.0的用户（只去统计8月5号之后的数据）
//			Date maxDate = DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd");
			DBCursor userSumCursor = userSum.find();
			Map<String, Integer> userSumMap = new HashMap<String, Integer>();
			
			while(userSumCursor.hasNext()){
				DBObject o = userSumCursor.next();
				Object payUserCount = o.get("pay_user_count");
				Object date = o.get("statistics_time");
				if(date !=null){
					String dateToString = DateUtil.dateToString((Date)date, "yyyy-MM-dd");
					userSumMap.put(dateToString, Integer.parseInt(payUserCount.toString()));
				}
			}
			userSumCursor.close();
			Set<String> dateSet = new LinkedHashSet<String>();
			DBCursor cursor = payUsersActive.find().sort(new BasicDBObject("statistics_time", -1));
			Map<String, String> activeMap = new HashMap<String, String>();
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				Object date = o.get("statistics_time");
				if(date != null){
					String activeSum = o.get("active_sum").toString();
					String remainSum = o.get("remain_sum").toString();
					String dateString = DateUtil.dateToString((Date)date, "yyyy-MM-dd");
					dateSet.add(dateString);
					
					activeMap.put(dateString, activeSum+";"+remainSum);
				}
			}
			cursor.close();
			

			
			XSSFWorkbook workbook = new XSSFWorkbook(); //创建一个空白的工作薄
			XSSFSheet sheet = workbook.createSheet("账户登录情况");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("统计时间");
			cell = row.createCell(1);
			cell.setCellValue("留存度");
			cell = row.createCell(2);
			cell.setCellValue("活跃度");
			cell = row.createCell(3);
			cell.setCellValue("付费用户总数");
			
			int rowUserop=1;
			
			for(String d : dateSet){
				
				 String activeRemain = activeMap.get(d);
				 String[] split = activeRemain.split(";");
				 int remain = Integer.parseInt(split[0]);
				 int active = Integer.parseInt(split[1]);
				 Integer paySum = userSumMap.get(d);
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(d);
				cell = row.createCell(1);
				cell.setCellValue(remain);
				cell = row.createCell(2);
				cell.setCellValue(active);
				cell = row.createCell(3);
				cell.setCellValue(paySum);
				rowUserop++;
			}
			
			long time = System.currentTimeMillis();
			String fileName = "付费用户留存量与活跃度"+time+".xlsx";
			
//			String fileNa = java.net.URLEncoder.encode(fileName != null ? fileName : "数据导出.xls", "UTF-8");
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 当前付费用户账号信息
	 * 
	 * @author yutao
	 * @date 2016年9月9日下午4:15:10
	 */
	public void exportPayUser(){

		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("42.62.50.226", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection userSum = db.getCollection("user_sum");
			DBCollection useropRecord = db.getCollection("userop_record");
			DBCollection accountRelation = db.getCollection("accountrelation");
			
			//找出登录过3.0的用户（只去统计8月5号之后的数据）
			Date maxDate = DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd");
			DBCursor userSumCursor = userSum.find().sort(new BasicDBObject("statistics_time", -1)).limit(1);
			Integer userCount = 0;
			Set<Integer> accountSet = new HashSet<Integer>();
			while(userSumCursor.hasNext()){
				DBObject o = userSumCursor.next();
				Object statisticsTime = o.get("statistics_time");
				if(statisticsTime != null){
					maxDate = (Date)statisticsTime;
					userCount = Integer.parseInt(o.get("user_count").toString());
					Object accountIdObject = o.get("accountIds");
					if(accountIdObject != null){
						accountSet.addAll((ArrayList)accountIdObject);
					}
				}
			}
			userSumCursor.close();
			
			BasicDBObject useropQuery = new BasicDBObject();
			useropQuery.append("status", 1);
			BasicDBList list = new BasicDBList();
			list.add("S3_01");
			list.add("S3_01_00");
			list.add("S3_02");
			list.add("S3_03");
			list.add("S3_03_00");
			list.add("S3_04");
			list.add("S3_05");
			list.add("S3_06_01");
			list.add("S3_06_02");
			list.add("S3_06_00");
			useropQuery.append("code", new BasicDBObject("$nin", list));
			if(DateUtil.compare(maxDate, DateUtil.getYesterday())==0){//只需要去找今天的
				useropQuery.append("createtime", new BasicDBObject("$gte", DateUtil.getBeginOfDay()).append("$lt", new Date()));
			}else{
				useropQuery.append("createtime", new BasicDBObject("$gte", maxDate).append("$lt", new Date()));
			}
			useropQuery.append("org_id", new BasicDBObject("$ne",4));
			useropQuery.append("type", 3);
			Map<String, Object> result = new HashMap<String, Object>();
			
			DBCursor cursor = useropRecord.find(useropQuery, new BasicDBObject("_id",0).append("account_id", 1).append("createtime", 1)); //埋点表
			Set<Integer> accountIds = new HashSet<Integer>();//登陆过3.0的用户accountId
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				String accountId = o.get("account_id").toString();
				accountIds.add(Integer.parseInt(accountId));
			}
			cursor.close();
			accountIds.removeAll(accountSet);//移除过去的登录accountId,这样获取到的就是当天首次登录的accountId
			
			result.put("user_count", accountIds.size()+userCount); //当前的用户总数
			accountIds.addAll(accountSet);//这样就是总的当前登录的accountid集合
			
			DBCollection payUser = db.getCollection("t_paySys_user_permi");
			BasicDBObject ptQuery = new BasicDBObject();
			ptQuery.append("open_date", new BasicDBObject("$lte",DateUtil.getBeginOfDay()));//有些付费用户开始时间会大于今天，这种暂时不算
			ptQuery.append("end_date", new BasicDBObject("$gt", DateUtil.getBeginOfDay()));//其中end_date等于今天都是失效的数据
			Set<String> paySet = new HashSet<String>();
			Set<String> trySet = new HashSet<String>();
			DBCursor payCursor = payUser.find(ptQuery);
			while(payCursor.hasNext()){//付费为1，试用为0
				DBObject o = payCursor.next();
				Integer accountId = Integer.parseInt(o.get("user_id").toString());
				String isPay = o.get("isPay").toString();
				if(accountIds.contains(accountId)){//包含说明它是登录过3.0的
					if("1".equals(isPay)){
						paySet.add(o.get("user_id").toString());
					}else if("0".equals(isPay)){
						trySet.add(o.get("user_id").toString());
					}
				}
			}
			payCursor.close();
			
			Set<Integer> payIntSet = new HashSet<Integer>();
			for(String p : paySet){
				payIntSet.add(Integer.parseInt(p));
			}
//			Set<String> trySet = payTryMap.get("trySet");
			List<Integer> payList = new ArrayList<Integer>(payIntSet);
			BasicDBObject query = new BasicDBObject();
			BasicDBObject q = new BasicDBObject();
			q.append("$in", payList);
			query.append("account_id", q);
			
			XSSFWorkbook workbook = new XSSFWorkbook(); //创建一个空白的工作薄
			XSSFSheet sheet = workbook.createSheet("账户登录情况");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("用户名");
			cell = row.createCell(1);
			cell.setCellValue("机构类型");
			cell = row.createCell(2);
			cell.setCellValue("部门");
			cell = row.createCell(3);
			cell.setCellValue("gogoal账号");
			
			int rowUserop=1;
			DBCursor payAccCursor = accountRelation.find(query);
			while(payAccCursor.hasNext()){
				DBObject o = payAccCursor.next();
				Object fullName = o.get("full_name");
				String name = fullName == null ? "暂时没有姓名" : fullName.toString();
				String orgName = o.get("org_name") == null ? "暂无机构名称" : o.get("org_name").toString();
				String department = o.get("department") == null ? "暂无部门名称" : o.get("department").toString();
				String accountName = o.get("account_name") == null ? "暂无账号" : o.get("account_name").toString();
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(name);
				cell = row.createCell(1);
				cell.setCellValue(orgName);
				cell = row.createCell(2);
				cell.setCellValue(department);
				cell = row.createCell(3);
				cell.setCellValue(accountName);
				rowUserop++;
				
			}
			payAccCursor.close();
			
			long time = System.currentTimeMillis();
			String fileName = "当前付费用户账号信息"+time+".xlsx";
			
//			String fileNa = java.net.URLEncoder.encode(fileName != null ? fileName : "数据导出.xls", "UTF-8");
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
  
	}
	
	
	/**
	 * 导出用户信息的代码
	 * 
	 * @author yutao
	 * @date 2016年8月30日下午2:21:25
	 */
	public void exportUser(){

		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("42.62.50.226", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection useropRecord = db.getCollection("accountrelation");
		
			BasicDBObject match = new BasicDBObject();
			BasicDBObject matchDB = new BasicDBObject();
			matchDB.append("org_name", new BasicDBObject("$regex","海通证券"));
			match.append("$match", matchDB);
			
			BasicDBObject group = new BasicDBObject();
			BasicDBObject groupDB = new BasicDBObject();
			groupDB.append("_id", "$full_name");
			groupDB.append("account_id", new BasicDBObject("$first","$account_id"));
			groupDB.append("account_name", new BasicDBObject("$first","$account_name"));
			groupDB.append("org_name", new BasicDBObject("$first","$org_name"));
			groupDB.append("department", new BasicDBObject("$first","$department"));
			groupDB.append("duty", new BasicDBObject("$first","$duty"));
			
			group.append("$group", groupDB);
			
			XSSFWorkbook workbook = new XSSFWorkbook(); //创建一个空白的工作薄
			XSSFSheet sheet = workbook.createSheet("海通证券");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("用户名");
			cell = row.createCell(1);
			cell.setCellValue("账号id");
			cell = row.createCell(2);
			cell.setCellValue("账号名");
			cell = row.createCell(3);
			cell.setCellValue("机构名称");
			cell = row.createCell(4);
			cell.setCellValue("部门");
			cell = row.createCell(5);
			cell.setCellValue("岗位");
			
			AggregationOutput output = useropRecord.aggregate(match, group);
			Iterable<DBObject> iterable = output.results();
			int rowUserop=1;
			for(DBObject o:iterable){
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				Object userName = o.get("_id");
				if(userName != null){
					cell.setCellValue(userName.toString());
					cell = row.createCell(1);
					cell.setCellValue(o.get("account_id").toString());
					cell = row.createCell(2);
					cell.setCellValue(o.get("account_name").toString());
					cell = row.createCell(3);
					cell.setCellValue(o.get("org_name").toString());
					cell = row.createCell(4);
					cell.setCellValue(o.get("department")==null?"":o.get("department").toString());
					cell = row.createCell(5);
					cell.setCellValue(o.get("duty")==null?"":o.get("duty").toString());
				}
				rowUserop++;
			}
			
			long time = System.currentTimeMillis();
			String fileName = "海通证券用户id"+time+".xlsx";
			
//			String fileNa = java.net.URLEncoder.encode(fileName != null ? fileName : "数据导出.xls", "UTF-8");
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
