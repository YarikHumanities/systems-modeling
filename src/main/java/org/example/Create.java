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
    }
}
