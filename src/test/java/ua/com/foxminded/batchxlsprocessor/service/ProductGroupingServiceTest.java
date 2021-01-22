package ua.com.foxminded.batchxlsprocessor.service;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProductGroupingServiceTest {

    @Test
    void whenInputsAreValidProducts_groupCorrectlyByName() {
        Product product1 = new Product("A", 12.5);
        Product product2 = new Product("A", 13.5);
        Product product3 = new Product("B", 13.5);
        ProductGroupingService service = new ProductGroupingService();

        service.group(product1);
        service.group(product2);
        service.group(product3);

        Map<String, Double> productSummary = service.getProductSummary();
        assertEquals(26.0, productSummary.get("A"));
        assertEquals(13.5, productSummary.get("B"));
    }
}
