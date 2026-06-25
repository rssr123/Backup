package com.maven.rms.services;

import com.maven.rms.models.SlipRequest;
import com.maven.rms.repositories.SlipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.OutputStream;
import java.awt.Color;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class SlipService {

        @Value("${refund.slip.folder.path}")
        private String folderPath;
        private final SlipRepository slipRepository;
        
        /*
        public void testLoadPng() throws Exception{
    		long start = System.currentTimeMillis();
            Image logo = Image.getInstance(getClass().getResource("/jasper/images/logo-ssm.png"));
            logo.scaleToFit(150, 150);

    		long end = System.currentTimeMillis();
    		String endString = "Thread '" + Thread.currentThread().getName() 
    				+ "' took a Total time of: " + Long.toString(end-start)
    				+ " milliseconds to load logo.";
    		System.out.println(endString);
        }
         */
        public byte[] generateSlipPDF(SlipRequest slipRequest) throws Exception {
                ImageIO.setUseCache(false);
                SlipRequest slipData = slipRepository.getSlipData(slipRequest);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                document.open();

                PdfContentByte canvas = writer.getDirectContentUnder();
                Rectangle yellowRect = new Rectangle(0, document.getPageSize().getHeight() / 4,
                                document.getPageSize().getWidth() / 4, document.getPageSize().getHeight());
                yellowRect.setBorder(Rectangle.LEFT);
                yellowRect.setBorderWidth(50);
                yellowRect.setBorderColor(new Color(251, 240, 3));
                canvas.rectangle(yellowRect);

                Rectangle blueRect = new Rectangle(0, 0, document.getPageSize().getWidth() / 4,
                                document.getPageSize().getHeight() * 22 / 26);
                blueRect.setBorder(Rectangle.LEFT);
                blueRect.setBorderWidth(50);
                blueRect.setBorderColor(new Color(45, 43, 132));
                canvas.rectangle(blueRect);

                String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));

                Font titleHeaderFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.BLACK);
                Font lighttitleHeaderFont = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(3, 3, 3));
                Font normalFont = new Font(Font.HELVETICA, 13, Font.NORMAL, Color.BLACK);
                Font SubHeaderFont = new Font(Font.HELVETICA, 17, Font.BOLD, Color.BLACK);
                Font lightHeaderFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(120, 120, 120));
                Font lightSubHeaderFont = new Font(Font.HELVETICA, 14, Font.NORMAL, new Color(120, 120, 120));

                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.setWidths(new float[] { 1, 2 });

                // FIXED: Load logo with proper error handling
                Image logo = null;
                try {
                        InputStream inputStream = getClass().getResourceAsStream("/jasper/images/logo-ssm.png");
                        if (inputStream != null) {
                                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                int bytesRead;
                                byte[] data = new byte[1024];
                                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                                        buffer.write(data, 0, bytesRead);
                                }
                                inputStream.close();
                                logo = Image.getInstance(buffer.toByteArray());
                        }
                } catch (Exception e) {
                        System.err.println("Failed to load logo: " + e.getMessage());
                        logo = null; // Ensure logo is null on failure
                }

                // FIXED: Handle both logo success and failure cases
                if (logo != null) {
                        logo.scaleToFit(150, 150);
                        PdfPCell logoCell = new PdfPCell(logo);
                        logoCell.setBorder(Rectangle.NO_BORDER);
                        headerTable.addCell(logoCell);
                } else {
                        // FIXED: Proper text fallback when logo fails
                        PdfPCell logoCell = new PdfPCell(new Phrase("SSM LOGO", titleHeaderFont));
                        logoCell.setBorder(Rectangle.NO_BORDER);
                        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        headerTable.addCell(logoCell);
                }

                // FIXED: This content should ALWAYS be added, regardless of logo status
                Phrase headerPhrase = new Phrase();
                headerPhrase.add(new Chunk(
                                "SURUHANJAYA SYARIKAT MALAYSIA\nMenara SSM@Sentral, No 7 Jalan Stesen Sentral 5,",
                                lighttitleHeaderFont));
                headerPhrase.add(new Chunk("\nKuala Lumpur Sentral, 50623, Kuala Lumpur,", lighttitleHeaderFont));
                headerPhrase.add(
                                new Chunk("\n\nTel: +60322994400 Email: enquiry@ssm.com.my Website: www.ssm.com.my",
                                                lightHeaderFont));
                PdfPCell textCell = new PdfPCell(headerPhrase);
                textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                textCell.setBorder(Rectangle.NO_BORDER);
                textCell.setLeading(1.2f, 1.2f);
                headerTable.addCell(textCell);
                document.add(headerTable);
                document.add(new Paragraph("\n"));

                LineSeparator line = new LineSeparator();
                document.add(line);

                Paragraph refundTitle = new Paragraph("REFUND SLIP", titleHeaderFont);
                refundTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(refundTitle);
                Paragraph slipNo = new Paragraph("Refund Slip No: " + slipData.getSlipNo(), lightSubHeaderFont);
                slipNo.setAlignment(Element.ALIGN_CENTER);
                document.add(slipNo);
                Paragraph RttAppNo = new Paragraph("Refund Application Number: " + slipData.getRttAppNo(),
                                lightSubHeaderFont);
                RttAppNo.setAlignment(Element.ALIGN_CENTER);
                document.add(RttAppNo);
                Paragraph printedDate = new Paragraph("Printed Date: " + currentDate, lightSubHeaderFont);
                printedDate.setAlignment(Element.ALIGN_CENTER);
                document.add(printedDate);
                document.add(new Paragraph("\n"));

                Paragraph payeeInfo = new Paragraph("Payee Information", SubHeaderFont);
                payeeInfo.setAlignment(Element.ALIGN_LEFT);
                payeeInfo.setSpacingBefore(20f);
                payeeInfo.setIndentationLeft(20f);
                document.add(payeeInfo);
                PdfPTable payeeTable = new PdfPTable(2);
                payeeTable.setWidthPercentage(100);
                payeeTable.setWidths(new float[] { 1.5f, 2.5f });
                document.add(new Paragraph("\n"));

                payeeTable.addCell(createNoWrapCellWithSpacing("Payee Name:", normalFont, Element.ALIGN_LEFT, 20f));
                payeeTable.addCell(
                                createNoWrapCellWithSpacing(slipData.getCustNm(), normalFont, Element.ALIGN_LEFT, 25f));
                payeeTable.addCell(createNoWrapCellWithSpacing("Phone Number:", normalFont, Element.ALIGN_LEFT, 20f));
                payeeTable.addCell(createNoWrapCellWithSpacing(slipData.getCustPhone(), normalFont, Element.ALIGN_LEFT,
                                25f));
                payeeTable.addCell(createNoWrapCellWithSpacing("Email:", normalFont, Element.ALIGN_LEFT, 20f));
                payeeTable.addCell(createNoWrapCellWithSpacing(slipData.getCustEmail(), normalFont, Element.ALIGN_LEFT,
                                25f));
                document.add(payeeTable);
                document.add(new Paragraph("\n"));

                Paragraph transactionInfo = new Paragraph("Information of Transaction to be Refunded", SubHeaderFont);
                transactionInfo.setAlignment(Element.ALIGN_LEFT);
                transactionInfo.setSpacingBefore(20f);
                transactionInfo.setIndentationLeft(20f);
                document.add(transactionInfo);
                PdfPTable transactionTable = new PdfPTable(2);
                transactionTable.setWidthPercentage(100);
                transactionTable.setWidths(new float[] { 1.5f, 2.5f });
                document.add(new Paragraph("\n"));

                String formattedRcptDate = slipData.getRcptdate().toInstant()
                                .atZone(ZoneId.of("GMT+8"))
                                .format(DateTimeFormatter.ofPattern("d MMMM yyyy"));

                transactionTable.addCell(
                                createNoWrapCellWithSpacing("Transaction Date:", normalFont, Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing(formattedRcptDate, normalFont, Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(createNoWrapCellWithSpacing("Order Reference Number:", normalFont,
                                Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing(slipData.getOrnNo(), normalFont, Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing("Customer Name:", normalFont, Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing(slipData.getCustNm(), normalFont, Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing("Payment Method:", normalFont, Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(createNoWrapCellWithSpacing(slipData.getRmsType(), normalFont,
                                Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing("Payment Mode:", normalFont, Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(createNoWrapCellWithSpacing(slipData.getRefundTy(), normalFont,
                                Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(createNoWrapCellWithSpacing("State (Branch Code):", normalFont,
                                Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(createNoWrapCellWithSpacing(slipData.getCustState(), normalFont,
                                Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing("Receipt Number:", normalFont, Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing(slipData.getRcptNo(), normalFont, Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(createNoWrapCellWithSpacing("Amount Approved for Refund:", normalFont,
                                Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(createNoWrapCellWithSpacing("RM" + slipData.getRefundAmt(), normalFont,
                                Element.ALIGN_LEFT, 25f));
                transactionTable.addCell(
                                createNoWrapCellWithSpacing("Reason for Refund:", normalFont, Element.ALIGN_LEFT, 20f));
                transactionTable.addCell(createNoWrapCellWithSpacing(slipData.getRefundReason(), normalFont,
                                Element.ALIGN_LEFT, 25f));
                document.add(transactionTable);
                document.add(new Paragraph("\n\n\n"));

                Paragraph footerNote = new Paragraph(
                                "*Perniagaan anda bermula di SSM*\nThis computer print does not require a signature",
                                lightHeaderFont);
                footerNote.setAlignment(Element.ALIGN_CENTER);
                document.add(footerNote);

                document.close();

                // Save to file
                Path folder = Paths.get(folderPath);
                if (Files.notExists(folder)) {
                        Files.createDirectories(folder);
                }

                String pdfName = "SSM-Receipt-" + slipData.getRttAppNo() + ".pdf";
                Path pdfFile = folder.resolve(pdfName);

                if (Files.exists(pdfFile)) {
                        Files.delete(pdfFile);
                }

                try (OutputStream out = Files.newOutputStream(pdfFile)) {
                        out.write(outputStream.toByteArray());
                }

                return outputStream.toByteArray();
        }

        private PdfPCell createNoWrapCellWithSpacing(String content, Font font, int alignment, float paddingLeft) {
                PdfPCell cell = new PdfPCell(new Phrase(content, font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setNoWrap(true);
                cell.setHorizontalAlignment(alignment);
                cell.setPaddingBottom(5f);
                cell.setPaddingLeft(paddingLeft);
                return cell;
        }
}
