package ua.com.foxminded.batchxlsprocessor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final ProductGroupingService service;

    public JobCompletionNotificationListener(ProductGroupingService service) {
        this.service = service;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        service.getProductSummary()
                .forEach((name, quantity) -> LOGGER.info("{} - {}", name, round(quantity)));
    }

    private BigDecimal round(double d) {
        return BigDecimal.valueOf(d).setScale(1, RoundingMode.HALF_UP);
    }
}
