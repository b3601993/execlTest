package gogoalExample.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import utils.ggservice.common.DateUtil;

public class QuanxianCommon {

	public static MongoDatabase database = initDataBase();
	
	public static MongoDatabase getDatabase() {
		return database;
	}
	
	/**
	 * 
	 * @author yutao
	 * @return 
	 * @date 2018年12月10日下午1:57:54
	 */
	private static MongoDatabase initDataBase() {
		//连接数据库 start
		MongoCredential credential = MongoCredential.createCredential("api_r_acc", "ft_account", "api_r_acc@suntime.6806!".toCharArray());
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
		
		database = mongoClient.getDatabase("ft_account");
		return database;
	}

	/**
	 * 获取所有的有过订单的用户
	 * @param startDate
	 * @param endDate
	 * @author yutao
	 * @return 
	 * @date 2018年7月16日上午10:16:05
	 */
	public static Set<Long> getQuan(){
		
		MongoCollection<Document> thirdSpmsOrder = database.getCollection("tau_auth_order");
		
		Iterable<Integer> distinctIterable = thirdSpmsOrder.distinct("user_id", Integer.class);
		
		Set<Long> allAccountIdSet = new HashSet<>();
		Iterator<Integer> iterator = distinctIterable.iterator();
		while(iterator.hasNext()){
			Integer o = iterator.next();
			allAccountIdSet.add(Long.valueOf(o));
		}
		return allAccountIdSet;
	}
	
	/*public static void setDatabase(MongoDatabase database) {
		QuanxianCommon.database = database;
	}*/


