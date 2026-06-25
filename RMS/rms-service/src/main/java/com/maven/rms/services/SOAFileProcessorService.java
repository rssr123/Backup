package com.maven.rms.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class SOAFileProcessorService {

    @Value("${soa.processor.inputfolder}")
    private String INPUT_FOLDER;

    @Value("${soa.processor.inprogressfolder}")
    private String IN_PROGRESS_FOLDER;

    @Value("${soa.processor.processedfolder}")
    private String PROCESSED_FOLDER;

    @Value("${soa.processor.failedfolder}")
    private String FAILED_FOLDER;

    //#region 
    // public static void main(String[] args) {
    //     try {
    //         SOAFileProcessorService processor = new SOAFileProcessorService();
    //         processor.processFiles();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void processFiles() {
    //     File folder = new File(INPUT_FOLDER);
    //     File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx"));

    //     if (files != null) {
    //         for (File file : files) {
    //             try {
    //                 System.out.println("Processing file: " + file.getName());
    //                 processFile(file);
    //                 moveFile(file, PROCESSED_FOLDER);
    //                 System.out.println("File processed successfully: " + file.getName());
    //             } catch (Exception e) {
    //                 e.printStackTrace();
    //                 moveFile(file, FAILED_FOLDER);
    //                 System.err.println("Failed to process file: " + file.getName());
    //             }
    //         }
    //     } else {
    //         System.out.println("No files found in input folder.");
    //     }
    // }
    // #endregion

    public void processFiles() {
        File folder = new File(INPUT_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx"));

        if (files != null) {
            for (File file : files) {
                try {
                    if (!(file.getName().toLowerCase().endsWith(".xlsx")
                            || file.getName().toLowerCase().endsWith(".xls"))) {
                        log.error("Non-Excel file detected, moving to Failed folder: " + file.getName());
                        moveFile(file, FAILED_FOLDER);
                        continue; // Skip to next file
                    }
                    File inProgressFile = new File(IN_PROGRESS_FOLDER + "/" + file.getName());

                    // ✅ Move file to In Progress folder
                    Files.move(file.toPath(), inProgressFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    processFile(inProgressFile);

                    // ✅ Move file to Processed folder after success
                    moveFile(inProgressFile, PROCESSED_FOLDER);
                } catch (Exception e) {
                    e.printStackTrace();
                    // ✅ Move to Failed folder if processing fails
                    log.error("Failed to process file: " + file.getName() + e.toString());
                    moveFile(new File(IN_PROGRESS_FOLDER + "/" + file.getName()), FAILED_FOLDER);
                }
            }
        } else {
            System.out.println("No files found in input folder.");
        }
    }

    private void processFile(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
    
        // ✅ Create output workbook
        Workbook outputWorkbook = new XSSFWorkbook();
    
        // =============================
        // ✅ Step 1: Extract Account Summary
        // =============================
        Sheet summarySheet = outputWorkbook.createSheet("Account Summary");
        int summaryRowNum = 0;
    
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Bill To", getCellValue(sheet, "B11"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Address", getCellValue(sheet, "B14"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Merchant ID", getCellValue(sheet, "K4"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Statement No", getCellValue(sheet, "K5"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Statement Date", getCellValue(sheet, "K6"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Balance Brought Forward", getCellValue(sheet, "L9"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Total Transactions", getCellValue(sheet, "L10"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Total Chargeback / Refund", getCellValue(sheet, "L11"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Total Transaction Adjustments", getCellValue(sheet, "L12"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Others", getCellValue(sheet, "L13"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Less: Paid by GHL", getCellValue(sheet, "L14"));
        summaryRowNum = writeRow(summarySheet, summaryRowNum, "Balance Carried Forward", getCellValue(sheet, "L15"));
    
        // =============================
        // ✅ Step 2: Extract Details Section
        // =============================
        Sheet detailsSheet = outputWorkbook.createSheet("Details");
        int detailsRowNum = 0;
    
        // Write headers for Details sheet
        Row headerRow = detailsSheet.createRow(detailsRowNum++);
        headerRow.createCell(0).setCellValue("No.");
        headerRow.createCell(1).setCellValue("Txn Date");
        headerRow.createCell(2).setCellValue("Txn ID");
        headerRow.createCell(3).setCellValue("Txn Type");
        headerRow.createCell(4).setCellValue("Txn Code");
        headerRow.createCell(5).setCellValue("Txn Amount");
        headerRow.createCell(6).setCellValue("MDR");
        headerRow.createCell(7).setCellValue("SST");
        headerRow.createCell(8).setCellValue("Net Amount");
    
        boolean isReadingDetails = true;
        boolean isReadingSummary = false;
        int endIndexOfDetails = -1;
    
        for (int rowIndex = 20; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
    
            // String no = getCellValue(row.getCell(0));
            String no = getIntegerValue(row.getCell(0));
            String txnDate = getCellValue(row.getCell(1));
            String txnID = getCellValue(row.getCell(2));
            String txnType = getCellValue(row.getCell(4));
            String txnCode = getCellValue(row.getCell(6));
            String txnAmount = getCellValue(row.getCell(8));
            String mdr = getCellValue(row.getCell(9));
            String sst = getCellValue(row.getCell(10));
            // String mdr = getMdrValue(row.getCell(9));
            // String sst = getSstValue(row.getCell(10));
            String netAmount = getCellValue(row.getCell(11));
    
            // ✅ Stop details extraction at TOTAL ROW but write the TOTAL row
            if ("Total".equalsIgnoreCase(txnCode) && isReadingDetails) {
                Row detailRow = detailsSheet.createRow(detailsRowNum++);
                detailRow.createCell(0).setCellValue("Total");
                detailRow.createCell(1).setCellValue("");
                detailRow.createCell(2).setCellValue("");
                detailRow.createCell(3).setCellValue("");
                detailRow.createCell(4).setCellValue("");
                detailRow.createCell(5).setCellValue(txnAmount);
                detailRow.createCell(6).setCellValue(mdr);
                detailRow.createCell(7).setCellValue(sst);
                detailRow.createCell(8).setCellValue(netAmount);
    
                isReadingDetails = false;
                endIndexOfDetails = rowIndex;
                isReadingSummary = true;
                break; // ✅ Stop after first table's total row
            }
    
            // ✅ Write to "Details" sheet
            if (isReadingDetails && !no.equals("")) {
                Row detailRow = detailsSheet.createRow(detailsRowNum++);
                detailRow.createCell(0).setCellValue(no);
                detailRow.createCell(1).setCellValue(txnDate);
                detailRow.createCell(2).setCellValue(txnID);
                detailRow.createCell(3).setCellValue(txnType);
                detailRow.createCell(4).setCellValue(txnCode);
                detailRow.createCell(5).setCellValue(txnAmount);
                detailRow.createCell(6).setCellValue(mdr);
                detailRow.createCell(7).setCellValue(sst);
                detailRow.createCell(8).setCellValue(netAmount);
            }
        }
    
        // =============================
        // ✅ Step 3: Extract Summarized Section
        // =============================
        if (endIndexOfDetails != -1) {
            Sheet summarizedSheet = outputWorkbook.createSheet("Sum- Daily Transaction");
            int summarizedRowNum = 0;
    
            // Write headers for Summarized sheet
            Row summarizedHeader = summarizedSheet.createRow(summarizedRowNum++);
            summarizedHeader.createCell(0).setCellValue("No.");
            summarizedHeader.createCell(1).setCellValue("Txn Type");
            summarizedHeader.createCell(2).setCellValue("Txn Code");
            summarizedHeader.createCell(3).setCellValue("Txn Amount");
            summarizedHeader.createCell(4).setCellValue("MDR");
            summarizedHeader.createCell(5).setCellValue("SST");
            summarizedHeader.createCell(6).setCellValue("Net Amount");
    
            for (int rowIndex = endIndexOfDetails + 6; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
    
                // String no = getCellValue(row.getCell(0));
                String no = getIntegerValue(row.getCell(0));
                String txnType = getCellValue(row.getCell(4));
                String txnCode = getCellValue(row.getCell(6));
                String txnAmount = getCellValue(row.getCell(8));
                String mdr = getCellValue(row.getCell(9));
                String sst = getCellValue(row.getCell(10));
                // String mdr = getMdrValue(row.getCell(9));
                // String sst = getSstValue(row.getCell(10));
                String netAmount = getCellValue(row.getCell(11));
    
                // ✅ Stop at second "Total" row
                if ("Total".equalsIgnoreCase(txnCode)) {
                    Row summarizedRow = summarizedSheet.createRow(summarizedRowNum++);
                    summarizedRow.createCell(0).setCellValue("Total");
                    summarizedRow.createCell(1).setCellValue("");
                    summarizedRow.createCell(2).setCellValue("");
                    summarizedRow.createCell(3).setCellValue(txnAmount);
                    summarizedRow.createCell(4).setCellValue(mdr);
                    summarizedRow.createCell(5).setCellValue(sst);
                    summarizedRow.createCell(6).setCellValue(netAmount);
                    isReadingSummary = false;
                    break;
                }

                if (isReadingSummary && !no.equals("")) {
                    Row detailRow = summarizedSheet.createRow(summarizedRowNum++);
                    detailRow.createCell(0).setCellValue(no);
                    // detailRow.createCell(1).setCellValue(txnDate);
                    // detailRow.createCell(2).setCellValue(txnID);
                    detailRow.createCell(1).setCellValue(txnType);
                    detailRow.createCell(2).setCellValue(txnCode);
                    detailRow.createCell(3).setCellValue(txnAmount);
                    detailRow.createCell(4).setCellValue(mdr);
                    detailRow.createCell(5).setCellValue(sst);
                    detailRow.createCell(6).setCellValue(netAmount);
                }
            }
        }
    
        // ✅ Write to file
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String outputPath = PROCESSED_FOLDER + "/Processed_"  + timestamp + "_" + file.getName() ;
        FileOutputStream fileOut = new FileOutputStream(outputPath);
        outputWorkbook.write(fileOut);
        fileOut.close();
    
        workbook.close();
        outputWorkbook.close();
    }
    

    private String getCellValue(Sheet sheet, String cellReference) {
        try {
            CellReference ref = new CellReference(cellReference);
            Row row = sheet.getRow(ref.getRow());
            if (row != null) {
                Cell cell = row.getCell(ref.getCol());
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            return String.valueOf(cell.getNumericCellValue());
                        case BOOLEAN:
                            return String.valueOf(cell.getBooleanCellValue());
                        default:
                            return "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private void moveFile(File file, String targetFolder) {
        try {
            Path sourcePath = file.toPath();
            Path targetPath = Paths.get(targetFolder, file.getName());
            Files.move(sourcePath, targetPath);
            System.out.println("Moved file to " + targetFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int writeRow(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value);
        return rowNum;
    }

    // ✅ Remove decimal point from No. column
    private String getIntegerValue(Cell cell) {
        if (cell == null)
            return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return getCellValue(cell);
    }
}
