package com.gmail.alexei28.aws.docproc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentApiApplication {
    private static final Logger logger = LoggerFactory.getLogger(DocumentApiApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(DocumentApiApplication.class, args);

        logger.info("\n\nApplication started SUCCESSFULLY | Java Version: {} | Java Vendor: {}\n",
                System.getProperty("java.version"),
                System.getProperty("java.vendor")
        );
    }

}
