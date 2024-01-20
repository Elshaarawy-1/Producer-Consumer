package com.csed.producerconsumer.Clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController implements SimulationUpdateListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void updateSimulation(SimulationMemento memento) {
        System.out.println("Inside updateSimulation WS");
        messagingTemplate.convertAndSend("/topic/ws/simulation", memento);
    }
}
