package ua.com.foxminded.batchxlsprocessor.service;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductGroupingServiceTest {

    private final ProductGroupingService service = new ProductGroupingService();

    @Test
    void merge() {
        Product product1 = new Product("A", 12.5);
        Product product2 = new Product("A", 13.5);
        Product product3 = new Product("B", 13.5);

        service.group(product1);
        service.group(product2);
        service.group(product3);

        List<Product> productSummary = service.getProductSummaryAsList();
        assertEquals(26.0, productSummary.get(0).getQuantity());
        assertEquals(13.5, productSummary.get(1).getQuantity());
    }
}