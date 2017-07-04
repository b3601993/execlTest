package gogoalExample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import utils.DateUtil;

public class ExportExecl {

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
			
			DBCollection useropRecord = db.getCollection("userop_record");
//			DBCollection loginRecord = db.getCollection("login_record");
//			DBCollection accountrelation = db.getCollection("accountrelation");
//			DBCollection payUser = db.getCollection("t_paySys_user_permi");
//			DBCollection userListStatistics = db.getCollection("user_list_statistics");
//			DBCollection collectionLog = db.getCollection("version_upgrade_log");
//			DBCollection userSum = db.getCollection("user_sum");
			
//			File file = new File("C:\\Users\\yutao\\Desktop\\试用用户使用数据中心的统计.xls");
//			Set<String> accountNameSet = ExcelPoiCommon.getAccountNameSet(2, 0, file);
			
			BasicDBObject match = new BasicDBObject();
			
			match.append("type", new BasicDBObject("$in", new Object[]{3}));
			match.append("code", "S3_06");
			match.append("type", 22);
			match.append("org_id", new BasicDBObject("$in", new Object[]{4, 624}));
			BasicDBObject timeQuery = new BasicDBObject();
//			timeQuery.append("$lte", DateUtil.stringToDate("2016-08-05", "yyyy-MM-dd"));
			timeQuery.append("$gte", DateUtil.stringToDate("2017-01-01", "yyyy-MM-dd"));
			match.append("createtime", timeQuery);
			
			DBCursor cursor = useropRecord.find(match).sort(new BasicDBObject("createtime", -1));
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("用户使用插件情况");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("账号名称");
			cell = row.createCell(1);
			cell.setCellValue("用户Id");
			cell = row.createCell(2);
			cell.setCellValue("用户名称");
			cell = row.createCell(3);
			cell.setCellValue("用户所属机构");
			cell = row.createCell(4);
			cell.setCellValue("对应销售名称");
			cell = row.createCell(5);
			cell.setCellValue("登录时间");
			cell = row.createCell(6);
			cell.setCellValue("产品版本");
			int rowUserop = 1;
			while(cursor.hasNext()){
				DBObject o = cursor.next();
				row = sheet.createRow(rowUserop);
				cell = row.createCell(0);
				cell.setCellValue(o.get("account_name")==null?"无":o.get("account_name").toString());
				cell = row.createCell(1);
				cell.setCellValue(o.get("account_id")==null?"无":o.get("account_id").toString());
				cell = row.createCell(2);
				cell.setCellValue(o.get("user_name")==null?"无":o.get("user_name").toString());
				cell = row.createCell(3);
				cell.setCellValue(o.get("org_name")==null?"无":o.get("org_name").toString());
				cell = row.createCell(4);
				cell.setCellValue(o.get("saler_name")==null?"无":o.get("saler_name").toString());
				cell = row.createCell(5);
				cell.setCellValue(DateUtil.dateToString((Date)o.get("createtime"), "yyyy-MM-dd HH:mm:ss"));
				cell = row.createCell(6);
				cell.setCellValue(o.get("version")==null?"无":o.get("version").toString());
				rowUserop++;
			}
			cursor.close();
			
			long time = System.currentTimeMillis();
			String fileName =  sheet.getSheetName() + time + ".xlsx";
			FileOutputStream out = new FileOutputStream(new File("C:\\Users\\yutao\\Desktop\\"+fileName));
			workbook.write(out);
			workbook.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
