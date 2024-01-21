package com.csed.producerconsumer.Clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Machine implements Runnable {
    private int id;
    private List<Queue> inputQueues;
    private List<Queue> outputQueues;
    private final String defaultColor = "#808080";
    private String currentColor;
    private final Random random = new Random();

    private boolean isReady = true;
    private final long machineServiceTime;
    private List<MachineObserver> observers = new ArrayList<>();

    public Machine(int id) {
        this.id = id;
        this.currentColor=this.defaultColor;
        this.inputQueues = new ArrayList<>();
        this.outputQueues = new ArrayList<>();
        // Random processing time between 3500 and 8500 milliseconds
        this.machineServiceTime=random.nextInt(3000) + 5500;
        System.out.println("Machine " + this.id + " Service time " + this.machineServiceTime);
    }

    public void setInputQueues(List<Queue> inputQueues) {
        this.inputQueues = inputQueues;
    }

    public void setOutputQueues(List<Queue> outputQueues) {
        this.outputQueues = outputQueues;
    }
    public void addInputQueue(Queue inputQueue) {
        this.inputQueues.add(inputQueue);
    }

    public void addOutputQueue(Queue outputQueue) {
        this.outputQueues.add(outputQueue);
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                while (!isReady) {
                    try {
                        wait(); // Wait until the machine is ready
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return; // Exit the thread if interrupted
                    }
                }
            }
            // Get a list of non-empty input queues
            List<Queue> nonEmptyInputQueues = inputQueues.stream()
                    .filter(queue -> !(queue.getCurrentNumberOfProducts() == 0))
                    .toList();

            if (nonEmptyInputQueues.isEmpty()) {
                // No non-empty input queues available, wait for a while and then continue the loop
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Consume a product from a randomly selected non-empty input queue
            Product product = null;
            try {
                int randomInputQueueIndex = new Random().nextInt(nonEmptyInputQueues.size());
                product = nonEmptyInputQueues.get(randomInputQueueIndex).dequeue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (this) {
                // Change machine color to product color
                this.currentColor = product.getColor();
                notifyObservers();
            }

            // Simulate processing time
            try {
                Thread.sleep(machineServiceTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Produce the finished product by adding it to the output queue
            int randomOutputQueueIndex = new Random().nextInt(outputQueues.size());
            outputQueues.get(randomOutputQueueIndex).enqueue(product);

            synchronized (this) {
                // Reset machine color to default
                this.currentColor = defaultColor;

                // Machine is ready for the next product
                isReady = true;

                notifyObservers();
                notify(); // Notify waiting threads that the machine is ready
            }
        }
    }
    // Method to add observers
    public void addObserver(MachineObserver observer) {
        observers.add(observer);
    }
    // Method to notify observers
    private void notifyObservers() {
        for (MachineObserver observer : observers) {
            observer.update(this);
        }
    }

    public List<Queue> getInputQueues() {
        return inputQueues;
    }

    public List<Queue> getOutputQueues() {
        return outputQueues;
    }

    public int getId() {
        return id;
    }

    public String getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(String currentColor) {
        this.currentColor = currentColor;
    }

    public void setId(int id) {
        this.id = id;
    }
}
