package com.gmail.alexei28.aws.docproc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocprocApplication {
	private static final Logger logger = LoggerFactory.getLogger(DocprocApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(DocprocApplication.class, args);

		logger.info("\n\n ===== Application started successfully! =====\nAWS DocProc");
		logger.info(
				"\nJava version: {}, Java vendor: {}",
				System.getProperty("java.version"),
				System.getProperty("java.vendor"));
	}

}
