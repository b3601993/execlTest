package test;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

public class test2 {
	public static void main(String[] args) {
		
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
			ServerAddress serverAddress;
			serverAddress = new ServerAddress("106.75.51.20", 35520);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
			addrs.add(serverAddress);
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			MongoDatabase database = mongoClient.getDatabase("gg_openapi");
			System.out.println("Connect to database successfully");
			//连接数据库 end
			
			MongoCollection<Document> collection = database.getCollection("userop_record");//埋点表
			
			
			
			Block<Document> printBlock = new Block<Document>(){
				@Override
				public void apply(Document t) {
					System.out.println(t.toJson());
				}
			};
			
			Document document = new Document();
			document.append("account_name", "E00002449");
			document.append("status", 1);
			document.append("type", 3);
			document.append("org_id", new Document("$ne", 4));
			
//			collection.find(document).projection(projection);
			
			
//			collection.find(eq("account_name", "E00002449"));
			
		
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
