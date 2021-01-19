package ua.com.foxminded.batchxlsprocessor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.excel.ExcelFileParseException;
import org.springframework.stereotype.Component;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

@Component
public class CustomSkipListener implements SkipListener<Product, Product> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CustomSkipListener.class);

    @Override
    public void onSkipInRead(Throwable throwable) {
        if (throwable instanceof ExcelFileParseException) {
            ExcelFileParseException exception = (ExcelFileParseException) throwable;
            String filename = exception.getFilename();
            int rowNumber = exception.getRowNumber();
            LOGGER.error("{} Filename: {}. Row number: {}", throwable.getCause().getMessage(), filename, rowNumber + 1);
        }
    }

    @Override
    public void onSkipInWrite(Product o, Throwable throwable) {

    }

    @Override
    public void onSkipInProcess(Product o, Throwable throwable) {
        LOGGER.error(throwable.getMessage());
    }
}
