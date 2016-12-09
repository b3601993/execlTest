package execlTest.jiandan;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSQWriter {

	public static void main(String[] args) throws IOException {
		//创建工作薄
		XSSFWorkbook workbook = new XSSFWorkbook();
		//在工作薄中创建一工作表
		XSSFSheet sheet = workbook.createSheet("表1");
		//workbook.setSheetName(0, "sheet1"); 这句话要先创建表（sheet）才能用
		//在指定的索引处创建一行
		XSSFRow row = sheet.createRow(2);
		// 在指定的索引处创建一列（单元格）
		XSSFCell cell = row.createCell(1);
		// 定义单元格为字符串类型 
		cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		// 在单元格输入内容 
		XSSFRichTextString cellContent = new XSSFRichTextString("医院编号");
		cell.setCellValue(cellContent);
		//依然创建单元格
		XSSFCell city = row.createCell(2);
		city.setCellType(Cell.CELL_TYPE_STRING);
		XSSFRichTextString cityContent = new XSSFRichTextString("城市");
		city.setCellValue(cityContent);
		OutputStream fos = new FileOutputStream("C:/Users/yutao/Desktop/test3.xlsx");
		workbook.write(fos);
		fos.flush();
		System.out.println("文件生成");
		workbook.close();
		
	}

}
