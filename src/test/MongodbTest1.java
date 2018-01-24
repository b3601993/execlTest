package test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import utils.DateUtil;

public class MongodbTest1 {
	public static void main(String[] args) {
		
		try {
		//连接数据库 start
		MongoCredential credential = MongoCredential.createCredential("gg_user_db", "gg_user_db", "gg_user_db".toCharArray());
		ServerAddress serverAddress;
		serverAddress = new ServerAddress("106.75.51.20", 35724);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(addrs, credentials);
		System.out.println("Connect to database successfully");
		//连接数据库 end
		
		MongoDatabase database = mongoClient.getDatabase("gg_user_db");
		MongoCollection<Document> useropRecord = database.getCollection("userop_record");
		MongoCollection<Document> accountRelation = database.getCollection("accountrelation");
		
		Document accountQuery = new Document();
		List<Integer> li = new ArrayList<Integer>();
		li.add(4);
		li.add(0);
		accountQuery.append("org_id", new BasicDBObject("$in", li));
		Pattern pattern = Pattern.compile("[朝阳,测试,null,mac,a-zA-Z0-9]");
		List<String> ll = new ArrayList<String>();
		ll.add(null);
		ll.add("");
		accountQuery.append("full_name", new Document("$not", pattern).append("$nin", ll));
		accountQuery.append("gender", new Document("$ne", ""));
		
		FindIterable<Document> accountF = accountRelation.find(accountQuery);
		MongoCursor<Document> accountIt = accountF.iterator();
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		while(accountIt.hasNext()){
			Document o = accountIt.next();
			String fullName = o.getString("full_name");
			String department = o.getString("department");//部门
			String userId = o.get("user_id").toString();//账号ID
			Date date = o.getDate("insert_date");
			String dateToString = DateUtil.dateToString(date, "yyyy-MM-dd");
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("full_name", fullName);
			map.put("department", department);
			map.put("insert_date", dateToString);
			map.put("account_id", userId);
			result.put(userId, map);
		}
		accountIt.close();
		
		List<String> list = new ArrayList<String>();
		list.add("G3_02"); //寻宝
		list.add("G3_03"); //事件
		list.add("G3_04"); //主题
		list.add("G3_05"); //诊股
		list.add("G3_06"); //研报
		list.add("G3_07"); //自选
		list.add("G3_08"); //个股
		list.add("G3_09"); //行情
		list.add("G3_10"); //数据
		list.add("G3_11"); //直播
		list.add("G3_13"); //业绩
		list.add("G3_17"); //好公司
		list.add("G3_18"); //资讯
		list.add("G3_15"); //私募
		list.add("G3_16"); //社交
		list.add("S3_06"); //登录
		
		Document userQuery = new Document();
		userQuery.append("status", 1);
		userQuery.append("code", new Document("$in", list));
		
		Set<String> keySet = result.keySet();
		List<Integer> accountList = new ArrayList<Integer>();
		for(String s : keySet){
			accountList.add(Integer.valueOf(s));
		}
		
		Document timeQuery = new Document();
		timeQuery.append("$gte", DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd"));
		timeQuery.append("$lte", DateUtil.stringToDate("2016-12-31", "yyyy-MM-dd"));
		userQuery.append("createtime", timeQuery);
		
		userQuery.append("account_id", new Document("$in", accountList));
		
		long count = useropRecord.count(userQuery);
		System.out.println(count);
		
		FindIterable<Document> userFi = useropRecord.find(userQuery);
		MongoCursor<Document> userCursor = userFi.iterator();
		
		Map<String, Integer> moduleCount = new HashMap<String, Integer>();
		Map<String, Integer> moduleDayCount = new HashMap<String, Integer>();
		
		
		Set<String> daySet = new HashSet<String>();
		Set<String> accountSet = new HashSet<String>();
		while(userCursor.hasNext()){
			Document o = userCursor.next();
			String accountId = o.get("account_id").toString();
			accountSet.add(accountId);
			String code = o.getString("code");
			Date createtime = o.getDate("createtime");
			String dateString = DateUtil.dateToString(createtime, "yyyy-MM-dd");
			
			String accountIdDate = accountId + ";" + code + ";" + dateString;
			String accountIdCode = accountId + ";" + code;
			
			Integer moduCountInt = moduleCount.get(accountIdCode);//模块次数
			
			if(moduCountInt == null){//用户模块次数
				moduleCount.put(accountIdCode, 1);
			}else{
				moduleCount.put(accountIdCode, ++moduCountInt);
			}
			
			Integer moduDateCount = moduleDayCount.get(accountIdCode);//模块天次
			
			if(moduDateCount == null){
				moduleDayCount.put(accountIdCode, 1);
			}else{
				if(!daySet.contains(accountIdDate)){
					moduleDayCount.put(accountIdCode, ++moduDateCount);
				}
			}
			daySet.add(accountIdDate);
		}
		userCursor.close();
		
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("内部用户使用情况");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("姓名");
		cell = row.createCell(1);
		cell.setCellValue("部门");
		cell = row.createCell(2);
		cell.setCellValue("起始时间");
		cell = row.createCell(3);
		cell.setCellValue("登录");
		cell = row.createCell(4);
		cell.setCellValue("寻宝");
		cell = row.createCell(5);
		cell.setCellValue("事件");
		cell = row.createCell(6);
		cell.setCellValue("主题");
		cell = row.createCell(7);
		cell.setCellValue("诊股");
		cell = row.createCell(8);
		cell.setCellValue("研报");
		cell = row.createCell(9);
		cell.setCellValue("自选");
		cell = row.createCell(10);
		cell.setCellValue("个股");
		cell = row.createCell(11);
		cell.setCellValue("行情");
		cell = row.createCell(12);
		cell.setCellValue("数据");
		cell = row.createCell(13);
		cell.setCellValue("直播");
		cell = row.createCell(14);
		cell.setCellValue("业绩");
		cell = row.createCell(15);
		cell.setCellValue("好公司");
		cell = row.createCell(16);
		cell.setCellValue("资讯");
		cell = row.createCell(17);
		cell.setCellValue("私募");
		cell = row.createCell(18);
		cell.setCellValue("社交");
		cell = row.createCell(19);
		cell.setCellValue("社交");
		
		int rowUserop=1;
		for(String s : accountSet){
			row = sheet.createRow(rowUserop);
			cell = row.createCell(0);
			
			Map<String, Object> map = result.get(s);
			cell.setCellValue(map.get("full_name").toString());//姓名
			
			cell = row.createCell(1);
			cell.setCellValue(map.get("department")==null?"":map.get("department").toString());//部门
			
			cell = row.createCell(2);
			cell.setCellValue(map.get("insert_date")==null?"":map.get("insert_date").toString());//起始时间
			cell = row.createCell(3);
			cell.setCellValue(moduleCount.get(s+";"+"S3_06")+"("+moduleDayCount.get(s+";"+"S3_06")+")");//登录
			cell = row.createCell(4);
			cell.setCellValue(moduleCount.get(s+";"+"G3_02")+"("+moduleDayCount.get(s+";"+"G3_02")+")");//寻宝
			cell = row.createCell(5);
			cell.setCellValue(moduleCount.get(s+";"+"G3_03")+"("+moduleDayCount.get(s+";"+"G3_03")+")");//寻宝
			cell = row.createCell(6);
			cell.setCellValue(moduleCount.get(s+";"+"G3_04")+"("+moduleDayCount.get(s+";"+"G3_04")+")");//寻宝
			cell = row.createCell(7);
			cell.setCellValue(moduleCount.get(s+";"+"G3_05")+"("+moduleDayCount.get(s+";"+"G3_05")+")");//寻宝
			cell = row.createCell(8);
			cell.setCellValue(moduleCount.get(s+";"+"G3_06")+"("+moduleDayCount.get(s+";"+"G3_06")+")");//寻宝
			cell = row.createCell(9);
			cell.setCellValue(moduleCount.get(s+";"+"G3_07")+"("+moduleDayCount.get(s+";"+"G3_07")+")");//寻宝
			cell = row.createCell(10);
			cell.setCellValue(moduleCount.get(s+";"+"G3_08")+"("+moduleDayCount.get(s+";"+"G3_08")+")");//寻宝
			cell = row.createCell(11);
			cell.setCellValue(moduleCount.get(s+";"+"G3_09")+"("+moduleDayCount.get(s+";"+"G3_09")+")");//寻宝
			cell = row.createCell(12);
			cell.setCellValue(moduleCount.get(s+";"+"G3_10")+"("+moduleDayCount.get(s+";"+"G3_10")+")");//寻宝
			cell = row.createCell(13);
			cell.setCellValue(moduleCount.get(s+";"+"G3_11")+"("+moduleDayCount.get(s+";"+"G3_11")+")");//寻宝
			cell = row.createCell(14);
			cell.setCellValue(moduleCount.get(s+";"+"G3_13")+"("+moduleDayCount.get(s+";"+"G3_13")+")");//寻宝
			cell = row.createCell(15);
			cell.setCellValue(moduleCount.get(s+";"+"G3_17")+"("+moduleDayCount.get(s+";"+"G3_17")+")");//寻宝
			cell = row.createCell(16);
			cell.setCellValue(moduleCount.get(s+";"+"G3_18")+"("+moduleDayCount.get(s+";"+"G3_18")+")");//寻宝
			cell = row.createCell(17);
			cell.setCellValue(moduleCount.get(s+";"+"G3_15")+"("+moduleDayCount.get(s+";"+"G3_15")+")");//寻宝
			cell = row.createCell(18);
			cell.setCellValue(moduleCount.get(s+";"+"G3_16")+"("+moduleDayCount.get(s+";"+"G3_16")+")");//寻宝
			rowUserop++;
		}
		
		long time = System.currentTimeMillis();
		String fileName = "内部用户使用情况"+time+".xlsx";
		
		FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
		workbook.write(out);
		workbook.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
