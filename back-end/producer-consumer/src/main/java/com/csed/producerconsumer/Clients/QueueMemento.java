package com.csed.producerconsumer.Clients;

public class QueueMemento {
    private int id;
    private int numberOfProducts;

    public QueueMemento(int id, int numberOfProducts) {
        this.id = id;
        this.numberOfProducts = numberOfProducts;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }
}
