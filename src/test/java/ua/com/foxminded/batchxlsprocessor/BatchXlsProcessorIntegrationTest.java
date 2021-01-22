package ua.com.foxminded.batchxlsprocessor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.com.foxminded.batchxlsprocessor.config.JobConfig;
import ua.com.foxminded.batchxlsprocessor.listener.CustomSkipListener;
import ua.com.foxminded.batchxlsprocessor.listener.JobCompletionNotificationListener;
import ua.com.foxminded.batchxlsprocessor.mapper.ProductExcelRowMapper;
import ua.com.foxminded.batchxlsprocessor.processor.ProductItemProcessor;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;
import ua.com.foxminded.batchxlsprocessor.writer.NoOperationItemWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.batch.test.AssertFile.assertFileEquals;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {JobConfig.class, ProductExcelRowMapper.class, ProductItemProcessor.class,
        CustomSkipListener.class, JobCompletionNotificationListener.class, ProductGroupingService.class,
        NoOperationItemWriter.class})
class BatchXlsProcessorIntegrationTest {

    private static final String TEST_INPUT = "test-input.xls";
    private static final String ACTUAL_RESULT = "src/test/resources/productSummary.log";
    private static final String EXPECTED_RESULT = "src/test/resources/expectedProductSummary.log";
    private static final String ACTUAL_ERRORS = "src/test/resources/parsingErrors.log";
    private static final String EXPECTED_ERRORS = "src/test/resources/expectedParsingErrors.log";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", TEST_INPUT);
        return paramsBuilder.toJobParameters();
    }

    @Test
    void givenXlsFileWithErrors_jobOutputsCorrectLogFilesWithProductSummaryAndErrorSummary() throws Exception {
        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_RESULT);
        FileSystemResource actualResult = new FileSystemResource(ACTUAL_RESULT);
        FileSystemResource expectedErrors = new FileSystemResource(EXPECTED_ERRORS);
        FileSystemResource actualErrors = new FileSystemResource(ACTUAL_ERRORS);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertFileEquals(expectedResult, actualResult);
        assertFileEquals(expectedErrors, actualErrors);
    }
}
