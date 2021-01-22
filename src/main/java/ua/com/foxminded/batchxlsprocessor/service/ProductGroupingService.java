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
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductGroupingService.class);
	
	private Map<String, Double> products = new HashMap<>();
	
	public void merge(Product product) {
		if(LOG.isTraceEnabled())
			LOG.trace("Merging in {}", product);
		products.merge(product.getName(), product.getQuantity(), Double::sum);
	}
	
	public List<Product> getGroupedResult() {
		LOG.debug("Returning grouped result of {} entries", products.size());
		return products.entrySet().stream()
				.map(e -> new Product(e.getKey(), e.getValue()))
				.collect(Collectors.toUnmodifiableList());
	}
}
