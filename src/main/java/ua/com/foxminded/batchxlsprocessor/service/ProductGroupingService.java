package ua.com.foxminded.batchxlsprocessor.service;

import org.springframework.stereotype.Service;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductGroupingService {

    private final Map<String, Double> productSummary = new HashMap<>();

    public void group(Product product) {
        productSummary.merge(product.getName(), product.getQuantity(), Double::sum);
    }

    public Map<String, Double> getProductSummary() {
        return productSummary;
    }
}
