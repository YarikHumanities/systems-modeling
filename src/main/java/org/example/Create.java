package org.example;

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

        this.defaultChoice();
    }

}
