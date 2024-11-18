package com.integration.zoy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.lowagie.text.DocumentException;
@Service
public class pdfGenerateService {
private static final Logger log = LoggerFactory.getLogger(pdfGenerateService.class);
	
	@Autowired
    private TemplateEngine templateEngine;
	
	public byte[] generatePdfFile(String templateName, Map<String, Object> data) {
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
    
    public String imageToBase64(String imagePath) {
        byte[] imageBytes = null;
		try {
			imageBytes = Files.readAllBytes(Paths.get(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
