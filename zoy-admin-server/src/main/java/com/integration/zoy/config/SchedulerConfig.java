package com.integration.zoy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@EnableScheduling 
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
     
    Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegister) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setErrorHandler(t-> logger.error("Caught exception on the @Scheduled task. " + t.getMessage()));
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix("Spring-scheduler-task-pool-");
        taskScheduler.initialize();
        scheduledTaskRegister.setTaskScheduler(taskScheduler);
    }
}