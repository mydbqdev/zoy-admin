package com.integration.zoy.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
@Service
public class pdfGenerateService {
private static final Logger log = LoggerFactory.getLogger(pdfGenerateService.class);
	
	@Autowired
    private TemplateEngine templateEngine;
	
	@Value("${app.zoy.reports}")
    private String pdfDirectory;
	
	public String generatePdfFile(String templateName, Map<String, Object> data, String pdfFileName) throws IOException {
		Context context = new Context();
		context.setVariables(data);

        String htmlContent = templateEngine.process(templateName, context);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pdfDirectory + pdfFileName);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(fileOutputStream, false);
            renderer.finishPDF();
            return "https://downloads-qa.zoypg.com/" + pdfFileName;
        } catch (FileNotFoundException e) {
        	log.error(e.getMessage(), e);
        } catch (DocumentException e) {
        	log.error(e.getMessage(), e);
        }
        return "";
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
