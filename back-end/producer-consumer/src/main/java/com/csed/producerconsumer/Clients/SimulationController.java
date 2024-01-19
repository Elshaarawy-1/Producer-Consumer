package com.csed.producerconsumer.Clients;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*" )
@RequestMapping("/api")
public class SimulationController {

    /** POST Request
     * End point to confirm Add Queue
     * @return the ID of the Queue added
     */
    @PostMapping("/add/queue")
    public int addQueue(){
        SimulationService service = SimulationService.getInstance();
        return service.addQueue();
    }

    /** POST Request
     * End point to confirm add Machine
     * @return the ID of the Machine added
     */
    @PostMapping("/add/machine")
    public int addMachine(){
        SimulationService service = SimulationService.getInstance();
        return service.addMachine();
    }


    /** POST Request
     * Connect machine and queue
     * @param from String like "M2" form, in request body
     * @param to String like "Q3" form, in request body
     */
    @PostMapping("/connect")
    public void connectMAndQ(@RequestBody String from, @RequestBody String to) {
        SimulationService service = SimulationService.getInstance();
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
    @PostMapping("/replay")
    public void replaySimulation(){
        SimulationService service = SimulationService.getInstance();
        // TODO: Replay functionality
    }
    @PostMapping("/clear")
    public void clearSimulation(){
        SimulationService service = SimulationService.getInstance();
        service.resetService();
    }
}