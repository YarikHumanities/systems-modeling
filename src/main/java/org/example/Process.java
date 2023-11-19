package org.example;

import java.util.*;

public class Process extends Element {
    protected int maxqueue, failure;
    protected Queue <Item> queue;
    protected double meanQueue;
    protected final List<Channel> channels = new ArrayList<>();
    protected int workerQuant = 1;

    public static double clientsInClinicTime = 0;

    public static double intervalBetweenLabs = 0;

    public static double prevTimeLeave = 0;

    public ProcessTypes getProcessType() {
        return processType;
    }

    protected ProcessTypes processType;

    public Process(String nameOfElement, double delay, int quantOfWorkers, ProcessTypes processType) {

        super(nameOfElement, delay);
        this.processType = processType;
        this.workerQuant = quantOfWorkers;
        this.queue = new ArrayDeque<>();
        //so it won't be triggered at 1 iteration when tcurr = 0.0
        setTnext(Double.MAX_VALUE);
        maxqueue = Integer.MAX_VALUE;
        meanQueue = 0.0;

        for(int i=0; i<workerQuant; i++){
            Channel channel = new Channel(Double.MAX_VALUE, 0, i);
            channels.add(channel);
        }
    }
    protected void sortWorkersToSetTnext(){
        Collections.sort(channels, Comparator.comparing(Channel::getTnext));
    }
    @Override
    public void inAct(Item item) {
        var freeChannel = this.getFreeWorker();
        super.incrementQuantity();

        if(this.getProcessType()==ProcessTypes.TAKING_TEST) {
            Process.intervalBetweenLabs += super.getTcurr() - prevTimeLeave;
            prevTimeLeave = super.getTcurr();
        }

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
                printQueue();
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
        System.out.println(this.getName() + " finished its work with Item<" + itemOfEarliestChannel.getId() + "> (" + itemOfEarliestChannel.getType() + " type)");

        earliestChannel.setCurrentItem(null);
        earliestChannel.state = 0;
        earliestChannel.tnext = Double.MAX_VALUE;

        if (getQueue() > 0) {
            Item newItemFromQueue = this.queue.poll();
            var freeChannel = this.getFreeWorker();
            freeChannel.state = 1;
            freeChannel.setCurrentItem(newItemFromQueue);
            System.out.println("Queue in " + this.getName() + " was " + getQueue()+1 + " so it was polled and item<" + newItemFromQueue.getId() + "> sent to worker " + freeChannel.id);
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

        //if (!super.getNextElementsList().isEmpty()) {

            if(this.getProcessType()==ProcessTypes.DUTY) {
                System.out.print("We are in DUTY process ");
                if (itemOfEarliestChannel.getType() == 1) {
                    System.out.println(" - Item has 1 type");
                    var accompanyingIndex = findNextProcessByType(ProcessTypes.ACCOMPANYING);
                    super.getNextElementsList().get(accompanyingIndex).inAct(itemOfEarliestChannel);
                } else {
                    System.out.println(" - Item hasn't 1 type");
                    var labTransferIndex = findNextProcessByType(ProcessTypes.LAB_TRANSFER);
                    super.getNextElementsList().get(labTransferIndex).inAct(itemOfEarliestChannel);
                }
            }
            if (this.getProcessType() ==ProcessTypes.ACCOMPANYING) {
                itemOfEarliestChannel.setTimeOut(super.getTcurr());
                Process.clientsInClinicTime+=itemOfEarliestChannel.calcTimeDiapason();
                System.out.println("Item <" + itemOfEarliestChannel.getId() + "> with type (" + itemOfEarliestChannel.getType() + ") arrived ar the ward");
            }
            if(this.getProcessType() ==ProcessTypes.LAB_TRANSFER){
                System.out.println("We are in LAB_TRANSFER process so Item <" +  + itemOfEarliestChannel.getId() + "> with type (" + itemOfEarliestChannel.getType() + ") will be sent to REGISTRATION");
                var registrationProcessIndex = findNextProcessByType(ProcessTypes.REGISTRATION);
                super.getNextElementsList().get(registrationProcessIndex).inAct(itemOfEarliestChannel);
            }
            if(this.getProcessType() ==ProcessTypes.REGISTRATION) {
                System.out.println("We are in REGISTRATION process so Item <" +  + itemOfEarliestChannel.getId() + "> with type (" + itemOfEarliestChannel.getType() + ") will be sent to TESTS");
                var testsProcessIndex = findNextProcessByType(ProcessTypes.TAKING_TEST);
                super.getNextElementsList().get(testsProcessIndex).inAct(itemOfEarliestChannel);
            }
            if(this.getProcessType() ==ProcessTypes.TAKING_TEST) {
                if(itemOfEarliestChannel.getType()==3){
                    itemOfEarliestChannel.setTimeOut(super.getTcurr());
                    Process.clientsInClinicTime+=itemOfEarliestChannel.calcTimeDiapason();
                    System.out.println("We are in TAKING_TEST process so Item <" +  + itemOfEarliestChannel.getId() + "> with type (" + itemOfEarliestChannel.getType() + ") will be sent to EXIT");
                }
                else{
                    System.out.println("We are in TAKING_TEST process so Item <" +  + itemOfEarliestChannel.getId() + "> with type (" + itemOfEarliestChannel.getType() + ") will be sent BACK");
                    if(itemOfEarliestChannel.getType()!=1){
                        System.out.println("And it's Type will be changed to (1)");
                        itemOfEarliestChannel.setType(1);
                    }
                    var backTransferProcessIndex = findNextProcessByType(ProcessTypes.BACK_TRANSFER);
                    super.getNextElementsList().get(backTransferProcessIndex).inAct(itemOfEarliestChannel);
                }
            }
            if (this.getProcessType() == ProcessTypes.BACK_TRANSFER) {
                System.out.println("We are in BACK_TRANSFER process so Item <" +  + itemOfEarliestChannel.getId() + "> will be sent BACK TO DUTY");
                var backedToDutyIndex = findNextProcessByType(ProcessTypes.DUTY);
                super.getNextElementsList().get(backedToDutyIndex).inAct(itemOfEarliestChannel);
            }
        //}

    }
    protected int findNextProcessByType(ProcessTypes processType){
        //МАЄТЬСЯ НА УВАЗІ ЩО ПРОЦЕСІВ З 2 ОДНАКОВИМИ ТИПАМИ НЕ МОЖЕ ІСНУВАТИ
        int searchedIndex = 0;
        for (int i = 0; i<super.getNextElementsList().size(); i++){
            if (super.getNextElementsList().get(i) instanceof Process) {
                Process p = (Process) super.getNextElementsList().get(i);
                if(p.getProcessType()==processType){
                    searchedIndex = i;
                }
            }
        }
        return searchedIndex;
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
            System.out.print("Item <" + item.getId() + "> (" + item.getType() + ") |");
        }
        System.out.println("]");
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
