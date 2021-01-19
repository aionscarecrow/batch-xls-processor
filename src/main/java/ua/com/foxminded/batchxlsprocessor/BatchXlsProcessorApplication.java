package ua.com.foxminded.batchxlsprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchXlsProcessorApplication {

    public static void main(String[] args) {
    	System.out.println(Thread.currentThread().getName());
        SpringApplication.run(BatchXlsProcessorApplication.class, args);
    }

}
