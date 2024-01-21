package com.csed.producerconsumer.Clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplayCareTaker implements MachineObserver{
    private final List<SimulationMemento> mementos = new ArrayList<>();
    private SimulationService service;
    private List<Long> times = new ArrayList<>();
    private List<Long> sleepTimes = new ArrayList<>();

    private final SimulationUpdateListener updateListener;

    @Autowired
    ReplayCareTaker(SimulationService service, SimulationUpdateListener updateListener) {
        this.service = service;
        this.updateListener = updateListener;
    }
    @Override
    public void update(Machine machine) {
        List<Queue> inputQs=machine.getInputQueues();
        List<Queue> outputQs=machine.getOutputQueues();
        for (Queue inputQ : inputQs)
            service.updateQueueById(inputQ.getId(), inputQ);
        for (Queue outputQ : outputQs)
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
            if (mementos.isEmpty()){
                sleepTimes.add(0L);
                times.add(System.currentTimeMillis());
            }
            else {
                long now = System.currentTimeMillis();
                sleepTimes.add(now - times.get(times.size() - 1));
                times.add(now);
            }
            mementos.add(service.takeSnapshot());
        }
    }
    public void replaySimulation() {
        int count = 0;
        for (SimulationMemento memento : mementos) {
            updateListener.updateSimulation(memento);
            System.out.println("Inside replay");
            try {
                Thread.sleep(sleepTimes.get(count)); 
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count++;
        }

    }
}
