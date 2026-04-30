package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
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

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Reading the data from Excel file
                String loanAmount   = formatter.formatCellValue(row.getCell(0));
                String interestRate = formatter.formatCellValue(row.getCell(1));
                String tenure       = formatter.formatCellValue(row.getCell(2));
                String testCaseName = formatter.formatCellValue(row.getCell(3));

                // Skip fully blank rows
                if (loanAmount.isEmpty()) continue;

                dataList.add(new Object[]{loanAmount, interestRate, tenure, testCaseName, i});
            }

        } catch (IOException e) {
            System.out.println("ERROR: Could not read Excel file.");
            e.printStackTrace();
        }

        return dataList.toArray(new Object[0][]);
    }

    // WRITE results back with color-coded Status
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

            // Write values
            expectedCell.setCellValue(expectedEMI);
            actualCell.setCellValue(actualEMI);
            statusCell.setCellValue(status);

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

        } catch (IOException e) {
            System.out.println("ERROR: Could not write to Excel.");
            e.printStackTrace();
        }
    }

    // === READ Home Loan rows (10 input columns + testCaseName + rowNum) ===
    public static Object[][] readHomeLoanExcel(String filePath, String sheetName) {
        List<Object[]> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            DataFormatter formatter = new DataFormatter();

            int lastRow = sheet.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String homeValue        = formatter.formatCellValue(row.getCell(0));
                String downPayment      = formatter.formatCellValue(row.getCell(1));
                String loanInsurance    = formatter.formatCellValue(row.getCell(2));
                String interestRate     = formatter.formatCellValue(row.getCell(3));
                String tenure           = formatter.formatCellValue(row.getCell(4));
                String loanFees         = formatter.formatCellValue(row.getCell(5));
                String oneTimeExpenses  = formatter.formatCellValue(row.getCell(6));
                String propertyTaxes    = formatter.formatCellValue(row.getCell(7));
                String homeInsurance    = formatter.formatCellValue(row.getCell(8));
                String maintenance      = formatter.formatCellValue(row.getCell(9));
                String testCaseName     = formatter.formatCellValue(row.getCell(10));

                if (homeValue.isEmpty()) continue;

                dataList.add(new Object[]{
                        homeValue, downPayment, loanInsurance, interestRate, tenure,
                        loanFees, oneTimeExpenses, propertyTaxes, homeInsurance,
                        maintenance, testCaseName, i
                });
            }

        } catch (IOException e) {
            System.out.println("ERROR: Could not read Home Loan sheet.");
            e.printStackTrace();
        }

        return dataList.toArray(new Object[0][]);
    }

    // === WRITE Home Loan results (6 result columns + status + time) ===
    public static void writeHomeLoanResult(String filePath, String sheetName, int rowNum,
                                           int expectedLoanAmount,
                                           int expectedEMI, long actualEMI,
                                           long expectedTotalPayment, long actualTotalPayment,
                                           String status) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);

            // Columns 11–18: Expected/Actual Loan Amount, EMI, Total Payment, Status, Time
            getOrCreateCell(row, 11).setCellValue(expectedEMI);
            getOrCreateCell(row, 12).setCellValue(actualEMI);
            getOrCreateCell(row, 13).setCellValue(expectedTotalPayment);
            getOrCreateCell(row, 14).setCellValue(actualTotalPayment);

            Cell statusCell = getOrCreateCell(row, 15);
            statusCell.setCellValue(status);

            // Apply green/red color to Status cell
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

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Result written to row " + (rowNum + 1) + " — " + status);

        } catch (IOException e) {
            System.out.println("ERROR: Could not write Home Loan result. Is Excel file open?");
            e.printStackTrace();
        }
    }

    public static void exportYearlyDataToExcel(List<String[]> data, String outputFilePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Yearly Schedule");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Loop through each row in the list
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                String[] rowData = data.get(rowIndex);

                for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
                    Cell cell = row.createCell(colIndex);
                    cell.setCellValue(rowData[colIndex]);

                    // Apply bold header style only for first row
                    if (rowIndex == 0) {
                        cell.setCellStyle(headerStyle);
                    }
                }
            }

            // Auto-fit each column to its content
            if (!data.isEmpty()) {
                for (int i = 0; i < data.get(0).length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            // Make sure the output folder exists
            File file = new File(outputFilePath);
            file.getParentFile().mkdirs();

            // Save the file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            System.out.println("Yearly data exported to: " + outputFilePath);
            System.out.println("Total rows written: " + data.size());

        } catch (IOException e) {
            System.out.println("ERROR: Could not export yearly data to Excel.");
            e.printStackTrace();
        }
    }

    private static Cell getOrCreateCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }
}
