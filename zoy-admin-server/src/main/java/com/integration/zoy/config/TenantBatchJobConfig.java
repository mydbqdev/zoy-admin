package com.integration.zoy.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.integration.zoy.service.BulkUploadProcessTasklet;
import com.integration.zoy.service.JobCompletionNotificationListener;
import com.integration.zoy.service.PropertyProcessTasklet;
import com.integration.zoy.service.TenantProcessTasklet;

@Configuration
@EnableBatchProcessing
public class TenantBatchJobConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobPropertyBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private TenantProcessTasklet tenantProcessTasklet;
	
	@Autowired
	private PropertyProcessTasklet propertyProcessTasklet;
	
	@Autowired
	private BulkUploadProcessTasklet bulkUploadProcessTasklet;

	@Autowired
	private JobCompletionNotificationListener jobCompletionNotificationListener; 

	@Bean
	public Step tenantProcessStep() {
		return stepBuilderFactory.get("tenantProcessStep")
				.tasklet(tenantProcessTasklet)
				.build();
	}

	@Bean
	public Job tenantProcessJob() {
		return jobBuilderFactory.get("tenantProcessJob")
				.listener(jobCompletionNotificationListener) 
				.start(tenantProcessStep()) 
				.build();
	}
	
	@Bean
	public Step propertyProcessStep() {
		return stepBuilderFactory.get("propertyProcessJob")
				.tasklet(propertyProcessTasklet)
				.build();
	}

	@Bean
	public Job propertyProcessJob() {
		return jobPropertyBuilderFactory.get("propertyProcessJob")
				.listener(jobCompletionNotificationListener) 
				.start(propertyProcessStep()) 
				.build();
	}
	
	@Bean
	public Step bulkUploadProcessStep() {
		return stepBuilderFactory.get("bulkUploadProcessStep")
				.tasklet(bulkUploadProcessTasklet)
				.build();
	}

	@Bean
	public Job bulkUploadProcessJob() {
		return jobBuilderFactory.get("bulkUploadProcessJob")
				.listener(jobCompletionNotificationListener) 
				.start(bulkUploadProcessStep()) 
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
	    return new SimpleAsyncTaskExecutor(); 
	}

}
