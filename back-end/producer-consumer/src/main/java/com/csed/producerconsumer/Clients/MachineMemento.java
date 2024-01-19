package com.csed.producerconsumer.Clients;

public class MachineMemento {
    private int id;
    private String color;

    public MachineMemento(int id, String color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
