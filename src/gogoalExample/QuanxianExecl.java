package gogoalExample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import gogoalExample.common.QuanxianCommon;
import utils.ggservice.common.DateUtil;

public class QuanxianExecl {

	private static MongoDatabase database = null;
	
	public static void main(String[] args) {

		MongoCollection<Document> useropRecord = null;
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
			
			database = mongoClient.getDatabase("gg_user_db");
			useropRecord = database.getCollection("userop_record");//埋点表
			
			Date startDate = DateUtil.stringToDate("2018-05-01", "yyyy-MM-dd");
			Date endDate = DateUtil.stringToDate("2018-06-01", "yyyy-MM-dd");
			
			//获取账号登录数
//			getAccountLoginCount(startDate, endDate);
			
			//获取每个月的新用户
			getMonthNewAccountIds(startDate, endDate);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取每个月的新用户
	 * 
	 * @author yutao
	 * @date 2018年6月14日上午10:33:13
	 */
	private static void getMonthNewAccountIds(Date startDate, Date endDate) {
		Date sDate = startDate, ssDate = startDate;
		LinkedHashMap<Date, Set<Long>> monthNewAccountIdMap = new LinkedHashMap<>();
		LinkedHashMap<Date, Set<Long>> oneNewAccountIdMap = new LinkedHashMap<>();
		LinkedHashMap<Date, Set<Long>> oneLoginAccountIdMap = new LinkedHashMap<>();
		LinkedHashMap<Date, Set<Long>> twoLoginAccountIdMap = new LinkedHashMap<>();
		
		//每个月的新用户
		Date bDate = DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd");
		while(DateUtil.compare(sDate, endDate) < 0){
			//当月新增的用户
			Set<Long> monthNewAccountIdSet = getMonthNewAccountIds(sDate);
			monthNewAccountIdMap.put(sDate, monthNewAccountIdSet);
			//截止到当月1号，登录1次的用户
			Set<Long> oneLoginSet = getLoginTwoAccountIds(bDate, sDate, 1, 3);
			oneNewAccountIdMap.put(sDate, oneLoginSet);
			//当前登录一次的用户
			Set<Long> oneCurrLogin = getLoginTwoAccountIds(sDate, DateUtil.getDateAfterMonths(sDate, 1), 1, 3);
			oneLoginAccountIdMap.put(sDate, oneCurrLogin);
			//当前登录二次的用户
			Set<Long> twoCurrLogin = getLoginTwoAccountIds(sDate, DateUtil.getDateAfterMonths(sDate, 1), 2, 3);
			twoLoginAccountIdMap.put(sDate, twoCurrLogin);
			sDate = DateUtil.getDateAfterMonths(sDate, 1);
		}
		
		LinkedHashMap<Date, Set<Long>> result = new LinkedHashMap<>();
		while(DateUtil.compare(ssDate, endDate) < 0){
			
			HashSet<Long> newSet = (HashSet)monthNewAccountIdMap.get(ssDate);
			HashSet<Long> ns = (HashSet<Long>)newSet.clone();
			
			Set<Long> oneLoginSet = oneNewAccountIdMap.get(ssDate);
			ns.retainAll(oneLoginSet);
			
			Set<Long> currOneLoginSet = oneLoginAccountIdMap.get(ssDate);
			ns.retainAll(currOneLoginSet);
			
			Set<Long> twoCurrLogin = twoLoginAccountIdMap.get(ssDate);
			newSet.retainAll(twoCurrLogin);
			newSet.addAll(ns);
			result.put(ssDate, newSet);
			ssDate = DateUtil.getDateAfterMonths(ssDate, 1);
		}
		
		//获取指定区间内付费用户数
//		QuanxianCommon.setDatabase(database);
		Map<Date, Map<String, Set<Long>>> payUserAccountIds = getPayUserAccountIds(startDate, endDate, "103");
		
		String fileName = "新增登录用户数";
		//标题
		List<String> titleList = new ArrayList<String>();
		titleList.add("时间");
		titleList.add("登录用户数");
		titleList.add("付费登录用户数");
		titleList.add("试用登录用户数");
		titleList.add("过期登录用户数");
		
		List<LinkedHashMap<String,Object>> queryList = new ArrayList<>();
//		System.out.println(payUserAccountIds.keySet() + "---- " + loginAccountDateMap.keySet());
		Iterator<Entry<Date, Set<Long>>> iterator = result.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Date, Set<Long>> o = iterator.next();
			Date date = o.getKey();
			Set<Long> accountIdSet = o.getValue();
			int size = accountIdSet.size();
			Map<String, Set<Long>> validMap = payUserAccountIds.get(date);
			//累计付费用户数
			Set<Long> paySet = validMap.get("ZSL");
			//营销赠送
			Set<Long> zpSet = validMap.get("ZP");
			//商务合作
			Set<Long> sjddSet = validMap.get("SJDD");
			//vip
			Set<Long> vipSet = validMap.get("JCZZ");
			//试用用户数
			Set<Long> trySet = validMap.get("SY");
			
			LinkedHashMap<String,Object> lMap = new LinkedHashMap<String,Object>();
			lMap.put("date", DateUtil.dateToString(date, "yyyy-MM"));
			lMap.put("login_count", size);
			
			Set<Long> payS = QuanxianCommon.getChangeSet(paySet, accountIdSet);
			lMap.put("pay_count", payS.size());
			//试用用户数
			Set<Long> tryS = QuanxianCommon.getChangeSet(trySet, accountIdSet);
			lMap.put("try_count", tryS.size());
			
			//过期
			Set<Long> changeSet1 = QuanxianCommon.getRemoveAll(paySet, accountIdSet);
			Set<Long> changeSet2 = QuanxianCommon.getRemoveAll(zpSet, changeSet1);
			Set<Long> changeSet3 = QuanxianCommon.getRemoveAll(sjddSet, changeSet2);
			Set<Long> changeSet4 = QuanxianCommon.getRemoveAll(vipSet, changeSet3);
			Set<Long> changeSet5 = QuanxianCommon.getRemoveAll(trySet, changeSet4);
			lMap.put("guoqi", changeSet5.size());
			queryList.add(lMap);
		}
		
		export(fileName, titleList, queryList);
		
		
		
	}

