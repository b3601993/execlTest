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
		binarySort();
	}
	
	/**
	 * 二分查找 符合要求的个数
	 * 
	 * @author yutao
	 * @date 2018年6月7日下午1:37:53
	 */
	public static void binarySort(){
		List<Map<String, Object>> stockCodeList = new ArrayList<Map<String, Object>>(){{
			add(new HashMap<String, Object>(){{
				put("ner_month", 1);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 1);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
			add(new HashMap<String, Object>(){{
				put("ner_month", 0);
			}});
		}};
		
		int left = 0;
		int size = stockCodeList.size();
		int right = size;
		while(left < right){
			int mid = (left + right) >>> 1;
			Map<String, Object> midMap = stockCodeList.get(mid);
			if((int)midMap.get("ner_month") == 1){
				left = mid + 1;
			}else{
				right = mid;
			}
		}
		if(left == right){
			System.out.println(left);
		}
	}
	
	private static List<Map<String, Object>> stockCodeList = new ArrayList<Map<String, Object>>(){{
		add(new HashMap<String, Object>(){{
			put("ner_month", 1);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 1);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
		add(new HashMap<String, Object>(){{
			put("ner_month", 0);
		}});
	}};
	
	public static void binarySortM(List<Map<String, Object>> result){

		
		int left = 0;
		int size = stockCodeList.size();
		int right = size;
		while(left < right){
			int mid = (left + right) >>> 1;
			Map<String, Object> midMap = stockCodeList.get(mid);
			if((int)midMap.get("ner_month") == 1){
				left = mid + 1;
			}else{
				right = mid;
			}
		}
		if(left == right){
			System.out.println(left);
		}
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
