package org.molgenis.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class chd7tool
{

	public static void readXLSFile() throws IOException
	{
		InputStream ExcelFileToRead = new FileInputStream("C:/Test.xls");
		HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);

		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row;
		HSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext())
		{
			row = (HSSFRow) rows.next();
			Iterator cells = row.cellIterator();

			while (cells.hasNext())
			{
				cell = (HSSFCell) cells.next();

				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
				{
					System.out.print(cell.getStringCellValue() + " ");
				}
				else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
				{
					System.out.print(cell.getNumericCellValue() + " ");
				}
				else
				{
					//U Can Handel Boolean, Formula, Errors
				}
			}
			System.out.println();
		}

	}

	public static void writeXLSFile() throws IOException
	{

		String excelFileName = "C:/Test.xls";//name of excel file

		String sheetName = "Sheet1";//name of sheet

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);

		//iterating r number of rows
		for (int r = 0; r < 5; r++)
		{
			HSSFRow row = sheet.createRow(r);

			//iterating c number of columns
			for (int c = 0; c < 5; c++)
			{
				HSSFCell cell = row.createCell(c);

				cell.setCellValue("Cell " + r + " " + c);
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		//write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	public static void readXLSXFile() throws IOException
	{
		InputStream ExcelFileToRead = new FileInputStream(
				"C:\\Users\\Bart\\Documents\\GitHub\\molgenis-EMX-downloader\\chd7.xlsx");
		XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

		XSSFWorkbook test = new XSSFWorkbook();

		XSSFSheet sheet = wb.getSheetAt(4);
		XSSFRow row;
		XSSFCell cell;

		Iterator rows = sheet.rowIterator();

		Map<String, List<String>> patMutMap = new HashMap<>();
		while (rows.hasNext())
		{
			row = (XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			int i = 0;
			String pat = "unknown";
			while (i < row.getPhysicalNumberOfCells())
			{
				cell = (XSSFCell) row.getCell(i);

				if (i == 0)
				{
					pat = cell.getStringCellValue();
				}
				if (i == 3)
				{
					List mutations = Arrays.asList(cell.getStringCellValue().split(","));
					patMutMap.put(pat, mutations);

				}
				i++;
			}
		}
		System.out.println(patMutMap);
		SortedMap<String, String> mutPatMap = new TreeMap<>();
		for (String key : patMutMap.keySet())
		{
			List<String> mutations = patMutMap.get(key);
			for (String mutation : mutations)
			{
				if (mutPatMap.containsKey(mutation))
				{
					mutPatMap.put(mutation, mutPatMap.get(mutation) + "," + key);
				}
				else
				{
					mutPatMap.put(mutation, key);
				}
			}
		}
		System.out.println("---BEGIN RESULT---");
		for (String key : mutPatMap.keySet())
		{
			System.out.println(key + "\t" + mutPatMap.get(key));
		}
		System.out.println("---END RESULT---");
	}

	public static void writeXLSXFile() throws IOException
	{

		String excelFileName = "C:/Test.xlsx";//name of excel file

		String sheetName = "Sheet1";//name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);

		//iterating r number of rows
		for (int r = 0; r < 5; r++)
		{
			XSSFRow row = sheet.createRow(r);

			//iterating c number of columns
			for (int c = 0; c < 5; c++)
			{
				XSSFCell cell = row.createCell(c);

				cell.setCellValue("Cell " + r + " " + c);
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		//write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}

	public static void main(String[] args) throws IOException
	{
		readXLSXFile();

	}

}
