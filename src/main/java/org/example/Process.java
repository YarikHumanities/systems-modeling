package org.example;

import java.util.*;

public class Process extends Element {
    private int maxqueue, failure;
    private Deque<Item> queue = new ArrayDeque<>();
    private double meanQueue;
    private final List<Channel> channels = new ArrayList<>();
    private final int workerQuant = 1;

    public double averageClientTimeInBank = 0;

    public double averageTimeBetweenLeave = 0;

    public double prevLeaveTime = 0;

    public void setBusyInStart() throws Exception {
        if(this.channels.size()==1) {
            Item item = new Item();
            this.channels.get(0).state = 1;
            this.channels.get(0).setCurrentItem(item);

            var superDelay = super.getDelay();
            var delay = super.getTcurr() + superDelay;

            this.channels.get(0).tnext = delay;
            super.setTnext(getEarliestChannel().tnext);

            System.out.println(this.getName() + " will be set to busy at start and its tnext = " + super.getTnext());

            super.setDelayMean(0.3);
            super.setDistribution("exp");

            this.queue.offer(new Item(super.getTcurr()));
            this.queue.offer(new Item(super.getTcurr()));


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
    public void inAct(Item item) {
        var freeChannel = this.getFreeWorker();
        super.incrementQuantity();
        if(freeChannel!=null){
            System.out.println(this.getName() + " and its worker " + freeChannel.id + " inActed and took item <" + item.getId() + ">");
            freeChannel.state = 1;
            freeChannel.setCurrentItem(item);
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
                setQueue(item);
                System.out.println(this.getName() + " was not free and will get +1 to queue with item <" + item.getId() + ">");
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

        var itemOfEarliestChannel = earliestChannel.getCurrentItem();
        System.out.println(this.getName() + " finished its work with Item<" + itemOfEarliestChannel.getId() + ">");
        itemOfEarliestChannel.setTimeOut(this.getTcurr());

        this.averageClientTimeInBank += itemOfEarliestChannel.calcTimeInBank();
        this.averageTimeBetweenLeave += (this.getTcurr() - this.prevLeaveTime);
        this.prevLeaveTime = this.getTcurr();

        earliestChannel.setCurrentItem(null);
        earliestChannel.state = 0;
        earliestChannel.tnext = Double.MAX_VALUE;

        if (getQueue() > 0) {
            Item newItemFromQueue = this.queue.poll();
            var freeChannel = this.getFreeWorker();
            freeChannel.state = 1;
            freeChannel.setCurrentItem(newItemFromQueue);
            System.out.println("Queue in " + this.getName() + " was " + getQueue() + " so it was polled and item<" + newItemFromQueue.getId() + "> sent to worker " + freeChannel.id);
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
                super.getNextElementsList().get(nextElement).inAct(itemOfEarliestChannel);
            }
            else{
                var maxPriorElement = super.findIndexOfMaxPriorityElement();
                super.getNextElementsList().get(maxPriorElement).inAct(itemOfEarliestChannel);
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
    @Override
    public void printResult(){
        System.out.println(getName()+ " quantity = " + (super.quantity - this.failure));
    }

    public int getFailure() {
        return failure;
    }
    public int getQueue() {
        return this.queue.size();
    }
    public void printQueue(){
        System.out.print("<-[");
        for(Item item: this.queue){
            System.out.print("Item <" + item.getId() + ">, ");
        }
        System.out.println("]");
    }
    public Item peekLastItem(){
        return this.queue.pollLast();
    }
    public void setQueue(Item item) {
        this.queue.offer(item);
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
        meanQueue = getMeanQueue() + queue.size() * delta;
    }
    public double getMeanQueue() {
        return meanQueue;
    }

}
