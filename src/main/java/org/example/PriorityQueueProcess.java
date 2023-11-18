package org.example;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueueProcess extends Process{

    public PriorityQueueProcess(String nameOfElement, double delay, int quantOfWorkers, ProcessTypes processTypes) {
        super(nameOfElement, delay, quantOfWorkers, processTypes);
        //1 ТИП - ПРІОРІТЕТНИЙ
        Comparator<Item> typeComparator = Comparator.comparing(Item::getType);
        this.queue = new PriorityQueue<>(typeComparator);
    }

    @Override
    public void inAct(Item item) {
        var freeChannel = this.getFreeWorker();
        super.incrementQuantity();
        if(freeChannel!=null){
            System.out.println(this.getName() + " and its worker " + freeChannel.id + " inActed and took item <" + item.getId() + ">");
            freeChannel.state = 1;
            freeChannel.setCurrentItem(item);

            //РІЗНИЙ ЧАС РЕЄСТРАЦІЇ В ЗАЛЕЖНОСТІ ВІД ТИПУ ХВОРОГО
            if(item.getType()==1){
                this.setDelayMean(15.0);
            }
            else if(item.getType()==2){
                this.setDelayMean(40.0);
            }
            else{
                this.setDelayMean(30.0);
            }
            System.out.println("Item <" + item.getId() + "> in " + this.getName() + " has type " + item.getType() + " so process have exp:" + this.getDelayMean());
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

}
