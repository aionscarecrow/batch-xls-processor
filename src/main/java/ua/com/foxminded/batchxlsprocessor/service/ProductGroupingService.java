package ua.com.foxminded.batchxlsprocessor.service;

import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductGroupingService {

    private final Map<String, Double> productSummary = new HashMap<>();

    public void group(Product product) {
        productSummary.merge(product.getName(), product.getQuantity(), Double::sum);
    }

    public List<Product> getProductSummaryAsList() {
        return productSummary.entrySet().stream()
                .map(e -> new Product(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
