package org.example;

import java.util.Random;

public class Create extends Element {
    public Create(double delay) {
        super(delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create
    }
    public Create(String nameOfElement, double delay, boolean chooseByProbability) {
        super(nameOfElement, delay, chooseByProbability);
        super.setTnext(0.0); // імітація розпочнеться з події Create
    }
    @Override
    public void outAct() throws Exception {
        super.incrementQuantity();
        var superDelay = super.getDelay();
        var delay = super.getTcurr() + superDelay;
        super.setTnext(delay);
        totalWorkTime += superDelay;

        if(super.isChooseByProbability()) {
            var nextElement = super.chooseNextElement();
            super.getNextElementsList().get(nextElement).inAct();
        }
        else{
            if(checkQueues()) {
                var maxPriorElement = super.findIndexOfMaxPriorityElement();
                super.getNextElementsList().get(maxPriorElement).inAct();
            }
            else{
                //Вибір шляху за довжиною черги (блокування шляху)
                Process process1 = (Process) super.getNextElementsList().get(0);
                Process process2 = (Process) super.getNextElementsList().get(1);
                if(process1.getQueue() < process2.getQueue()){
                    super.getNextElementsList().get(0).inAct();
                }
                else{
                    super.getNextElementsList().get(1).inAct();
                }
            }
        }

    }

    private boolean checkQueues(){
       Process process1 = (Process) super.getNextElementsList().get(0);
       Process process2 = (Process) super.getNextElementsList().get(1);
       return (process1.getQueue() == process2.getQueue()) || (process1.getQueue() == 0 && process2.getQueue() == 0);
    }

}
