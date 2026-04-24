package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    // === READ all rows (skipping header) and return as Object[][] for DataProvider ===
    public static Object[][] readExcel(String filePath, String sheetName) {
        List<Object[]> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            DataFormatter formatter = new DataFormatter();

            int lastRow = sheet.getLastRowNum();

            // Skip header at row 0 — start from row 1
            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Read only the first 4 input columns
                String loanAmount   = formatter.formatCellValue(row.getCell(0));
                String interestRate = formatter.formatCellValue(row.getCell(1));
                String tenure       = formatter.formatCellValue(row.getCell(2));
                String testCaseName = formatter.formatCellValue(row.getCell(3));

                // Skip fully blank rows
                if (loanAmount.isEmpty()) continue;

                dataList.add(new Object[]{loanAmount, interestRate, tenure, testCaseName, i});
                // last element 'i' is the row number — needed when writing results back
            }

        } catch (IOException e) {
            System.out.println("ERROR: Could not read Excel file.");
            e.printStackTrace();
        }

        return dataList.toArray(new Object[0][]);
    }

    // === WRITE results back with color-coded Status ===
    public static void writeResultToExcel(String filePath, String sheetName,
                                          int rowNum, int expectedEMI, int actualEMI,
                                          String status) {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);

            // Create cells if missing
            Cell expectedCell = getOrCreateCell(row, 4);
            Cell actualCell   = getOrCreateCell(row, 5);
            Cell statusCell   = getOrCreateCell(row, 6);
            Cell timeCell     = getOrCreateCell(row, 7);

            // Write values
            expectedCell.setCellValue(expectedEMI);
            actualCell.setCellValue(actualEMI);
            statusCell.setCellValue(status);
            timeCell.setCellValue(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            // Apply green/red background to Status cell
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);

            if (status.equalsIgnoreCase("PASS")) {
                style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            } else {
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
            }
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            statusCell.setCellStyle(style);

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Result written to Excel row " + (rowNum + 1) + " — " + status);

        } catch (IOException e) {
            System.out.println("ERROR: Could not write to Excel. Is the file open in Excel?");
            e.printStackTrace();
        }
    }

    // Helper to safely get or create a cell
    private static Cell getOrCreateCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }
}
