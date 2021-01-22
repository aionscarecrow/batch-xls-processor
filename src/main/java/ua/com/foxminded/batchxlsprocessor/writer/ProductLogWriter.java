package ua.com.foxminded.batchxlsprocessor.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import ua.com.foxminded.batchxlsprocessor.domain.Product;

public class ProductLogWriter implements ItemWriter<Product> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductLogWriter.class);

	@Override
	public void write(List<? extends Product> items) throws Exception {
		if(LOG.isTraceEnabled()) {
			items.forEach(item -> LOG.trace("Writing out: [{}]", item));
		}
		
		items.forEach(item -> LOG.info(item.toString()));
	}
}
