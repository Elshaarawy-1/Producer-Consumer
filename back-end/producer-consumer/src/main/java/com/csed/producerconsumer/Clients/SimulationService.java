package com.csed.producerconsumer.Clients;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SimulationService {
    private HashMap<Integer,Queue> queues = new HashMap<>();

    private HashMap<Integer,Machine> machines = new HashMap<>();
    private boolean isSimulationRunning;
    int machineId = 1;
    int queueId=0;

    public void processInputProducts(int numberOfProducts){
        Queue q0 = queues.getOrDefault(0, null);
        if (q0 == null) {
            System.out.println("No input queue!");
            return;
        }
        Set<String> colorSet = new HashSet<>();
        Random random = new Random();

        while (colorSet.size() < numberOfProducts) {
            // Generate a random RGB color
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);

            // Convert RGB to hexadecimal
            String hexColor = String.format("#%02X%02X%02X", r, g, b);

            // Add the color to the set (ensuring uniqueness)
            colorSet.add(hexColor);
        }
        String[] colors=colorSet.toArray(new String[0]);
        LinkedList<Product> products= new LinkedList<>();
        for (String color : colors) {
            products.add(new Product(color));
        }
        q0.setProducts(products);
        // update hashmap
        queues.put(0,q0);
        // Iterate over machines and update their input/output queues if connected to old Q0
        for (Map.Entry<Integer, Machine> machineEntry : machines.entrySet()) {
            Machine machine = machineEntry.getValue();

            // Check if the machine is connected to the oldQ0
            if (machine.getInputQueue().getId() == 0) {
                machine.setInputQueue(q0);
            }

            if (machine.getOutputQueue().getId() == 0) {
                machine.setOutputQueue(q0);
            }
        }
    }
    public int addQueue(){
        queues.put(queueId,new Queue(queueId));
        return queueId++;
    }

    public int addMachine(){
        machines.put(machineId,new Machine(machineId));
        return machineId++;
    }
    public void startSimulation() {
        if (!isSimulationRunning) {
            // Initialize and start threads for machines
            for (Map.Entry<Integer, Machine> machineEntry : machines.entrySet()) {
                Thread machineThread=new Thread(machineEntry.getValue());
                machineThread.start();
            }
            // Set the simulation state to running
            isSimulationRunning = true;
        }
    }

    public void stopSimulation() {
        if (isSimulationRunning) {
            // Stop threads for machines
            for (Map.Entry<Integer, Machine> machineEntry : machines.entrySet()) {
                Thread machineThread=new Thread(machineEntry.getValue());
                machineThread.interrupt();
            }

            // Set the simulation state to stopped
            isSimulationRunning = false;
        }
    }

    public void connect(String source, String destination) {
        if (source.charAt(0) == 'M') {
            connectMachineToQueue(Integer.parseInt(source.substring(1)), Integer.parseInt(destination.substring(1)));
        } else if (destination.charAt(0) == 'M') {
            connectQueueToMachine(Integer.parseInt(source.substring(1)), Integer.parseInt(destination.substring(1)));
        } else {
            System.out.println("Error connecting machine and queue!");
        }
    }

    // Machine is the source
    private void connectMachineToQueue(int machineId, int queueId) {
        Machine machine = machines.getOrDefault(machineId,null);
        Queue outputQueue = queues.getOrDefault(queueId,null);

        if (machine == null || outputQueue == null) {
            System.out.println("Error connecting machine and queue!");
            return;
        }

        machine.setOutputQueue(outputQueue);
        machines.put(machineId,machine);
    }

    // Queue is the source
    private void connectQueueToMachine(int queueId, int machineId) {
        Machine machine = machines.getOrDefault(machineId,null);
        Queue inputQueue = queues.getOrDefault(queueId,null);

        if (machine == null || inputQueue == null) {
            System.out.println("Error connecting machine and queue!");
            return;
        }

        machine.setInputQueue(inputQueue);
        machines.put(machineId,machine);
    }

}
