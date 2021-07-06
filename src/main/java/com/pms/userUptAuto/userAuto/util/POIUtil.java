package com.pms.userUptAuto.userAuto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POIUtil {

		private static Logger log = LoggerFactory.getLogger(POIUtil.class);
		// 导出Excel
		private final static int MAX_ROWS = 65536;
		public final static int HEADER_ROW = 1; // 头列标题行占的行数默认值为1
		public final static int DEFAULT_CELL_WIDTH = 8000;
		public static final int FLUSH_LINE = 500;

		/**
		 * @param path
		 *            excel文件的路径
		 * @param isHasHeader
		 *            第一行是否为标题行
		 * @param columnCount
		 *            规定excel的最小列数
		 * @param isStringFirst
		 *            第一列是否必须为文本
		 * @param ref 枚举类
		 * @return List<Map<String, Object>>
		 * @throws IOException
		 * @description 读取excel文件(兼容2003 xls与2007 xlsx)
		 * @author zikc
		 * @date 2015年12月26日 上午11:39:25
		 * @update 2015年12月26日 上午11:39:25
		 * @version V1.0
		 * @param <T>
		 */
		public static <T> List<Map<String, String>> readExcel(String path,
				boolean isHasHeader, Integer columnCount, boolean isStringFirst,Class<T> ref)
				throws IOException {
			if (path.equals("")) {
				throw new IOException("文件路径不能为空！");
			} else {
				File file = new File(path);
				if (!file.exists()) {
					throw new IOException("文件不存在！");
				}
			}
			// 获取扩展名
			String ext = path.substring(path.lastIndexOf(".") + 1);
			try {
				if (!"xlsx".equals(ext)) 
					throw new IOException("当前只支持文件扩展名为xlsx的Excel文件！");
				return readExcelXlsx(path, isHasHeader, columnCount,
							isStringFirst,ref);
			} catch (IOException e) {
				throw e;
			}
		}

		/**
		 * @param path
		 * @param isHasHeader
		 * @param columnCount
		 * @param isStringFirst
		 * @return
		 * @throws IOException
		 * @description 读取xls
		 * @author zikc
		 * @date 2016年1月6日 下午12:37:03
		 * @update 2016年1月6日 下午12:37:03
		 * @version V1.0
		 */
		@SuppressWarnings({ "resource", "static-access", "unused" })
		private static List<Map<String, String>> readExcelXls(String path,
				boolean isHasHeader, Integer columnCount, boolean isStringFirst)
				throws IOException {
			try {
				InputStream is = new FileInputStream(path);
				HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
				HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0); // 只取第一个sheet
				Integer firstContentIndex = (isHasHeader ? 1 : 0);
				if (hssfSheet == null
						|| hssfSheet.getLastRowNum() < firstContentIndex)
					throw new IOException("Excel文件有误，数据必须放在第一个sheet，请检查Excel");
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				// 循环行Row
				for (int rowNum = firstContentIndex; rowNum <= hssfSheet
						.getLastRowNum(); rowNum++) {
					HSSFRow hssfRow = hssfSheet.getRow(rowNum);
					if (hssfRow == null)
						throw new IOException("excel文件第" + (rowNum + 1) + "行数据为空");
					// 根据传入的columnCount与实际excel中读到的列数量进行比较
					if (hssfRow.getLastCellNum() < columnCount)
						throw new IOException("excel文件第" + (rowNum + 1)
								+ "行数据列总数小于规定的格式");
					// excel一行数据就是一个map
					Map<String, String> map = new HashMap<String, String>();
					// 根据传入的columnCount与实际excel中读到的列数量取最小值
					Integer cellCountInRow = Math.min(hssfRow.getLastCellNum(),
							columnCount);
					// 循环列Cell
					for (int cellNum = 0; cellNum < cellCountInRow; cellNum++) {
						HSSFCell hssfCell = hssfRow.getCell(cellNum);
						if (hssfCell == null)
							throw new IOException("excel文件第" + (rowNum + 1) + "行第"
									+ (cellNum + 1) + "列数据为空");
						if (cellNum == 0
								&& isStringFirst
								&& (hssfCell.getCellType() != CellType.STRING))
							throw new IOException("excel文件第" + (rowNum + 1) + "行第"
									+ "1列的列数据格式必须为文本");
						String cellStr = getValue(hssfCell).trim();
						if ("".equals(cellStr))
							throw new IOException("excel文件第" + (rowNum + 1) + "行第"
									+ (cellNum + 1) + "列的数据不能为空单元格");
						map.put("key-" + cellNum, cellStr);
					}
					list.add(map);
				}
				return list;
			} catch (IOException e) {
				log.error(e.getMessage());
				throw e;
			}
		}

		/**
		 * @param path
		 * @param isHasHeader
		 * @param columnCount
		 * @param isStringFirst
		 * @param ref 枚举类
		 * @return
		 * @throws IOException
		 * @description 读取xlsx
		 * @author zikc
		 * @date 2016年1月6日 下午12:37:26
		 * @update 2016年1月6日 下午12:37:26
		 * @version V1.0
		 * @param <T>
		 */
		@SuppressWarnings({ "resource", "static-access" })
		private static <T> List<Map<String, String>> readExcelXlsx(String path,
				boolean isHasHeader, Integer columnCount, boolean isStringFirst,Class<T> ref)
				throws IOException {
			try {
				if(!isHasHeader)
					throw new IOException("第一行必须为标题行");
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(path);
				// 循环工作表Sheet
				// for(int numSheet = 0; numSheet <
				// xssfWorkbook.getNumberOfSheets();
				// numSheet++){
				// XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
				// if(xssfSheet == null){
				// continue;
				// }
				XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
				Integer firstContentIndex = (isHasHeader ? 1 : 0);
				if (xssfSheet == null
						|| xssfSheet.getLastRowNum() < firstContentIndex)
					throw new IOException("Excel文件有误，数据必须放在第一个sheet，请检查Excel");
				//第一行为标题行
				XSSFRow xssfHeadRow = xssfSheet.getRow(0);
				// 根据传入的columnCount与实际excel中读到的标题行的列数量进行比较
				if (xssfHeadRow.getLastCellNum() < columnCount)
					throw new IOException("Excel文件标题行数据列总数小于规定的格式");
				// 根据传入的columnCount与实际excel中读到的列数量取最小值
				Integer cellCountInHeadRow = Math.min(xssfHeadRow.getLastCellNum(),
						columnCount);
				// 字段英文名数组
				List<String> keys=new ArrayList<String>();
				//遍历标题行的列
				for (int cellNum = 0; cellNum < cellCountInHeadRow; cellNum++) {
					XSSFCell xssfCell = xssfHeadRow.getCell(cellNum);
					if (xssfCell == null)
						throw new IOException("Excel文件标题行第"+ (cellNum + 1) + "列数据为空");
					if (xssfCell.getCellType() !=CellType.STRING)
						throw new IOException("Excel文件标题行第"+ (cellNum + 1) + "列的列数据格式必须为文本");
					//得到标题行本列的中文名
					String headText = getValue(xssfCell).trim();
					//得到标题行本列的中文名对应的字段名英文编码
					String headCode=EnumUtil.getCodeFromText(ref,headText);
					//如果标题行本列的中文名不规范，则会获取不到字段英文编码
					if("".equals(headCode))
						throw new IOException("Excel文件标题行第"+ (cellNum + 1) + "列的名字有误");
					//将字段名英文编码添加到键数组中
					keys.add(headCode);
				}
				//键数组
				String[] keyArray=keys.toArray(new String[]{});
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				// 循环数据行Row
				for (int rowNum = (isHasHeader ? 1 : 0); rowNum <= xssfSheet
						.getLastRowNum(); rowNum++) {
					XSSFRow xssfRow = xssfSheet.getRow(rowNum);
					if (xssfRow == null)
						throw new IOException("Excel文件第" + (rowNum + 1) + "行数据为空");
					// 根据传入的columnCount与实际excel中读到的列数量进行比较
					if (xssfRow.getLastCellNum() < columnCount)
						throw new IOException("Excel文件第" + (rowNum + 1)
								+ "行数据列总数小于规定的格式");
					// excel一行数据就是一个map
					Map<String, String> map = new HashMap<String, String>();
					// 根据传入的columnCount与实际excel中读到的列数量取最小值
					Integer cellCountInRow = Math.min(xssfRow.getLastCellNum(),
							columnCount);
					// 循环数据列Cell
					for (int cellNum = 0; cellNum < cellCountInRow; cellNum++) {
						XSSFCell xssfCell = xssfRow.getCell(cellNum);
						if (xssfCell == null)
							throw new IOException("Excel文件第" + (rowNum + 1) + "行第"
									+ (cellNum + 1) + "列数据为空");
						if (cellNum == 0
								&& isStringFirst
								&& (xssfCell.getCellType() != CellType.STRING))
							throw new IOException("Excel文件第" + (rowNum + 1) + "行第"
									+ "1列的列数据格式必须为文本");
						String cellStr = getValue(xssfCell).trim();
						//log.info(cellStr+":"+xssfCell.getCellType()+"");
						if ("".equals(cellStr))
							throw new IOException("Excel文件第" + (rowNum + 1) + "行第"
									+ (cellNum + 1) + "列的数据不能为空单元格");
						//按照索引从键数组中得到字段英文名
						map.put(keyArray[cellNum],cellStr);
					}
					list.add(map);
				}
				return list;
			} catch (IOException e) {
				log.error(e.getMessage());
				throw e;
			}
		}

		@SuppressWarnings("static-access")
		private static String getValue(HSSFCell hssfCell) {
			if (hssfCell.getCellType() == CellType.BOOLEAN) {
				return String.valueOf(hssfCell.getBooleanCellValue());
			} else if (hssfCell.getCellType() == CellType.NUMERIC) {
				return String.valueOf(hssfCell.getNumericCellValue());
			} else {
				return String.valueOf(hssfCell.getStringCellValue());
			}
		}

		@SuppressWarnings("static-access")
		private static String getValue(XSSFCell xssfCell) {
			if (xssfCell.getCellType() == CellType.BOOLEAN) {
				return String.valueOf(xssfCell.getBooleanCellValue());
			} else if (xssfCell.getCellType() == CellType.NUMERIC) {
				return String.valueOf(xssfCell.getNumericCellValue());
			} else {
				return String.valueOf(xssfCell.getStringCellValue());
			}
		}
		
}
