package bitmap;

import org.roaringbitmap.RoaringBitmap;

public class RoaringBitmapTest {

	public static void main(String[] args) {
		RoaringBitmap rr = RoaringBitmap.bitmapOf(1, 2, 3, 1000);
		RoaringBitmap rr2 = new RoaringBitmap();
		
		rr2.add(4000L, 4255L);
		rr.select(3);
		System.out.println(rr.rank(2));
		rr.contains(1000);
		rr.contains(7);
		
		RoaringBitmap rror = RoaringBitmap.or(rr, rr2);
		rr.or(rr2);
		boolean equals = rror.equals(rr);
		if(!equals)
			throw new RuntimeException("bug");
		
		long longCardinality = rr.getLongCardinality();
		System.out.println(longCardinality);
		for(int i: rr){
			System.out.println(i);
		}
	}
}
