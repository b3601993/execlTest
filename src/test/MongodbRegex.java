package test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongodbRegex {

	
	public static void main(String[] args) {
		//连接数据库 start
		MongoCredential credential = MongoCredential.createCredential("gg_user_db", "gg_user_db", "gg_user_db".toCharArray());
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
		MongoCollection<Document> useropRecord = database.getCollection("accountrelation");
		Document match = new Document();
//		match.append("account_name", new Document("$regex", "e00002449").append("$options", "i"));
		Pattern p = Pattern.compile("e00002449", Pattern.CASE_INSENSITIVE);
		match.append("account_name", p);
		Document first = useropRecord.find(match).first();
		
		List<String> arrayList = new ArrayList<String>(){{
			add("苹果");
			add("梨子");
			
		}};
		
		System.out.println(first);
	}
}
