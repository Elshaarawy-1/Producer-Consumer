package com.csed.producerconsumer.Clients;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/update")
    @SendTo("/topic/simulation")
    public SimulationMemento updateSimulation(SimulationMemento memento) {
        return memento;
    }
}
