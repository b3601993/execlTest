package bitmap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;
import org.roaringbitmap.RoaringBitmap;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

public class BitMapMongodb {

	private static MongoDatabase database = null;
	private static MongoCollection<Document> collection = null;
	
	public static void main(String[] args) {
		try {
			//连接数据库 start
			MongoCredential credential = MongoCredential.createCredential("gg_user_db_rw", "gg_user_db", "gg_user_db_rw.gogoal.com".toCharArray());
			ServerAddress serverAddress;
			serverAddress = new ServerAddress("106.75.51.20", 35531);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
			addrs.add(serverAddress);
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(addrs, credentials);
			System.out.println("Connect to database successfully");
			//连接数据库 end
			
			database = mongoClient.getDatabase("gg_user_db");
			collection = database.getCollection("yt_temp_bitmap");
			
//			init();
			
			updateData();
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void updateData() {
		
		Document match = new Document();
		match.append("key", "S3_06");
		
		Document doc = collection.find(match).first();
		
		
		List<Integer> bitList = doc.get("bitmap", List.class);
		int[] array = bitList.stream().mapToInt(Integer::valueOf).toArray();
		RoaringBitmap bitmapOf = RoaringBitmap.bitmapOf(array);
		System.out.println(bitmapOf);
		
		Document map = (Document) doc.get("sequence");
		
		ArrayList<Long> list = map.get("347209", ArrayList.class);
		ArrayList<Long> clone = (ArrayList<Long>)list.clone();
		List<Long> accountMillList = new ArrayList<>();
		long tempInt = list.get(0);
		accountMillList.add(tempInt);
		for(int i=1,size=list.size(); i<size; i++){
			tempInt += list.get(i);
			accountMillList.add(tempInt);
		}
		long timeMillis = System.currentTimeMillis();
		long a = ((Long)(timeMillis - accountMillList.get(accountMillList.size()-1)));
		System.out.println(timeMillis);
		clone.add(a);
		Document key = new Document();
		key.append("key", "S3_06");
		Document update = new Document();
		
		Document sMap = new Document();
		sMap.put("347209", clone);
		update.append("sequence", sMap);
		
		UpdateOptions up = new UpdateOptions();
		up.upsert(true);
		collection.updateMany(key, new BasicDBObject("$set", update), up);
	}

	public static void init() {
		RoaringBitmap bitmapOf = RoaringBitmap.bitmapOf(347209, 127427, 300321 , 451245, 526654);
		
		Document key = new Document();
		key.append("key", "S3_06");
		
		Document update = new Document();
		update.append("bitmap", bitmapOf);
		update.append("key", "S3_06");
		
		Long millis = System.currentTimeMillis();
		List<Long> mList = new ArrayList<>();
		mList.add(millis);
		
		Document sMap = new Document();
		sMap.put("347209", mList);
		update.append("sequence", sMap);
		
		UpdateOptions up = new UpdateOptions();
		up.upsert(true);
		collection.updateMany(key, new BasicDBObject("$set", update), up);
	}
	
}
