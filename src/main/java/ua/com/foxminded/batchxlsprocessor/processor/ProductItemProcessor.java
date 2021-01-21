package ua.com.foxminded.batchxlsprocessor.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

@Component
public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    private final ProductGroupingService service;

    public ProductItemProcessor(ProductGroupingService service) {
        this.service = service;
    }

    @Override
    public Product process(Product product) throws Exception {
        service.group(product);
        return product;
    }
}