	/**
	 * 判断是否为当前月
	 * @param currentDate
	 * @return
	 * @author yutao
	 * @date 2018年3月19日下午2:33:16
	 */
	public static boolean isCurrentMonth(Date currentDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		
		Calendar ncal = Calendar.getInstance();
		ncal.setTime(new Date());
		
		int month = cal.get(Calendar.MONTH);
		int nmonth = ncal.get(Calendar.MONTH);
		
		int year = cal.get(Calendar.YEAR);
		int nyear = ncal.get(Calendar.YEAR);
		
		if((year == nyear) && (month == nmonth)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 获取截止到当前月的有效的付费用户数
	 * @param goodsLinesCode 产品线编号；不传默认就是终端3.0
	 * @param currentDate
	 * @author yutao
	 * @return 返回该月有效的各种类型（付费>营销赠送>商务合作>VIP>试用>基础类）的用户数
	 * @date 2018年3月19日下午2:54:26
	 */
	public static Map<String, Set<Long>> getValidCurrentMonthPay(Date currentDate, String type, String ...goodsLinesCode){
		Document match = new Document();
		match.append("goods_lines_code", type);
		match.append("goods_subtype_code", new Document("$ne", "report"));
		match.append("user_name", new BasicDBObject("$nin", Arrays.asList("测试账号", "测试")));
		
		//该月的第一天
//		Date firstDayOfMonth = DateUtil.firstDayOfMonth(currentDate);
		//该月的最后一天 ====== 对于按月统计而言，就是该月的最后一天而言的
		Date lastDayOfMonth = currentDate;
		boolean b = isCurrentMonth(currentDate);
		if(!b){
			lastDayOfMonth = DateUtil.getLastDayOfMonth(currentDate);
		}
		
		match.append("opendate", new Document("$lte", lastDayOfMonth));
		match.append("enddate", new Document("$gt", lastDayOfMonth));
		
		if(goodsLinesCode != null && goodsLinesCode.length >0){
			Set<String> sCode = new HashSet<>();
			for(String s : goodsLinesCode){
				sCode.add(s);
			}
			match.append("goods_lines_code", new Document("$in", sCode));
		}
		
		List<String> neibu = new ArrayList<String>();
		//测试
		neibu.add("LS");
		//内部
		neibu.add("NB");
		match.append("goods_type_code", new BasicDBObject("$nin", neibu));
		List<Integer> orgId = new ArrayList<>();
		orgId.add(4);
		orgId.add(1531);
		orgId.add(9214);
		match.append("org_id", new BasicDBObject("$nin", orgId));
		
		Document group = new Document();
		group.append("_id", "$goods_type_code");
		group.append("accountId", new Document("$addToSet", "$user_id"));
		MongoCollection<Document> orderBuy = database.getCollection("tau_auth_order");
		
		
		List<Document> groupList = new ArrayList<>();
		groupList.add(new Document("$match", match));
		groupList.add(new Document("$group", group));
		
		
		MongoCursor<Document> iterable = orderBuy.aggregate(groupList).iterator();
		Map<String, Set<Long>> map = new HashMap<>();
		while(iterable.hasNext()){
			Document o = iterable.next();
			//权限类型编号
			String _id = o.getString("_id");
			List<Long> aList = o.get("accountId", List.class);
			map.put(_id, aList==null?null:new HashSet<Long>(aList));
		}
		
		
		//付费>营销赠送>商务合作>VIP>试用>基础类（即免费过期用户)
		Set<Long> paySet = map.get("ZSL")==null?new HashSet<Long>():map.get("ZSL");
		map.put("ZSL", changeLong(paySet));
		//营销赠送
		Set<Long> zpSet = map.get("ZP")==null?new HashSet<Long>():map.get("ZP");
		zpSet.removeAll(paySet);
		map.put("ZP", changeLong(zpSet));
		//商务合作
		Set<Long> sjddSet = map.get("SJDD")==null?new HashSet<Long>():map.get("SJDD");
		sjddSet.removeAll(paySet);
		sjddSet.removeAll(zpSet);
		map.put("SJDD", changeLong(sjddSet));
		//VIP
		Set<Long> vipSet = map.get("JCZZ")==null?new HashSet<Long>():map.get("JCZZ");
		vipSet.removeAll(paySet);
		vipSet.removeAll(zpSet);
		vipSet.removeAll(sjddSet);
		map.put("JCZZ", changeLong(vipSet));
		//试用
		Set<Long> trySet = map.get("SY")==null?new HashSet<Long>():map.get("SY");
		trySet.removeAll(paySet);
		trySet.removeAll(zpSet);
		trySet.removeAll(sjddSet);
		trySet.removeAll(vipSet);
		map.put("SY", changeLong(trySet));
		return map;
	}
	
	/**
	 * 保证转换为Long
	 * @param set
	 * @return
	 * @author yutao
	 * @date 2018年7月16日下午7:13:25
	 */
	public static Set<Long> changeLong(Set<Long> set){
		
		Set<Long> result = new HashSet<>();
		for(Number n : set){
			result.add(Long.valueOf(n.toString()));
		}
		
		return result;
	}
	
	
	public static Set<Long> getRemoveAll(Set<Long> paySet, Set<Long> expireSet) {
		Set<Long> ss = new HashSet<Long>();
		for(Number l : expireSet){
			boolean flag = false;
			for(Number p : paySet){
				if( p.intValue() == l.intValue()){
					flag = true;
					break;
				}
			}
			if(!flag){
				ss.add(l.longValue());
			}
		}
		return ss;
	}

	public static Set<Long> getChangeSet(Set<Long> eventMonthPayCount, Set<Long> loginTwoAccountSet) {
		Set<Long> ss = new HashSet<Long>();
		for(Number l : eventMonthPayCount){
			if(loginTwoAccountSet.contains(l.longValue())){
				ss.add(Long.valueOf(l.toString()));
			}
		}
		return ss;
	}
	
	/**
	 * 初始化模块名称
	 * @return
	 * @author yutao
	 * @date 2018年3月23日下午3:00:45
	 */
	public static LinkedHashMap<String, String> initClickMap(){
		LinkedHashMap <String, String> map = new LinkedHashMap<String, String>();
		//情报
		map.put("G3_20", "情报");
		//好公司
		map.put("G3_17", "好公司");
		//寻宝
		map.put("G3_02", "寻宝");
		//自选
		map.put("G3_07", "自选");
		//资讯
		map.put("G3_18", "资讯");
		//诊股
		map.put("G3_05", "诊股");
		//公告
		map.put("G3_19", "公告");
		//研报
		map.put("G3_06", "研报");
		//行情
		map.put("G3_09", "行情");
		//数据
		map.put("G3_10", "数据");
		//个股
		map.put("G3_08", "个股");
		//首页
		map.put("G3_01", "首页");
		return map;
	}
	
	public static LinkedHashMap<String, String> initClick4Map(){
		
		LinkedHashMap <String, String> map = new LinkedHashMap<String, String>();
		map.put("G4_c01", "自选股");
		map.put("G4_c02", "诊个股");
		map.put("G4_c03", "够牛会");//诊行业
		map.put("G4_c04", "旺旺投研");
		map.put("G4_c05", "好公司");
		map.put("G4_c06", "寻宝");
		map.put("G4_c07", "好标的漏斗");
		map.put("G4_c08", "确定性Top榜");
		map.put("G4_c09", "圈子");
		map.put("G4_c10", "智能选股");
		map.put("G4_c11", "主题热点");
		map.put("G4_c12", "标签选股-工具");
		map.put("G4_c13", "Go-Goal直播");
		
		map.put("newsList", "资讯");
		map.put("hqList", "行情");
		map.put("school", "学院");
		
		map.put("G4_b01", "首页");
		map.put("G4_b02", "自选标签");
		map.put("G4_b03", "选股+");
		map.put("G4_b04", "诊个股");
		map.put("G4_b05", "诊行业");
		map.put("G4_b06", "诊大盘");
		map.put("G4_b07", "催化剂");
		map.put("G4_b08", "旺旺投研");
		map.put("G4_b09", "数据中心");
		map.put("G4_b10", "VIP服务");
		map.put("G4_a01", "个股");
		map.put("G4_a04", "委托");
		map.put("G4_a05", "帮助");
		map.put("G4_03", "机器人");
		
		return map;
	}
}
