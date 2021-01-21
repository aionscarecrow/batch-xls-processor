package ua.com.foxminded.batchxlsprocessor.processor;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.exception.ItemWriteException;
import ua.com.foxminded.batchxlsprocessor.reader.ReaderExhaustedWrapper;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

public class ProductProcessor implements ItemProcessor<Product, Product> {
	
	private ReaderExhaustedWrapper<Product> itemReader;
	private ItemWriter<Product> itemWriter;
	
	@Autowired
	ProductGroupingService groupingService;
	
	private static final Logger log = LoggerFactory.getLogger(ProductProcessor.class);
	
	public ProductProcessor(ReaderExhaustedWrapper<Product> itemReader, ItemWriter<Product> productLogWriter) {
		this.itemReader = itemReader;
		this.itemWriter = productLogWriter;
	}
	

	@Override
	public Product process(Product item) throws Exception {
		log.info("Process called with: [{}]" + item);
		groupingService.merge(item);
		
//		if(itemReader.isExhausted()) {
//			System.out.println("Inside feed block");
//			feedWriter(groupingService.getGroupedResult());
//		}
		
		return null;
	}
	
	private void feedWriter(List<Product> groupedProducts) {
		System.out.println("FeedWriter received items: " + groupedProducts);//HERE
		
		BiConsumer<List<Product>, ItemWriter<Product>> feed = (list, writer) -> {
			try {
				writer.write(list);
			} catch(Exception e) {
				throw new ItemWriteException("Failed to feed item to writer", e);
			}
		};
		groupedProducts.stream().map(Collections::<Product>singletonList).forEach(list -> feed.accept(list, itemWriter));
	}
	

}

















