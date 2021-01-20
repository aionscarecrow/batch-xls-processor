package ua.com.foxminded.batchxlsprocessor.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MapProductWriter implements ItemWriter<Product> {

    private final Map<String, Double> productSummary = new HashMap<>();

    @Override
    public void write(List<? extends Product> list) throws Exception {
        for (Product product : list) {
            String name = product.getName();
            Double quantity = product.getQuantity();
            if (!productSummary.containsKey(name)) {
                productSummary.put(name, quantity);
            } else {
                Double updatedQuantity = productSummary.get(name) + quantity;
                productSummary.put(name, updatedQuantity);
            }
        }
    }

    public Map<String, Double> getProductSummary() {
        return productSummary;
    }
}
