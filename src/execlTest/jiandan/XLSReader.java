package execlTest.jiandan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSReader {
	
	
	public static List readExcelData(String url) throws IOException{
		//创建了一个输入流
		FileInputStream fis = new FileInputStream(url);
		List hospitaList = new ArrayList();
		Workbook workbook = null;
		//xlsx 是2007以上版本，xlx是2003版本
 		if(url.toLowerCase().endsWith("xlsx")){
			workbook = new XSSFWorkbook(fis);
		}else if(url.toLowerCase().endsWith("xlx")){
			workbook = new HSSFWorkbook(fis);
		}
		//得到sheet的数量
		int numberOfSheets = workbook.getNumberOfSheets();
		//去循环numberOfSheets
		for(int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++){
			// 得到 工作薄 的第 N个表  
			Sheet sheetAt = workbook.getSheetAt(sheetNum);
			Row row;
			String cell;
			// 遍历 表中的记录（会有多条记录）
			for(int i = sheetAt.getFirstRowNum();i<sheetAt.getPhysicalNumberOfRows();i++){
				//循环行数
				row = sheetAt.getRow(i);
				//遍历每条记录的值（在excel中，每一行的记录都有多个值，所以也需要进行遍历）
				for(int j = row.getFirstCellNum(); j<row.getPhysicalNumberOfCells();j++){
					//循环列数
					cell = row.getCell(j).toString();
					hospitaList.add(cell);
				}
			}
		}
		return hospitaList;
	}

	public static void main(String[] args) throws IOException {
		List list = readExcelData("C:/Users/yutao/Desktop/test.xlsx");
		System.out.println(list);
	}

}
