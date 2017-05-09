package gogoalExample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import gogoalExample.common.ExcelPoiCommon;
import utils.DateUtil;
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

//			File file = new File("C:\\Users\\yutao\\Desktop\\研报使用统计.xlsx");
//			Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(0, 1, file);
			BasicDBObject query = new BasicDBObject();
			query.append("down_sum", new BasicDBObject("$gte", 20));
			DBCursor cursor = report.find(query);
			
			
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			String workbookStr = "下载研报超过20次使用情况";
			XSSFSheet sheet = workbook.createSheet(workbookStr);
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("日期");
			cell = row.createCell(1);
			cell.setCellValue("账号");
			cell = row.createCell(2);
			cell.setCellValue("下载次数");
			cell = row.createCell(3);
			cell.setCellValue("机构名称");
			
			int rowUserop=1;
			
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(DateUtil.dateToString((Date)o.get("createtime"), "yyyy-MM-dd"));
				cell = row.createCell(1);
				cell.setCellValue(o.get("account_name").toString());
				cell = row.createCell(2);
				cell.setCellValue(o.get("down_sum").toString());
				cell = row.createCell(3);
				cell.setCellValue(o.get("org_name").toString());
				
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
	 * 导出带
	 * 
	 * @author yutao
	 * @date 2017年2月27日上午11:04:52
	 */
	public static void getReport(){
		try {
			MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi",
					"gg..openapi#!".toCharArray());
			ServerAddress serverAddress = new ServerAddress("106.75.51.20", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
			addrs.add(serverAddress);
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			// 连接数据库 end
			DB db = mongoClient.getDB("gg_openapi");

			DBCollection useropRecord = db.getCollection("userop_record");

			BasicDBObject query = new BasicDBObject();
			BasicDBObject timeQuery = new BasicDBObject();
			timeQuery.append("$gte", DateUtil.getBeginOfDay(DateUtil.stringToDate("2016-12-01", "yyyy-MM-dd")));
			timeQuery.append("$lte", DateUtil.stringToDate("2017-02-28", "yyyy-MM-dd"));

			query.append("createtime", timeQuery);
			query.append("code", new BasicDBObject("$in", new Object[] { "D2_002", "D2_002_00" }));
			query.append("status", 1);

			XSSFWorkbook workbook = new XSSFWorkbook();
			String workbookStr = "研报下载情况";
			XSSFSheet sheet = workbook.createSheet(workbookStr);
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("账号");
			cell = row.createCell(1);
			cell.setCellValue("机构");
			cell = row.createCell(2);
			cell.setCellValue("部门");
			cell = row.createCell(3);
			cell.setCellValue("职务");
			cell = row.createCell(4);
			cell.setCellValue("时间");
			cell = row.createCell(5);
			cell.setCellValue("报告机构");

			int rowUserop = 1;

			DBCursor cursor = useropRecord.find(query);
			while (cursor.hasNext()) {
				DBObject o = cursor.next();
				if (o.get("account_name") == null || o.get("title") == null) {
					continue;
				}

				String accountName = o.get("account_name").toString();
				String orgName = o.get("org_name") == null ? "暂无机构名" : o.get("org_name").toString();

				String guid = o.get("guid").toString();
//				List<Map<String, Object>> report = ReportSearchService.searchReportSummary(guid, null, null, null,true);
//				if (report != null) {
//					Map<String, Object> mm = report.get(0);
//					mm.get("organ_id");
				
					row = sheet.createRow(rowUserop);
					cell = row.createCell(0);// 账号
					cell.setCellValue(accountName);
					cell = row.createCell(1);
					cell.setCellValue(orgName);
					cell = row.createCell(2);
					cell.setCellValue("暂无");
					cell = row.createCell(3);
					cell.setCellValue("暂无");
					cell = row.createCell(4);
					cell.setCellValue(DateUtil.dateToString((Date)o.get("createtime"), "yyyy-MM-dd HH:mm:ss"));
					
					String title = o.get("title").toString();
					String[] titlea = title.split("--");
					
					String organ_name = null;
					
					if(titlea.length>2){
						organ_name = titlea[1];
					}else if(titlea.length==1){
						organ_name = title.split("-")[2].substring(2);
					}
					
					cell = row.createCell(5);
					cell.setCellValue(organ_name);
//					cell = row.createCell(5);
//					cell.setCellValue(mm.get("organ_id") == null ? "暂无" : mm.get("organ_id").toString());
					
//				}
					rowUserop++;
			}
			cursor.close();

			long time = System.currentTimeMillis();
			String fileName = workbookStr + time + ".xlsx";

			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\" + fileName));
			workbook.write(out);
			workbook.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
