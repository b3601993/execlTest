package test;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializerMap;


public class Token {

	
	public static void main(String[] args) {
		
//		System.out.println(java.util.UUID.randomUUID());
//		String key = StringUtils.join(java.util.UUID.randomUUID().toString().split("-"));
//		System.out.println(key);
		String data = "["+"{'count':"+40+"}]";
		Object parse = JSONArray.parse(data);
		
		JSONObject json = new JSONObject();
		
		JSONArray array = new JSONArray();
		json.put("count", 40);
		array.add(json);
		
		System.out.println(array);
	}
	
	public static String addToken(Long accountId, Long userId, Long loginId, String loginName){
		if (accountId == null) {
			accountId = 0l;
		}
		if (userId == null) {
			userId = 0l;
		}
		if (loginId == null) {
			loginId = 0l;
		}
		if (loginName == null) {
			loginName = "";
		}
		String tokenValue = "{\"AccountId\":" + accountId + ",\"UserId\":" + userId + ",\"LoginId\":" + loginId + ",\"Name\":\"" +loginName+ "\"}";
		return null;
//		return GGToken.set(tokenValue, 24 * 60 * 60);
	}
	
	
	/*public class GGToken {
		private static final String TOKEN_SPACE = "os_token";

		public static String set(String value, int seconds) {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			if (seconds < 1) {
				seconds = 300;
			}
			String key = StringUtils.join(java.util.UUID.randomUUID().toString().split("-"));
			GGObjectStore.setex("os_token", key, value, seconds);
			return key;
		}

		public static String get(String key) {
			if (StringUtils.isBlank(key)) {
				return null;
			}
			return GGObjectStore.get("os_token", key);
		}

		public static boolean delete(String key) {
			if (StringUtils.isBlank(key)) {
				return false;
			}
			return GGObjectStore.del("os_token", key);
		}
	}*/
}
