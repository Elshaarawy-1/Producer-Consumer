package com.csed.producerconsumer.Clients;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*" )
@RequestMapping("/")
public class SimulationController {

    /** POST Request
     * End point to confirm Add Queue
     * @return the ID of the Queue added
     */
    @GetMapping("/add/queue")
    public int addQueue(){
        SimulationService service = SimulationService.getInstance();
        return service.addQueue();
    }

    /** POST Request
     * End point to confirm add Machine
     * @return the ID of the Machine added
     */
    @GetMapping("/add/machine")
    public int addMachine(){
        SimulationService service = SimulationService.getInstance();
        return service.addMachine();
    }


    /** POST Request
     * Connect machine and queue
     * @param connection: Array of Strings like "M2" form, in request body
     */
    @PostMapping("/connect")
    public void connectMAndQ(@RequestBody ConnectRequest connection) {
        SimulationService service = SimulationService.getInstance();
        service.connect(connection.getSource(), connection.getDestination());
    }

    /**
     * POST Request
     * End point to start simulation
     * @param numberOfProducts : integer of number of products sent in request body
     */
    @PostMapping("/start")
    public void startSimulation(@RequestBody int numberOfProducts){
        SimulationService service = SimulationService.getInstance();
        service.processInputProducts(numberOfProducts);
        service.startSimulation();
    }
    @GetMapping("/replay")
    public void replaySimulation(){
        SimulationService service = SimulationService.getInstance();
        System.out.println("Inside Controller");
        service.replaySimulation();
    }
    @GetMapping("/clear")
    public void clearSimulation(){
        SimulationService service = SimulationService.getInstance();
        service.resetService();
    }
}