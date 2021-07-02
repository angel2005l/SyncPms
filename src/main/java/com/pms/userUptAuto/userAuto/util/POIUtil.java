package com.pms.userUptAuto.userAuto.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
				// 循环工作表Sheet
				// for(int numSheet = 0; numSheet <
				// hssfWorkbook.getNumberOfSheets();
				// numSheet++){
				// HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
				// if(hssfSheet == null){
				// continue;
				// }
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
								&& (hssfCell.getCellType() != hssfCell.CELL_TYPE_STRING))
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
					if (xssfCell.getCellType() != xssfCell.CELL_TYPE_STRING)
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
								&& (xssfCell.getCellType() != xssfCell.CELL_TYPE_STRING))
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
			if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
				return String.valueOf(hssfCell.getBooleanCellValue());
			} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
				return String.valueOf(hssfCell.getNumericCellValue());
			} else {
				return String.valueOf(hssfCell.getStringCellValue());
			}
		}

		@SuppressWarnings("static-access")
		private static String getValue(XSSFCell xssfCell) {
			if (xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN) {
				return String.valueOf(xssfCell.getBooleanCellValue());
			} else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC) {
				return String.valueOf(xssfCell.getNumericCellValue());
			} else {
				return String.valueOf(xssfCell.getStringCellValue());
			}
		}

		/**
		 * 设置单元格宽度
		 * 
		 * @param sheet
		 * @param cellWidth
		 * @param columnNum
		 */
//		private static void setCellWidth(HSSFSheet sheet, int cellWidth,
//				int columnNum) {
//			for (int i = 0; i < columnNum; i++) {
//				sheet.setColumnWidth(i, cellWidth);
//			}
//		}
		
		/**
		 * 设置单元格宽度
		 * 
		 * @param sheet
		 * @param cellWidth
		 * @param columnNum
		 */
//		private static void setCellWidth(XSSFSheet sheet, int cellWidth,
//				int columnNum) {
//			for (int i = 0; i < columnNum; i++) {
//				sheet.setColumnWidth(i, cellWidth);
//			}
//		}
		
		/**
		 * 设置单元格宽度
		 * 
		 * @param sheet
		 * @param cellWidth
		 * @param columnNum
		 */
		private static void setCellWidth(Sheet sheet, int cellWidth,
				int columnNum) {
			for (int i = 0; i < columnNum; i++) {
				sheet.setColumnWidth(i, cellWidth);
			}
		}

		/**
		 * @param wb
		 * @return
		 * @description 得到首列标题行的样式
		 * @author zikc
		 * @date 2016年1月15日 上午12:17:56
		 * @update 2016年1月15日 上午12:17:56
		 * @version V1.0
		 */
//		private static HSSFCellStyle getHeaderStyle(HSSFWorkbook wb) {
//			HSSFCellStyle headerStyle = wb.createCellStyle();
//			HSSFFont f = wb.createFont();
//			f.setFontHeightInPoints((short) 12);// 字号
//			f.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);// 加粗
//			headerStyle.setFont(f);
//			headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
//			// headerStyle.setFillBackgroundColor(HSSFColor.AQUA.index);
//			// headerStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
//			headerStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
//			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//			return headerStyle;
//		}
		
		/**
		 * @param wb
		 * @return
		 * @description 得到首列标题行的样式
		 * @author zikc
		 * @date 2016年1月15日 上午12:17:56
		 * @update 2016年1月15日 上午12:17:56
		 * @version V1.0
		 */
//		private static CellStyle getHeaderStyle(Workbook wb) {
//			CellStyle headerStyle = wb.createCellStyle();
//			Font f = wb.createFont();
//			f.setFontHeightInPoints((short) 12);// 字号
//			f.setBoldweight(Font.BOLDWEIGHT_NORMAL);// 加粗
//			headerStyle.setFont(f);
//			headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
//			// headerStyle.setFillBackgroundColor(HSSFColor.AQUA.index);
//			// headerStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
//			headerStyle.setFillForegroundColor(IndexedColors.ORANGE.index);
//			headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//			return headerStyle;
//		}

		/**
		 * @param wb
		 * @return
		 * @description 得到首列标题行的样式
		 * @author zikc
		 * @date 2016年1月15日 上午12:17:56
		 * @update 2016年1月15日 上午12:17:56
		 * @version V1.0
		 */
		private static XSSFCellStyle getHeaderStyle(SXSSFWorkbook wb) {
			XSSFCellStyle headerStyle = (XSSFCellStyle) wb.createCellStyle();
			XSSFFont f = (XSSFFont) wb.createFont();
			f.setFontHeightInPoints((short) 12);// 字号
			f.setBoldweight(Font.BOLDWEIGHT_NORMAL);// 加粗
			headerStyle.setFont(f);
			headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
			// headerStyle.setFillBackgroundColor(HSSFColor.AQUA.index);
			// headerStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
			headerStyle.setFillForegroundColor(new XSSFColor(color)); //自定义标题行的颜色
			headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			return headerStyle;
		}
		
		/**
		 * @param wb
		 * @return
		 * @description 得到合并行的样式
		 * @author zikc
		 * @date 2016年1月15日 上午12:17:56
		 * @update 2016年1月15日 上午12:17:56
		 * @version V1.0
		 */
