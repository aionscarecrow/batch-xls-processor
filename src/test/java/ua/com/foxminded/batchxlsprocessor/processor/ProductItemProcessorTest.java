package ua.com.foxminded.batchxlsprocessor.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductItemProcessorTest {

    @Mock
    private ProductGroupingService mockedService;
    @InjectMocks
    private ProductItemProcessor processor;

    @Test
    void whenInputIsProduct_processCallsServiceWithProductAndReturnsProduct() throws Exception {
        Product product = new Product("A", 12.5);

        Product returnedProduct = processor.process(product);

        verify(mockedService).group(product);
        assertSame(product, returnedProduct);
    }
}
