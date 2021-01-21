package ua.com.foxminded.batchxlsprocessor.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

import java.util.List;

@Component
public class NoOperationItemWriter implements ItemWriter<Product> {

    @Override
    public void write(List<? extends Product> list) throws Exception {
        // no operation
    }
}
