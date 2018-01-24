package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
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
			
			BasicDBObject sort = new BasicDBObject();
			sort.append("createtime", 1);
			
			BasicDBObject match = new BasicDBObject();
			match.append("type", 3).append("org_id", new BasicDBObject("$ne", 4)).append("status", 1)
			.append("code", "S3_06");
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd"));
			match.append("date", timeQuery);
			
			BasicDBObject group = new BasicDBObject();
			group.append("_id", "$account_id").append("date_list", new BasicDBObject("$addToSet", "$date"));
			
			List<BasicDBObject> piList = new ArrayList<>();
			piList.add(new BasicDBObject("$sort", sort));
			piList.add(new BasicDBObject("$match", match));
			piList.add(new BasicDBObject("$group", group));
			
			AggregateIterable<Document> iterable = useropRecord.aggregate(piList);
			MongoCursor<Document> cursor = iterable.iterator();
			Map<String, Integer> result = new HashMap<String, Integer>();
			Map<String, List<String>> resultAcc = new HashMap<String, List<String>>();
			
			while(cursor.hasNext()){
				Document o = cursor.next();
				Long accountId = o.getLong("_id");
				List dateList = o.get("date_list", List.class);
				if(dateList.size() < 4){
					continue;
				}
				
				fourWeek(dateList, result, resultAcc, accountId);
				
				
			}
			cursor.close();
			
			System.out.println(result);
//			System.out.println(resultAcc);
			
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
