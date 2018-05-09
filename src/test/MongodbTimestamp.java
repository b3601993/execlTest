package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.BSONTimestamp;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import utils.ggservice.common.DateUtil;


public class MongodbTimestamp {

	public static void main(String[] args) {
		final MongoCollection<Document> useropRecord;
		//连接数据库 start
		MongoCredential credential = MongoCredential.createCredential("gg_user_db_rw", "gg_user_db", "gg_user_db_rw.gogoal.com".toCharArray());
		ServerAddress serverAddress;
		serverAddress = new ServerAddress("106.75.51.20", 35724);//35724
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
		
		
		Document match = new Document();
		List<Long> ll = new ArrayList<Long>();
		ll.add(1549370L);
		ll.add(1549371L);
//		match.append("_id", new Document("$in", ll));
		match.append("_tm", null);
		
		Date stringToDate = DateUtil.stringToDate("2018-04-01", "yyyy-MM-dd");
		match.append("date", new Document("$gte", stringToDate));
		useropRecord.find(match).forEach(new Block<Document>() {
			int aa=3000;
			@Override
			public void apply(Document doc) {
				Document project = new Document();
				project.append("$set", new Document("_tm", new BSONTimestamp((int)(System.currentTimeMillis() / 1000), aa++)));
				useropRecord.updateMany(new BasicDBObject("_id", doc.get("_id")), project);
				if(aa >= 4000){
					aa = 3000;
				}
			  }
			}
		);
		
		
		
		
		
		
	}
}
