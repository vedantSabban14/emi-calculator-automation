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
                                           int expectedLoanAmount, int actualLoanAmount,
                                           int expectedEMI, int actualEMI,
                                           int expectedTotalPayment, int actualTotalPayment,
                                           String status) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);

            // Columns 11–18: Expected/Actual Loan Amount, EMI, Total Payment, Status, Time
            getOrCreateCell(row, 11).setCellValue(expectedLoanAmount);
            getOrCreateCell(row, 12).setCellValue(actualLoanAmount);
            getOrCreateCell(row, 13).setCellValue(expectedEMI);
            getOrCreateCell(row, 14).setCellValue(actualEMI);
            getOrCreateCell(row, 15).setCellValue(expectedTotalPayment);
            getOrCreateCell(row, 16).setCellValue(actualTotalPayment);

            Cell statusCell = getOrCreateCell(row, 17);
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

    private static Cell getOrCreateCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }
}
