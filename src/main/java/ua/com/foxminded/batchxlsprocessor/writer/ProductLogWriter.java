package ua.com.foxminded.batchxlsprocessor.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import ua.com.foxminded.batchxlsprocessor.domain.Product;

public class ProductLogWriter implements ItemWriter<Product> {

	@Override
	public void write(List<? extends Product> items) throws Exception {
		System.out.println("Writing out list: " + items);//HERE
		items.forEach(System.out::println);
	}
	
	

}
