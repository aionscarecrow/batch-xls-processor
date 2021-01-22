package ua.com.foxminded.batchxlsprocessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchXlsProcessorApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Value("${file.input}")
    private String input;

    public static void main(String[] args) {
        SpringApplication.run(BatchXlsProcessorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", input);
        jobLauncher.run(job, paramsBuilder.toJobParameters());
    }
}
