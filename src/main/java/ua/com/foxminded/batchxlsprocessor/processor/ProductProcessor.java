package ua.com.foxminded.batchxlsprocessor.processor;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.exception.ItemWriteException;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

public class ProductProcessor implements ItemProcessor<Product, Product> {
	
	private SingleItemPeekableItemReader<Product> itemReader;
	private ItemWriter<Product> itemWriter;
	
	@Autowired
	ProductGroupingService groupingService;
	
	private static final Logger log = LoggerFactory.getLogger(ProductProcessor.class);
	
	public ProductProcessor(SingleItemPeekableItemReader<Product> itemReader, ItemWriter<Product> productLogWriter) {
		//System.out.println("Processor innitialized");//HERE
		this.itemReader = itemReader;
		this.itemWriter = productLogWriter;
	}
	

	@Override
	public Product process(Product item) throws Exception {
//		System.out.println("Process called with: " + item);
		Product current = itemReader.read();
		groupingService.merge(current);
		
		System.out.println(">>> Current next: " + current + ", called with item: " + item.getName());
		if(current == null) {
			System.out.println("Inside feed block");
			feedWriter(groupingService.getGroupedResult());
		}
		
		return null;
	}
	
	private void feedWriter(List<Product> groupedProducts) {
		System.out.println("FeedWriter received items: " + groupedProducts);//HERE
		
		BiConsumer<List<Product>, ItemWriter<Product>> feed = (list, writer) -> {
			try {
				writer.write(list);
			} catch(Exception e) { //REname ProcessingException to WriteException
				throw new ItemWriteException("Failed to feed item to writer");
			}
		};
		groupedProducts.stream().map(Collections::<Product>singletonList).forEach(list -> feed.accept(list, itemWriter));
	}
	

}

















