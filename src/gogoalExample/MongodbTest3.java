package gogoalExample;

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

public class MongodbTest3 {
	
	
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
		accountQuery.append("full_name", new Document("$not", pattern).append("$gt", ""));
		accountQuery.append("gender", new Document("$gt", ""));
		
		FindIterable<Document> accountF = accountRelation.find(accountQuery);
		MongoCursor<Document> accountIt = accountF.iterator();
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		while(accountIt.hasNext()){
			Document o = accountIt.next();
			String fullName = o.getString("full_name");
			String accountName = o.getString("account_name");//账号
			String duty = o.getString("duty");//职务
			String gender = o.getString("gender");//性别
			String department = o.getString("department");//部门
			String userId = o.get("user_id").toString();//账号ID
			Date date = o.getDate("insert_date");
			String dateToString = DateUtil.dateToString(date, "yyyy-MM-dd");
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("full_name", fullName);
			map.put("department", department);
			map.put("insert_date", dateToString);
			map.put("account_id", userId);
			map.put("accountName", accountName);
			map.put("duty", duty);
			map.put("gender", gender);
			result.put(userId, map);
		}
		accountIt.close();
		
		Set<String> keySet = result.keySet();
		List<Integer> accountList = new ArrayList<Integer>();
		for(String s : keySet){
			accountList.add(Integer.valueOf(s));
		}
		
		Document userQuery = new Document();
		userQuery.append("code", "G3_02").append("status", 1)
				 .append("org_id", new BasicDBObject("$ne", 4))
				 .append("type", 3);
		
		Document timeQuery = new Document();
		timeQuery.append("$gte", DateUtil.stringToDate("2017-07-01", "yyyy-MM-dd"));
		timeQuery.append("$lte", DateUtil.stringToDate("2018-01-31", "yyyy-MM-dd"));
		userQuery.append("date", timeQuery);
		
		userQuery.append("account_id", new Document("$in", accountList));
		
		long count = useropRecord.count(userQuery);
		System.out.println(count);
		
		FindIterable<Document> userFi = useropRecord.find(userQuery).sort(new Document("createtime", -1));
		MongoCursor<Document> userCursor = userFi.iterator();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("寻宝使用情况");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("账号");
		cell = row.createCell(1);
		cell.setCellValue("姓名");
		cell = row.createCell(2);
		cell.setCellValue("机构");
		cell = row.createCell(3);
		cell.setCellValue("职位");
		cell = row.createCell(4);
		cell.setCellValue("性别");
		cell = row.createCell(5);
		cell.setCellValue("使用时间");
		
		int rowUserop=1;
		while(userCursor.hasNext()){
			row = sheet.createRow(rowUserop);
			
			
			Document o = userCursor.next();
			String accountId = o.get("account_id").toString();
			Map<String, Object> map = result.get(accountId);
			
			cell = row.createCell(0);
			cell.setCellValue(map.get("accountName")==null?"":map.get("accountName").toString());
			
			cell = row.createCell(1);
			cell.setCellValue(map.get("full_name").toString());
			
			cell = row.createCell(2);
			cell.setCellValue(o.getString("org_name"));
			
			cell = row.createCell(3);
			cell.setCellValue(map.get("duty")==null?"":map.get("duty").toString());
			
			cell = row.createCell(4);
			cell.setCellValue(map.get("gender")==null?"":map.get("gender").toString());
			
			cell = row.createCell(5);
			cell.setCellValue(DateUtil.dateToString(o.getDate("createtime"), "yyyy-MM-dd HH:mm:ss"));
			rowUserop++;
		}
		userCursor.close();
		
		long time = System.currentTimeMillis();
		String fileName = "寻宝使用情况"+time+".xlsx";
		
		FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
		workbook.write(out);
		workbook.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
