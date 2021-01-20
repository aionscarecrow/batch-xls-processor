package ua.com.foxminded.batchxlsprocessor.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;

import ua.com.foxminded.batchxlsprocessor.domain.Product;

public class ProductProcessor implements ItemProcessor<Product, Product> {
	
	private Map<String, Double> map = new HashMap<>();
	private SingleItemPeekableItemReader<Product> itemReader;
	
	public ProductProcessor(SingleItemPeekableItemReader<Product> itemReader) {
		this.itemReader = itemReader;
	}
	

	@Override
	public Product process(Product item) throws Exception {
		while(itemReader.peek() != null) {
			System.out.println("got next: " + itemReader.read());
		}
		return new Product("Success", 3.14);
	}
	
	private void merge(Product product) {
		
	}
	
	

}
