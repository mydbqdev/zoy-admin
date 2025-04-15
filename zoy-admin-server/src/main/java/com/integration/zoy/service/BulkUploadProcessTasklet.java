package com.integration.zoy.service;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.integration.zoy.utils.PropertyList;
import com.integration.zoy.utils.TenantList;

@Component
public class BulkUploadProcessTasklet implements Tasklet{
	@Autowired
    private UploadService uploadService;
	
	private String ownerId;
    private String propertyId;
    private List<PropertyList> propertyList;
    private List<TenantList> tenantList;
    
    public void setParameters(String ownerId, String propertyId, List<PropertyList> propertyList,List<TenantList> tenantList) {
        this.ownerId = ownerId;
        this.propertyId = propertyId;
        this.propertyList = propertyList;
        this.tenantList = tenantList;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try {
            ResponseEntity<String> responses = uploadService.zoyPartnerBulkUpload(ownerId, propertyId, propertyList,tenantList);
            
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
