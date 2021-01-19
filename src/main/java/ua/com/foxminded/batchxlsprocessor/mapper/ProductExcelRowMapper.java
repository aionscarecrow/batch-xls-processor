package ua.com.foxminded.batchxlsprocessor.mapper;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.exception.MissingDataException;

public class ProductExcelRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(RowSet rowSet) {
        Product product = new Product();
        String name = rowSet.getColumnValue(0);
        if (name.isEmpty()) {
            throw new MissingDataException("Name is missing.");
        } else {
            product.setName(name);
        }
        String quantityString = rowSet.getColumnValue(1);
        if (quantityString.isEmpty()) {
            throw new MissingDataException("Quantity is missing.");
        } else {
            product.setQuantity(Double.parseDouble(quantityString));
        }
        return product;
    }
}
