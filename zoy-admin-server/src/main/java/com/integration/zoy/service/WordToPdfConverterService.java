package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordToPdfConverterService {
    private static final Logger logger = LoggerFactory.getLogger(WordToPdfConverterService.class);

    @Autowired
    private OfficeManager officeManager;

    public byte[] generateWordDocument(String templatePath, List<Map<String, Object>> dataList) {
    	if (dataList == null || dataList.isEmpty()) {
            return new byte[0];
        }
    	try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis);
             ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream()) {

    		 adjustHeaderFooterMargins(document);
            // Replace placeholders in headers
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    replacePlaceholdersInParagraph(paragraph, dataList.get(0));
                    for (XWPFRun run : paragraph.getRuns()) {
                        run.setBold(true);
                    }
                }
            }
            
            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    replacePlaceholdersInParagraph(paragraph, dataList.get(0));
                }
            }

            // Process table
            XWPFTable table = document.getTables().get(0);
            int templateRowId = 0;
            XWPFTableRow headerRow = table.getRow(templateRowId);
            headerRow.setRepeatHeader(true);
            templateRowId = 1;
            XWPFTableRow rowTemplate = table.getRow(templateRowId);

            for (Map<String, Object> data : dataList) {
                XWPFTableRow oldRow = rowTemplate;
                CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());

                XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                for (XWPFTableCell cell : newRow.getTableCells()) {
                    String placeHolder = cell.getText().replace("$", "").replace("}", "").replace("{", "");
                    cell.removeParagraph(0);
                    Object value = data.get(placeHolder);
                    cell.setText(value != null ? value.toString() : "");
                }
                table.addRow(newRow);
            }
            table.removeRow(templateRowId);

            document.write(docxOutputStream);
            return convertToPdf(docxOutputStream);
        } catch (IOException | XmlException e) {
            logger.error("An error occurred while generating WordDocument: {}", e.getMessage(), e);
        }
        return null;
    }

    private static void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, Object> placeholders) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) {
            return;
        }

        StringBuilder paragraphText = new StringBuilder();

        for (XWPFRun run : runs) {
            if (run.getText(0) != null) {
                paragraphText.append(run.getText(0));
            }
        }

        String replacedText = paragraphText.toString();
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            replacedText = replacedText.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }

        // Remove $ symbols
        replacedText = replacedText.replace("$", "");

        if (replacedText.equals(paragraphText.toString())) {
            return;
        }

        int cursor = 0;
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text == null) {
                continue;
            }

            int length = text.length();
            if (cursor + length <= replacedText.length()) {
                String newText = replacedText.substring(cursor, cursor + length);
                run.setText(newText, 0);
                cursor += length;
            } else {
                run.setText("", 0);
            }
        }

        if (cursor < replacedText.length()) {
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(replacedText.substring(cursor));
        }
    }
    
    private void adjustHeaderFooterMargins(XWPFDocument document) {
        XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
        if (headerFooterPolicy != null) {
            // Adjust header margins
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    paragraph.setSpacingAfter(200);
                    paragraph.setSpacingBefore(200);
                }
            }

            // Adjust footer margins
            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    paragraph.setSpacingAfter(200);
                    paragraph.setSpacingBefore(200);
                }
            }
        }
    }

    private byte[] convertToPdf(ByteArrayOutputStream docxBytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(docxBytes.toByteArray());
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            DocumentConverter converter = LocalConverter.builder().officeManager(officeManager).build();
            converter.convert(byteArrayInputStream).to(pdfOutputStream)
                    .as(DefaultDocumentFormatRegistry.getFormatByExtension("pdf")).execute();
            return pdfOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("IOException occurred while converting DOCX to PDF: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while converting DOCX to PDF: {}", e.getMessage(), e);
        }
        return null;
    }
}