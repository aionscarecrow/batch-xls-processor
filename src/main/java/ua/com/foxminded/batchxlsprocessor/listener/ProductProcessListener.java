package ua.com.foxminded.batchxlsprocessor.listener;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import ua.com.foxminded.batchxlsprocessor.domain.Product;

public class ProductProcessListener implements ItemProcessListener<Product, Product> {

	@Override
	public void beforeProcess(Product item) {
	}

	@Override
	public void afterProcess(Product item, Product result) {
		
	}

	@Override
	public void onProcessError(Product item, Exception e) {
	}
	
	

}
