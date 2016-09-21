package gogoalExample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

import utils.DateUtil;
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
			ServerAddress serverAddress = new ServerAddress("42.62.50.226", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			//连接数据库 end
			DB db = mongoClient.getDB("gg_openapi");
			
			DBCollection versionMessage = db.getCollection("version_upgrade_log");
			
			BasicDBObject query = new BasicDBObject();
			BasicDBList list = new BasicDBList();
			list.add(2);
			list.add(4);
			query.append("type", new BasicDBObject("$in", list));
			
			XSSFWorkbook workbook = new XSSFWorkbook(); //创建一个空白的工作薄
			XSSFSheet sheet = workbook.createSheet("版本升级情况");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("日期");
			cell = row.createCell(1);
			cell.setCellValue("版本");
			cell = row.createCell(2);
			cell.setCellValue("升级日志");
			cell = row.createCell(3);
			cell.setCellValue("当前版本");
			
			DBCursor cursor = versionMessage.find(query);
			
			int rowUserop=1;
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				Object intoDate = o.get("into_date");
				if(intoDate != null){
					String date = DateUtil.dateToString((Date)intoDate, "yyyy-MM-dd");
					String content = o.get("content")==null ? "" : o.get("content").toString();
					row = sheet.createRow(rowUserop);
					cell = row.createCell(0);
					cell.setCellValue(date);
					cell = row.createCell(1);
					String type = o.get("type").toString();
					if("2".equals(type)){
						cell.setCellValue("gogoal2.0");
					}else if("4".equals(type)){
						cell.setCellValue("gogoal3.0");
					}
					cell = row.createCell(2);
					cell.setCellValue(content);
					cell = row.createCell(3);
					cell.setCellValue(o.get("current_version").toString());
					rowUserop++;
				}
				
			}
			cursor.close();
			
			
			
			long time = System.currentTimeMillis();
			String fileName = "版本升级情况"+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
		}catch (IOException e) {
			e.printStackTrace();
		}
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
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
