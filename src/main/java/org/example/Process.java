package org.example;

import java.util.*;

public class Process extends Element {
    private int queue, maxqueue, failure;
    private double meanQueue;
    private List<Channel> channels = new ArrayList<>();
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

        for(int i=0; i<1; i++){
            Channel channel = new Channel(Double.MAX_VALUE, 0, i);
            channels.add(channel);
        }
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
            Collections.sort(channels, Comparator.comparing(Channel::getTnext));
            System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
            super.setTnext(channels.get(0).tnext);
        }
        else{
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
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

            Collections.sort(channels, Comparator.comparing(Channel::getTnext));
            System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
            super.setTnext(channels.get(0).tnext);

            if(this.getBusyWorker()!=null){
                Collections.sort(channels, Comparator.comparing(Channel::getTnext));
                System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
                super.setTnext(channels.get(0).tnext);
            }
        }

        if(this.getBusyWorker()!=null){
            Collections.sort(channels, Comparator.comparing(Channel::getTnext));
            System.out.println("Tnext of " + this.getName() + " will be set to tnext of worker " + channels.get(0).id + " and it's " + channels.get(0).tnext);
            super.setTnext(channels.get(0).tnext);
        }

        if (!super.getNextElementsList().isEmpty()) {
            var nextElementsQuant = super.getNextElementsList().size();
            Random random = new Random();
            var randomElementFromList = random.nextInt(nextElementsQuant);
            super.getNextElementsList().get(randomElementFromList).inAct();
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
