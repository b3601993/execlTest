package gogoalExample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import gogoalExample.common.QuanxianCommon;
import utils.ToolsUtil;
import utils.ggservice.common.DateUtil;

public class Click3Execl {

	
	private static MongoDatabase database = null;
	public static void main(String[] args) {
		MongoCollection<Document> useropRecord = null;
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createCredential("api_rw_accb", "ft_account_behavior", "api_rw_accb@suntime.8908!".toCharArray());
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
			
			database = mongoClient.getDatabase("ft_account_behavior");
			useropRecord = database.getCollection("cabs_userop_record");//埋点表
			
			Date startDate = DateUtil.stringToDate("2019-02-01", "yyyy-MM-dd");
			Date endDate = DateUtil.stringToDate("2019-03-01", "yyyy-MM-dd");
			
			List<String> codeList = new ArrayList<>();
			
			/*codeList.add("G4_b01");
			codeList.add("G4_b02");
			codeList.add("G4_b03");
			codeList.add("G4_b04");
			codeList.add("G4_b05");
			codeList.add("G4_b06");
			codeList.add("G4_b07");
			codeList.add("G4_b08");
			codeList.add("G4_b09");
			codeList.add("G4_b10");
			//个股
			codeList.add("G4_a01");
			//行情
			codeList.add("G4_a02_01");
			codeList.add("G4_a02_02");
			codeList.add("G4_a02_03");
			codeList.add("G4_a02_04");
			codeList.add("G4_a02_05");
			codeList.add("G4_a02_06");
			codeList.add("G4_a02_07");
			codeList.add("G4_a02_08");
			//资讯
			codeList.add("G4_a03_01");
			codeList.add("G4_a03_02");
			codeList.add("G4_a03_03");
			codeList.add("G4_a03_04");
			codeList.add("G4_a03_05");
			codeList.add("G4_a03_06");
			codeList.add("G4_a03_07");
			codeList.add("G4_a03_08");
			codeList.add("G4_a03_09");
			//委托
			codeList.add("G4_a04");
			//帮助
			codeList.add("G4_a05");*/
			
			//推荐工具栏
			/*codeList.add("G4_c01");
			codeList.add("G4_c02");
			codeList.add("G4_c03");
			codeList.add("G4_c04");
			codeList.add("G4_c05");
			codeList.add("G4_c06");
			codeList.add("G4_c07");
			codeList.add("G4_c08");
			codeList.add("G4_c09");
			codeList.add("G4_c10");*/
			//首页
			codeList.add("G3_01");
			codeList.add("G3_17");
			codeList.add("G3_02");
			codeList.add("G3_07");
			codeList.add("G3_18");
			codeList.add("G3_05");
			codeList.add("G3_19");
			codeList.add("G3_06");
			codeList.add("G3_09");
			codeList.add("G3_10");
			codeList.add("G3_08");
			
			//获取指定区间内付费用户数
//			QuanxianCommon.setDatabase(database);
//			Map<Date, Map<String, Set<Long>>> payUserAccountIds = QuanxianExecl.getPayUserAccountIds(startDate, endDate, "3");
			Map<Date, Map<String, Set<Long>>> payUserAccountIds = QuanxianExecl.getPayUserAccountIds(startDate, endDate, "103");
			
			//获取权限用户
			Set<Long> allUserSet = QuanxianCommon.getQuan();
			
			Map<String, Set<Long>> validMap = payUserAccountIds.get(startDate);
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
			
			List<Integer> asList = Arrays.asList(4, 1531, 9214);
			
			Pattern userNameP = Pattern.compile("^(?!.*测试)");
			Pattern orgNameP = Pattern.compile("^(?!.*朝阳永续)");
			
			Document match = new Document();
			match.append("status", 1).append("type", 3).append("code", new Document("$in", codeList)).append("org_id", new Document("$nin", asList))
			.append("user_name", userNameP).append("org_name", orgNameP);
			
			Document timeQuery = new Document();
			timeQuery.append("$gte", startDate);
			timeQuery.append("$lt", endDate);
			
			match.append("createtime", timeQuery);
			
			/*int payI=0, tryI=0, expireI=0, allI=0;
			Set<Long> payAccSet = new HashSet<>();
			Set<Long> payTwoAccSet = new HashSet<>();
			Set<Long> payFiveAccSet = new HashSet<>();
			Set<Long> payTenAccSet = new HashSet<>();
			Set<Long> tryAccSet = new HashSet<>();
			
			Set<Long> tryTwoAccSet = new HashSet<>();
			Set<Long> tryFiveAccSet = new HashSet<>();
			Set<Long> tryTenAccSet = new HashSet<>();
			Set<Long> expireAccSet = new HashSet<>();
			Set<Long> expireTwoAccSet = new HashSet<>();
			Set<Long> expireFiveAccSet = new HashSet<>();
			Set<Long> expireTenAccSet = new HashSet<>();*/
			
			MongoCursor<Document> cursor = useropRecord.find(match).iterator();
//			Set<Long> accountIdSet = new HashSet<>();
			
			Map<String, Integer> payclickCount = new HashMap<>();
			Map<String, Integer> tryclickCount = new HashMap<>();
			Map<String, Integer> expireclickCount = new HashMap<>();
			
			Map<String, Set<Long>> payAccMap = new HashMap<>();
			Map<String, Set<Long>> payTwoAccMap = new HashMap<>();
			Map<String, Set<Long>> payFiveAccMap = new HashMap<>();
			Map<String, Set<Long>> payTenAccMap = new HashMap<>();
			
			Map<String, Set<Long>> tryAccMap = new HashMap<>();
			Map<String, Set<Long>> tryTwoAccMap = new HashMap<>();
			Map<String, Set<Long>> tryFiveAccMap = new HashMap<>();
			Map<String, Set<Long>> tryTenAccMap = new HashMap<>();
			
			Map<String, Set<Long>> expireAccMap = new HashMap<>();
			Map<String, Set<Long>> expireTwoAccMap = new HashMap<>();
			Map<String, Set<Long>> expireFiveAccMap = new HashMap<>();
			Map<String, Set<Long>> expireTenAccMap = new HashMap<>();
			
			Map<String, Integer> allMap = new HashMap<>();
			
			while(cursor.hasNext()){
				Document o = cursor.next();
//				allI++;
				Long accountId = o.getLong("account_id");
				String code = o.getString("code");
				
				Integer allInt = allMap.get(code);
				if(allInt == null){
					allMap.put(code, 1);
				}else{
					allMap.put(code, ++allInt);
				}
				
				//付费用户的点击次数和点击人次
				if(paySet.contains(accountId)){
//					payI++;
					
					Integer payCount = payclickCount.get(code);
					if(payCount == null){
						payCount=1;
						payclickCount.put(code, 1);
					}else{
						payclickCount.put(code, ++payCount);
					}
					
					if(payCount >= 2){
//						payTwoAccSet.add(accountId);
						Set<Long> twoSet = payTwoAccMap.get(code);
						if(twoSet == null){
							twoSet = new HashSet<>();
							twoSet.add(accountId);
							payTwoAccMap.put(code, twoSet);
						}else{
							twoSet.add(accountId);
						}
					}
					if(payCount >= 5){
//						payFiveAccSet.add(accountId);
						Set<Long> fiveSet = payFiveAccMap.get(code);
						if(fiveSet == null){
							fiveSet = new HashSet<>();
							fiveSet.add(accountId);
							payFiveAccMap.put(code, fiveSet);
						}else{
							fiveSet.add(accountId);
						}
					}
					if(payCount >= 10){
//						payTenAccSet.add(accountId);
						Set<Long> tenSet = payTenAccMap.get(code);
						if(tenSet == null){
							tenSet = new HashSet<>();
							tenSet.add(accountId);
							payTenAccMap.put(code, tenSet);
						}else{
							tenSet.add(accountId);
						}
					}
//					payAccSet.add(accountId);
					
					Set<Long> pSet = payAccMap.get(code);
					if(pSet == null){
						pSet = new HashSet<>();
						pSet.add(accountId);
						payAccMap.put(code, pSet);
					}else{
						pSet.add(accountId);
					}
					
				}else if(trySet.contains(accountId)){
//					tryI++;
					
					Integer tryCount = tryclickCount.get(code);
					if(tryCount == null){
						tryCount=1;
						tryclickCount.put(code, 1);
					}else{
						tryclickCount.put(code, ++tryCount);
					}
					
					if(tryCount >= 2){
//						tryTwoAccSet.add(accountId);
						
						Set<Long> twoSet = tryTwoAccMap.get(code);
						if(twoSet == null){
							twoSet = new HashSet<>();
							twoSet.add(accountId);
							tryTwoAccMap.put(code, twoSet);
						}else{
							twoSet.add(accountId);
						}
					}
					if(tryCount >= 5){
//						tryFiveAccSet.add(accountId);
						
						Set<Long> fiveSet = tryFiveAccMap.get(code);
						if(fiveSet == null){
							fiveSet = new HashSet<>();
							fiveSet.add(accountId);
							tryFiveAccMap.put(code, fiveSet);
						}else{
							fiveSet.add(accountId);
						}
					}
					if(tryCount >= 10){
//						tryTenAccSet.add(accountId);
						
						Set<Long> tenSet = tryTenAccMap.get(code);
						if(tenSet == null){
							tenSet = new HashSet<>();
							tenSet.add(accountId);
							tryTenAccMap.put(code, tenSet);
						}else{
							tenSet.add(accountId);
						}
					}
//					tryAccSet.add(accountId);
					
					Set<Long> tSet = tryAccMap.get(code);
					if(tSet == null){
						tSet = new HashSet<>();
						tSet.add(accountId);
						tryAccMap.put(code, tSet);
					}else{
						tSet.add(accountId);
					}
				}else if(!paySet.contains(accountId) && !zpSet.contains(accountId) && 
				   !sjddSet.contains(accountId) && !vipSet.contains(accountId) && 
				   !trySet.contains(accountId) && allUserSet.contains(accountId)){
					
//					expireI++;
					
					Integer expireCount = expireclickCount.get(code);
					if(expireCount == null){
						expireCount=1;
						expireclickCount.put(code, 1);
					}else{
						expireclickCount.put(code, ++expireCount);
					}
					
					if(expireCount >= 2){
//						expireTwoAccSet.add(accountId);
						Set<Long> twoSet = expireTwoAccMap.get(code);
						if(twoSet == null){
							twoSet = new HashSet<>();
							twoSet.add(accountId);
							expireTwoAccMap.put(code, twoSet);
						}else{
							twoSet.add(accountId);
						}
					}
					if(expireCount >= 5){
//						expireFiveAccSet.add(accountId);
						
						Set<Long> fiveSet = expireFiveAccMap.get(code);
						if(fiveSet == null){
							fiveSet = new HashSet<>();
							fiveSet.add(accountId);
							expireFiveAccMap.put(code, fiveSet);
						}else{
							fiveSet.add(accountId);
						}
					}
					if(expireCount >= 10){
//						expireTenAccSet.add(accountId);
						Set<Long> tenSet = expireTenAccMap.get(code);
						if(tenSet == null){
							tenSet = new HashSet<>();
							tenSet.add(accountId);
							expireTenAccMap.put(code, tenSet);
						}else{
							tenSet.add(accountId);
						}
					}
//					expireAccSet.add(accountId);
					
					Set<Long> tSet = expireAccMap.get(code);
					if(tSet == null){
						tSet = new HashSet<>();
						tSet.add(accountId);
						expireAccMap.put(code, tSet);
					}else{
						tSet.add(accountId);
					}
					
				}
//				accountIdSet.add(accountId);
			}
			cursor.close();
			
			/*System.out.println("付费用户pv" + payI);
			System.out.println("试用用户pv" + tryI);
			System.out.println("过期用户pv" + expireI);
			
			int paySize = payAccSet.size();
			System.out.println("付费用户uv" + paySize + "付费账号" + payAccSet);
			int trySize = tryAccSet.size();
			System.out.println("试用用户uv" + trySize);
			int expireSize = expireAccSet.size();
			System.out.println("过期用户uv" + expireSize);
			
			System.out.println("付费用户 2次=-=-" + payTwoAccSet.size());
			System.out.println("付费用户 5次=-=-" + payFiveAccSet.size());
			System.out.println("付费用户 10次=-=-" + payTenAccSet.size());
			
			System.out.println("试用用户 2次=-=-" + tryTwoAccSet.size());
			System.out.println("试用用户 5次=-=-" + tryFiveAccSet.size());
			System.out.println("试用用户 10次=-=-" + tryTenAccSet.size());
			
			System.out.println("过期用户 2次=-=-" + expireTwoAccSet.size());
			System.out.println("过期用户 5次=-=-" + expireFiveAccSet.size());
			System.out.println("过期用户 10次=-=-" + expireTenAccSet.size());
			
			System.out.println("付费用户点击率" + ToolsUtil.diviValidOrFraction(payI, allI, 2));
			System.out.println("试用用户点击率" + ToolsUtil.diviValidOrFraction(tryI, allI, 2));
			System.out.println("过期用户点击率" + ToolsUtil.diviValidOrFraction(expireI, allI, 2));*/
			
			//标题
			List<String> titleList = new ArrayList<String>();
			titleList.add("模块");
			titleList.add("对应编码");
			titleList.add("月份");
			titleList.add("付费用户pv");
			titleList.add("试用用户pv");
			titleList.add("过期用户pv");
			
			titleList.add("付费用户uv");
			titleList.add("试用用户uv");
			titleList.add("过期用户uv");
			
			titleList.add("付费用户点击率");
			titleList.add("试用用户点击率");
			titleList.add("过期用户点击率");
			
			titleList.add("付费用户 2次");
			titleList.add("付费用户 5次");
			titleList.add("付费用户 10次");
			
			titleList.add("试用用户 2次");
			titleList.add("试用用户 5次");
			titleList.add("试用用户 10次");
			
			titleList.add("过期用户 2次");
			titleList.add("过期用户 5次");
			titleList.add("过期用户 10次");
			
			String fileName = "终端3.0 点击次数";
			
			LinkedHashMap<String,String> initClickMap = QuanxianCommon.initClickMap();
//			LinkedHashMap<String,String> initClickMap = QuanxianCommon.initClick4Map();
			
			List<LinkedHashMap<String,Object>> queryList = new ArrayList<>();
			
			for(String code : codeList){
				LinkedHashMap<String,Object> lMap = new LinkedHashMap<String,Object>();
				lMap.put("name", initClickMap.get(code));
				lMap.put("code", code);
				lMap.put("month", "");
				lMap.put("payUserPv", payclickCount.get(code));
				lMap.put("tryUserPv", tryclickCount.get(code));
				lMap.put("expireUserPv", expireclickCount.get(code));
				
				lMap.put("payUserUv", payAccMap.get(code)==null?0:payAccMap.get(code).size());
				lMap.put("tryUserUv", tryAccMap.get(code)==null?0:tryAccMap.get(code).size());
				lMap.put("expireUserUv", expireAccMap.get(code)==null?0:expireAccMap.get(code).size());
				
				Integer allInt = allMap.get(code);
				lMap.put("payClickRate", allInt==null?0:ToolsUtil.diviValidOrFraction(payclickCount.get(code)==null?0:payclickCount.get(code), allInt, 2));
				lMap.put("tryClickRate", allInt==null?0:ToolsUtil.diviValidOrFraction(tryclickCount.get(code)==null?0:tryclickCount.get(code), allInt, 2));
				lMap.put("expireClickRate", allInt==null?0:ToolsUtil.diviValidOrFraction(expireclickCount.get(code)==null?0:expireclickCount.get(code), allInt, 2));
				
				lMap.put("payTwo", payTwoAccMap.get(code)==null ? 0:payTwoAccMap.get(code).size());
				lMap.put("payFive", payFiveAccMap.get(code)==null ? 0:payFiveAccMap.get(code).size());
				lMap.put("payTen", payTenAccMap.get(code)==null ? 0:payTenAccMap.get(code).size());
				
				lMap.put("tryTwo", tryTwoAccMap.get(code)==null ? 0:tryTwoAccMap.get(code).size());
				lMap.put("tryFive", tryFiveAccMap.get(code)==null ? 0:tryFiveAccMap.get(code).size());
				lMap.put("tryTen", tryTenAccMap.get(code)==null ? 0:tryTenAccMap.get(code).size());
				
				lMap.put("expireTwo", expireTwoAccMap.get(code)==null ? 0:expireTwoAccMap.get(code).size());
				lMap.put("expireFive", expireFiveAccMap.get(code)==null ? 0:expireFiveAccMap.get(code).size());
				lMap.put("expireTen", expireTenAccMap.get(code)==null ? 0:expireTenAccMap.get(code).size());
				
				queryList.add(lMap);
			}
			
			LinkedHashMap<String,Object> lMap = new LinkedHashMap<String,Object>();
			lMap.put("name", "所有模块");
			lMap.put("code", "");
			lMap.put("month", "");
			Collection<Integer> payvalues = payclickCount.values();
			int paySum = payvalues.stream().mapToInt(e -> e).sum();
			lMap.put("allpayUserPv", paySum);
			
			Collection<Integer> tryValue = tryclickCount.values();
			int trySum = tryValue.stream().mapToInt(e -> e).sum();
			lMap.put("alltryUserPv", trySum);
			
			Collection<Integer> expireValue = expireclickCount.values();
			int expireSum = expireValue.stream().mapToInt(e->e).sum();
			lMap.put("allexpireUserPv", expireSum);
			
			Set<Long> allpaySet = new HashSet<>();
			payAccMap.values().forEach(s -> {allpaySet.addAll(s);});
			
			Set<Long> alltrySet = new HashSet<>();
			tryAccMap.values().forEach(s -> {alltrySet.addAll(s);});
			
			Set<Long> allexpireSet = new HashSet<>();
			expireAccMap.values().forEach(s -> {allexpireSet.addAll(s);});
			
			lMap.put("allpayUserUv", allpaySet.size());
			lMap.put("alltryUserUv", alltrySet.size());
			lMap.put("allexpireUserUv", allexpireSet.size());
			
			Collection<Integer> allInt = allMap.values();
			int allSum = allInt.stream().mapToInt(e -> e).sum();
			
			Collection<Integer> payClickInt = payclickCount.values();
			int payCInt = payClickInt.stream().mapToInt(e -> e).sum();
			
			Collection<Integer> tryClickInt = tryclickCount.values();
			int tryCInt = tryClickInt.stream().mapToInt(e -> e).sum();
			
			Collection<Integer> expireClickInt = expireclickCount.values();
			int expireCInt = expireClickInt.stream().mapToInt(e -> e).sum();
			
			lMap.put("allpayClickRate", allSum==0?0:ToolsUtil.diviValidOrFraction(payCInt, allSum, 2));
			lMap.put("alltryClickRate", allSum==0?0:ToolsUtil.diviValidOrFraction(tryCInt, allSum, 2));
			lMap.put("allexpireClickRate", allSum==0?0:ToolsUtil.diviValidOrFraction(expireCInt, allSum, 2));
			
			Collection<Set<Long>> payTwoSet = payTwoAccMap.values();
			Set<Long> payTw = new HashSet<>();
			payTwoSet.forEach(s -> {payTw.addAll(s);});
			lMap.put("allpayTwo", payTw.size());
			Collection<Set<Long>> payFiveSet = payFiveAccMap.values();
			Set<Long> payFi = new HashSet<>();
			payFiveSet.forEach(s -> {payFi.addAll(s);});
			lMap.put("allpayFive", payFi.size());
			Collection<Set<Long>> payTenSet = payFiveAccMap.values();
			Set<Long> payTe = new HashSet<>();
			payTenSet.forEach(s -> {payTe.addAll(s);});
			lMap.put("allpayTen", payTe.size());
			
			Set<Long> tryTw = new HashSet<>();
			tryTwoAccMap.values().forEach(s -> {tryTw.addAll(s);});
			lMap.put("alltryTwo", tryTw.size());
			Set<Long> tryFi = new HashSet<>();
			tryFiveAccMap.values().forEach(s -> {tryFi.addAll(s);});
			lMap.put("alltryFive", tryFi.size());
			Set<Long> tryTe = new HashSet<>();
			tryTenAccMap.values().forEach(s -> {tryTe.addAll(s);});
			lMap.put("alltryTen", tryTe.size());
			
			Set<Long> expireTw = new HashSet<>();
			expireTwoAccMap.values().forEach(s -> {expireTw.addAll(s);});
			lMap.put("allexpireTwo", expireTw.size());
			Set<Long> expireFi = new HashSet<>();
			expireFiveAccMap.values().forEach(s -> {expireFi.addAll(s);});
			lMap.put("allexpireFive", expireFi.size());
			Set<Long> expireTe = new HashSet<>();
			expireTenAccMap.values().forEach(s -> {expireTe.addAll(s);});
			lMap.put("allexpireTen", expireTe.size());
			queryList.add(lMap);
			QuanxianExecl.export(fileName, titleList, queryList);
		}catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * 
	 * @param code
	 * @author yutao
	 * @param endDate 
	 * @param startDate 
	 * @return 
	 * @date 2018年7月13日下午6:21:03
	 */
	private static Map<String, Object> getUseropRecordCount(List<String> codeList, Date startDate, Date endDate) {
		MongoCollection<Document> useropRecord = database.getCollection("userop_record");
		
		
		List<Integer> asList = Arrays.asList(1, 1531, 9214);
		
		BasicDBObject match = new BasicDBObject();
		match.append("status", 1).append("type", 901).append("code", new BasicDBObject("$in", codeList)).append("org_id", new BasicDBObject("$in", asList));
		match.append("createtime", new BasicDBObject("$gte", startDate).append("$lte", endDate));
		
		int i=0;
		MongoCursor<Document> cursor = useropRecord.find(match).iterator();
		Set<Long> accountIdSet = new HashSet<>();
		while(cursor.hasNext()){
			Document o = cursor.next();
			++i;
			Long accountId = o.getLong("account_id");
			accountIdSet.add(accountId);
			
		}
		cursor.close();
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("pv", i);
		result.put("uv", accountIdSet);
		
		return result;
	}
	
	/**
	 * 获取所有的有过订单的用户
	 * @param startDate
	 * @param endDate
	 * @author yutao
	 * @return 
	 * @date 2018年7月16日上午10:16:05
	 */
	private static Set<Long> getQuan(){
		
		MongoCollection<Document> thirdSpmsOrder = database.getCollection("v_third_spms_order_buy_list");
		
		Iterable<Integer> distinctIterable = thirdSpmsOrder.distinct("user_id", Integer.class);
		
		Set<Long> allAccountIdSet = new HashSet<>();
		Iterator<Integer> iterator = distinctIterable.iterator();
		while(iterator.hasNext()){
			Integer o = iterator.next();
			allAccountIdSet.add(Long.valueOf(o));
		}
		return allAccountIdSet;
	}
}
