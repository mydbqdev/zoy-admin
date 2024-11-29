package com.integration.zoy.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PropertyProcessTasklet implements Tasklet{
	@Autowired
    private UploadService uploadService;
	
	private String ownerId;
    private String propertyId;
    private byte[] fileBytes;
    
    public void setParameters(String ownerId, String propertyId, byte[] fileBytes) {
        this.ownerId = ownerId;
        this.propertyId = propertyId;
        this.fileBytes = fileBytes;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try {
            ResponseEntity<String> responses = uploadService.zoyPartnerUserIdPropertyIdUploadXlsxPost(ownerId, propertyId, fileBytes);
            
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("responseBody", responses.getBody());
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("statusCode", responses.getStatusCodeValue());
            return RepeatStatus.FINISHED;
        } catch (Exception e) {
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("responseBody", e.getMessage());
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
    }
}
