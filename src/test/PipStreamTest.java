package test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.Buffer;

public class PipStreamTest {

	
	public static void main(String[] args) throws IOException {
		
		byte[] buffer = new byte[]{45, 114, 119, 45, 114, 45, 45, 114, 45, 45, 32, 32, 32, 32, 49, 32, 121, 117, 116, 97, 111, 32, 32, 32, 32, 121, 117, 116, 97, 111, 32, 32, 32, 32, 32, 32, 32, 32, 32, 53, 48, 48, 32, 78, 111, 118, 32, 50, 55, 32, 32, 50, 48, 49, 52, 32, 46, 101, 109, 97, 99, 115};
		byte[] buffer2 = new byte[]{-16, 43, 3, 65, 88, -34, -75, -86, 85, 101, -68, 107, -34, 14, -68, 105, 121, 78, -37, 101, 68, 78, 62, 27, 85, -6, -44, 52, -115, -86, -105, 90, -49, -45, 118, -111, -57, 115, -42, 88, 36, -29, 121, 98, -121, -5, 46, -87, 109, -54, 85, 56, -27, -29, -123, -20, -4, 91, 112, -109, -81, -95, -70, 90, 20, 102, 115, 116, 97, 116, 118, 102, 115, 64, 111, 112, 101, 110, 115, 115, 104, 46, 99, 111, 109};
		//83, 83, 72, 45, 50, 46, 48, 45, 79, 112, 101, 110, 83, 83, 72, 95, 53, 46, 51, 13, 10
		byte[] sshbyte = new byte[]{83, 83, 72, 45, 50, 46, 48, 45, 74, 83, 67, 72, 45, 48, 46, 49, 46, 53, 52, 10};
		byte[] sshbyte2 =new byte[]{0, 0, 0, 7, 115, 115, 104, 45, 114, 115, 97, 0, 0, 0, 1, 35, 0, 0, 1, 1, 0, -27, -112, -66, -21, -38, 48, -90, -36, -90, -44, 25, -22, 126, -20, 36, -62, 120, 95, -19, 90, 84, -82, -57, -18, -78, -94, -125, -19, -56, 107, -5, -94, -87, -75, 90, 35, -52, 121, -42, -15, 81, 104, -65, -79, 35, 95, 92, -17, 37, 84, -52, 19, -38, 91, -33, -6, -64, 20, 25, -3, -1, 32, 61, 65, -35, -68, 71, 52, 29, -48, -35, -43, -96, -122, -126, 127, -70, -71, 100, -26, 21, -22, -116, -57, 48, 60, -46, -65, -102, 12, -97, -20, -73, -30, -1, -106, -98, 126, 97, -51, -123, -57, -112, 5, -9, 2, 26, -86, 75, 13, -57, 43, 78, 127, 106, -49, -103, -14, 90, -121, -2, -73, 106, -84, 54, 0, 97, 85, -13, -96, -24, -29, 34, 41, -107, 42, -52, 33, 45, 120, -51, 26, 14, 39, 31, 51, -8, 59, -103, -113, -87, -38, -21, 72, -30, -50, -31, 33, -95, 79, -83, 84, 1, -90, 7, 41, 38, 42, -84, -39, 64, -43, -43, -31, -74, -38, 80, -113, -38, -102, -72, -109, 88, 75, 103, 3, 32, 65, 125, -47, 84, -112, 13, -92, 58, -61, -126, 97, -56, -53, -116, 15, -127, -95, -75, 104, 20, 101, 87, -118, -26, -84, -30, 12, -23, -25, -69, -14, 111, -90, 121, 82, -87, 39, -106, -97, 56, -26, -55, 63, -62, -105, -51, 66, 9, 8, 90, 119, 9, 28, 4, -85, 52, 74, 13, -67, 66, 52, 15, 80, -62, 5, -73, 18, -117, -7};
		byte[] sshbyte3 =new byte[]{0, 0, 2, -68, 10, 33, 0, 0};
		StringBuilder str1 = new StringBuilder();
		str1.append(new String(sshbyte2));
		System.out.println(str1);
		/*StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i<sshbyte.length;){
			
			if(i>=17){
				stringBuilder.append(new String(sshbyte, i, 2));
			}else{
				stringBuilder.append(new String(sshbyte, i, 3));
			}
			System.out.println(stringBuilder.toString());
			i=i+3;
		}*/
		
		
		String string2 = new String(buffer2, 0, buffer2.length);
		
		PipedOutputStream out = new PipedOutputStream();
		
		PipedInputStream pis = new PipedInputStream(out);
		
		out.write(buffer);
		
		int read = pis.read(buffer, 0, 9);
		
		String string = new String(buffer, 0, buffer.length);
//		System.out.println(string);
//		System.out.println(string2);
		pis.close();
		
		String str = "中";
		byte[] bytes = str.getBytes("UTF-8");
//		System.out.println(bytes);
	}
}
