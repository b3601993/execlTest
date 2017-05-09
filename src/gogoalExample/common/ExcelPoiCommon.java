package gogoalExample.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	 * @param sheeetInt excel表中哪个sheet(从0开始)
	 * @param cellInt excel表中哪一列开始读(从0开始)
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
				System.out.println(sheetAt.getSheetName());
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
				if(cell == null){
					continue;
				}
				String cellValue = cell.getStringCellValue().trim();
				accountNameSet.add(cellValue);
			}
			workbookIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accountNameSet;
	}
	
	
	/**
	 * 读写同一份excel文件
	 * @param file 需要读写的文件
	 * @param countMap 统计的map集合（key:账号，value：次数）
	 * @param sheetNum 读取哪个sheet（从0开始数）
	 * @param cellNum 读哪一列（从0开始数）
	 * @param writeCellNum 写入到哪一列（从0开始数）
	 * @author yutao
	 * @date 2017年5月9日下午2:50:45
	 */
	public static void readWriteExcel(File file, Map<String, Object> countMap, int sheetNum, int cellNum, int writeCellNum){
		FileInputStream in;
		String name = file.getName();
		name = name.substring(name.lastIndexOf(".")+1);
		try {
			in = new FileInputStream(file);
			Workbook workbookIn = null;
			Sheet sheetAt = null;
			if("xlsx".equals(name)){
			    workbookIn = new XSSFWorkbook(in);
			    sheetAt = workbookIn.getSheetAt(sheetNum);
			}else if("xls".equals(name)){
			    workbookIn = new HSSFWorkbook(in);
			    sheetAt = workbookIn.getSheetAt(sheetNum);
			}else{
			    throw new FileFormatException("文件的后缀格式不对");
			}
			System.out.println("----readWriteExcel---sheet---" + sheetAt.getSheetName());
			
			int firstRowNum = sheetAt.getFirstRowNum();
			int lastRowNum = sheetAt.getLastRowNum();
			// 根据账号、写入统计次数
			for (int i = firstRowNum + 1; i <= lastRowNum; i++) {
			    Row rowReload = sheetAt.getRow(i);
			    Cell cellReload = rowReload.getCell(cellNum);
			    if(cellReload == null){
			        continue;
			    }
			    String cellValue = cellReload.getStringCellValue().trim();
			    Cell cell5 = rowReload.getCell(writeCellNum);
			    if(cell5 ==null){
			    	cell5 = rowReload.createCell(writeCellNum);
			    }
			    cell5.setCellValue(countMap.get(cellValue)==null?0:Integer.valueOf(countMap.get(cellValue).toString()));
			}
			in.close();
			FileOutputStream out = new FileOutputStream(file);
			workbookIn.write(out);
			workbookIn.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 公用统计次数
	 * @param accountMap
	 * @param name
	 * @author yutao
	 * @date 2017年5月9日下午2:53:33
	 */
	public static void getAccountCount(Map<String, Integer> accountMap, String name){
		Integer nameInt = accountMap.get(name);
		if(nameInt == null){
			accountMap.put(name, Integer.valueOf(1));
		}else{
			accountMap.put(name, ++nameInt);
		}
	}
}
