package execlTest.jiandan;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateWorkBook {

	/*public static void main(String[] args) throws IOException {
		//Create Blank workbook
	      XSSFWorkbook workbook = new XSSFWorkbook(); 
	      //Create file system using specific name
	      FileOutputStream out = new FileOutputStream(new File("createworkbook.xlsx"));
	      //write operation workbook using file out object 
	      workbook.write(out);
	      out.close();
	      workbook.close();
	      System.out.println("createworkbook.xlsx written successfully");

	}*/
	
	/*public static void main(String[] args) throws IOException {
		File file = new File("createworkbook.xlsx");
	      FileInputStream fIP = new FileInputStream(file);
	      //Get the workbook instance for XLSX file 
	      XSSFWorkbook workbook = new XSSFWorkbook(fIP);
	      if(file.isFile() && file.exists())
	      {
	         System.out.println(
	         "openworkbook.xlsx file open successfully.");
	      }
	      else
	      {
	         System.out.println(
	         "Error to open openworkbook.xlsx file.");
	      }
		
	}*/
	
	public static void main(String[] args) throws Exception 
	{
		// Create blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet(" Employee Info ");
		// Create row object
		XSSFRow row;
		// This data needs to be written (Object[])
		Map<String, Object[]> empinfo = new TreeMap<String, Object[]>();
		empinfo.put("1", new Object[] { "EMP ID", "EMP NAME", "DESIGNATION" });
		empinfo.put("2", new Object[] { "tp01", "Gopal", "Technical Manager" });
		empinfo.put("3", new Object[] { "tp02", "Manisha", "Proof Reader" });
		empinfo.put("4", new Object[] { "tp03", "Masthan", "Technical Writer" });
		empinfo.put("5", new Object[] { "tp04", "Satish", "Technical Writer" });
		empinfo.put("6", new Object[] { "tp05", "Krishna", "Technical Writer" });
		// Iterate over data and write to sheet
		Set<String> keyid = empinfo.keySet();
		int rowid = 0;
		for (String key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = empinfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}
		// Write the workbook in file system
		FileOutputStream out = new FileOutputStream(new File("Writesheet.xlsx"));
		workbook.write(out);
		out.close();
		System.out.println("Writesheet.xlsx written successfully");
	}
}
