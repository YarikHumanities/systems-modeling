package org.example;

import java.util.ArrayList;

public class Model {
    private ArrayList<Element> list = new ArrayList<>();
    double tnext, tcurr;
    int event;

    int swaps;
    public Model(ArrayList<Element> elements) throws Exception {
        list = elements;
        tnext = 0.1;
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

    //TODO : remake to new Queue implementation
    private void swapQueues(Create createElement){
        Process process1 = (Process) createElement.getNextElementsList().get(0);
        Process process2 = (Process) createElement.getNextElementsList().get(1);
        if(process1.getQueue()==process1.getMaxqueue() || process2.getQueue()==process2.getMaxqueue()){

            if((process1.getQueue()-process2.getQueue())>=2){
                System.out.println("Before:");
                System.out.print(process1.getName() + " ");
                process1.printQueue();
                System.out.print(process2.getName() + " ");
                process2.printQueue();

                var lastItem = process1.peekLastItem();
                process2.setQueue(lastItem);
                System.out.println("SWAP");
                swaps++;

                System.out.println("After:");
                System.out.print(process1.getName() + " ");
                process1.printQueue();
                System.out.print(process2.getName() + " ");
                process2.printQueue();
            } else if ((process2.getQueue()-process1.getQueue())>=2) {
                System.out.println("Before:");
                System.out.print(process1.getName() + " ");
                process1.printQueue();
                System.out.print(process2.getName() + " ");
                process2.printQueue();

                var lastItem = process2.peekLastItem();
                process1.setQueue(lastItem);
                System.out.println("SWAP");
                swaps++;

                System.out.println("After:");
                System.out.print(process1.getName() + " ");
                process1.printQueue();
                System.out.print(process2.getName() + " ");
                process2.printQueue();
            }

        }
    }
    public void simulate(double time) throws Exception {
        while (tcurr < time) {

            //Swap queues if one is 2 longer that another
            this.swapQueues((Create) list.get(0));

            tnext = Double.MAX_VALUE;

            for (Element e : list) {

                if (e.getTnext() < tnext) {
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
        double averageQuantity = 0;
        double averageTimeInBank = 0;
        double averageTimeBetweenLeave = 0;

        for (Element e : list) {
            e.printResult();
            if (e instanceof Process) {
                Process p = (Process) e;
                averageQuantity += ((p.getQuantity() - p.getFailure()) + (p.getMeanQueue() / tcurr));
                averageTimeInBank += (p.averageClientTimeInBank/ (double) p.getQuantity());
                averageTimeBetweenLeave += (p.averageTimeBetweenLeave/ (double) p.getQuantity());
                System.out.printf("mean length of queue = %.3f%n", p.getMeanQueue() / tcurr);
                System.out.printf("Failed = " + p.getFailure() + "\n");
                System.out.printf("failure probability = %.3f%n", p.getFailure() / (double) p.getQuantity());
                System.out.printf("Average Load of Device: %.3f%n", p.totalWorkTime / timeOfModeling);
                System.out.println("Swaps: " + this.swaps);

            }
        }
        averageQuantity = averageQuantity / 2;
        System.out.println();
        System.out.println("Average Quantity: " + averageQuantity);
        System.out.println("Average Time in Bank: " + averageTimeInBank);
        System.out.println("Average Time Between Leaves: " + averageTimeBetweenLeave);
    }
}
