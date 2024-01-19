package com.csed.producerconsumer.Clients;

import java.util.Random;

public class Machine implements Runnable {
    private int id;
    private Queue inputQueue;
    private Queue outputQueue;
    private String defaultColor = "#B96E37";
    private String currentColor;

    private boolean isReady = true;
    private long machineServiceTime;

    public Machine(int id) {
        this.id = id;
        this.currentColor=this.defaultColor;
        // Example: Random processing time between 500 and 1500 milliseconds
        this.machineServiceTime=new Random().nextInt(1000) + 500;
    }

    public void setInputQueue(Queue inputQueue) {
        this.inputQueue = inputQueue;
    }

    public void setOutputQueue(Queue outputQueue) {
        this.outputQueue = outputQueue;
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
                product = inputQueue.dequeue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Simulate processing time
            try {
                Thread.sleep(machineServiceTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Change machine color to product color
            this.currentColor = product.getColor();

            // Notify the UI or other components about the simulation state
            printMachineState();
            // You may use observers or other mechanisms for this purpose
            // notifySimulationUpdate();

            // Produce the finished product by adding it to the output queue
            outputQueue.enqueue(product);

            // Reset machine color to default
            this.currentColor = defaultColor;

            // Machine is ready for the next product
            isReady = true;

            synchronized (this) {
                notify(); // Notify waiting threads that the machine is ready
            }

            // Notify the UI or other components about the simulation state
            printMachineState();
            // You may use observers or other mechanisms for this purpose
            // notifySimulationUpdate()
        }
    }
    private void printMachineState() {
        System.out.println(Thread.currentThread());
        System.out.println("Machine " + id + " color: " + currentColor);
        System.out.println("Machine " + id + " - Input Queue State:");
        inputQueue.printQueueState();
        System.out.println("Machine " + id + " - Output Queue State:");
        outputQueue.printQueueState();
        System.out.println("============================");
    }

    public Queue getInputQueue() {
        return inputQueue;
    }

    public Queue getOutputQueue() {
        return outputQueue;
    }
}
