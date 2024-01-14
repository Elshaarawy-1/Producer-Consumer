package com.csed.producerconsumer.Clients;

public class Product {
    private String id;
    private String color;
    private long processingTime;  // in milliseconds

    public Product(String id, String color, long processingTime) {
        this.id = id;
        this.color = color;
        this.processingTime = processingTime;
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public long getProcessingTime() {
        return processingTime;
    }
}
