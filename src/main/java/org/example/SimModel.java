package org.example;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) {

        Create c = new Create("Create-1", 2.0);
        Process p1 = new Process("Process-1", 1.0);
        Process p2 = new Process("Process-2", 1.0);
        Process p3 = new Process("Process-3", 1.0);

//        System.out.println("id0 = " + c.getId() + " id1=" + p.getId());

        c.setNextElement(p1);
        p1.setNextElement(p2);
        p2.setNextElement(p3);

        p1.setMaxqueue(5);
        p2.setMaxqueue(5);
        p3.setMaxqueue(5);

//        c.setName("CREATOR");
//        p.setName("PROCESSOR");

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");
        p3.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);

        Model model = new Model(list);
        model.simulate(1000.0);
    }

}
