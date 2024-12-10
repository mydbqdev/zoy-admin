package com.integration.zoy.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TenantProcessTasklet implements Tasklet {
	@Autowired
    private UploadService uploadService;
	
	private String ownerId;
    private String propertyId;
    private  byte[] file;
    
    public void setParameters(String ownerId, String propertyId, byte[] file) {
        this.ownerId = ownerId;
        this.propertyId = propertyId;
        this.file = file;
    }
   
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try {
            ResponseEntity<String> responses = uploadService.tenatantWriteDataPost(ownerId, propertyId, file);
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
