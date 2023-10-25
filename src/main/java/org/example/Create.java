package org.example;

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
    public void outAct() {
        super.outAct();
        var delay = super.getTcurr() + super.getDelay();
        super.setTnext(delay);
        totalWorkTime += delay;
        super.getNextElement().inAct();
    }

}
