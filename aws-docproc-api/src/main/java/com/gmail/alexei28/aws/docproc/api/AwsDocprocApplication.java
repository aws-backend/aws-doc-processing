package com.gmail.alexei28.aws.docproc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.gmail.alexei28.aws.docproc")
public class AwsDocprocApplication {
    private static final Logger logger = LoggerFactory.getLogger(AwsDocprocApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AwsDocprocApplication.class, args);
        logger.info("\n\nApplication started SUCCESSFULLY | Java Version: {} | Java Vendor: {}\n",
                System.getProperty("java.version"),
                System.getProperty("java.vendor")
        );
    }
}