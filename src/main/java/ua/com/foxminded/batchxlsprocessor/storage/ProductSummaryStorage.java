package ua.com.foxminded.batchxlsprocessor.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductSummaryStorage {

    private final Map<String, Double> productSummary = new HashMap<>();

    public Map<String, Double> getProductSummary() {
        return productSummary;
    }
}
