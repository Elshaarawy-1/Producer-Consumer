package com.csed.producerconsumer.Clients;

public class Demo {

    public static void main(String[] args) {
        SimulationService service = SimulationService.getInstance();
        for (int i = 0; i < 3; i++) {
            service.addQueue();
        }
        for (int i = 0; i < 3; i++){
            service.addMachine();
        }
        service.connect("Q0","M1");
        service.connect("Q0","M2");
        service.connect("M1","Q1");
        service.connect("M2","Q1");
        service.connect("Q1","M3");
        service.connect("M3","Q2");

        service.processInputProducts(5);
        service.startSimulation();
    }
}
