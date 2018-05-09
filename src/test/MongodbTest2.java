package test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import utils.ggservice.common.DateUtil;

public class MongodbTest2 {
	public static void main(String[] args) {
		MongoCollection<Document> useropRecord = null;
//		Document deleDoc = new Document();
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createCredential("gg_user_db_rw", "gg_user_db", "gg_user_db_rw.gogoal.com".toCharArray());
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
			
			useropRecord = database.getCollection("userop_record");//埋点表
			MongoCollection<Document> accountrelation = database.getCollection("accountrelation");
			
			BasicDBObject sort = new BasicDBObject();
			sort.append("createtime", 1);
			
			BasicDBObject match = new BasicDBObject();
			match.append("type", 3).append("user_type", new BasicDBObject("$gt", 0)).append("status", 1)
				 .append("code", "S3_06");
			
			List<String> account = new ArrayList<String>();
			account.add("E00035582");
			account.add("E00007934");
			account.add("E00007936");
			account.add("E00020126");
			account.add("E00021255");
			
			Document doc = new Document();
			doc.append("nick_name", new Document("$in", account));
			List<Long> accountList = new ArrayList<>();
			FindIterable<Document> filter = accountrelation.find(doc).projection(new Document("account_id", 1).append("full_name", 1));
			MongoCursor<Document> iterator = filter.iterator();
			Map<Long, String> map = new HashMap<>();
			while(iterator.hasNext()){
				Document o = iterator.next();
				Long accountId = o.getLong("account_id");
				map.put(accountId, o.getString("full_name"));
				accountList.add(accountId);
			}
			accountList.add(394565L);
			
			match.append("account_name", new BasicDBObject("$in", account));
			
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.stringToDate("2017-01-01", "yyyy-MM-dd"));
			match.append("date", timeQuery);
			
			BasicDBObject group = new BasicDBObject();
			group.append("_id", "$account_name").append("count", new BasicDBObject("$sum", 1));
			
			List<BasicDBObject> piList = new ArrayList<>();
//			piList.add(new BasicDBObject("$sort", sort));
			piList.add(new BasicDBObject("$match", match));
			piList.add(new BasicDBObject("$group", group));
			
			AggregateIterable<Document> iterable = useropRecord.aggregate(piList);
			MongoCursor<Document> cursor = iterable.iterator();
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("登录次数");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("账号");
			cell = row.createCell(1);
			cell.setCellValue("名称");
			cell = row.createCell(2);
			cell.setCellValue("次数");
			int rowUserop=1;
			while(cursor.hasNext()){
				Document o = cursor.next();
				String accountId = o.getString("_id");
				int count = o.getInteger("count");
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(accountId);
				
				cell = row.createCell(1);
				cell.setCellValue(map.get(accountId));
				
				cell = row.createCell(2);
				cell.setCellValue(count);
				rowUserop++;
			}
			cursor.close();
			
			long time = System.currentTimeMillis();
			String fileName = "登录次数"+time+".xlsx";
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 连续四周
	 * @param dateList
	 * @author yutao
	 * @param resultAcc 
	 * @param accountId 
	 * @date 2017年10月27日下午2:22:19
	 */
	private static void fourWeek(List dateList, Map<String, Integer> result, Map<String, List<String>> resultAcc, Long accountId) {
		
		Map<String, List<Date>> groupDateMap = new HashMap<>();
		for(int i=0; i < dateList.size(); i++){
			Date date = (Date)dateList.get(i);
			String dateToString = DateUtil.dateToString(date, "yyyy-MM");
			
			List<Date> list = groupDateMap.get(dateToString);
			if(list == null){
				list = new ArrayList<Date>();
				list.add(date);
				groupDateMap.put(dateToString, list);
			}else{
				list.add(date);
			}
		}
		
		qufen(groupDateMap, result, resultAcc, accountId);
	}

	/**
	 * 具体某个月的
	 * @param groupDateMap
	 * @author yutao
	 * @param result 
	 * @param resultAcc 
	 * @param accountId 
	 * @return 
	 * @date 2017年10月27日下午2:37:13
	 */
	private static void qufen(Map<String, List<Date>> groupDateMap, Map<String, Integer> result, Map<String, List<String>> resultAcc, Long accountId) {
		for(Map.Entry<String, List<Date>> m : groupDateMap.entrySet()){
			List<Date> value = m.getValue();//年月日
			String key = m.getKey();//年月
			if(value.size()<4){
				continue;
			}
			
			List<Boolean> ll = new ArrayList<Boolean>();
			ll.add(false);
			ll.add(false);
			ll.add(false);
			ll.add(false);
			//下面遍历的是某个月的数据
			for(Date d : value){
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				cal.set(Calendar.DAY_OF_MONTH, 1);//定位到该月1号
				
				Date date1 = cal.getTime();
				
				cal.add(Calendar.DAY_OF_MONTH, 7);
				Date date2 = cal.getTime();
				
				cal.add(Calendar.DAY_OF_MONTH, 7);
				Date date3 = cal.getTime();
				
				cal.add(Calendar.DAY_OF_MONTH, 7);
				Date date4 = cal.getTime();
				
				if(DateUtil.compare(d, date1) >= 0 && DateUtil.compare(d, date2)<0 && !ll.get(0)){
					ll.add(0, true);
					ll.remove(1);
				}else if(DateUtil.compare(d, date2)>=0 && DateUtil.compare(d, date3)<0 && !ll.get(1)){
					ll.add(1, true);
					ll.remove(2);
				}else if(DateUtil.compare(d, date3)>=0 && DateUtil.compare(d, date4)<0 && !ll.get(2)){
					ll.add(2, true);
					ll.remove(3);
				}else if(DateUtil.compare(d, date4)>=0 && !ll.get(3)){
					ll.add(3, true);
					ll.remove(4);
				}
			}
			
			if((ll.get(0) && ll.get(1)) || (ll.get(1) && ll.get(2)) && (ll.get(2) && ll.get(3))){
				Integer in = result.get(key);
				if(in == null){
					result.put(key, 1);
				}else{
					result.put(key, ++in);
				}
				
			}
			/*if(ll.get(0) && ll.get(1) && ll.get(2) && ll.get(3)){
				Integer in = result.get(key);
				if(in == null){
					result.put(key, 1);
				}else{
					result.put(key, ++in);
				}
				List<String> list = resultAcc.get(key);
				if(list == null){
					list = new ArrayList<String>();
					list.add(accountId.toString());
					resultAcc.put(key, list);
				}
				list.add(accountId.toString());
			}*/
		}
	}
}
