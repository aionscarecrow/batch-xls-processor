package ua.com.foxminded.batchxlsprocessor.processor;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.exception.ItemWriteOutException;
import ua.com.foxminded.batchxlsprocessor.reader.ExhaustedAwareItemReaderWrapper;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

public class ProductProcessor extends ValidatingItemProcessor<Product> implements ItemProcessor<Product, Product> {
	
	private ExhaustedAwareItemReaderWrapper<Product> itemReader;
	private ItemWriter<Product> itemWriter;
	
	@Autowired
	ProductGroupingService groupingService;
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductProcessor.class);
	
	
	public ProductProcessor(ExhaustedAwareItemReaderWrapper<Product> itemReader, 
			ItemWriter<Product> productLogWriter, Validator<Product> validator) {
		
		this.itemReader = itemReader;
		this.itemWriter = productLogWriter;
		this.setValidator(validator);
	}
	

	@Override
	public Product process(Product item) {
		
		if(LOG.isTraceEnabled())
			LOG.trace("Process called with: [{}]" + item);
		
		groupingService.merge(validate(item));
		
		if(itemReader.isExhausted()) {
			LOG.debug("Delegate ItemReader exhausted");
			feedWriter(groupingService.getGroupedResult());
		}
		
		return null;
	}
	
	
	private Product validate(Product item) {
		return super.process(item);
	}
	
	
	private void feedWriter(List<Product> groupedProducts) {
		LOG.debug("Feeding {} items to writer", groupedProducts.size());
		
		groupedProducts.stream()
			.map(Collections::<Product>singletonList)
			.forEach(this::feed);
	}
	
	
	private void feed(List<Product> items) {
		try {
			this.itemWriter.write(items);
		} catch(Exception e) {
			throw new ItemWriteOutException("Failed to feed item to writer", e);
		}
	}
	

}

















