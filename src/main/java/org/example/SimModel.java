package org.example;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) throws Exception {

        Create c = new Create("Create-1", 0.5, false);
        Process p1 = new Process("Process-1", 0.3, 10, 10, false);
        Process p2 = new Process("Process-2", 0.3, 10, 1, false);

       c.setNextElementsList(new ArrayList<>(){{
           add(p1);
           add(p2);
       }});


        p1.setMaxqueue(3);
        p2.setMaxqueue(3);

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);

        Model model = new Model(list);
        model.simulate(1000.0);
    }

}
