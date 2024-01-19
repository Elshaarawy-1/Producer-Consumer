package com.csed.producerconsumer.Clients;

import java.util.ArrayList;
import java.util.List;

public class ReplayCareTaker implements MachineObserver{
    private final List<SimulationMemento> mementos = new ArrayList<>();
    private SimulationService service;

    ReplayCareTaker(SimulationService service){
        this.service=service;
    }
    @Override
    public void update(Machine machine) {
        Queue inputQ=machine.getInputQueue();
        Queue outputQ=machine.getOutputQueue();
        service.updateQueueById(inputQ.getId(), inputQ);
        service.updateQueueById(inputQ.getId(), outputQ);
        service.updateMachineById(machine.getId(),machine);
        synchronized (mementos) {
            saveSnapshot();
        }
    }
    public void saveSnapshot()
    {
        synchronized (mementos) {
            // Add the memento to the list
            mementos.add(service.takeSnapshot());
        }
    }
}
