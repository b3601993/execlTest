package gogoalExample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import gogoalExample.common.ExcelPoiCommon;
/**
 * 研报使用情况
 *
 *
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2017年1月24日下午2:11:09
 */
public class ReportReadDown {
	
	
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
			
//			DBCollection useropRecord = db.getCollection("userop_record");
//			DBCollection loginRecord = db.getCollection("login_record");
//			DBCollection accountrelation = db.getCollection("accountrelation");
//			DBCollection payUser = db.getCollection("t_paySys_user_permi");
//			DBCollection userListStatistics = db.getCollection("user_list_statistics");
//			DBCollection collectionLog = db.getCollection("version_upgrade_log");
			DBCollection report = db.getCollection("gg_report_down_read");
			

			File file = new File("C:\\Users\\yutao\\Desktop\\研报使用统计.xlsx");
			Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(0, 1, file);
			//现获取老用户
			BasicDBObject match = new BasicDBObject();
			match.put("status", 1);
			match.append("account_name", new BasicDBObject("$in", accountNameSet));
			BasicDBObject group = new BasicDBObject();
			group.append("_id", "$account_name");
			group.append("down_sum", new BasicDBObject("$sum", "$down_sum"));
			group.append("read_sum", new BasicDBObject("$sum", "$read_sum"));
			
			
			AggregationOutput output = report.aggregate(new BasicDBObject("$match", match), new BasicDBObject("$group", group));
			Iterator<DBObject> iterator = output.results().iterator();
			Map<String, String> map = new HashMap<String, String>();
			//拿到了老用户
			while(iterator.hasNext()){
				DBObject o= iterator.next();
				String read = o.get("read_sum")==null?"0":o.get("read_sum").toString();
				String down = o.get("down_sum")==null?"0":o.get("down_sum").toString();
				map.put(o.get("_id").toString(), read+";"+down);
			}
			
			
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			String workbookStr = "研报使用情况";
			XSSFSheet sheet = workbook.createSheet(workbookStr);
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("账号");
			cell = row.createCell(1);
			cell.setCellValue("下载次数");
			cell = row.createCell(2);
			cell.setCellValue("阅读次数");
			
			int rowUserop=1;
			
			
			for(String s : accountNameSet){
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);//账号
				cell.setCellValue(s);
				cell = row.createCell(1);
				String value = map.get(s);
				int read_sum = 0;
				int down_sum = 0;
				if(value != null){
					String[] split = value.split(";");
					read_sum = Integer.parseInt(split[0]);
					down_sum = Integer.parseInt(split[1]);
				}
				cell.setCellValue(down_sum);
				cell = row.createCell(2);
				cell.setCellValue(read_sum);
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

}
