package ua.com.foxminded.batchxlsprocessor.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.storage.ProductSummaryStorage;

import java.util.Map;

@Component
public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Autowired
    private ProductSummaryStorage storage;

    @Override
    public Product process(Product product) throws Exception {
        Map<String, Double> productSummary = storage.getProductSummary();
        String name = product.getName();
        Double quantity = product.getQuantity();
        if (!productSummary.containsKey(name)) {
            productSummary.put(name, quantity);
        } else {
            Double updatedQuantity = productSummary.get(name) + quantity;
            productSummary.put(name, updatedQuantity);
        }
        return product;
    }
}
