
package Running;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSheet {
	XSSFWorkbook wb;
	XSSFSheet sheet;
	File f;
	FileOutputStream fos;
	XSSFRow rowhead;

	public ExcelSheet(String path) {
		try {
			f = new File(path);
			f.createNewFile();
			wb = new XSSFWorkbook();
			wb.createSheet("Result");
			sheet = wb.getSheet("Result");
			rowhead = sheet.createRow((short) 0);
			rowhead.createCell(0).setCellValue("File Name");
			rowhead.createCell(1).setCellValue("Status");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void removeCellvalue(String index, int row, int cell) {
		sheet = wb.getSheet(index);
		Row r = sheet.getRow(row);
		Cell cell1 = r.getCell(cell);
		if (cell1 != null) {
			r.removeCell(cell1);
		}
	}

	public void writedata(int row, String obsfileNameString, String status) {
		try {
			CellStyle style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBold(true);

			if (status.equalsIgnoreCase("pass")) {
				font.setColor(IndexedColors.WHITE.getIndex());
			   
				style.setFont(font);
				style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			} else if (status.equalsIgnoreCase("fail")) {
				
				style.setFont(font);
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			}
			//style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			XSSFRow record = sheet.createRow(row);
			Cell cell1 = record.createCell(0);
			cell1.setCellValue(obsfileNameString);
			Cell cell2 = record.createCell(1);
			cell2.setCellValue(status);
			cell2.setCellStyle(style);
			FileOutputStream dest = new FileOutputStream(f);
			wb.write(dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}