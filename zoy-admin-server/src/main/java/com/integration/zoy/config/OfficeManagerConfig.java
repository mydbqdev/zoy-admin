package com.integration.zoy.config;

import java.io.File;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class OfficeManagerConfig {

	private static final Logger log = LoggerFactory.getLogger(OfficeManagerConfig.class);

	@Value("${office.home}")
	private String officeHomePath;

	@Bean(destroyMethod = "stop")
	OfficeManager officeManager() {
		OfficeManager officeManager = LocalOfficeManager.builder()
				.officeHome(officeHomePath)
				.install()
				.build();
		try {
			officeManager.start();
			log.info("Office Manager started successfully.");
		} catch (OfficeException e) {
			log.error("Error starting Office Manager: ", e);
			throw new RuntimeException("Failed to start Office Manager", e);
		}

		return officeManager;
	}
}
