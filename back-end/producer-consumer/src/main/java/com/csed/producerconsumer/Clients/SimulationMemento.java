package com.csed.producerconsumer.Clients;

import java.util.List;

public class SimulationMemento {
    private List<QueueMemento> queueMementos;
    private List<MachineMemento> machineMementos;

    public SimulationMemento(List<QueueMemento> queueMementos, List<MachineMemento> machineMementos) {
        this.queueMementos = queueMementos;
        this.machineMementos = machineMementos;
    }

    public List<QueueMemento> getQueueMementos() {
        return queueMementos;
    }

    public List<MachineMemento> getMachineMementos() {
        return machineMementos;
    }
}
