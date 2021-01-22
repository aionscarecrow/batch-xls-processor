package ua.com.foxminded.batchxlsprocessor.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobCompletionNotificationListenerTest {

    @Mock
    private ProductGroupingService mockedService;
    @Mock
    private JobExecution jobExecution;
    @InjectMocks
    private JobCompletionNotificationListener listener;

    @Test
    void afterJob() {
        listener.afterJob(jobExecution);

        verify(mockedService).getProductSummaryAsList();
    }
}