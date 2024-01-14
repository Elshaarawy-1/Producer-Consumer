package com.csed.producerconsumer.Clients;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationService {
    private List<Queue> queues;
    private List<Machine> machines;
    private boolean isSimulationRunning;

    public SimulationService(List<Queue> queues, List<Machine> machines) {
        this.queues = queues;
        this.machines = machines;
        this.isSimulationRunning = false;
    }

    public void startSimulation() {
        if (!isSimulationRunning) {
            // Initialize and start threads for machines
            for (Machine machine : machines) {
                Thread machineThread=new Thread(machine);
                machineThread.start();
            }

            // Set the simulation state to running
            isSimulationRunning = true;
        }
    }

    public void stopSimulation() {
        if (isSimulationRunning) {
            // Stop threads for machines
            for (Machine machine : machines) {
                Thread machineThread=new Thread(machine);
                machineThread.interrupt();
            }

            // Set the simulation state to stopped
            isSimulationRunning = false;
        }
    }

}
