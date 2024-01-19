package com.csed.producerconsumer.Clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplayCareTaker implements MachineObserver{
    private final List<SimulationMemento> mementos = new ArrayList<>();
    private SimulationService service;
    private final WebSocketController webSocketController;

    @Autowired
    ReplayCareTaker(SimulationService service, WebSocketController webSocketController){
        this.service=service;
        this.webSocketController=webSocketController;
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
        SimulationMemento memento = service.takeSnapshot();
        webSocketController.updateSimulation(memento);
    }
    public void saveSnapshot()
    {
        synchronized (mementos) {
            // Add the memento to the list
            mementos.add(service.takeSnapshot());
        }
    }
}
