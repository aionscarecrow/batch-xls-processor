package ua.com.foxminded.batchxlsprocessor.domain;

import javax.validation.constraints.Positive;

public class Product {

    private String name;
    @Positive
    private double quantity;

    public Product() {}

    public Product(String name, double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
