package ua.com.foxminded.batchxlsprocessor.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.batch.repeat.RepeatStatus;

import ua.com.foxminded.batchxlsprocessor.domain.Product;

public class ItemWriterWrapper implements ItemWriter<Product>, Tasklet {
	
	private ItemWriter<Product> wrappedWriter;
	
	private Map<String, Double> map = new HashMap<>();
	

	public ItemWriterWrapper(AbstractFileItemWriter<Product> wrappedTasklet) {
		this.wrappedWriter = wrappedWriter;
	}
	
	


	@Override
	public void write(List<? extends Product> items) throws Exception {
		
		System.out.println("Received item: " + items);
		Product product = items.get(0);
		map.compute(product.getName(), (k, v) -> (v == null)? product.getQuantity() : v + product.getQuantity());
	}
	
	public void printContents() {
		System.out.println("Current map: " + map);
	}




	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		System.out.println("Writer's execute() called: " + Thread.currentThread().getName());
		wrappedWriter.write(Collections.singletonList(contribution).stream().map(c -> Product.class.cast(c)).collect(Collectors.toList()));
		System.out.println(contribution.getClass() + " is: " + contribution);
		return null;
	}

}
