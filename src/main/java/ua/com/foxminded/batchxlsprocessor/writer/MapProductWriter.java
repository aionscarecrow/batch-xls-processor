package ua.com.foxminded.batchxlsprocessor.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.storage.ProductSummaryStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MapProductWriter implements ItemWriter<Product> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MapProductWriter.class);

    @Autowired
    private ProductSummaryStorage storage;

    @Override
    public void write(List<? extends Product> list) throws Exception {
        Set<Map.Entry<String, Double>> entries = storage.getProductSummary().entrySet();
        for (Map.Entry<String, Double> entry : entries) {
            LOGGER.info("{} - {}", entry.getKey(), entry.getValue());
        }
    }
}
