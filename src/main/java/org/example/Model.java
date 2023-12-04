package org.example;

import java.util.ArrayList;

public class Model {
    public Double getTimeModelling() {
        return timeModelling;
    }

    public void setTimeModelling(Double timeModelling) {
        this.timeModelling = timeModelling;
    }

    private Double timeModelling;
    private ArrayList<Element> list = new ArrayList<>();
    double tnext, tcurr;
    int event;
    public Model(ArrayList<Element> elements) throws Exception {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;

        for (Element e : list) {
            if(e.nextElementsExists() && !e.singleNextElement()) {
                boolean checkprobability = e.checkNextElementsprobabilityCorrectness();
                if (!checkprobability) {
                    throw new Exception("probability of " + e.getName() + " next elements is incorrect");
                }
            }
        }
    }

    public void simulate(double time) throws Exception {
        while (tcurr < time) {

            tnext = Double.MAX_VALUE;

            for (Element e : list) {
                if (e.getTnext() < tnext && e.isAvailable()) {
                    tnext = e.getTnext();
                    event = e.getId();
                }
            }
            System.out.println("\nIt's time for event in " + list.get(event).getName() + ", time = " + tnext);

            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }

            System.out.println("tcurr " + tcurr + " will be changed to tnext " + tnext);
            tcurr = tnext;
            if(tcurr==Double.MAX_VALUE){
                printInfo();
                System.out.println("-----END-----");
                break;
            }
            else{
                this.timeModelling = tcurr;
            }
                //&& this.list.get(list.size()).getQuantity()==list.size()
            for (Element e : list) {
                e.setTcurr(tcurr);
            }

            list.get(event).outAct();
            System.out.println("Event in " + list.get(event).getName() + " outActed and its tnext " + list.get(event).getTnext());

            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    System.out.println("Event " + e.getName() + " appeared to have the same tnext as tcurr, " + tnext + ", and will be outActed");
                    e.outAct();
                }
            }

            printInfo();

        }
        printResult(time);
    }
    private void blockElement(Element element){
        element.setAvailable(false);
    }
    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }
    public void printResult(double timeOfModeling) {
        System.out.println("\n-------------RESULTS-------------");
        for (Element e : list) {
            e.printResult();
            if (e instanceof Process) {
                Process p = (Process) e;
                System.out.printf("mean length of queue = %.3f%n", p.getMeanQueue() / tcurr);
                System.out.printf("Failed = " + p.getFailure() + "\n");
                System.out.printf("failure probability = %.3f%n", p.getFailure() / (double) p.getQuantity());
                System.out.printf("Average Load of Device: %.3f%n", p.totalWorkTime / timeOfModeling);

            }
        }
    }
}
