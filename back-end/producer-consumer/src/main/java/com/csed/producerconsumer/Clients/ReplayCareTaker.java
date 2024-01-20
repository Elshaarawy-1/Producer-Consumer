package com.csed.producerconsumer.Clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplayCareTaker implements MachineObserver{
    private final List<SimulationMemento> mementos = new ArrayList<>();
    private SimulationService service;
    private final SimulationUpdateListener updateListener;

    @Autowired
    ReplayCareTaker(SimulationService service, SimulationUpdateListener updateListener) {
        this.service = service;
        this.updateListener = updateListener;
    }
    @Override
    public void update(Machine machine) {
        Queue inputQ=machine.getInputQueue();
        Queue outputQ=machine.getOutputQueue();
        service.updateQueueById(inputQ.getId(), inputQ);
        service.updateQueueById(outputQ.getId(), outputQ);
        service.updateMachineById(machine.getId(),machine);
        saveSnapshot();
        SimulationMemento memento = service.takeSnapshot();
        System.out.println("Calling updateSimulation");
        updateListener.updateSimulation(memento);
    }
    public void saveSnapshot()
    {
        synchronized (mementos) {
            // Add the memento to the list
            mementos.add(service.takeSnapshot());
        }
    }
}
