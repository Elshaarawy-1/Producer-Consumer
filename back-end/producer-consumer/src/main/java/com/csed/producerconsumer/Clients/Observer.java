package com.csed.producerconsumer.Clients;

public class Observer implements SimulationObserver{
    @Override
    public void updateSimulationState() {
        System.out.println("TEST");
    }
}
