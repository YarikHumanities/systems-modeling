package org.example;

import java.util.*;

public class Process extends Element {
    private int queue, maxqueue, failure;
    private double meanQueue;
    private final List<Channel> channels = new ArrayList<>();
    private final int workerQuant = 1;

    private boolean firstIteration = true;
    public void setBusyInStart() throws Exception {
        //TODO: set tnext for primary busy worker so it can reach outAct
        if(this.channels.size()==1) {

            this.channels.get(0).state = 1;

            var superDelay = super.getDelay();
            var delay = super.getTcurr() + superDelay;

            this.channels.get(0).tnext = delay;
            super.setTnext(getEarliestChannel().tnext);

            System.out.println(this.getName() + " will be set to busy at start and its tnext = " + super.getTnext());

            super.setDelayMean(0.3);
            super.setDistribution("exp");

            System.out.println("And now it has exp. distribution with mean = " + super.getDelayMean());

        }
        else{
            throw new Exception("Can't set BUSY state for Process if it has more than 1 Worker");
        }
    }
    public Process(String nameOfElement, double delay, int probability, int priority, boolean chooseByProbability) {

        super(nameOfElement, delay, chooseByProbability);

        //so it won't be triggered at 1 iteration when tcurr = 0.0
        setTnext(Double.MAX_VALUE);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;

        super.probability = probability;

        super.priority = priority;

        for(int i=0; i<workerQuant; i++){
            Channel channel = new Channel(Double.MAX_VALUE, 0, i);
            channels.add(channel);
        }
    }

    private void sortWorkersToSetTnext(){
        Collections.sort(channels, Comparator.comparing(Channel::getTnext));
    }
    @Override
    public void inAct() {
        var freeChannel = this.getFreeWorker();
        super.incrementQuantity();
        if(freeChannel!=null){
            System.out.println(this.getName() + " and its worker " + freeChannel.id + " inActed");
            freeChannel.state = 1;
            var superDelay = super.getDelay();
            var delay = super.getTcurr() + superDelay;
            freeChannel.tnext = delay;
            totalWorkTime += superDelay;
            this.sortWorkersToSetTnext();
            System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
            super.setTnext(getEarliestChannel().tnext);
        }
        else{
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
                System.out.println(this.getName() + " inActed and will get +1 to queue");
            } else {
                failure++;
            }
        }

    }
    @Override
    public void outAct() throws Exception {
        super.outAct();
        super.setTnext(Double.MAX_VALUE);

        if(this.getBusyWorker()==null ){
            throw new Exception("outAct cannot be called when there is no busy workers");
        }

        if(this.getTcurr()!=channels.get(0).tnext){
            throw new Exception("tcurr of process cannot be different from tnext of youngest channel");
        }

        var earliestChannel = getEarliestChannel();

        if(earliestChannel.state!=1){
            throw new Exception("Earliest Channel is Free in outAct");
        }

        earliestChannel.state = 0;
        earliestChannel.tnext = Double.MAX_VALUE;

        if (getQueue() > 0) {
            setQueue(getQueue() - 1);
            var freeChannel = this.getFreeWorker();
            freeChannel.state = 1;
            var superDelay = super.getDelay();
            var delay = super.getTcurr() + superDelay;
            freeChannel.tnext = delay;
            totalWorkTime += superDelay;

            this.sortWorkersToSetTnext();
            System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
            super.setTnext(getEarliestChannel().tnext);
        }
        else if (this.getBusyWorker()!=null){
            this.sortWorkersToSetTnext();
            System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
            super.setTnext(getEarliestChannel().tnext);
        }

        if (!super.getNextElementsList().isEmpty()) {

            if(super.isChooseByProbability()) {
                var nextElement = super.chooseNextElement();
                super.getNextElementsList().get(nextElement).inAct();
            }
            else{
                var maxPriorElement = super.findIndexOfMaxPriorityElement();
                super.getNextElementsList().get(maxPriorElement).inAct();
            }
        }

    }

    public Channel getEarliestChannel(){
        return this.channels.get(0);
    }
    public Channel getBusyWorker(){
        for(int i = 0; i<channels.size(); i++){
            if(channels.get(i).state==1){
                return channels.get(i);
            }
        }
        return null;
    }
    public Channel getFreeWorker(){
        for(int i = 0; i<channels.size(); i++){
            if(channels.get(i).state==0){
                return channels.get(i);
            }
        }
        return null;
    }

    public int getFailure() {
        return failure;
    }
    public int getQueue() {
        return queue;
    }

    public void decrementQueue(){
        --this.queue;
    }

    public void incrementQueue(){
        ++this.queue;
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