	private static Set<Long> getMonthNewAccountIds(Date sDate) {
		
//		Date currEndDate = DateUtil.getLastDayOfMonth(sDate);
		
		Date beforeDate = DateUtil.getDateAfterMonths(sDate, 1);
		
		Date bDate = DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd");
		Set<Long> currSet = getLoginTwoAccountIds(bDate, sDate, 2, 3);
		/*Long[] currInt = currSet.toArray(new Long[currSet.size()]);
		int[] currArray = Arrays.stream(currInt).mapToInt(Integer::valueOf).toArray();
		RoaringBitmap cBit = RoaringBitmap.bitmapOf(currArray);*/
		
		Set<Long> bSet = getLoginTwoAccountIds(bDate, beforeDate, 2, 3);
		/*Integer[] array = bSet.toArray(new Integer[bSet.size()]);
		int[] intArray = Arrays.stream(array).mapToInt(Integer::valueOf).toArray();
		RoaringBitmap bBit = RoaringBitmap.bitmapOf(intArray);*/
		bSet.removeAll(currSet);
		/*cBit.andNot(bBit);
		Set<Long> accountSet = new HashSet<>();
		Iterator<Integer> iterator = cBit.iterator();
		while(iterator.hasNext()){
			Integer o = iterator.next();
			accountSet.add(Long.valueOf(o));
		}*/
		return bSet;
	}

