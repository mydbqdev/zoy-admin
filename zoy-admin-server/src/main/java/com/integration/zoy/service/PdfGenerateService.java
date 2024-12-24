package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class PdfGenerateService {
private static final Logger log = LoggerFactory.getLogger(PdfGenerateService.class);
	
	@Autowired
    private TemplateEngine templateEngine;
    
    @Value("${app.zoy.rupee}")
    private String Rupee;
    
    @Value("${zoy.admin.watermark}")
    private String watermarkImagePath;
    
    @Value("${zoy.admin.logo}")
   	private String zoyLogoPath;

    @Autowired
    ZoyS3Service s3Service;
    
    public byte[] generatePdfFile(InputStream reportStream,Map<String, Object> data) throws FileNotFoundException  {
    	List<?> reportData = (List<?>) data.get("reportData");
        if (data == null || data.isEmpty() || reportData == null || reportData.isEmpty()) {
            return new byte[0]; 
        }
    	
    	
        if (reportStream == null) {
            log.error("Report template not found.");
            throw new FileNotFoundException("Report template not found.");
        }
        
        JRDataSource dataSource = new JRBeanCollectionDataSource(reportData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, data, dataSource);

            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            byte[] watermarkedPdf = addWatermark(outputStream.toByteArray());
            return watermarkedPdf;

        } catch (JRException e) {
            log.error("Error generating PDF: " + e.getMessage(), e);
            return new byte[0];  
        }
    }
    
    private byte[] addWatermark(byte[] pdfBytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
            stamper = new PdfStamper(reader, outputStream);
            
            InputStream inputStreamImg = getClass().getResourceAsStream(watermarkImagePath);
            String base64Logo = imageToBase64(inputStreamImg);
            byte[] imageBytes = Base64.getDecoder().decode(base64Logo);
            Image watermarkImage = Image.getInstance(imageBytes);
            
            watermarkImage.scaleToFit(300, 300);

            int totalPages = reader.getNumberOfPages();
            for (int i = 1; i <= totalPages; i++) {
                float pageWidth = reader.getPageSize(i).getWidth();
                float pageHeight = reader.getPageSize(i).getHeight();

                float xPos = (pageWidth - watermarkImage.getScaledWidth()) / 2;
                float yPos = (pageHeight - watermarkImage.getScaledHeight()) / 2;

                watermarkImage.setAbsolutePosition(xPos, yPos);
                PdfContentByte content = stamper.getOverContent(i);
                content.addImage(watermarkImage);
            }
        } catch (IOException | DocumentException e) {
            log.error("Error adding watermark: " + e.getMessage(), e);
        } finally {
            try {
                if (stamper != null) stamper.close();
                if (reader != null) reader.close();
            } catch (IOException | DocumentException e) {
                log.error("Error closing PDF stamper/reader: " + e.getMessage(), e);
            }
        }
        return outputStream.toByteArray();
    }

    public String imageToBase64(InputStream inputStream) {
        byte[] imageBytes = null;
        try {
            imageBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error reading image bytes: " + e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    
    public Boolean generateRenatalPdfFile(String templateName, Map<String, Object> data,String userId,String bookingId) throws IOException {
    	Context context = new Context();
    	context.setVariables(data);

    	String htmlContent = templateEngine.process(templateName, context);
    	try {
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		ITextRenderer renderer = new ITextRenderer();
    		renderer.setDocumentFromString(htmlContent);
    		renderer.getFontResolver().addFont(Rupee, PdfEncodings.IDENTITY_H, BaseFont.EMBEDDED);
    		renderer.layout();
    		renderer.createPDF(outputStream, false);
    		renderer.finishPDF();
    		InputStream file=new ByteArrayInputStream(outputStream.toByteArray());
    		return s3Service.uploadRentalAgreement(userId, bookingId, ContentType.APPLICATION_PDF.getMimeType(), file);
    	} catch (FileNotFoundException e) {
    		log.error(e.getMessage(), e);
    	} catch (DocumentException e) {
    		log.error(e.getMessage(), e);
    	}
    	return null;
    }
    
    public byte[] imageToBytes(InputStream inputStream) {
	    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            byteArrayOutputStream.write(buffer, 0, bytesRead);
	        }
	        return byteArrayOutputStream.toByteArray();
	    } catch (IOException e) {
	        log.error("Error while converting image to bytes: {}", e.getMessage(), e);
	        throw new RuntimeException("Failed to convert image to bytes", e);
	    }
	}
}
