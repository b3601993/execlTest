package utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongodbConnection {

	public static DB getCollection(){
		//连接数据库 start
		MongoCredential credential = MongoCredential.createMongoCRCredential("gg_openapi", "gg_openapi", "gg..openapi#!".toCharArray());
		ServerAddress serverAddress;
		DB db = null;
		serverAddress = new ServerAddress("106.75.51.20", 35520);
		List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
		addrs.add(serverAddress);
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		MongoClient mongoClient = new MongoClient(addrs, credentials);
		db = mongoClient.getDB("gg_openapi");
		//连接数据库 end
		return db;
	}
}
