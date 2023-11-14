package org.example;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) throws Exception {

        Create c = new Create("Create-1", 2.0, false);
        Process p1 = new Process("Process-1", 1.0, 10, 3, true);
        Process p2 = new Process("Process-2", 1.0, 90, 2, true);
        //Process p3 = new Process("Process-3", 1.0, 80);

       c.setNextElementsList(new ArrayList<>(){{
           add(p1);
           add(p2);
           //add(p3);
       }});



        p1.setMaxqueue(5);
        p2.setMaxqueue(5);
        //p3.setMaxqueue(5);

        c.setDistribution("exp");
        p1.setDistribution("exp");
        p2.setDistribution("exp");
        //p3.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        //list.add(p3);

        Model model = new Model(list);
        model.simulate(1000.0);
    }

}
