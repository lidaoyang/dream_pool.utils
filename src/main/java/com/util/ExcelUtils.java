package com.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ExcelUtils {
	// jqgrid上显示出来的属性
	private String FileName;
	private String properties;
	private String names;
	private String[] cols;
	private String[] titles;
	private Object[] colNames;
	private Object[] colModel;

	public String[] getCols() {
		return cols;
	}

	public void setCols(String[] cols) {
		this.cols = cols;
	}

	public String[] getTitles() {
		return titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public Object[] getColNames() {
		return colNames;
	}

	public void setColNames(Object[] colNames) {
		this.colNames = colNames;
	}

	public Object[] getColModel() {
		return colModel;
	}

	public void setColModel(Object[] colModel) {
		this.colModel = colModel;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
		this.setCols(properties.split(","));
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
		this.setTitles(names.split(","));
	}


	public static List<String> getSheetNames(File file) {
		InputStream is = null;
		List<String> sheetNames = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			HSSFWorkbook hwk = new HSSFWorkbook(is);
			int numofsheet = hwk.getNumberOfSheets();
			sheetNames = new ArrayList<String>();
			String name = null;
			for (int i = 0; i < numofsheet; i++) {
				name = hwk.getSheetName(i);
				sheetNames.add(name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sheetNames;
	}

	private static DecimalFormat PHONENUMBER_DF = new DecimalFormat(
			"############.####");

	/**
	 * 以excel首行字段为title
	 * 
	 * @param file
	 * @param sheetnum
	 * @return
	 */
	public static List<Object[]> getListExcel(File file, int sheetnum) {
		return getListExcel(file, sheetnum, false);
	}

	/**
	 * 数据初始化模块excel转换为List
	 * 
	 * @param file
	 * @param sheetnum
	 * @return
	 */
	public static ArrayList<HashMap<String, Object>> getMapListExcel(File file,
			int sheetnum) {
		List<Object[]> list = getListExcel(file, sheetnum);
		return getArraysToMap(list);
	}

	/**
	 * 将数组的List转为map的List
	 * 
	 * @param list
	 * @return
	 */
	public static ArrayList<HashMap<String, Object>> getArraysToMap(
			List<Object[]> list) {
		String[] titles = (String[]) list.get(0);
		ArrayList<HashMap<String, Object>> hashlist = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hashmap = null;
		tt: for (int j = 1; j < list.size(); j++) {
			Object[] value = list.get(j);
			hashmap = new HashMap<String, Object>();
			for (int i = 0; i < titles.length; i++) {
				String title = titles[i];
				if (title == null || title.length() == 0) {
					hashlist.add(hashmap);
					continue tt;
				}
				hashmap.put(titles[i].toLowerCase(), value[i]);
			}
			hashlist.add(hashmap);
		}
		return hashlist;
	}

	/**
	 * 数据初始化 字节转化为List
	 * 
	 * @param bytefile
	 * @param sheetnum
	 * @return
	 */
	public static ArrayList<HashMap<String, Object>> getMapListFromFile(
			byte[] bytefile, int sheetnum) {
		ArrayList<HashMap<String, Object>> result = null;
		try {
			// 把二进制字节流转化成File对象
			File tempFile = ExcelUtils.byteTranSterFile(bytefile);
			result = ExcelUtils.getMapListExcel(tempFile, sheetnum);
			tempFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 字节数组转换为临时文件
	 * 
	 * @param bytefile
	 * @return
	 * @throws IOException
	 */
	public static File byteTranSterFile(byte[] bytefile) throws IOException {
		File tempFile = File.createTempFile("excel", ".xls");
		FileOutputStream ots = new FileOutputStream(tempFile);
		ots.write(bytefile);
		ots.flush();
		ots.close();
		return tempFile;
	}

	/**
	 * 递归删除文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists() && file.isDirectory()) {
			String[] childnames = file.list();
			if (childnames == null || childnames.length <= 0) {
				file.delete();
			} else {
				File childfile = null;
				for (String childname : childnames) {
					childfile = new File(file.getPath() + "/" + childname);
					deleteFile(childfile);
				}
				file.delete();
			}
		} else if (file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/**
	 * 字节数组写入到文件
	 * 
	 * @param bytefile
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File uploadFile(byte[] bytefile, String path)
			throws IOException {
		File tempfile = new File(path);
		FileOutputStream ots = new FileOutputStream(tempfile);
		ots.write(bytefile);
		ots.flush();
		ots.close();
		return tempfile;
	}

	/**
	 * 获取excel中的所有sheet名称
	 * 
	 * @param bytefile
	 * @return
	 */
	public static ArrayList<String> getSheetNames(byte[] bytefile) {
		ArrayList<String> sheetNames = null;
		try {
			File tempFile = ExcelUtils.byteTranSterFile(bytefile);
			sheetNames = (ArrayList<String>) ExcelUtils.getSheetNames(tempFile);
			tempFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sheetNames;
	}

	/**
	 * 创建excel表
	 * 
	 * @param colDescs
	 * @param colNames
	 * @param datas
	 * @return
	 * @throws IOException
	 */
	public static HSSFWorkbook createExcel(String[] colDescs,
			String[] colNames, List<HashMap<String, Object>> datas)
			throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(0, "Sheet");//
		HSSFRow row = sheet.createRow(0);
		// 隐藏列
		sheet.setColumnHidden(0, true);
		sheet.setColumnHidden(1, true);
		sheet.setColumnHidden(2, true);
		sheet.setColumnHidden(3, true);
		// 产生列头
		HSSFFont font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 头样式
		HSSFCellStyle CellStyle_H = workbook.createCellStyle();
		CellStyle_H.setFont(font);
		CellStyle_H
				.setFillForegroundColor(org.apache.poi.hssf.util.HSSFColor.LIGHT_GREEN.index);
		CellStyle_H.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
		CellStyle_H
				.setFillBackgroundColor(org.apache.poi.hssf.util.HSSFColor.LIGHT_GREEN.index);
		CellStyle_H.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		CellStyle_H.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		CellStyle_H.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		CellStyle_H
				.setBottomBorderColor(org.apache.poi.hssf.util.HSSFColor.BLACK.index);
		CellStyle_H.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		CellStyle_H
				.setLeftBorderColor(org.apache.poi.hssf.util.HSSFColor.BLACK.index);
		CellStyle_H.setBorderTop(HSSFCellStyle.BORDER_THIN);
		CellStyle_H
				.setTopBorderColor(org.apache.poi.hssf.util.HSSFColor.BLACK.index);
		CellStyle_H.setBorderRight(HSSFCellStyle.BORDER_THIN);
		CellStyle_H
				.setRightBorderColor(org.apache.poi.hssf.util.HSSFColor.BLACK.index);
		// 居左样式
		HSSFCellStyle CellStyle_L = workbook.createCellStyle();
		CellStyle_L.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		CellStyle_L.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		CellStyle_L.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		CellStyle_L.setBottomBorderColor(HSSFColor.BLACK.index);
		CellStyle_L.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		CellStyle_L.setLeftBorderColor(HSSFColor.BLACK.index);
		CellStyle_L.setBorderTop(HSSFCellStyle.BORDER_THIN);
		CellStyle_L.setTopBorderColor(HSSFColor.BLACK.index);
		CellStyle_L.setBorderRight(HSSFCellStyle.BORDER_THIN);
		CellStyle_L.setRightBorderColor(HSSFColor.BLACK.index);
		int count = 0;
		HSSFPatriarch p = sheet.createDrawingPatriarch();
		HSSFClientAnchor clientAnchor = new HSSFClientAnchor();
		HSSFComment comment = null;
		for (int i = 0; i < colDescs.length; i++) {
			row.createCell(count).setCellValue(colDescs[count]);
			comment = p.createComment(clientAnchor);
			comment.setString(new HSSFRichTextString(colNames[count]
					.toUpperCase()));
			comment.setAuthor("swell");
			row.getCell(count).setCellStyle(CellStyle_H);
			row.getCell(count).setCellComment(comment);
			count++;
		}
		int rownum = 1;
		for (int i = 0; i < datas.size(); i++) {
			Map<String, Object> data = datas.get(i);
			row = sheet.createRow(rownum);
			count = 0;
			for (int j = 0; j < colNames.length; j++) {
				row.createCell(count)
						.setCellValue(
								String.valueOf(data.get(colNames[count]
										.toLowerCase()) == null ? "" : data
										.get(colNames[count].toLowerCase())));
				row.getCell(count).setCellStyle(CellStyle_L);
				count++;
			}
			rownum++;
		}
		for (int i = 0; i < colNames.length; i++) {
			sheet.autoSizeColumn(i);
		}
		// 获取当前列的宽度，然后对比本列的长度，取最大值
		for (int columnNum = 0; columnNum <= colNames.length; columnNum++) {
			int columnWidth = sheet.getColumnWidth(columnNum) / 256;
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row currentRow;
				// 当前行未被使用过
				if (sheet.getRow(rowNum) == null) {
					currentRow = sheet.createRow(rowNum);
				} else {
					currentRow = sheet.getRow(rowNum);
				}

				if (currentRow.getCell(columnNum) != null) {
					Cell currentCell = currentRow.getCell(columnNum);
					int length = currentCell.toString().getBytes().length;
					if (columnWidth < length) {
						columnWidth = length;
					}
				}
			}
			sheet.setColumnWidth(columnNum, columnWidth * 256);
		}
		return workbook;

	}

	/**
	 * 物资字典读取文件
	 * 
	 * @param bytefile
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<HashMap<String, Object>> getExcelList(
			byte[] bytefile) throws IOException {
		File file = byteTranSterFile(bytefile);
		List<Object[]> list = getListExcel(file, 1, true);
		return getArraysToMap(list);
	}

	/**
	 * 导出excel表
	 * 
	 * @param response
	 * @param fileName
	 * @param colDescs
	 * @param colNames
	 * @param datas
	 * @throws IOException
	 */
	public static void export_Excel(HttpServletResponse response,
			String fileName, String[] colDescs, String[] colNames,
			List<HashMap<String, Object>> datas) throws IOException {
		HSSFWorkbook workbook = createExcel(colDescs, colNames, datas);
		response.setContentType("aplication/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ new String((fileName).getBytes("GB2312"), "ISO8859_1")
				+ ".xls");
		response.setContentType("application/x-download");
		ServletOutputStream servletOutputStream = response.getOutputStream();
		workbook.write(servletOutputStream);
		servletOutputStream.flush();
		servletOutputStream.close();
	}

	public static void download(HttpServletRequest request,
			HttpServletResponse response, String fileName, String path)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		String downloadPath =  path;
		long fileLength = new File(downloadPath).length();
		response.setHeader("Content-disposition", "attachment; filename="
				+ new String(fileName.getBytes("GB2312"), "ISO8859-1"));
		response.setHeader("Content-Length", String.valueOf(fileLength));
		response.setContentType("application/x-download");
		bis = new BufferedInputStream(new FileInputStream(downloadPath));
		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
	}

	/**
	 * 导出zip文件
	 * 
	 * @param response
	 * @param fileName
	 * @param colDescs
	 * @param colNames
	 * @param hashmap
	 * @throws IOException
	 */
	public static void export_zip(HttpServletResponse response,
			String fileName, String[] colDescs, String[] colNames,
			Map<String, List<HashMap<String, Object>>> hashmap)
			throws IOException {
		response.setContentType("application/octet-stream ");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ new String((fileName).getBytes("GB2312"), "ISO8859_1")
				+ ".zip");
		ServletOutputStream servletOutputStream = response.getOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(
				servletOutputStream);
		zipOutputStream.setEncoding("GB2312");
		HSSFWorkbook workbook = null;
		Set<String> zipEntryNames = hashmap.keySet();
		Iterator<String> iterator = zipEntryNames.iterator();
		String zipEntryName = null;
		List<HashMap<String, Object>> datas = null;
		while (iterator.hasNext()) {
			zipEntryName = iterator.next();
			datas = hashmap.get(zipEntryName);
			workbook = createExcel(colDescs, colNames, datas);
			zipOutputStream.putNextEntry(new ZipEntry(zipEntryName + ".xls"));
			workbook.write(zipOutputStream);
		}
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	/**
	 * 以excel表首行批注为title
	 * 
	 * @param file
	 * @param sheetnum
	 * @param byComment
	 * @return
	 */
	public static List<Object[]> getListExcel(File file, int sheetnum,
			boolean byComment) {

		// List<Object[]> 中的元素 行数组Object[]为excel中的每一行
		InputStream is = null;
		List<Object[]> list = new ArrayList<Object[]>();
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			HSSFWorkbook hwk = new HSSFWorkbook(is);// 将is流实例到 一个excel流里
			HSSFSheet sheet = hwk.getSheetAt(sheetnum - 1);// 得到book的一个工作薄sheet
			Iterator<Row> rit = sheet.rowIterator();
			Row row = rit.next();
			if (row == null || row.getLastCellNum() <= 0) {
				return null;
			}
			int length = row.getLastCellNum();
			String[] titleshasEmpty = new String[row.getLastCellNum()];
			String[] titles = new String[row.getLastCellNum()];
			int count = 0;
			if (byComment) {
				Comment comment = null;
				for (int i = 0; i < row.getLastCellNum(); i++) {
					comment = row.getCell(i).getCellComment();
					if (comment == null || comment.getString() == null
							|| comment.getString().length() == 0
							|| comment.getString().getString() == null
							|| comment.getString().getString().length() == 0) {
						titleshasEmpty[i] = null;
					} else {
						titleshasEmpty[i] = comment.getString().getString()
								.toLowerCase();
						titles[count++] = titleshasEmpty[i];
					}
				}
			} else {
				Cell cell = null;
				for (int i = 0; i < row.getLastCellNum(); i++) {
					cell = row.getCell(i);
					if (cell == null || cell.getStringCellValue() == null
							|| cell.getStringCellValue().length() == 0) {
						titleshasEmpty[i] = null;
					} else {
						titleshasEmpty[i] = cell.getStringCellValue().trim()
								.toLowerCase();
						titles[count++] = titleshasEmpty[i];
					}
				}
			}
			list.add(titles);
			while (rit.hasNext()) {
				row = rit.next();
				if (row == null || row.getLastCellNum() <= 0) {
					continue;
				}
				Object[] strArray = new Object[length];
				count = 0;
				for (short j = 0; j < length; j++) {
					Cell cell = row.getCell(j);
					String title = titleshasEmpty[j];
					if (title == null || title.length() == 0) {
						continue;
					}
					if (cell == null) {
						strArray[count++] = null;
						continue;
					}
					Object cellValue = null;
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							cellValue = date;
						} else {
							cellValue = PHONENUMBER_DF.format(cell
									.getNumericCellValue());
						}
						break;
					case HSSFCell.CELL_TYPE_STRING:
						cellValue = cell.getRichStringCellValue().toString();
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						cellValue = PHONENUMBER_DF.format(cell
								.getNumericCellValue());
						break;
					default:
						cellValue = null;
						break;
					}
					strArray[count++] = cellValue;
				}
				list.add(strArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static List<HashMap<String, String>> getListExcelMapString(
			File file, int sheetnum, boolean byComment) {

		// List<Object[]> 中的元素 行数组Object[]为excel中的每一行
		InputStream is = null;
		HashMap<String, String> map = new HashMap<String, String>();
		String[] titles = null;
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			HSSFWorkbook hwk = new HSSFWorkbook(is);// 将is流实例到 一个excel流里
			HSSFSheet sheet = hwk.getSheetAt(sheetnum - 1);// 得到book的一个工作薄sheet
			Iterator<Row> rit = sheet.rowIterator();
			Row row = rit.next();
			if (row == null || row.getLastCellNum() <= 0) {
				return null;
			}
			titles = new String[row.getLastCellNum()];
			if (byComment) {
				Comment comment = null;
				for (int i = 0; i < row.getLastCellNum(); i++) {
					comment = row.getCell(i).getCellComment();
					if (comment == null || comment.getString() == null
							|| comment.getString().getString() == null) {
						titles[i] = null;
					} else {
						titles[i] = comment.getString().getString()
								.toLowerCase();
					}
				}
			} else {
				Cell cell = null;
				for (int i = 0; i < row.getLastCellNum(); i++) {
					cell = row.getCell(i);
					if (cell == null || cell.getStringCellValue() == null) {
						titles[i] = null;
					} else {
						titles[i] = cell.getStringCellValue().trim()
								.toLowerCase();
					}
				}
			}
			while (rit.hasNext()) {
				row = rit.next();
				if (row == null || row.getLastCellNum() <= 0) {
					continue;
				}
				map = new HashMap<String, String>();
				for (int j = 0; j < titles.length; j++) {
					String title = titles[j];
					if (title == null || title.length() == 0) {
						continue;
					}
					Cell cell = row.getCell(j);
					if (cell == null) {
						map.put(titles[j], "");
						continue;
					}
					String cellValue = null;
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							cellValue = (date == null) ? "" : DateUtils
									.DateToStr(date, "yyyy-MM-dd");
						} else {
							cellValue = PHONENUMBER_DF.format(cell
									.getNumericCellValue());
						}
						break;
					case HSSFCell.CELL_TYPE_STRING:
						cellValue = cell.getRichStringCellValue().toString();
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						cellValue = PHONENUMBER_DF.format(cell
								.getNumericCellValue());
						break;
					default:
						cellValue = "";
						break;
					}
					map.put(titles[j], cellValue);
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	public static ArrayList<String> readExcel(File file){
		ArrayList<String> result = new ArrayList<String>();
		try {
			InputStream is = new FileInputStream(file);
			HSSFWorkbook hwk = new HSSFWorkbook(is);// 将is流实例到 一个excel流里
			HSSFSheet sheet = hwk.getSheetAt(0);// 得到book的一个工作薄sheet
			Iterator<Row> rit = sheet.rowIterator();
			while (rit.hasNext()) {
				Row row = rit.next();
				if (row == null || row.getLastCellNum() <= 0) {
					break;
				}
				Cell cell1 = row.getCell(0);
				String celvalue = cell1.getStringCellValue();
				if ("代码".equals(celvalue)) {
					continue;
				}else if ("数据来源:通达信".equals(celvalue)) {
					break;
				}
				result.add(celvalue);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	public static void main(String[] args) {
//		File file = new File("E:/workspace3/3.4更新/stock_plate");
//		System.out.println(FileUtils.get_stock_plate(file,""));
		
	}
}