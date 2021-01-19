package ua.com.foxminded.batchxlsprocessor.mapper;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import ua.com.foxminded.batchxlsprocessor.domain.Product;

public class ProductExcelRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(RowSet rowSet) {
        Product product = new Product();
        product.setName(rowSet.getColumnValue(0));
        product.setQuantity(Double.parseDouble(rowSet.getColumnValue(1)));
        return product;
    }
}