//		private static HSSFCellStyle getMergeStyle(HSSFWorkbook wb) {
//			HSSFCellStyle mergeStyle = wb.createCellStyle();
//			mergeStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
//			// mergeStyle.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);//下边框
//			// mergeStyle.setBorderLeft(HSSFCellStyle.BORDER_DOTTED);//左边框
//			// mergeStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
//			// mergeStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//			return mergeStyle;
//		}
		
		/**
		 * @param wb
		 * @return
		 * @description 得到合并行的样式
		 * @author zikc
		 * @date 2016年1月15日 上午12:17:56
		 * @update 2016年1月15日 上午12:17:56
		 * @version V1.0
		 */
		private static CellStyle getMergeStyle(Workbook wb) {
			CellStyle mergeStyle = wb.createCellStyle();
			mergeStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 上下居中
			return mergeStyle;
		}
		
		/**
		 * @param wb
		 * @return
		 * @description 得到合并行的样式
		 * @author zikc
		 * @date 2016年1月15日 上午12:17:56
		 * @update 2016年1月15日 上午12:17:56
		 * @version V1.0
		 */
//		@SuppressWarnings("unused")
//		private static XSSFCellStyle getMergeStyle(XSSFWorkbook wb) {
//			XSSFCellStyle mergeStyle = wb.createCellStyle();
//			mergeStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
//			return mergeStyle;
//		}

		/**
		 * @param String
		 *            sheetName sheet名称
		 * @param int headerRow 设置标题行占的行数，传入0就是取默认值1
		 * @param String
		 *            [] headers 标题行的列值
		 * @param String
		 *            [] columns 列key(即 list<Map<String ,Ojbect>> 中
		 *            map的key)，可以指定导出的列
		 * @param List
		 *            <Map<String, Object>> list 数据
		 * @param int cellWidth 设置单元格宽度，传入0就是取默认值8000
		 * @param int firstMergeColumnCount 前几列需要自动合并的列数，传入0就是不合并
		 * @param int nextMergeColumnCount 后几列需要合并的列数，不自动合并，按照前几列的最后一列的合并方式来合并
		 * @param int mergeFlagColumnIndex 列索引，从0开始，用来标记本行是否需要合并，flag值大于1需要合并
		 * @return
		 * @description 导出Excel
		 * @author zikc
		 * @date 2016年1月13日 下午1:30:40
		 * @update 2016年1月13日 下午1:30:40
		 * @version V1.0
		 * @throws Exception 
		 */
		public static Workbook export(String sheetName, int headerRow,
				String[] headers, String[] columns, List<Object> list,
				int cellWidth, int firstMergeColumnCount, int nextMergeColumnCount,int mergeFlagColumnIndex) throws Exception {
			boolean hasHeaders = false;
			int columnNum = 0;
			if (headers != null && headers.length > 0) {
				hasHeaders = true;
				columnNum = headers.length;
			}
			if (columns == null) {
				columns = new String[] {};
			}
			columnNum = Math.max(columnNum, columns.length); // 列数 取标题行与数据行 列数的较大值
			if (cellWidth <= 0) {
				cellWidth = DEFAULT_CELL_WIDTH;
			}
			//导出Excel（XSSF,2007格式）
			return exportToXlsxByXSSF(sheetName, cellWidth, headerRow, headers, columns, list,
					columnNum, hasHeaders, firstMergeColumnCount,
					nextMergeColumnCount,mergeFlagColumnIndex);
//			return exportToXls(sheetName, cellWidth, headerRow, headers, columns, list,
//					columnNum, hasHeaders, firstMergeColumnCount,
//					nextMergeColumnCount,mergeFlagColumnIndex);
		}


		/**
		 * @param sheetName
		 *            sheet名称
		 * @param cellWidth
		 *            设置单元格宽度
		 * @param headerRowCount
		 *            设置头列占的行数
		 * @param headers
		 *            头列值
		 * @param columns
		 *            列key(即 list<Map<String ,Ojbect>> 中 map的key)，可以指定导出的列
		 * @param list
		 *            数据
		 * @param columnNum
		 *            列的数量
		 * @param hasHeaders
		 *            是否有头列
		 * @param firstMergeColumnCount
		 *            前几列需要自动合并的列数，传入0就是不合并
		 * @param nextMergeColumnCount
		 *            后几列需要合并的列数，不自动合并，按照前几列的最后一列的合并方式来合并
		 * @param mergeFlagColumnIndex
		 *            列索引，用来标记本行是否需要合并，flag值大于1需要合并
		 * @return
		 * @description 导出Excel（HSSF,2003格式）
		 * @author zikc
		 * @date 2016年1月6日 下午10:18:40
		 * @update 2016年1月6日 下午10:18:40
		 * @version V1.0
		 * @throws Exception 
		 */
		@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
		private static Workbook exportToXls(String sheetName, int cellWidth,
				int headerRowCount, String[] headers, String[] columns, List list,
				int columnNum, boolean hasHeaders, int firstMergeColumnCount,
				int nextMergeColumnCount,int mergeFlagColumnIndex) throws Exception {
			if (sheetName == null || sheetName.isEmpty()) {
				sheetName = "new sheet";
			}
			HSSFWorkbook wb = null;
			try {
		        wb = new HSSFWorkbook();
				Sheet sheet = wb.createSheet(sheetName); // 一个wb只能创建一个sheet
				Row row = null;
				Cell cell = null;
				CellStyle headerStyle = null;
				//getHeaderStyle(wb);
				setCellWidth(sheet, cellWidth, columnNum);
				if (hasHeaders) {
					row = sheet.createRow(0);
					if (headerRowCount <= 0) {
						headerRowCount = HEADER_ROW;
					}
					headerRowCount = Math.min(headerRowCount, MAX_ROWS);
					log.info("开始生成Excel标题行，行数为[" + headerRowCount + "]");
					for (int h = 0, lenH = headers.length; h < lenH; h++) {
						// 按每列合并标题行，h为列索引
						CellRangeAddress region=new CellRangeAddress(0,headerRowCount-1, h,h);
//						Region region = new Region(0, (short) h,
//								(short) headerRow - 1, (short) h);// 合并从第rowFrom行columnFrom列到rowTo行columnTo的区域
						sheet.addMergedRegion(region);
						// 得到所有区域
						sheet.getNumMergedRegions();
						sheet.setColumnWidth(h, cellWidth);
						cell = row.createCell(h);
						cell.setCellValue(headers[h]);
						cell.setCellStyle(headerStyle);
					}
				}
				log.info("生成Excel标题行结束");
				if (list == null || list.size() == 0) {
					log.info("数据行内容为空，不再生成数据行");
					return wb;
				}
				log.info("开始生成Excel数据行，行数为[" + list.size() + "]");
				int mergeTotalCount = firstMergeColumnCount + nextMergeColumnCount;
				int currentRowIndex=0;
				Record record =null;
				Map<String, Object> map =null;
				Object v = null;
				Set<String> keys =null;
				for (int i = 0, len = list.size(); i < len; i++) {
					currentRowIndex=i + headerRowCount;
					row = sheet.createRow(currentRowIndex);
					Object obj = list.get(i);
					if (obj == null)
						continue;
					// obj : record map model
					if (obj instanceof Map) {
						map = (Map<String, Object>) obj;
						if (columns.length == 0) {// 未设置显示列，默认全部
							keys = map.keySet();
							if (mergeTotalCount > keys.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							int columnIndex = 0;
							for (String key : keys) {
								cell = row.createCell(columnIndex);
								v = map.get(map.get(key));
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) {
								cell = row.createCell(j);
								v = map.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
					} else if (obj instanceof Model) {
						Model model = (Model) obj;
						Set<Entry<String, Object>> entries = model._getAttrsEntrySet();
						if (columns.length == 0) {// 未设置显示列，默认全部
							int columnIndex = 0;
							if (mergeTotalCount > entries.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (Entry<String, Object> entry : entries) {
								cell = row.createCell(columnIndex);
								v = entry.getValue();
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) {
								cell = row.createCell(j);
								v = model.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}

					} else if (obj instanceof Record) {
						record = (Record) obj; // 每一行record
						if (columns.length == 0) {// 未设置显示列，默认全部
							map = record.getColumns(); // 从record得到map
							keys = map.keySet();
							if (mergeTotalCount > keys.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							int columnIndex = 0;
							for (String key : keys) {
								cell = row.createCell(columnIndex);
								v = record.get(key);
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) { // 按列循环,j是列的索引
								cell = row.createCell(j);
								v=record.get(columns[j]);
								//v = map.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
					}
				}
				log.info("生成Excel数据行结束");
				//firstMergeColumnCount=0;
				if (firstMergeColumnCount > 0) {
					int endRowIndex = sheet.getLastRowNum();
					log.info("开始合并Excel数据行，数据行[起始index]为[" + headerRowCount
							+ "]，[终止index]为[" + endRowIndex + "]，" + "前["
							+ firstMergeColumnCount + "]列按行内容自动合并，" + "后["
							+ nextMergeColumnCount + "]列随着列index["
							+ (firstMergeColumnCount - 1) + "]同时合并");
					Map<String, Integer> retMap=mergeSheetRow(wb, sheet, headerRowCount, endRowIndex,
							firstMergeColumnCount, nextMergeColumnCount,mergeFlagColumnIndex);
//					Integer rowCountAfterMerge=retMap.get("rowCountAfterMerge");
					Integer removeRowCount=retMap.get("removeRowCount");
					log.info("合并Excel数据行结束，Excel包含标题行["+headerRowCount+"]行，"
							+ "消除掉重复数据行["+removeRowCount+"]行，"
									+ "合并前数据行为["+(endRowIndex-headerRowCount+1)+"]行，"
									+ "合并后数据行为["+(endRowIndex-headerRowCount+1-removeRowCount)+"]行"
							);
				}
			} catch (Exception ex) {
				throw ex;
			}
			return wb;
		}
		
		/**
		 * @param sheetName
		 *            sheet名称
		 * @param cellWidth
		 *            设置单元格宽度
		 * @param headerRowCount
		 *            设置头列占的行数
		 * @param headers
		 *            头列值
		 * @param columns
		 *            列key(即 list<Map<String ,Ojbect>> 中 map的key)，可以指定导出的列
		 * @param list
		 *            数据
		 * @param columnNum
		 *            列的数量
		 * @param hasHeaders
		 *            是否有头列
		 * @param firstMergeColumnCount
		 *            前几列需要自动合并的列数，传入0就是不合并
		 * @param nextMergeColumnCount
		 *            后几列需要合并的列数，不自动合并，按照前几列的最后一列的合并方式来合并
		 * @param mergeFlagColumnIndex
		 *            列索引，用来标记本行是否需要合并，flag值大于1需要合并
		 * @return
		 * @description 导出Excel（XSSF,2007格式）
		 * @author zikc
		 * @date 2016年1月6日 下午10:18:40
		 * @update 2016年1月6日 下午10:18:40
		 * @version V1.0
		 * @throws Exception 
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private static Workbook exportToXlsxByXSSF(String sheetName, int cellWidth,
				int headerRowCount, String[] headers, String[] columns, List list,
				int columnNum, boolean hasHeaders, int firstMergeColumnCount,
				int nextMergeColumnCount,int mergeFlagColumnIndex) throws Exception {
			if (sheetName == null || sheetName.isEmpty()) {
				sheetName = "new sheet";
			}
			SXSSFWorkbook wb = null;
			try {
		        wb = new SXSSFWorkbook();
				Sheet sheet = wb.createSheet(sheetName); // 一个wb只能创建一个sheet
				Row row = null;
				Cell cell = null;
				CellStyle headerStyle = getHeaderStyle(wb);
				setCellWidth(sheet, cellWidth, columnNum);
				if (hasHeaders) {
					row = sheet.createRow(0);
					if (headerRowCount <= 0) {
						headerRowCount = HEADER_ROW;
					}
					headerRowCount = Math.min(headerRowCount, MAX_ROWS);
					log.info("开始生成Excel标题行，行数为[" + headerRowCount + "]");
					for (int h = 0, lenH = headers.length; h < lenH; h++) {
						// 按每列合并标题行，h为列索引
						CellRangeAddress region=new CellRangeAddress(0,headerRowCount-1, h,h);
//						Region region = new Region(0, (short) h,
//								(short) headerRow - 1, (short) h);// 合并从第rowFrom行columnFrom列到rowTo行columnTo的区域
						sheet.addMergedRegion(region);
						// 得到所有区域
						sheet.getNumMergedRegions();
						sheet.setColumnWidth(h, cellWidth);
						cell = row.createCell(h);
						cell.setCellValue(headers[h]);
						cell.setCellStyle(headerStyle);
					}
				}
				log.info("生成Excel标题行结束");
				if (list == null || list.size() == 0) {
					log.info("数据行内容为空，不再生成数据行");
					return wb;
				}
				log.info("开始生成Excel数据行，行数为[" + list.size() + "]");
				int mergeTotalCount = firstMergeColumnCount + nextMergeColumnCount;
				int currentRowIndex=0;
				Record record =null;
				Map<String, Object> map =null;
				Object v = null;
				Set<String> keys =null;
				Object obj;
				int columnIndex;
				Model model;
				for (int i = 0, len = list.size(); i < len; i++) {
					currentRowIndex=i + headerRowCount;
					row = sheet.createRow(currentRowIndex);
					obj = list.get(i);
					if (obj == null) {
						continue;
					}
					// obj : record map model
					if (obj instanceof Map) {
						map = (Map<String, Object>) obj;
						if (columns.length == 0) {// 未设置显示列，默认全部
							keys = map.keySet();
							if (mergeTotalCount > keys.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							columnIndex = 0;
							for (String key : keys) {
								cell = row.createCell(columnIndex);
								v = map.get(map.get(key));
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) {
								cell = row.createCell(j);
								v = map.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
						if (i % FLUSH_LINE == 0)
							((SXSSFSheet)sheet).flushRows();
					} else if (obj instanceof Model) {
						model = (Model) obj;
						Set<Entry<String, Object>> entries = model._getAttrsEntrySet();
						if (columns.length == 0) {// 未设置显示列，默认全部
							columnIndex = 0;
							if (mergeTotalCount > entries.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (Entry<String, Object> entry : entries) {
								cell = row.createCell(columnIndex);
								v = entry.getValue();
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) {
								cell = row.createCell(j);
								v = model.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
						if (i % FLUSH_LINE == 0)
							((SXSSFSheet)sheet).flushRows();
					} else if (obj instanceof Record) {
						record = (Record) obj; // 每一行record
						if (columns.length == 0) {// 未设置显示列，默认全部
							map = record.getColumns(); // 从record得到map
							keys = map.keySet();
							if (mergeTotalCount > keys.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							columnIndex = 0;
							for (String key : keys) {
								cell = row.createCell(columnIndex);
								v = record.get(key);
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) { // 按列循环,j是列的索引
								cell = row.createCell(j);
								v=record.get(columns[j]);
								//v = map.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
						if (i % FLUSH_LINE == 0)
							((SXSSFSheet)sheet).flushRows();
					}
				}
				((SXSSFSheet)sheet).flushRows();
				log.info("生成Excel数据行结束");
				//firstMergeColumnCount=0;
				if (firstMergeColumnCount > 0) {
					int endRowIndex = sheet.getLastRowNum();
					log.info("开始合并Excel数据行，数据行[起始index]为[" + headerRowCount
							+ "]，[终止index]为[" + endRowIndex + "]，" + "前["
							+ firstMergeColumnCount + "]列按行内容自动合并，" + "后["
							+ nextMergeColumnCount + "]列随着列index["
							+ (firstMergeColumnCount - 1) + "]同时合并");
					Map<String, Integer> retMap=mergeSheetRow(wb, sheet, headerRowCount, endRowIndex,
							firstMergeColumnCount, nextMergeColumnCount,mergeFlagColumnIndex);
//					Integer rowCountAfterMerge=retMap.get("rowCountAfterMerge");
					Integer removeRowCount=retMap.get("removeRowCount");
					log.info("合并Excel数据行结束，Excel包含标题行["+headerRowCount+"]行，"
							+ "消除掉重复数据行["+removeRowCount+"]行，"
									+ "合并前数据行为["+(endRowIndex-headerRowCount+1)+"]行，"
									+ "合并后数据行为["+(endRowIndex-headerRowCount+1-removeRowCount)+"]行"
							);
				}
			} catch (Exception ex) {
				throw ex;
			}
			return wb;
		}
		
		/**
		 * @param sheetName
		 *            sheet名称
		 * @param cellWidth
		 *            设置单元格宽度
		 * @param headerRowCount
		 *            设置头列占的行数
		 * @param headers
		 *            头列值
		 * @param columns
		 *            列key(即 list<Map<String ,Ojbect>> 中 map的key)，可以指定导出的列
		 * @param list
		 *            数据
		 * @param columnNum
		 *            列的数量
		 * @param hasHeaders
		 *            是否有头列
		 * @param firstMergeColumnCount
		 *            前几列需要自动合并的列数，传入0就是不合并
		 * @param nextMergeColumnCount
		 *            后几列需要合并的列数，不自动合并，按照前几列的最后一列的合并方式来合并
		 * @return
		 * @description 导出Excel（SXSSF,2007格式）
		 * @author zikc
		 * @date 2016年1月6日 下午10:18:40
		 * @update 2016年1月6日 下午10:18:40
		 * @version V1.0
		 * @throws Exception 
		 */
		@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
		private static SXSSFWorkbook exportToXlsxBySXSSF(String sheetName, int cellWidth,
				int headerRowCount, String[] headers, String[] columns, List list,
				int columnNum, boolean hasHeaders, int firstMergeColumnCount,
				int nextMergeColumnCount) throws Exception {
			if (sheetName == null || sheetName.isEmpty()) {
				sheetName = "new sheet";
			}
			SXSSFWorkbook wb = null;
			try {
				int rowInCacheCount=5000;//内存中缓存记录行数
		        wb = new SXSSFWorkbook(-1);
				SXSSFSheet sheet = wb.createSheet(sheetName); // 一个wb只能创建一个sheet
				Row row = null;
				Cell cell = null;
				CellStyle headerStyle = null;
				//getHeaderStyle(wb);
				setCellWidth(sheet, cellWidth, columnNum);
				if (hasHeaders) {
					row = sheet.createRow(0);
					if (headerRowCount <= 0) {
						headerRowCount = HEADER_ROW;
					}
					headerRowCount = Math.min(headerRowCount, MAX_ROWS);
					log.info("开始生成Excel标题行，行数为[" + headerRowCount + "]");
					for (int h = 0, lenH = headers.length; h < lenH; h++) {
						// 按每列合并标题行，h为列索引
						CellRangeAddress region=new CellRangeAddress(0,headerRowCount-1, h,h);
//						Region region = new Region(0, (short) h,
//								(short) headerRow - 1, (short) h);// 合并从第rowFrom行columnFrom列到rowTo行columnTo的区域
						sheet.addMergedRegion(region);
						// 得到所有区域
						sheet.getNumMergedRegions();
						sheet.setColumnWidth(h, cellWidth);
						cell = row.createCell(h);
						cell.setCellValue(headers[h]);
						cell.setCellStyle(headerStyle);
					}
				}
				log.info("生成Excel标题行结束");
				if (list == null || list.size() == 0) {
					log.info("数据行内容为空，不再生成数据行");
					return wb;
				}
				log.info("开始生成Excel数据行，行数为[" + list.size() + "]");
				int mergeTotalCount = firstMergeColumnCount + nextMergeColumnCount;
				int currentRowIndex=0;
				for (int i = 0, len = list.size(); i < len; i++) {
					currentRowIndex=i + headerRowCount;
					row = sheet.createRow(currentRowIndex);
					Object obj = list.get(i);
					if (obj == null) {
						continue;
					}
					// obj : record map model
					if (obj instanceof Map) {
						Map<String, Object> map = (Map<String, Object>) obj;
						if (columns.length == 0) {// 未设置显示列，默认全部
							Set<String> keys = map.keySet();
							if (mergeTotalCount > keys.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							int columnIndex = 0;
							for (String key : keys) {
								cell = row.createCell(columnIndex);
								Object v = map.get(map.get(key));
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) {
								cell = row.createCell(j);
								Object v = map.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
					} else if (obj instanceof Model) {
						Model model = (Model) obj;
						Set<Entry<String, Object>> entries = model._getAttrsEntrySet();
						if (columns.length == 0) {// 未设置显示列，默认全部
							int columnIndex = 0;
							if (mergeTotalCount > entries.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (Entry<String, Object> entry : entries) {
								cell = row.createCell(columnIndex);
								Object v = entry.getValue();
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) {
								cell = row.createCell(j);
								Object v = model.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}

					} else if (obj instanceof Record) {
						Record record = (Record) obj; // 每一行record
						Map<String, Object> map = record.getColumns(); // 从record得到map
						if (columns.length == 0) {// 未设置显示列，默认全部
							Set<String> keys = map.keySet();
							if (mergeTotalCount > keys.size()) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							int columnIndex = 0;
							for (String key : keys) {
								cell = row.createCell(columnIndex);
								Object v = record.get(key);
								cell.setCellValue((v == null ? "" : v) + "");
								columnIndex++;
							}
						} else {
							if (mergeTotalCount > columns.length) // 如果merge总列数超过了需要导出的列数，则不合并
								firstMergeColumnCount = 0;
							for (int j = 0, lenJ = columns.length; j < lenJ; j++) { // 按列循环,j是列的索引
								cell = row.createCell(j);
								Object v = map.get(columns[j]);
								cell.setCellValue((v == null ? "" : v) + "");
							}
						}
					}
					//每当行数达到设置的值就刷新数据到硬盘,以清理内存
					//i为当前数据行的行索引，有别于 sheet的行索引
	                if((i>0) && (i%rowInCacheCount==0)){
//	        			if (firstMergeColumnCount > 0) {
//	        				//int endRowIndex = sheet.getLastRowNum();
//	        				int mergeEndRowIndex=i+headerRowCount; //起始行要加上 标题行数
//	        				int mergeStartRowIndex=i-rowInCacheCount+headerRowCount; //结束行也要加上 标题行数
//	        				log.info("开始合并Excel数据行，数据行[起始index]为[" + mergeStartRowIndex
//	        						+ "]，[终止index]为[" + mergeEndRowIndex + "]，" + "前["
//	        						+ firstMergeColumnCount + "]列按行内容自动合并，" + "后["
//	        						+ nextMergeColumnCount + "]列随着列index["
//	        						+ (firstMergeColumnCount - 1) + "]同时合并");
//	        				Integer dataRowCount=mergeSheetRow(wb, sheet, headerRowCount, mergeEndRowIndex,
//	        						firstMergeColumnCount, nextMergeColumnCount);
//	        				log.info("合并Excel数据行结束，合并后的Excel包含标题行["+headerRowCount+"]行，数据行["+dataRowCount+"]行");
//	        			}
	                   ((SXSSFSheet)sheet).flushRows();
	                }
				}
				log.info("生成Excel数据行结束");
				//firstMergeColumnCount=0;
				if (firstMergeColumnCount > 0) {
					int endRowIndex = sheet.getLastRowNum();
					log.info("开始合并Excel数据行，数据行[起始index]为[" + headerRowCount
							+ "]，[终止index]为[" + endRowIndex + "]，" + "前["
							+ firstMergeColumnCount + "]列按行内容自动合并，" + "后["
							+ nextMergeColumnCount + "]列随着列index["
							+ (firstMergeColumnCount - 1) + "]同时合并");
					Integer dataRowCount=mergeSheetRow(wb, sheet, headerRowCount, endRowIndex,
							firstMergeColumnCount, nextMergeColumnCount);
					log.info("合并Excel数据行结束，合并后的Excel包含标题行["+headerRowCount+"]行，数据行["+dataRowCount+"]行");
				}
			} catch (Exception ex) {
				throw ex;
			}
			return wb;
		}
		
		@SuppressWarnings("unused")
		private static Integer mergeSheetRow(HSSFWorkbook wb, HSSFSheet sheet,
				int startRowIndex, int endRowIndex, int firstMergeColumnCount,
				int nextMergeColumnCount) {
			String curCellValue = null;
			String nextCellValue = null;
			HSSFCell cell = null;
			HSSFCell sameCell = null;
			CellStyle cs = getMergeStyle(wb);
			Integer rowCountAfterMerge=0;
			for (int i = 0; i < firstMergeColumnCount; i++) { // 遍历前firstMergeColumnCount列
				for (int j = startRowIndex; j < endRowIndex + 1; j++) { // 遍历行，外循环开始
					HSSFRow row = sheet.getRow(j);
					curCellValue = row.getCell(i).getStringCellValue(); // 得到当前行单元格的值
					boolean isLoop = true; // 代表是否需要进行内循环比较
					int k = 1; // k代表内循环的次数
					while (isLoop) { // 内循环开始，逐行比对下一行单元格与当前行单元格的内容，如果相同，则继续内循环
						HSSFRow nextRow = null;
						if (j + k <= endRowIndex) { // 数据行范围内比对
							nextRow = sheet.getRow(j + k);
							nextCellValue = nextRow.getCell(i).getStringCellValue(); // 得到下一行单元格的值
							if (!curCellValue.equals(nextCellValue)) { // 值不等，停止内循环，添加合并行，并设置值，继续外循环
								if (k > 1) { // 内循环1次以上才需要合并，否则依旧保留原来的cell
									sheet.addMergedRegion(new CellRangeAddress(j, j
											+ k - 1, i, i));
//									cell = row.createCell(i);
//									cell.setCellValue(curCellValue);
//									cell.setCellStyle(cs);
								}
								isLoop = false;
							} else { // 值相等则继续内循环,同时清除值相同的cell，以防Excel计数出问题
								sameCell = sheet.getRow(j + k).getCell(i);
								sheet.getRow(j + k).removeCell(sameCell);
								k++;
							}
						} else { // 如果已经比对到数据最后一行的下一行，如果此时loop还是true，代表最后一行往上的都需要合并
							sheet.addMergedRegion(new CellRangeAddress(j,
									j + k - 1, i, i));
//							cell = row.createCell(i);
//							cell.setCellValue(curCellValue);
//							cell.setCellStyle(cs);
							isLoop = false;
						}
						// 假如内循环结束，并且当前cell的column索引已经到了前firstMergeColumnCount列的最后一列
						// 此时按照前firstMergeColumnCount列的最后一列 的合并方式 去同时合并 后面的cell
						if (!isLoop && (i == (firstMergeColumnCount - 1))) {
							String tmpCellValue = null;
							rowCountAfterMerge++; //统计合并以后的数据行数
							for (int m = 1; m <= nextMergeColumnCount; m++) {// i+m为当前cell的列索引，m为后几列需要合并的总列数
								sheet.addMergedRegion(new CellRangeAddress(j, j + k
										- 1, i + m, i + m));
								tmpCellValue = sheet.getRow(j).getCell(i + m)
										.getStringCellValue();
								// 先清除原cell(行索引范围为 j到j+k-1，列索引为i+m)，为以防计数出问题
								for (int n = j; n <= j + k - 1; n++) {
									sameCell = sheet.getRow(n).getCell(i + m);
									sheet.getRow(n).removeCell(sameCell);
								}
								cell = row.createCell(i + m);
								cell.setCellValue(tmpCellValue);
								cell.setCellStyle(cs);
							}
						}
					}
					j = j + k - 1;
				}
			}
			return rowCountAfterMerge;
		}
		
		private static Map<String, Integer> mergeSheetRow(Workbook wb, Sheet sheet,
				int startRowIndex, int endRowIndex, int firstMergeColumnCount,
				int nextMergeColumnCount,int mergeFlagColumnIndex) {
			String curCellValue = null;
			String nextCellValue = null;
			Cell cell = null;
			Cell sameCell = null;
			CellStyle cs = getMergeStyle(wb);
			Integer rowCountAfterMerge=0,removeRowCount=0;
			Row row=null;
			for (int i = 0; i < firstMergeColumnCount; i++) { // 遍历前firstMergeColumnCount列,i为当前列索引
				for (int j = startRowIndex; j <= endRowIndex; j++) { // j为外循环所在的行索引，遍历行，外循环开始
					row = sheet.getRow(j);
					//先判断标记位，如果小于等于1，则直接不启动合并，第一列无需判断，直接合并
					if (i>0 && Float.parseFloat(row.getCell(mergeFlagColumnIndex).getStringCellValue())<=1)
						continue;
					if (row == null)
						continue;
					//log.info("j="+j+"getRow end "+DateUtil.curDate());
					curCellValue = row.getCell(i) == null ? "" : row.getCell(i).getStringCellValue(); // 得到当前行单元格的值
					//log.info("getCurCellValue end "+DateUtil.curDate());
					boolean isLoop = true; // 代表是否需要进行内循环比较
					int k = 1; // k代表内循环中的比较次数
					while (isLoop) { // 内循环开始，逐行比对下一行单元格与当前行单元格的内容，如果相同，则继续内循环
						Row nextRow = null;
						if (j + k <= endRowIndex) { // 数据行范围内比对
							nextRow = sheet.getRow(j + k);
							//log.info("j+k="+(j+k)+" getNextRow end "+DateUtil.curDate());
							nextCellValue = nextRow.getCell(i).getStringCellValue(); // 得到下一行单元格的值
							//log.info("j+k="+(j+k)+" getNextCellValue end "+DateUtil.curDate());
							if (!curCellValue.equals(nextCellValue)) { // 值不等，停止内循环，添加合并行，并设置值，继续外循环
								if (k > 1) { // 内循环1次以上才需要合并，否则依旧保留原来的cell
									//log.info("j="+j+" merge start "+DateUtil.curDate());
									sheet.addMergedRegion(new CellRangeAddress(j, j
											+ k - 1, i, i));
									//log.info("j="+j+" merge end" +DateUtil.curDate());
									cell = row.createCell(i); // HSSF这里需要再createCell一次，是为了让单元格文本上下居中
									cell.setCellValue(curCellValue); //XSSF可以删掉这里3行，提高性能
									cell.setCellStyle(cs);
								}
								isLoop = false; //设置为 false，结束本次内循环
							} else { // 值相等则继续内循环,同时清除值相同的cell，以防Excel计数出问题
								sameCell = sheet.getRow(j + k).getCell(i);
								sheet.getRow(j + k).removeCell(sameCell);
								k++;
							}
						} else { // 如果已经比对到数据最后一行的下一行，如果此时loop还是true，代表最后一行往上的都需要合并
							sheet.addMergedRegion(new CellRangeAddress(j,
									j + k - 1, i, i));
							cell = row.createCell(i); // HSSF这里需要再createCell一次，是为了让单元格文本上下居中
							cell.setCellValue(curCellValue); //XSSF可以删掉这里3行，提高性能
							cell.setCellStyle(cs);
							isLoop = false; //设置为 false，结束本次内循环
						}
						// 假如内循环结束，并且内循环1次以上，并且当前cell的column索引已经到了前firstMergeColumnCount列的最后一列
						// 此时按照前firstMergeColumnCount列的最后一列 的合并方式 去同时合并 后面的cell
						if (!isLoop && k>1 && (i == (firstMergeColumnCount - 1))) {
							String tmpCellValue = null;
							rowCountAfterMerge++; //统计合并以后的数据行数
							//k-1为每次合并消除掉的行数,将每次合并消除掉的行数加起来，进行统计
							removeRowCount+=(k-1);
							for (int m = 1; m <= nextMergeColumnCount; m++) {// i+m为当前cell的列索引，m为后几列需要合并的总列数
								//log.info("merge 2 start"+DateUtil.curDate());
								sheet.addMergedRegion(new CellRangeAddress(j, j + k
										- 1, i + m, i + m));
								//log.info("merge 2 end"+DateUtil.curDate());
								tmpCellValue = sheet.getRow(j).getCell(i + m).getStringCellValue();
								// 先清除原cell(行索引范围为 j到j+k-1，列索引为i+m)，为以防计数出问题
								for (int n = j; n <= j + k - 1; n++) {
									sameCell = sheet.getRow(n).getCell(i + m);
									sheet.getRow(n).removeCell(sameCell);
								}
								cell = row.createCell(i + m);
								cell.setCellValue(tmpCellValue);
								cell.setCellStyle(cs);
							}
						}
					}
					j = j + k -1 ;// j为外循环所在的行索引，k代表内循环中的比较次数
					// (j+k) 为下一次外循环的起始index，因为for里面有个j++，所以这里要 -1
				}
			}
			Map<String, Integer> retMap=new HashMap<String, Integer>();
			retMap.put("rowCountAfterMerge", rowCountAfterMerge);
			retMap.put("removeRowCount", removeRowCount);
			return retMap;
		}
		
		private static Integer mergeSheetRow(SXSSFWorkbook wb, SXSSFSheet sheet,
				int startRowIndex, int endRowIndex, int firstMergeColumnCount,
				int nextMergeColumnCount) {
			String curCellValue = null;
			String nextCellValue = null;
			Cell cell = null;
			Cell sameCell = null;
			CellStyle cs = getMergeStyle(wb);
			Integer rowCountAfterMerge=0;
			Row row=null;
//			XSSFRow nextRow = null;
			for (int i = 0; i < firstMergeColumnCount; i++) { // 遍历前firstMergeColumnCount列
				for (int j = startRowIndex; j <= endRowIndex; j++) { // j为外循环所在的行索引，遍历行，外循环开始
					row = sheet.getRow(j);
					log.info("getRow end"+DateUtil.curDate());
					curCellValue = row.getCell(i).getStringCellValue(); // 得到当前行单元格的值
					boolean isLoop = true; // 代表是否需要进行内循环比较
					int k = 1; // k代表内循环的次数
					while (isLoop) { // 内循环开始，逐行比对下一行单元格与当前行单元格的内容，如果相同，则继续内循环
						Row nextRow = null;
						if (j + k <= endRowIndex) { // 数据行范围内比对
							nextRow = sheet.getRow(j + k);
							log.info("getNextRow end"+DateUtil.curDate());
							nextCellValue = nextRow.getCell(i).getStringCellValue(); // 得到下一行单元格的值
							log.info("getNextCellRow end "+DateUtil.curDate());
							if (!curCellValue.equals(nextCellValue)) { // 值不等，停止内循环，添加合并行，并设置值，继续外循环
								if (k > 1) { // 内循环1次以上才需要合并，否则依旧保留原来的cell
									log.info("merge start"+DateUtil.curDate());
									sheet.addMergedRegion(new CellRangeAddress(j, j
											+ k - 1, i, i));
									log.info("merge end"+DateUtil.curDate());
									//cell = row.createCell(i);
									//cell.setCellValue(curCellValue);
									//cell.setCellStyle(cs);
								}
								isLoop = false;
							} else { // 值相等则继续内循环,同时清除值相同的cell，以防Excel计数出问题
								sameCell = sheet.getRow(j + k).getCell(i);
								log.info("remove start"+DateUtil.curDate());
								sheet.getRow(j + k).removeCell(sameCell);
								log.info("remove end"+DateUtil.curDate());
								k++;
							}
						} else { // 如果已经比对到数据最后一行的下一行，如果此时loop还是true，代表最后一行往上的都需要合并
							sheet.addMergedRegion(new CellRangeAddress(j,
									j + k - 1, i, i));
							//cell = row.createCell(i);
							//cell.setCellValue(curCellValue);
							//cell.setCellStyle(cs);
							isLoop = false;
						}
						// 假如内循环结束，并且当前cell的column索引已经到了前firstMergeColumnCount列的最后一列
						// 此时按照前firstMergeColumnCount列的最后一列 的合并方式 去同时合并 后面的cell
						if (!isLoop && (i == (firstMergeColumnCount - 1))) {
							String tmpCellValue = null;
							rowCountAfterMerge++; //统计合并以后的数据行数
							for (int m = 1; m <= nextMergeColumnCount; m++) {// i+m为当前cell的列索引，m为后几列需要合并的总列数
								sheet.addMergedRegion(new CellRangeAddress(j, j + k
										- 1, i + m, i + m));
								//tmpCellValue=getValue(sheet.getRow(j).getCell(i + m));
								tmpCellValue = sheet.getRow(j).getCell(i + m).getStringCellValue();
								// 先清除原cell(行索引范围为 j到j+k-1，列索引为i+m)，为以防计数出问题
								for (int n = j; n <= j + k - 1; n++) {
									sameCell = sheet.getRow(n).getCell(i + m);
									sheet.getRow(n).removeCell(sameCell);
								}
								cell = row.createCell(i + m);
								cell.setCellValue(tmpCellValue);
								cell.setCellStyle(cs);
							}
						}
					}
					j = j + k-1; // j为外循环所在的行索引，k代表内循环的次数，也就是比的次数
				}
			}
			return rowCountAfterMerge;
		}
		
		/**
		 * @param path
		 * @param sheetName
		 * @param headerRow
		 * @param headers
		 * @param columns
		 * @param list
		 * @param cellWidth
		 * @param firstMergeColumnCount
		 * @param nextMergeColumnCount
		 * @param mergeFlagColumnIndex
		 * @throws Exception
		 * @description 生成Excel到指定文件目录下
		 * @author zikc
		 * @date 2017年4月5日 下午6:40:51
		 * @update 2017年4月5日 下午6:40:51
		 * @version V1.0
		 */
		public static void exportAsFile(String path,String sheetName, int headerRow,
				String[] headers, String[] columns, List<Object> list,
				int cellWidth, int firstMergeColumnCount, int nextMergeColumnCount,int mergeFlagColumnIndex) throws Exception {
			Workbook wb=export(sheetName, headerRow, headers, columns, list, cellWidth, firstMergeColumnCount, nextMergeColumnCount,mergeFlagColumnIndex);
//			if (wb instanceof HSSFWorkbook)
//				path += ".xls";
//			else if (wb instanceof XSSFWorkbook || wb instanceof SXSSFWorkbook)
//				path += ".xlsx";
			FileOutputStream fileOut= new FileOutputStream(path);
	        wb.write(fileOut);
	        fileOut.close();
		}
		
		/**
		 * @param excelList
		 * @param keys
		 * @return
		 * @description 检查Excel数据行指定列的内容是否唯一，可传入多个列名
		 * @author zikc
		 * @date 2017年4月14日 上午1:04:52
		 * @update 2017年4月14日 上午1:04:52
		 * @version V1.0
		 */
		public static JsonResult<Object> checkUnique(List<Map<String, String>> excelList,String... keys) {
			StringBuffer errorMsg = new StringBuffer("导入的Excel文件");
			//防止导入的Excel中出现主键相同的重复信息,进行必要的唯一性检查
			//key:把所有联合主键拼接成的唯一主键 
			//value:excel行数
			Map<String, Integer> excelInfoMap = new HashMap<String, Integer>();
			boolean isRepeatExist = false;
			Map<String, String> map = null;
			//excelList为除去标题行的数据行内容，数据行的第1行相当于Excel文件的第2行
			for (int i=0; i<excelList.size(); i++) {
				map = excelList.get(i);
				String uniqueKey="";
				for(String key:keys) {
					//把所有联合主键拼接成唯一主键
					uniqueKey+=map.get(key);
				}
				if (excelInfoMap.get(uniqueKey) == null) {
					excelInfoMap.put(uniqueKey, i + 2);
				} else { //excelInfoMap已存在相同主键的情况下，就报错
					isRepeatExist = true;
					Integer existIndex = excelInfoMap.get(uniqueKey);
					errorMsg.append("第【").append(i + 2).append("】行与第【").append(existIndex).append("】行，");
				}
			}
			JsonResult<Object> result=new JsonResult<Object>();
			if (isRepeatExist) {
				errorMsg=errorMsg.deleteCharAt(errorMsg.length()-1);
				errorMsg.append("存在重复，请检查修改后重新导入!");
				result.setFailMsg(errorMsg.toString());
			} else {
				result.setSuccessMsg("");
			}
			return result;
		}
	}

	
}
