package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListSort {

	
	public static void main(String[] args) {
		List<Long> accountMillList = new ArrayList<>();
		accountMillList.add(1529048031210L);
		accountMillList.add(1529048031311L);
		accountMillList.add(1529048031412L);
		accountMillList.add(1529048031513L);
		
		binaryFind(accountMillList, 1529048031220L, 1L);
	}
	
  /**
    * 二分查找 符合要求的个数
    * @param stockCodeList
    * @return
    * @author yutao
    * @date 2018年6月7日下午1:45:33
    */
	private static List binaryFind(List<Long> millList, long sLong, long eLong) {
		int left = 0;
		int size = millList.size();
		int right = size;
		while(left < right){
			int mid = (left + right) >>> 1;
			Long millLong = millList.get(mid);
			if(millLong <= sLong){
				left = mid + 1;
			}else{
				right = mid;
			}
		}
		int count = 0;
		if(left == right){
			count = left;
		}
		System.out.println(millList.get(left));
		return null;
	}

  /**
    * 二分查找 符合要求的个数
    * @param stockCodeList
    * @return
    * @author yutao
    * @date 2018年6月7日下午1:45:33
    */
   private static int binaryFindCount(List<Map<String, Object>> stockCodeList) {
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
       int count = 0;
       if(left == right){
           count = left;
       }
       return count;
   }
}
