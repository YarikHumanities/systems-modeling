package org.example;

public class Process extends Element {
    private int queue, maxqueue, failure;
    private double meanQueue;
    public Process(double delay) {
        super(delay);

        //so it won't be triggered at 1 iteration when tcurr = 0.0
        setTnext(Double.MAX_VALUE);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
    }
    public Process(String nameOfElement, double delay) {
        super(nameOfElement, delay);

        //so it won't be triggered at 1 iteration when tcurr = 0.0
        setTnext(Double.MAX_VALUE);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;
    }
    @Override
    public void inAct() {
        if (super.getState() == 0) {
            super.setState(1);
            var delay = super.getTcurr() + super.getDelay();
            super.setTnext(delay);
            totalWorkTime += delay;
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }
    }
    @Override
    public void outAct() {

        super.outAct();
        super.setTnext(Double.MAX_VALUE);
        super.setState(0);

        if (getQueue() > 0) {
            setQueue(getQueue() - 1);
            super.setState(1);
            var delay = super.getTcurr() + super.getDelay();
            super.setTnext(delay);
            totalWorkTime += delay;
        }
        if(super.getNextElement() != null) {
            super.getNextElement().inAct();
        }
    }

    public int getFailure() {
        return failure;
    }
    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }
    public int getMaxqueue() {
        return maxqueue;
    }
    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailure());
        System.out.println("Queue = " + this.getQueue());
    }
    @Override
    public void doStatistics(double delta) {
        meanQueue = getMeanQueue() + queue * delta;
    }
    public double getMeanQueue() {
        return meanQueue;
    }

}
