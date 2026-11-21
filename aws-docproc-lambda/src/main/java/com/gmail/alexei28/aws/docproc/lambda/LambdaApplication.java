package com.gmail.alexei28.aws.docproc.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/*
  Запускаем Lambda Poller
  ./gradlew :aws-docproc-lambda:bootRun  --args='--spring.profiles.active=local'
 */
@SpringBootApplication
public class LambdaApplication {
    private static final Logger logger = LoggerFactory.getLogger(LambdaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LambdaApplication.class, args);
        logger.info("\n\nApplication started SUCCESSFULLY | Java Version: {} | Java Vendor: {}\n",
                System.getProperty("java.version"),
                System.getProperty("java.vendor")
        );
    }
}