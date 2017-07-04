package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class test1 {
	public static void main(String[] args) {
		
		/*try {
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
		
		DBCollection collection = db.getCollection("gg_module_access_statistics");
		
		BasicDBObject fields = new BasicDBObject();
		fields.append("G3_01_01_02_access_account", 1);
		
		DBCursor cursor = collection.find(new BasicDBObject(), fields).limit(10);
		
		while(cursor.hasNext()){
			
			DBObject o = cursor.next();
			System.out.println(o.get("G3_01_01_02_access_account"));
		}
		cursor.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}*/
		
		File excelFile = new File("C:/Users/yutao/Desktop/test.xlsx"); // 替换你文档地址
		XSSFWorkbook wb = null;
		Sheet sheetAt = null;
		int sheetInt =0; //这里的sheetInt表示你在读取哪个sheet，默认从0开始
		try { // 打开工作薄
		wb = new XSSFWorkbook(new FileInputStream(excelFile));
		sheetAt = wb.getSheetAt(sheetInt);//获得sheet对象
		int lastRowNum = sheetAt.getLastRowNum();
		System.out.println(lastRowNum);
		} catch (IOException e) {
		e.printStackTrace();
		}
		
		
	}
}
