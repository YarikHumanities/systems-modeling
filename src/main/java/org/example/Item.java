package org.example;

import lombok.Data;

@Data
public class Item {
    private int id;

    public double getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(double timeIn) {
        this.timeIn = timeIn;
    }

    private double timeIn;

    public double getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(double timeOut) {
        this.timeOut = timeOut;
    }

    private double timeOut;
    private static int nextId=0;

    public Item(){
        id=nextId;
        nextId++;
    }

    public Item(double timeIt){
        this.timeIn = timeIt;
        id=nextId;
        nextId++;
    }
}
