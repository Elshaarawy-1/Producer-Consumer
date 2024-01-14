package com.csed.producerconsumer.Clients;

public class Machine implements Runnable {
    private String id;
    private Queue inputQueue;
    private Queue outputQueue;
    private String defaultColor;
    private boolean isReady = true;
    private SimulationObserver observer;

    public Machine(String id, Queue inputQueue, Queue outputQueue, String defaultColor, SimulationObserver observer) {
        this.id = id;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.defaultColor = defaultColor;
        this.observer = observer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Consume a product from the input queue
                Product product = inputQueue.dequeue();

                // Simulate processing time
                Thread.sleep(product.getProcessingTime());

                // Change machine color to product color
                this.defaultColor = product.getColor();

                // Simulate processing completion
                Thread.sleep(100);

                // Produce the finished product by adding it to the output queue
                outputQueue.enqueue(product);

                // Reset machine color to default
                this.defaultColor = defaultColor;

                // Machine is ready for the next product
                isReady = true;

                // Notify the UI or other components about the simulation state
                printMachineState();
                // You may use observers or other mechanisms for this purpose
                notifySimulationUpdate();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
    private void printMachineState() {
        System.out.println("Machine " + id + " - Input Queue State:");
        inputQueue.printQueueState();
        System.out.println("Machine " + id + " - Output Queue State:");
        outputQueue.printQueueState();
    }

    private void notifySimulationUpdate() {
        if (observer != null) {
            observer.updateSimulationState();
        }
    }
}