	public static void getAccountLoginCount(Date startDate, Date endDate) {
		
//		Map<String, Set<Long>> loginTwoAccountIdsMap = getLoginTwoAccountIds(2, startDate, endDate);
		
		LinkedHashMap<Date, Set<Long>> loginAccountDateMap = new LinkedHashMap<>();
		Date sDate = startDate;
		while(DateUtil.compare(sDate, endDate) < 0){
			Set<Long> loginTwoAccountIds = getLoginTwoAccountIds(sDate, DateUtil.getDateAfterMonths(sDate, 1), 2, 3);
			loginAccountDateMap.put(sDate, loginTwoAccountIds);
			sDate = DateUtil.getDateAfterMonths(sDate, 1);
		}
		
		//获取指定区间内付费用户数
//		QuanxianCommon.setDatabase(database);
		Map<Date, Map<String, Set<Long>>> payUserAccountIds = getPayUserAccountIds(startDate, endDate, "103");
		
		String fileName = "二次登录用户数";
		//标题
		List<String> titleList = new ArrayList<String>();
		titleList.add("时间");
		titleList.add("登录用户数");
		titleList.add("付费登录用户数");
		titleList.add("试用登录用户数");
		titleList.add("过期登录用户数");
		
		List<LinkedHashMap<String,Object>> queryList = new ArrayList<>();
//		System.out.println(payUserAccountIds.keySet() + "---- " + loginAccountDateMap.keySet());
		Iterator<Entry<Date, Set<Long>>> iterator = loginAccountDateMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Date, Set<Long>> o = iterator.next();
			Date date = o.getKey();
			Set<Long> accountIdSet = o.getValue();
			int size = accountIdSet.size();
			Map<String, Set<Long>> validMap = payUserAccountIds.get(date);
			//累计付费用户数
			Set<Long> paySet = validMap.get("ZSL");
			//营销赠送
			Set<Long> zpSet = validMap.get("ZP");
			//商务合作
			Set<Long> sjddSet = validMap.get("SJDD");
			//vip
			Set<Long> vipSet = validMap.get("JCZZ");
			//试用用户数
			Set<Long> trySet = validMap.get("SY");
			
			LinkedHashMap<String,Object> lMap = new LinkedHashMap<String,Object>();
			lMap.put("date", DateUtil.dateToString(date, "yyyy-MM"));
			lMap.put("login_count", size);
			
			Set<Long> payS = QuanxianCommon.getChangeSet(paySet, accountIdSet);
			lMap.put("pay_count", payS.size());
			//试用用户数
			Set<Long> tryS = QuanxianCommon.getChangeSet(trySet, accountIdSet);
			lMap.put("try_count", tryS.size());
			
			//过期
			Set<Long> changeSet1 = QuanxianCommon.getRemoveAll(paySet, accountIdSet);
			Set<Long> changeSet2 = QuanxianCommon.getRemoveAll(zpSet, changeSet1);
			Set<Long> changeSet3 = QuanxianCommon.getRemoveAll(sjddSet, changeSet2);
			Set<Long> changeSet4 = QuanxianCommon.getRemoveAll(vipSet, changeSet3);
			Set<Long> changeSet5 = QuanxianCommon.getRemoveAll(trySet, changeSet4);
			lMap.put("guoqi", changeSet5.size());
			queryList.add(lMap);
		}
		
