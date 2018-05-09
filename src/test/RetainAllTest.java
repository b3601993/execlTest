package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RetainAllTest {

	
	public static void main(String[] args) {
		
		Integer aa = 10;
		Long cc = 10L;
		
		List<Integer> aList = new ArrayList<Integer>();
		aList.add(aa);
		List<Long> db = (List)aList;
		Set<Long> s = new HashSet<Long>();
		s.addAll(db);//10
		
		Set<Long> sh = new HashSet<Long>();
		sh.add(cc);//10
		s.retainAll(sh);
		System.out.println(s);
		
		Set<Long> c = new HashSet<Long>();
		c.add(cc);
		c.retainAll(sh);
		System.out.println(c);
	}
}
