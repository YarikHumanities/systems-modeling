package org.example;

import java.util.Random;

public class Create extends Element {
    public Create(double delay) {
        super(delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create
    }
    public Create(String nameOfElement, double delay) {
        super(nameOfElement, delay);
        super.setTnext(0.0); // імітація розпочнеться з події Create
    }
    @Override
    public void outAct() throws Exception {
        //super.outAct();
        super.incrementQuantity();
        var superDelay = super.getDelay();
        var delay = super.getTcurr() + superDelay;
        super.setTnext(delay);
        totalWorkTime += superDelay;

        var nextElementsQuant = super.getNextElementsList().size();
        Random random = new Random();
        var randomElementFromList = random.nextInt(nextElementsQuant);
        super.getNextElementsList().get(randomElementFromList).inAct();
        //super.getNextElement().inAct();
    }

}
