package ua.com.foxminded.batchxlsprocessor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.writer.MapProductWriter;

import java.util.Map;
import java.util.Set;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private MapProductWriter mapProductWriter;

    @Override
    public void afterJob(JobExecution jobExecution) {
    	// HERE
//        Set<Map.Entry<String, Double>> entries = mapProductWriter.getProductSummary().entrySet();
//        for (Map.Entry<String, Double> entry : entries) {
//            LOGGER.info("{} - {}", entry.getKey(), entry.getValue());
//        }
    }
}
