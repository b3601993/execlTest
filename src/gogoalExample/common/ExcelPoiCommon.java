package gogoalExample.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;

public class ExcelPoiCommon {

	/**
	 * 获得文件中account_name的集合
	 * 
	 * @param sheeetInt excel表中哪个sheet
	 * @param cellInt excel表中哪一列开始读
	 * 
	 * @author yutao
	 * @return 
	 * @throws FileNotFoundException 
	 * @date 2016年12月9日下午4:14:50
	 */
	public static Set<String> getAccountNameSet(int sheetInt, int cellInt, File file) throws FileNotFoundException {
		if(file == null || !file.isFile()){
			throw new FileNotFoundException("文件名不能为空或文件的后缀格式不对");
		}
		FileInputStream in;
		Set<String> accountNameSet = new HashSet<String>();
		try {
			String name = file.getName();
			name = name.substring(name.lastIndexOf(".")+1);
			in = new FileInputStream(file);
			Workbook workbookIn = null;
			Sheet sheetAt = null;
			if("xlsx".equals(name)){
				workbookIn = new XSSFWorkbook(in);
				sheetAt = workbookIn.getSheetAt(sheetInt);
			}else if("xls".equals(name)){
				workbookIn = new HSSFWorkbook(in);
				sheetAt = workbookIn.getSheetAt(sheetInt);
			}else{
				throw new FileFormatException("文件的后缀格式不对");
			}

			int firstRowNum = sheetAt.getFirstRowNum();
			int lastRowNum = sheetAt.getLastRowNum();
			// 拿到所有的账号
			for (int i = firstRowNum + 1; i <= lastRowNum; i++) {
				Row row = sheetAt.getRow(i);
				Cell cell = row.getCell(cellInt);
				String cellValue = cell.getStringCellValue().trim();
				accountNameSet.add(cellValue);
			}
			workbookIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accountNameSet;
	}
	
	public static void getAccountCount(Map<String, Integer> accountMap, String name){
		Integer nameInt = accountMap.get(name);
		if(nameInt == null){
			accountMap.put(name, Integer.valueOf(1));
		}else{
			accountMap.put(name, ++nameInt);
		}
	}
}
