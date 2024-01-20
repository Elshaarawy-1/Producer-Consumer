package com.csed.producerconsumer.Clients;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Machine implements Runnable {
    private int id;
    private List<Queue> inputQueues;
    private List<Queue> outputQueues;
    private String defaultColor = "#864AF9";
    private String currentColor;

    private boolean isReady = true;
    private long machineServiceTime;
    private List<MachineObserver> observers = new ArrayList<>();

    public Machine(int id) {
        this.id = id;
        this.currentColor=this.defaultColor;
        this.inputQueues = new ArrayList<>();
        this.outputQueues = new ArrayList<>();
        // Random processing time between 3500 and 8500 milliseconds
        this.machineServiceTime=new Random().nextInt(3000) + 5500;
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

            // Consume a product from the input queue
            Product product = null;
            try {
                int randomInputQueueIndex = new Random().nextInt(inputQueues.size());
                product = inputQueues.get(randomInputQueueIndex).dequeue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Change machine color to product color
            this.currentColor = product.getColor();
            synchronized (this) {
                notifyObservers();
            }
            //printMachineState();

            // Simulate processing time
            try {
                Thread.sleep(machineServiceTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Produce the finished product by adding it to the output queue
            int randomOutputQueueIndex = new Random().nextInt(outputQueues.size());
            outputQueues.get(randomOutputQueueIndex).enqueue(product);

            // Reset machine color to default
            this.currentColor = defaultColor;

            // Machine is ready for the next product
            isReady = true;

            synchronized (this) {
                notifyObservers();
                notify(); // Notify waiting threads that the machine is ready
            }

            // Notify the UI or other components about the simulation state
            //printMachineState();
            // You may use observers or other mechanisms for this purpose
            // notifySimulationUpdate()
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