		export(fileName, titleList, queryList);
	}
	
	/**
	 * 导出通用代码
	 * @param fileName
	 * @param fields
	 * @author yutao
	 * @date 2018年6月13日下午3:47:24
	 */
	public static void export(String fileName, List<String> fields, List<LinkedHashMap<String,Object>> queryList) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(fileName);
		sheet.setColumnWidth(0, 20 * 256);
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = null;
		//标题
		for(int i=0; i<fields.size(); i++){
			cell = row.createCell(i);
			cell.setCellValue(fields.get(i));
		}
		int count = 1;
		for(LinkedHashMap<String,Object> m : queryList){
			int cellCount=0;
			row = sheet.createRow(count);
			
			Iterator<Entry<String, Object>> iterator = m.entrySet().iterator();
			while(iterator.hasNext()){
				cell = row.createCell(cellCount);
				Entry<String, Object> o = iterator.next();
				String key = o.getKey();
				Object value = o.getValue();
				if("date".equals(key)){
					cell.setCellValue(DateUtil.stringToDate(o.getValue().toString(), "yyyy-MM"));
					CellStyle cellStyle = workbook.createCellStyle();//创建样式
		            DataFormat format= workbook.createDataFormat();//设置时间格式
		            cellStyle.setDataFormat(format.getFormat("yyyy-MM"));//设置时间格式
		            sheet.autoSizeColumn(0);//宽度自适应
		            cell.setCellStyle(cellStyle);//设置时间格式
				}/*else if(key.contains("radio")){
					cell.setCellValue(20);  
		            XSSFCellStyle cellStyle = workbook.createCellStyle();
		            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("%"));  
		            cell.setCellStyle(cellStyle);
				}*/
				
				if(value instanceof String){
					cell.setCellValue(o.getValue().toString());
				}else if(value instanceof Double){
					cell.setCellValue((Double)o.getValue());
				}else if(value instanceof Integer){
					cell.setCellValue((Integer)o.getValue());
				}else if(value instanceof Long){
					cell.setCellValue((Long)o.getValue());
				}else if(value instanceof BigDecimal){
					cell.setCellValue(((BigDecimal)o.getValue()).doubleValue());
				}
				
				cellCount++;
			}
			count++;
		}
		long timeMillis = System.currentTimeMillis();
		fileName += timeMillis +".xlsx";
		try {
//			fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
			
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}


	/**
	 * 登录用户数
	 * 
	 * @author yutao
	 * @return 
	 * @date 2018年6月6日下午4:33:53
	 */
	private static Set<Long> getLoginAccountIds() {
		MongoCollection<Document> useropRecord = database.getCollection("userop_record");
		Document match = commonMatch(DateUtil.stringToDate("2018-05-01", "yyyy-MM-dd"), DateUtil.stringToDate("2018-06-01", "yyyy-MM-dd"), 901);
		
		Document group = new Document();
		group.append("_id", null).append("count", new Document("$addToSet", "$account_id"));
		
		List<BasicDBObject> aggList = new ArrayList<>();
		aggList.add(new BasicDBObject("$match", match));
		aggList.add(new BasicDBObject("$group", group));
		
		MongoCursor<Document> iterator = useropRecord.aggregate(aggList).iterator();
		while(iterator.hasNext()){
			Document o = iterator.next();
			List<Long> accountList = o.get("count", List.class);
			return new HashSet<Long>(accountList);
		}
		return null;
	}
	
	/**
	 * 公共查询字段
	 * @param startDate
	 * @param endDate
	 * @return
	 * @author yutao
	 * @date 2018年6月6日下午4:33:30
	 */
	private static Document commonMatch(Date startDate, Date endDate, int type){
		Document match = new Document();
		Document timeQuery = new Document();
		if(startDate != null){
			timeQuery.append("$gte", startDate);
		}
		if(endDate != null){
			timeQuery.append("$lt", endDate);
		}
		if(!timeQuery.isEmpty()){
			match.append("createtime", timeQuery);
		}
		match.append("status", 1).append("org_id", new BasicDBObject("$nin", Arrays.asList(4, 1531, 9214)));
		match.append("type", type).append("code", "S3_06");
		return match;
	}

	/**
	 * 二次登录
	 * 
	 * @author yutao
	 * @return 
	 * @date 2018年6月6日下午4:22:33
	 */
	private static Set<Long> getLoginTwoAccountIds(Date startDate, Date endDate, int count, int type) {
		MongoCollection<Document> useropRecord = database.getCollection("userop_record");
		Document match = commonMatch(startDate, endDate, type);
		
		BasicDBObject group = new BasicDBObject();
		group.append("_id", "$account_id").append("count", new BasicDBObject("$sum", 1));
		
		BasicDBObject match2 = new BasicDBObject();
		match2.append("count", new BasicDBObject("$gte", count));
		
		BasicDBObject group2 = new BasicDBObject();
		group2.append("_id", null).append("count", new BasicDBObject("$addToSet", "$_id"));
		
		List<BasicDBObject> aggList = new ArrayList<>();
		aggList.add(new BasicDBObject("$match", match));
		aggList.add(new BasicDBObject("$group", group));
		aggList.add(new BasicDBObject("$match", match2));
		aggList.add(new BasicDBObject("$group", group2));
		
		MongoCursor<Document> iterator = useropRecord.aggregate(aggList).iterator();
		while(iterator.hasNext()){
			Document o = iterator.next();
			List<Long> accountList = o.get("count", List.class);
			return new HashSet<Long>(accountList);
		}
		return null;
	}
	
	
	/**
	 * count登录
	 * 
	 * @author yutao
	 * @return 
	 * @date 2018年6月6日下午4:22:33
	 */
	private static Map<String, Set<Long>> getLoginTwoAccountIds(int count, Date startDate, Date endDate) {
		MongoCollection<Document> useropRecord = database.getCollection("userop_record");
		Document match = commonMatch(startDate, endDate, 3);
		
		BasicDBObject project = new BasicDBObject();
		project.append("month", new BasicDBObject("$dateToString", new Document("format", "%Y-%m").append("date", "$date")));
		project.append("createtime", 1).append("account_id", 1);
		
		BasicDBObject group = new BasicDBObject();
		group.append("_id", new BasicDBObject("date", "$month").append("aid", "$account_id")).append("count", new BasicDBObject("$sum", 1))
		.append("aid", new BasicDBObject("$first", "$account_id")).append("month", new BasicDBObject("$first", "$month"));
		
		BasicDBObject match2 = new BasicDBObject();
		match2.append("count", new BasicDBObject("$gte", count));
		
		BasicDBObject group2 = new BasicDBObject();
		group2.append("_id", "$month").append("count", new BasicDBObject("$addToSet", "$aid"));
		
		List<BasicDBObject> aggList = new ArrayList<>();
		aggList.add(new BasicDBObject("$match", match));
		aggList.add(new BasicDBObject("$project", project));
		aggList.add(new BasicDBObject("$group", group));
		aggList.add(new BasicDBObject("$match", match2));
		aggList.add(new BasicDBObject("$group", group2));
		
		MongoCursor<Document> iterator = useropRecord.aggregate(aggList).iterator();
		Map<String, Set<Long>> result = new HashMap<>();
		while(iterator.hasNext()){
			Document o = iterator.next();
			List<Long> accountList = o.get("count", List.class);
			result.put(o.getString("_id"), new HashSet<Long>(accountList));
		}
		return result;
	}


	public static Map<String, Set<Long>> getPayUserAccountIds() {
//		QuanxianCommon.setDatabase(database);
		Map<String, Set<Long>> validMap = QuanxianCommon.getValidCurrentMonthPay(DateUtil.stringToDate("2018-05-01", "yyyy-MM-dd"), "901");
		return validMap;
	}
	
	
	public static Map<Date, Map<String, Set<Long>>> getPayUserAccountIds(Date startDate, Date endDate, String goodLines) {
		goodLines = goodLines==null?"103":goodLines;
		Map<Date, Map<String, Set<Long>>> result = new HashMap<>();
		while(DateUtil.compare(startDate, endDate) < 0){
			Map<String, Set<Long>> validMap = QuanxianCommon.getValidCurrentMonthPay(startDate, goodLines);
			result.put(startDate, validMap);
			startDate = DateUtil.getDateAfterMonths(startDate, 1);
		}
		return result;
	}
	
	
}
