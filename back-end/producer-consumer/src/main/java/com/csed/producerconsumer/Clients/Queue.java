package com.csed.producerconsumer.Clients;

import java.util.LinkedList;

public class Queue {
    private int id;
    private LinkedList<Product> products;

    public Queue(int id) {
        this.id = id;
        this.products = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setProducts(LinkedList<Product> products) {
        this.products = products;
    }

    public synchronized void enqueue(Product product) {
        products.addLast(product);
        // Notify waiting threads (e.g., machines waiting for products)
        notifyAll();
    }

    public synchronized Product dequeue() throws InterruptedException {
        while (products.isEmpty()) {
            // Wait for products to be added to the queue
            wait();
        }
        return products.removeFirst();
    }

    // Additional method to get the current number of products in the queue
    public synchronized int getCurrentNumberOfProducts() {
        return products.size();
    }
}
