package ua.com.foxminded.batchxlsprocessor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ua.com.foxminded.batchxlsprocessor.domain.Product;

@Component
public class ProductGroupingService {
	
	private static final Logger log = LoggerFactory.getLogger(ProductGroupingService.class);
	
	private Map<String, Double> map = new HashMap<>();
	
	public void merge(Product product) {
//		System.out.println("Retrieved for merge: " + product);
		map.compute(product.getName(), (k, v) -> (v == null)? product.getQuantity() : v + product.getQuantity());
	}
	
	public List<Product> getGroupedResult() {
		log.info("Grouped result requested");
		return map.entrySet().stream().map(e -> new Product(e.getKey(), e.getValue())).collect(Collectors.toList());
	}

}
