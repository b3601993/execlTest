package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageResultUtils {
	
	public static List<Map<String, Object>> packageCountResult(long count) {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("count", count);
		result.add(map);
		return result;
	}

	public static Map<String, Object> packageMapResult(int code, String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", code);
		result.put("message", message);
		return result;
	}
	
	public static Map<String, Object> packageMapResult(String name, Object value) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(name, value);
		return result;
	}
	
}
