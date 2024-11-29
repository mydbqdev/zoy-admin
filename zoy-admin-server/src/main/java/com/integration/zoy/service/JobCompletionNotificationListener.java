package com.integration.zoy.service;

import java.util.Optional;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.integration.zoy.entity.BulkUploadDetails;
import com.integration.zoy.repository.BulkUploadDetailsRepository;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport{
	@Autowired
	BulkUploadDetailsRepository bulkUploadDetailsRepository;

	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobExecutionId = jobExecution.getJobParameters().getString("jobExecutionId");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Optional<BulkUploadDetails> bulkUploadDetailsOpt = bulkUploadDetailsRepository.findByJobExecutionId(jobExecutionId);

		bulkUploadDetailsOpt.ifPresent(bulkUploadDetails -> {
			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
				bulkUploadDetails.setStatus("Completed");
			} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
				bulkUploadDetails.setStatus("Failed");
			}
			bulkUploadDetailsRepository.save(bulkUploadDetails);
		});
	}
}

