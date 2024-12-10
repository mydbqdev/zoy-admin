package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
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
import com.lowagie.text.pdf.BaseFont;
@Service
public class PdfGenerateService {
private static final Logger log = LoggerFactory.getLogger(PdfGenerateService.class);
	
	@Autowired
    private TemplateEngine templateEngine;
	
	@Value("${app.zoy.rupee}")
	private String Rupee;
	
	@Autowired
	ZoyS3Service s3Service;
	
	public byte[] generatePdfFile(String templateName, Map<String, Object> data) {
		 List<?> reportData = (List<?>) data.get("reportData");
		 if (data == null || data.isEmpty() || reportData == null || reportData.isEmpty()) {
		        return new byte[0]; 
		    }
	    Context context = new Context();
	    context.setVariables(data);
	    String htmlContent = templateEngine.process(templateName, context);

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    try {
	        ITextRenderer renderer = new ITextRenderer();
	        renderer.setDocumentFromString(htmlContent);
	        renderer.layout();
	        renderer.createPDF(outputStream, false);
	        renderer.finishPDF();
	    } catch (DocumentException e) {
	        log.error("Error generating PDF: " + e.getMessage(), e);
	        return new byte[0]; 
	    }

	    return outputStream.toByteArray(); 
	}
    
	public String imageToBase64(InputStream inputStream) {
	    byte[] imageBytes = null;
	    try {
	        imageBytes = inputStream.readAllBytes();
	    } catch (IOException e) {
	        e.printStackTrace();
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
}
