package ua.com.foxminded.batchxlsprocessor.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.item.ChunkMonitor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.com.foxminded.batchxlsprocessor.domain.Product;
@Component
public class ProductProcessor implements ItemProcessor<Product, Product> {
	
	private Map<String, Double> map = new HashMap<>();
	private SingleItemPeekableItemReader<Product> itemReader;
	private ChunkMonitor chunkMonitor;
	
	@Autowired
	ChunkContext chunkContext;
	
	public ProductProcessor(SingleItemPeekableItemReader<Product> itemReader, ChunkMonitor chunkMonitor) {
		this.itemReader = itemReader;
		this.chunkMonitor = chunkMonitor;
	}
	

	@Override
	public Product process(Product item) throws Exception {
		System.out.println("Chunk offset on process() call: " + chunkMonitor.getOffset());
//		chunkMonitor.resetOffset();
		
		do {
			executeMerge(itemReader.read());
			System.out.println("Current chunk offset: " + chunkMonitor.getOffset());
		} while(itemReader.peek() != null) ;
		
		
//		do {
//			ExecutionContext context = new ExecutionContext();
//			itemReader.update(context);
//			System.out.println("Context entry set: " + context.entrySet());
//			executeMerge(item);
//			current = itemReader.read();
//		} while(itemReader.peek() != null);
		
//		do {
//			executeMerge(current);
//			current = itemReader.read();
//		} while (itemReader.peek() != null);
		
		return new Product("Success", 3.14);
	}
	
	private void executeMerge(Product item) throws Exception {
		System.out.println("executeMerge() reads: " + item);
	}
	
	

}
