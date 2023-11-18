package org.example;

import java.awt.desktop.SystemEventListener;
import java.util.Random;

public class Create extends Element {
    public Create(double delay) {
        super(delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create - в завданні про банк з 1.0
    }
    public Create(String nameOfElement, double delay) {
        super(nameOfElement, delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create
    }

    public int generateItemType(){
        Random random = new Random();
        double probability = random.nextDouble();
        if(probability<=0.5){
            return 1;
        } else if (probability>0.5 && probability<=0.6) {
            return 2;
        }
        else if(probability>0.6 && probability<=1.0){
            return 3;
        }
        return 1;
    }
    @Override
    public void outAct() throws Exception {
        super.incrementQuantity();
        var superDelay = super.getDelay();
        var delay = super.getTcurr() + superDelay;
        super.setTnext(delay);
        totalWorkTime += superDelay;

        Item item = new Item();
        item.setTimeIn(super.getTcurr());
        item.setType(this.generateItemType());

        System.out.println("Item <" + item.getId() + "> was created at " + super.getTcurr() + " with type " + item.getType());

        super.getNextElementsList().get(0).inAct(item);
//        if(super.isChooseByProbability()) {
//            var nextElement = super.chooseNextElement();
//            super.getNextElementsList().get(nextElement).inAct(item);
//        }
//        else{
//            if(checkQueues()) {
//                var maxPriorElement = super.findIndexOfMaxPriorityElement();
//                System.out.println("Item <" + item.getId() + "> will be sent to " +  super.getNextElementsList().get(maxPriorElement).getName());
//                super.getNextElementsList().get(maxPriorElement).inAct(item);
//            }
//            else{
//                //Вибір шляху за довжиною черги (блокування шляху)
//                Process process1 = (Process) super.getNextElementsList().get(0);
//                Process process2 = (Process) super.getNextElementsList().get(1);
//                if(process1.getQueue() < process2.getQueue()){
//                    System.out.println("Item <" + item.getId() + "> will be sent to " +  super.getNextElementsList().get(0).getName());
//                    super.getNextElementsList().get(0).inAct(item);
//                }
//                else{
//                    System.out.println("Item <" + item.getId() + "> will be sent to " +  super.getNextElementsList().get(1).getName());
//                    super.getNextElementsList().get(1).inAct(item);
//                }
//            }
//        }

    }
}
