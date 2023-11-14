package org.example;

import java.util.ArrayList;
import java.util.Random;

public class Element {
    private String name;
    private double tnext;
    private double delayMean, delayDev;
    private String distribution;
    public void incrementQuantity() {
        this.quantity++;
    }
    private int quantity;
    private double tcurr;
    private int state;
    private ArrayList<Element> nextElementsList = new ArrayList<>();
    private static int nextId=0;
    private int id;
    public double totalWorkTime;
    protected int probability;
    protected boolean available;
    protected int priority;
    protected boolean chooseByProbability;
    public boolean isChooseByProbability() {
        return chooseByProbability;
    }
    public int getPriority() {
        return priority;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public int getprobability() {
        return probability;
    }
    public boolean checkNextElementsprobabilityCorrectness(){
        int sum = 0;
        for (Element element : nextElementsList) {
            if(element instanceof Process) {
                sum += element.getprobability();
            }
        }
        return sum == 100;
    }

    protected int chooseNextElement(){
        var random = new Random();
        int randomNumber = random.nextInt(100);

        int currentProbabilitySum = 0;
        for (int index = 0; index < nextElementsList.size(); index++) {

            if(nextElementsList.get(index).isAvailable()) {
                Element element = nextElementsList.get(index);
                currentProbabilitySum += element.getprobability();

                if (randomNumber < currentProbabilitySum) {
                    return index;
                }
            }

        }

        //IN CASE IF ALL PROCESSES WILL BE NOT AVAILABLE
        return Integer.MAX_VALUE;
    }
    protected int findIndexOfMaxPriorityElement() {
        if (nextElementsList == null || nextElementsList.size() == 0) {
            throw new IllegalArgumentException("Array is empty or null");
        }

        int maxPriorityIndex = 0;

        for (int i = 1; i < nextElementsList.size(); i++) {

            if(nextElementsList.get(i).isAvailable()) {
                if (nextElementsList.get(i).getPriority() > nextElementsList.get(maxPriorityIndex).getPriority()) {
                    maxPriorityIndex = i;
                }
            }

        }

        return maxPriorityIndex;
    }
    public boolean nextElementsExists(){
        return !this.nextElementsList.isEmpty();
    }
    public boolean singleNextElement(){
       return this.nextElementsList.size() == 1;
    }
    public Element(){

        tnext = Double.MAX_VALUE;
        delayMean = 1.0;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
        name = "element"+id;
    }
    public Element(double delay){
        name = "anonymus";
        tnext = 0.0;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
        name = "element"+id;
    }
    public Element(String nameOfElement, double delay, boolean chooseByProbability){
        this.available = true;
        this.chooseByProbability = chooseByProbability;
        name = nameOfElement;
        tnext = 0.0;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
    }
    public void setNextElementsList(ArrayList<Element> nextElementsList) {
        this.nextElementsList = nextElementsList;
    }
    public ArrayList<Element> getNextElementsList() {
        return nextElementsList;
    }
    public double getDelay() {
        double delay = getDelayMean();
        if ("exp".equalsIgnoreCase(getDistribution())) {
            delay = FunRand.Exp(getDelayMean());
        } else {
            if ("norm".equalsIgnoreCase(getDistribution())) {
                delay = FunRand.Norm(getDelayMean(),
                        getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(),
                            getDelayDev());
                } else {
                    if("".equalsIgnoreCase(getDistribution()))
                        delay = getDelayMean();
                }
            }
        }
        return delay;
    }

    public double getDelayDev() {
        return delayDev;
    }
    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }
    public String getDistribution() {
        return distribution;
    }
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public int getQuantity() {
        return quantity;
    }
    public double getTcurr() {
        return tcurr;
    }
    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void inAct() {
        quantity++;
    }
    public void outAct() throws Exception {
        //quantity++;
    }

    public double getTnext() {
        return tnext;
    }
    public void setTnext(double tnext) {
        this.tnext = tnext;
    }
    public double getDelayMean() {
        return delayMean;
    }
    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void printResult(){
        System.out.println(getName()+ " quantity = "+ quantity);
    }

    public void printInfo(){
        System.out.println(getName()+ " state= " + state + " quantity = " + quantity + " tnext= "+tnext);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void doStatistics(double delta){

    }
}
