package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sort {

	
	public static void main(String[] args) {
//		List<Integer> mm = mm();
//		System.out.println(mm);
		
		Map<String, Object> mm = new HashMap<>();
		mm.put("code", "BK0158");
		
		Map<String, Map<String, Object>> pp = new HashMap<>();
		pp.put("BK0158", new HashMap<String, Object>(){{put("a", 1);}});
		
		System.out.println(pp.get(mm.get("code")));
	}
	
	public static List<Integer> mm(){
		
		List<Integer> result = new ArrayList<>();
		result.add(9);
		result.add(2);
		result.add(3);
		result.add(3);
		result.add(4);
		result.add(8);
		result.add(5);
		result.add(6);
		
		Collections.sort(result, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1 > o2){
					return -1;//-1表示第一个参数小于第二个参数
				}else if( o1 == o2){
					return 0;
				}
				return -1;
			}
			
			
		});
		return result;
	}
}
