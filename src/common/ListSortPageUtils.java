package common;

import java.math.BigDecimal;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListSortPageUtils {
	
	public static Map<String, Object> sortByComparator(Map<String, Object>unsortMap,Integer type){
		
		List list = new LinkedList(unsortMap.entrySet()); 
		final Integer orderType = type;
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				
				if(orderType == -1){
					return ((Comparable) ((Map.Entry) (o2)).getValue())
							.compareTo(((Map.Entry) (o1)).getValue());
				}
				else if(orderType == 1){
					return ((Comparable) ((Map.Entry) (o1)).getValue())
							.compareTo(((Map.Entry) (o2)).getValue());
				}
				
				return 0;
			}
		});
		Map<String, Object> sortedMap = new LinkedHashMap<String, Object>();

		for (Iterator it = list.iterator(); it.hasNext();) {
		Map.Entry entry = (Map.Entry)it.next();
		sortedMap.put(entry.getKey().toString(), entry.getValue());
		} 
		return sortedMap;

	}
	/**
	 * 
	 * 按【双字符串】升降序排序
	 *
	 * @param result
	 * @param order
	 * @param order_type
	 * @return
	 * 
	 * @author fanjj
	 * @date 2016年4月28日 上午9:20:31
	 */
	public static List<Map<String, Object>> dualStrSort(List<Map<String, Object>> result, String order1, Integer type1,String order2, Integer type2){
		final String orderName = order1;
		final Integer orderType = type1;
		final String orderName1 = order2;
		final Integer orderType1= type2;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				if (orderType == -1)
					if (m1.get(orderName) == null && m2.get(orderName) == null) {
						return 0;
					} else if (m2.get(orderName) == null) {
						return 1;
					} else if (m1.get(orderName) == null) {
						return -1;
					} else {
						 int sort = m2.get(orderName).toString().compareTo(m1.get(orderName).toString());
						 if(sort == 0){
							 sort = strCompare(orderName1, orderType1, m1, m2);
						 }
						 return sort;
					}
				else if (orderType == 1)
					if (m1.get(orderName) == null && m2.get(orderName) == null) {
						return 0;
					} else if (m2.get(orderName) == null) {
						return -1;
					} else if (m1.get(orderName) == null) {
						return 1;
					} else {
						int sort = m1.get(orderName).toString().compareTo(m2.get(orderName).toString());
						if(sort == 0){
							 sort = strCompare(orderName1, orderType1, m1, m2);
						 }
						return sort;
					}
				return 0;
			}
		});
		return result;
	}
	
	private static int strCompare(final String orderName,final Integer orderType, Map<String, Object> m1,Map<String, Object> m2) {
		
		if (orderType == -1)
			if (m1.get(orderName) == null && m2.get(orderName) == null) {
				
				return 0;
			} else if (m2.get(orderName) == null) {
				return 1;
			} else if (m1.get(orderName) == null) {
				return -1;
			} else {
				return (m2.get(orderName).toString()).compareTo(m1.get(orderName).toString());
			}
		else if (orderType == 1)
			if (m1.get(orderName) == null && m2.get(orderName) == null) {
				return 0;
			} else if (m2.get(orderName) == null) {
				return -1;
			} else if (m1.get(orderName) == null) {
				return 1;
			} else {
				return (m1.get(orderName).toString()).compareTo(m2.get(orderName).toString());
			}
		return 0;
	}
	
	/**
	 * 
	 * 按股票代码升降序排序
	 *
	 * @param result
	 * @param order
	 * @param order_type
	 * @return
	 * 
	 * @author fanjj
	 * @date 2016年4月28日 上午9:20:31
	 */
	public static List<Map<String, Object>> stockCodeSort(List<Map<String, Object>> result, String order, Integer order_type){
		final String orderName = order;
		final Integer orderType = order_type;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				
				if (orderType == -1)
					
					if (m1.get(orderName) == null && m2.get(orderName) == null) {
						return 0;
					} else if (m2.get(orderName) == null) {
						return 1;
					} else if (m1.get(orderName) == null) {
						return -1;
					} else {
						int value = m2.get(orderName).toString().trim().length() - m1.get(orderName).toString().trim().length();
						if (value != 0) {
		                      return value > 0 ? 1 : -1;
		                }
						return (m2.get(orderName).toString()).compareTo(m1.get(orderName).toString());
					}
				else if (orderType == 1)
					if (m1.get(orderName) == null && m2.get(orderName) == null) {
						return 0;
					} else if (m2.get(orderName) == null) {
						return -1;
					} else if (m1.get(orderName) == null) {
						return 1;
					} else {
						int value = m1.get(orderName).toString().trim().length() - m2.get(orderName).toString().trim().length();
						if (value != 0) {
		                      return value > 0 ? 1 : -1;
		                }
						return (m1.get(orderName).toString()).compareTo(m2.get(orderName).toString());
					}
				return 0;
			}
		});
		return result;
	}
	
	/**
	 * 
	 * 按【数字】升降序排序
	 *
	 * @param result
	 * @param order
	 * @param order_type
	 * @return
	 * 
	 * @author fanjj
	 * @date 2016年4月27日 下午6:48:31
	 */
	public static List<Map<String, Object>> numberSort(List<Map<String, Object>> result, String order, Integer order_type){
		final String orderName = order;
		final Integer orderType = order_type;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				Double num1;
				Double num2;
				if (m1.get(orderName) == null || m1.get(orderName).toString().isEmpty()) {
					num1 = 0d;
				} else {
					num1 = Double.parseDouble(m1.get(orderName).toString());
				}
				if (m2.get(orderName) == null || m2.get(orderName).toString().isEmpty()) {
					num2 = 0d;
				} else {
					num2 = Double.parseDouble(m2.get(orderName).toString());
				}
				if (orderType == -1) {
					if (num2 - num1 == 0)
						return 0;
					else
						return num2 - num1 > 0 ? 1 : -1;
				} else if (orderType == 1) {
					if (num2 - num1 == 0)
						return 0;
					else
						return num1 - num2 > 0 ? 1 : -1;
				}
				return 0;
			}
		});
	
		return result;
	}
	
	/**
	 * 
	 * 按【字符串】升降序排序
	 *
	 * @param result
	 * @param order
	 * @param order_type
	 * @return
	 * 
	 * @author fanjj
	 * @date 2016年4月28日 上午9:20:31
	 */
	public static List<Map<String, Object>> strSort(List<Map<String, Object>> result, String order, Integer order_type){
		final String orderName = order;
		final Integer orderType = order_type;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				
				if (orderType == -1)
					if (m1.get(orderName) == null && m2.get(orderName) == null) {
						return 0;
					} else if (m2.get(orderName) == null) {
						return 1;
					} else if (m1.get(orderName) == null) {
						return -1;
					} else {
						return (m2.get(orderName).toString()).compareTo(m1.get(orderName).toString());
					}
				else if (orderType == 1)
					if (m1.get(orderName) == null && m2.get(orderName) == null) {
						return 0;
					} else if (m2.get(orderName) == null) {
						return -1;
					} else if (m1.get(orderName) == null) {
						return 1;
					} else {
						return (m1.get(orderName).toString()).compareTo(m2.get(orderName).toString());
					}
				return 0;
			}
		});
		return result;
	}
	
	/**
	 * 支持两个字段排序
	 * @param result
	 * @param order
	 * @param orderType
	 * @param twoOrder 第二排序字段
	 * @param twoType 第二排序顺序
	 * @return
	 * @author yutao
	 * @date 2018年5月24日下午3:00:03
	 */
	public static List<Map<String, Object>> resultOrder(List<Map<String, Object>> result, String order, Integer orderType, 
														final String twoOrder, final Integer twoType){
		
		if(result == null || orderType == null){
			return result;
		}
		
		if(orderType != -1){
			orderType = 1;
		}
		
		final String orderKey = order;
		final Integer oType = orderType;
		
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if(o1 == null && o2 == null){
					return 0;
				}
				if (o1 == null) {
					if(oType < 0){
						return -oType;
					}
					return oType;
				}
				if (o2 == null) {
					if(oType < 0){
						return oType;
					}
					return -oType;
				}
				return commonOrder(orderKey, oType, twoOrder, twoType, o1, o2);
			}
		});
		return result;
	}
	
	
	/**
	 * 对结果集进行排序，目前支持日期、字符串、各种整形、各种浮点型
	 * @param result 结果集
	 * @param order
	 * @param orderType -1降序 1升序, 下面代码假设orderType为1
	 * @return
	 * @author yutao
	 * @date 2018年4月24日下午2:20:35
	 */
	public static List<Map<String, Object>> resultOrder(List<Map<String, Object>> result, String order, Integer orderType){
		return resultOrder(result, order, orderType, null, null);
	}

	/**
	 * 公共的排序部分
	 * @param orderKey
	 * @param oType
	 * @param obj1
	 * @param obj2
	 * @param twoOrder
	 * @param twoType
	 * @return
	 * @author yutao
	 * @param obj2 
	 * @param obj1 
	 * @param o2 
	 * @param o1 
	 * @date 2018年5月24日下午3:19:37
	 */
	public static Integer commonOrder(final String orderKey, final Integer oType, String twoOrder, Integer twoType, 
			Map<String, Object> o1, Map<String, Object> o2) {
		Object obj1 = o1.get(orderKey);
		Object obj2 = o2.get(orderKey);
		
		if(obj1 == null && obj2 == null){
			return 0;
		}
		if (obj1 == null) {
			if(oType < 0){
				return -oType;
			}
			return oType;
		}
		if (obj2 == null) {
			if(oType < 0){
				return oType;
			}
			return -oType;
		}
		
		if(obj1 instanceof Date && obj2 instanceof Date){
			//日期排序
			Date date1 = (Date)obj1;
			Date date2 = (Date)obj2;
			return longCompare(oType, date1.getTime(), date2.getTime(), twoOrder, twoType,o1, o2);
		}else if(obj1 instanceof String && obj2 instanceof String){
			//字符串排序
			String str1 = obj1.toString();
			String str2 = obj2.toString();
			
			if(str1.compareTo(str2) < 0){
				return -oType;
			}else if(str1.compareTo(str2) == 0){
				return 0;
			}else if(str1.compareTo(str2) > 0){
				return oType;
			}
		}else if((obj1 instanceof Double || obj1 instanceof Float || obj1 instanceof BigDecimal) && (obj2 instanceof Double || obj2 instanceof Float || obj2 instanceof BigDecimal)){
			//浮点型排序
			return doubleCompare(oType, obj1, obj2, twoOrder, twoType,o1, o2);
		}else if((obj1 instanceof Long || obj1 instanceof Integer || obj1 instanceof Short || obj1 instanceof Byte) && 
				 (obj2 instanceof Long || obj2 instanceof Integer || obj2 instanceof Short || obj2 instanceof Byte)){
			//整数型排序
			return longCompare(oType, obj1, obj2, twoOrder, twoType, o1, o2);
		}else if((obj1.getClass() != obj2.getClass()) && (obj1 instanceof Number && obj2 instanceof Number)){
			//这种情况可能是，既有整数又有浮点数
			return doubleCompare(oType, obj1, obj2, twoOrder, twoType,o1, o2);
		}
		return 0;
	}
	
	/**
	 * 整形比较大小
	 * @param oType
	 * @param obj1
	 * @param obj2
	 * @param twoOrder
	 * @param twoType
	 * @return
	 * @author yutao
	 * @param o2 
	 * @param o1 
	 * @date 2018年5月24日下午3:09:18
	 */
	private static int longCompare(final Integer oType, Object obj1, Object obj2, String twoOrder, Integer twoType, 
								   Map<String, Object> o1, Map<String, Object> o2) {
		long d1 = Long.parseLong(obj1.toString());
		long d2 = Long.parseLong(obj2.toString());
		if(d1 < d2){
			return -oType;
		}else if(d1 == d2){
			
			if(twoOrder != null && twoType != null){
				//相等就使用第二字段排序
				return commonOrder(twoOrder, twoType, null, null, o1, o2);
			}
			//相同的是否进行交互
			return 0;
		}else if(d1 > d2){
			return oType;
		}
		return 0;
	}
	
	/**
	 * 浮点型比较大小
	 * @param oType
	 * @param obj1
	 * @param obj2
	 * @return
	 * @author yutao
	 * @param o2 
	 * @param o1 
	 * @date 2018年5月24日下午3:09:41
	 */
	private static int doubleCompare(final Integer oType, Object obj1, Object obj2, String twoOrder, Integer twoType, 
									 Map<String, Object> o1, Map<String, Object> o2) {
		double d1 = Double.parseDouble(obj1.toString());
		double d2 = Double.parseDouble(obj2.toString());
		if(d1 < d2){
			return -oType;
		}else if(d1 == d2){
			if(twoOrder != null && twoType != null){
				//相等就使用第二字段排序
				return commonOrder(twoOrder, twoType, null, null, o1, o2);
			}
			return 0;
		}else if(d1 > d2){
			return oType;
		}
		return 0;
	}
	
	/**
	 * 
	 * 对list进行分页
	 *
	 * @param result
	 * @param page
	 * @param rows
	 * @return
	 * 
	 * @author yutao
	 * @date 2016年4月29日 下午1:23:32
	 */
	public static List<String> limitStr(List<String> result, Integer page, Integer rows) {
		if (result == null || page <= 0 || rows <= 0) {
			return result;
		}
		int size = result.size();
		if (rows * page <= size) {
			result = result.subList(rows * (page - 1), rows * page);
		} else if (rows * (page - 1) < size) {
			result = result.subList(rows * (page - 1), size);
		} else {
			return null;
		}
		return result;
	}
	
	/**
	 * 用于导出
	 * @param result
	 * @param page
	 * @param rows
	 * @return
	 * @author yutao
	 * @date 2018年5月7日上午10:42:16
	 */
	public static List limitFrom(List result, Integer from, Integer limit) {
		if (result == null || from < 0 || limit <= 0 || result.isEmpty()) {
			return result;
		}
		int size = result.size();
		if(from <= size){
			if(limit <= size){
				result = result.subList(from, limit);
			}else{
				result = result.subList(from, size);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * 对list进行分页
	 *
	 * @param result
	 * @param page
	 * @param rows
	 * @return
	 * 
	 * @author yutao
	 * @date 2016年4月29日 下午1:23:32
	 */
	public static List<Map<String, Object>> limit(List<Map<String, Object>> result, Integer page, Integer rows) {
		if (result == null || page <= 0 || rows <= 0) {
			return result;
		}
		if (rows * page <= result.size()) {
			result = result.subList(rows * (page - 1), rows * page);
		} else if (rows * (page - 1) < result.size()) {
			result = result.subList(rows * (page - 1), result.size());
		} else {
			return new ArrayList<>();
		}
		return result;
	}

	/**
	 * 从后往前截取数据
	 * @param result
	 * @param page
	 * @param rows
	 * @return
	 * 2018年3月14日
	 */
	public static List<Map<String, Object>> inverseLimit(List<Map<String, Object>> result, Integer page, Integer rows) {
		if (result == null || page <= 0 || rows <= 0) {
			return result;
		}
		int size = result.size();
		if (rows * page <= size) {
			result = result.subList(size - (rows * page) , size - (rows * (page-1)));
		} else if(rows * (page-1) < result.size()){
			result = result.subList(0 , result.size()-rows * (page-1));
		} else {
			result = null;
		}
		
		return result;
	}
	
	/**
	 * yyyy-MM-dd 字符串时间排序
	 *
	 * @param result	排序结果集
	 * @param order		排序字段
	 * @param type	排序方式
	 * @return
	 * 
	 * @author xuyong
	 * @date 2016年2月1日 上午10:30:33
	 */
	public static List<Map<String, Object>> yyyyMMddSort(List<Map<String, Object>> result, 
			String order, Integer type) {
		if(result == null || type == null){
			return result;
		}
		
		if(type != -1){
			type = 1;
		}
		
		final String orderName = order;
		final Integer orderType = type;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if (o1.get(orderName) == null && o2.get(orderName) == null) {
					return 0;
				}
				if (o1.get(orderName) == null) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				if (o2.get(orderName) == null) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				}
				Date value1 = DateUtil.stringToDate(o1.get(orderName).toString(), "yyyy-MM-dd");
				Date value2 = DateUtil.stringToDate(o2.get(orderName).toString(), "yyyy-MM-dd");
				if (value1 == null && value2 == null) {
					return 0;
				}
				
				if (value1 == null) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				
				if (value2 == null) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				}
				if (value1.getTime() > value2.getTime()) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				} else if (value1.getTime() < value2.getTime()) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				return 0;
			}
		});
		return result;
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss字符串时间排序
	 *
	 * @param result	排序结果集
	 * @param order		排序字段
	 * @param Type	排序方式
	 * @return
	 * 
	 * @author xuyong
	 * @date 2016年2月1日 上午10:56:16
	 */
	public static List<Map<String, Object>> yyyyMMddHHmmssSort(List<Map<String, Object>> result, 
			String order, Integer type) {
		if(result == null || type == null){
			return result;
		}
		
		if(type != -1){
			type = 1;
		}
		
		final String orderName = order;
		final Integer orderType = type;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if (o1.get(orderName) == null && o2.get(orderName) == null) {
					return 0;
				}
				if (o1.get(orderName) == null) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				if (o2.get(orderName) == null) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				}
				Date value1 = DateUtil.stringToDate(o1.get(orderName).toString(), "yyyy-MM-dd HH:mm:ss");
				Date value2 = DateUtil.stringToDate(o2.get(orderName).toString(), "yyyy-MM-dd HH:mm:ss");
				if (value1 == null && value2 == null) {
					return 0;
				}
				
				if (value1 == null) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				
				if (value2 == null) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				}
				if (value1.getTime() > value2.getTime()) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				} else if (value1.getTime() < value2.getTime()) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				return 0;
			}
		});
		return result;
	}
	
	/**
	 * 
	 * 按中文首字母排序
	 * @param result
	 * @param order
	 * @param type
	 * @return
	 * 2017年4月26日
	 */
	public static List<Map<String, Object>> initialSort(List<Map<String, Object>> result, 
			String order, Integer type) {
		if(result == null || type == null){
			return result;
		}
		
		if(type != -1){
			type = 1;
		}
		final String orderName = order;
		final Integer orderType = type;
		Collections.sort(result,new Comparator<Map<String, Object>>() {  
            Collator collator = Collator.getInstance(Locale.CHINA);  
            
            @Override  
            public int compare(Map<String, Object> o1, Map<String, Object> o2) { 
            	if (o1.get(orderName) == null && o2.get(orderName) == null) {
					return 0;
				}
				if (o1.get(orderName) == null) {
					if(orderType < 0){
						return 1;
					}
					return -1;
				}
				if (o2.get(orderName) == null) {
					if(orderType < 0){
						return -1;
					}
					return 1;
				}
                CollationKey key1 = collator.getCollationKey(o1.get(orderName).toString());  
                CollationKey key2 = collator.getCollationKey(o2.get(orderName).toString());  
                if(orderType == 1){
                	return key1.compareTo(key2);  
                }else{
                	return key2.compareTo(key1);  
                }
                
            }  
        });  
		
		return result;
	}
	
	/**
	 * 
	 * 数字排序
	 *
	 * @param result
	 * @param order
	 * @param order_type
	 * @return
	 * 
	 * @author fanjj
	 * @date 2017年8月24日 下午3:52:14
	 */
	public static List<Map<String, Object>> numSort(List<Map<String, Object>> result, String order, Integer order_type){
		if (order_type == null) {
			order_type = 1;
		} 
		if (order_type == 1) {
			return asc(result, order);
		} else {
			return desc(result, order);
		}
	}
	
	/**
	 * 升序
	 *
	 * @param result
	 * @param order
	 * @return
	 * 
	 * @author xuyong
	 * @date 2015年12月9日 下午2:26:02
	 */
	public static List<Map<String, Object>> asc(List<Map<String, Object>> result, String order){
		final String orderName = order;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if (o1.get(orderName) == null && o2.get(orderName) == null) {
					return 0;
				}
				if (o1.get(orderName) == null) {
					return -1;
				}
				if (o2.get(orderName) == null) {
					return 1;
				}
				Double value1 = Double.parseDouble(o1.get(orderName).toString());
				Double value2 = Double.parseDouble(o2.get(orderName).toString());
				if (value1 > value2) {
					return 1;
				} else if (value1 < value2) {
					return -1;
				}
				return 0;
			}
		});
		
		
		return result;
	}
	
	/**
	 * 对结果集对某字段降序排序
	 *
	 * @param result	结果集
	 * @param order		排序字段
	 * @return
	 * 
	 * @author xuyong
	 * @date 2015年12月9日 下午1:44:57
	 */
	public static List<Map<String, Object>> desc(List<Map<String, Object>> result, String order){
		final String orderName = order;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				if (o1.get(orderName) == null && o2.get(orderName) == null) {
					return 0;
				}
				if (o1.get(orderName) == null) {
					return 1;
				}
				if (o2.get(orderName) == null) {
					return -1;
				}
				
				Double value1 = Double.parseDouble(o1.get(orderName).toString());
				Double value2 = Double.parseDouble(o2.get(orderName).toString());
				if (value1 > value2) {
					return -1;
				} else if (value1 < value2) {
					return 1;
				}
				
				return 0;
			}
		});
		
		
		return result;
	}
	
	
	
	/**
	 * 
	 * 按【字符串】升降序排序
	 *
	 * @param result
	 * @param order
	 * @param order_type
	 * @return
	 * 
	 * @author fanjj
	 * @date 2016年4月28日 上午9:20:31
	 */
	public static List<Map<String, Object>> dateAndStringSort(List<Map<String, Object>> result, String order, Integer order_type){
		final String orderName = order;
		final Integer orderType = order_type;
		Collections.sort(result, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				if (orderType == -1) {
					if ((m1.get(orderName) == null && m2.get(orderName) == null) || ("--".equals(m1.get(orderName)) && "--".equals(m2.get(orderName)))) {
						return 0;
					} else if (m2.get(orderName) == null || "--".equals(m2.get(orderName))) {
						return -1;
					} else if (m1.get(orderName) == null || "--".equals(m1.get(orderName))) {
						return 1;
					} 
					
					else if ("超预期".equals(m1.get(orderName)) && "符合预期".equals(m2.get(orderName))){
						return -1;
					} else if ("超预期".equals(m1.get(orderName)) && "低于预期".equals(m2.get(orderName))){
						return -1;
					} else if ("符合预期".equals(m1.get(orderName)) && "超预期".equals(m2.get(orderName))){
						return 1;
					} else if ("符合预期".equals(m1.get(orderName)) && "低于预期".equals(m2.get(orderName))){
						return -1;
					} else if ("低于预期".equals(m1.get(orderName)) && "超预期".equals(m2.get(orderName))){
						return 1;
					} else if ("低于预期".equals(m1.get(orderName)) && "符合预期".equals(m2.get(orderName))){
						return 1;
					} else if ("超预期".equals(m1.get(orderName)) && "暂未鉴定".equals(m2.get(orderName))){
						return -1;
					} else if ("符合预期".equals(m1.get(orderName)) && "暂未鉴定".equals(m2.get(orderName))) {
						return -1;
					}  else if ("低于预期".equals(m1.get(orderName)) && "暂未鉴定".equals(m2.get(orderName))) {
						return -1;
					} else if ("暂未鉴定".equals(m1.get(orderName)) && "超预期".equals(m2.get(orderName))) {
						return 1;
					} else if ("暂未鉴定".equals(m1.get(orderName)) && "符合预期".equals(m2.get(orderName))) {
						return 1;
					} else if ("暂未鉴定".equals(m1.get(orderName)) && "低于预期".equals(m2.get(orderName))) {
						return 1;
					}
					
					else if (m1.get(orderName).toString().length() > 4 && m2.get(orderName).toString().length() <= 4) {
						return 1;
					} else if (m1.get(orderName).toString().length() <= 4 && m2.get(orderName).toString().length() > 4) {
						return -1;
					} else if (m1.get(orderName).toString().length() > 4 && m2.get(orderName).toString().length() > 4) {

						if ((m1.get(orderName) == null && m2.get(orderName) == null) || ("--".equals(m1.get(orderName)) && "--".equals(m2.get(orderName)))) {
							return 0;
						}
						if (m1.get(orderName) == null || "--".equals(m1.get(orderName))) {
							return -1;
						}
						if (m2.get(orderName) == null || "--".equals(m2.get(orderName))) {
							return 1;
						}
						Date value1 = null;
						Date value2 = null;
						
						if(m1.get(orderName) instanceof String){
							value1= DateUtil.stringToDate(m1.get(orderName).toString(), "yyyy-MM-dd HH:mm:ss");
						}else {
							value1 = (Date) m1.get(orderName);
						}
						
						if(m2.get(orderName) instanceof String){
							value2= DateUtil.stringToDate(m2.get(orderName).toString(), "yyyy-MM-dd HH:mm:ss");
						}else {
							value2 = (Date) m2.get(orderName);
						}
						
						if (value1 == null && value2 == null) {
							return 0;
						}
						
						if (value1 == null) {
							return 1;
						}
						
						if (value2 == null) {
							return -1;
						}
						if (value1.getTime() > value2.getTime()) {
							if(orderType < 0){
								return 1;
							}
							return -1;
						} else if (value1.getTime() < value2.getTime()) {
							return -1;
						}
						return 0;
					} 
				} else if (orderType == 1) {
					if ((m1.get(orderName) == null && m2.get(orderName) == null) || ("--".equals(m1.get(orderName)) && "--".equals(m2.get(orderName)))) {
						return 0;
					} else if (m2.get(orderName) == null || "--".equals(m2.get(orderName))) {
						return -1;
					} else if (m1.get(orderName) == null || "--".equals(m1.get(orderName))) {
						return 1;
					} 
					
					else if ("超预期".equals(m1.get(orderName)) && "符合预期".equals(m2.get(orderName))){
						return 1;
					} else if ("超预期".equals(m1.get(orderName)) && "低于预期".equals(m2.get(orderName))){
						return 1;
					} else if ("符合预期".equals(m1.get(orderName)) && "超预期".equals(m2.get(orderName))){
						return -1;
					} else if ("符合预期".equals(m1.get(orderName)) && "低于预期".equals(m2.get(orderName))){
						return 1;
					} else if ("低于预期".equals(m1.get(orderName)) && "超预期".equals(m2.get(orderName))){
						return -1;
					} else if ("低于预期".equals(m1.get(orderName)) && "符合预期".equals(m2.get(orderName))){
						return -1;
					} else if ("超预期".equals(m1.get(orderName)) && "暂未鉴定".equals(m2.get(orderName))){
						return 1;
					} else if ("符合预期".equals(m1.get(orderName)) && "暂未鉴定".equals(m2.get(orderName))) {
						return 1;
					}  else if ("低于预期".equals(m1.get(orderName)) && "暂未鉴定".equals(m2.get(orderName))) {
						return 1;
					} else if ("暂未鉴定".equals(m1.get(orderName)) && "超预期".equals(m2.get(orderName))) {
						return -1;
					} else if ("暂未鉴定".equals(m1.get(orderName)) && "符合预期".equals(m2.get(orderName))) {
						return -1;
					} else if ("暂未鉴定".equals(m1.get(orderName)) && "低于预期".equals(m2.get(orderName))) {
						return -1;
					}
					
					else if (m1.get(orderName).toString().length() > 4 && m2.get(orderName).toString().length() <= 4) {
						return -1;
					} else if (m1.get(orderName).toString().length() <= 4 && m2.get(orderName).toString().length() > 4) {
						return 1;
					} else if (m1.get(orderName).toString().length() > 4 && m2.get(orderName).toString().length() > 4) {

						if ((m1.get(orderName) == null && m2.get(orderName) == null) || ("--".equals(m1.get(orderName)) && "--".equals(m2.get(orderName)))) {
							return 0;
						}
						if (m1.get(orderName) == null || "--".equals(m1.get(orderName))) {
							if(orderType < 0){
								return 1;
							}
							return -1;
						}
						if (m2.get(orderName) == null || "--".equals(m2.get(orderName))) {
							if(orderType < 0){
								return -1;
							}
							return 1;
						}
						Date value1 = null;
						Date value2 = null;
						
						if(m1.get(orderName) instanceof String){
							value1= DateUtil.stringToDate(m1.get(orderName).toString(), "yyyy-MM-dd HH:mm:ss");
						}else {
							value1 = (Date) m1.get(orderName);
						}
						
						if(m2.get(orderName) instanceof String){
							value2= DateUtil.stringToDate(m2.get(orderName).toString(), "yyyy-MM-dd HH:mm:ss");
						}else {
							value2 = (Date) m2.get(orderName);
						}
						
						if (value1 == null && value2 == null) {
							return 0;
						}
						
						if (value1 == null) {
							return 1;
						}
						
						if (value2 == null) {
							return -1;
						}
						if (value1.getTime() < value2.getTime()) {
							return 1;
						} else if (value1.getTime() > value2.getTime()) {
							return -1;
						}
						return 0;
					} 
				}
				return 0;
			}
		});
		return result;
	}
}