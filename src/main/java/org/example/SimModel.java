package org.example;

import java.util.ArrayList;

public class SimModel {

    public static void main(String[] args) throws Exception {
        setTestCore();
    }

    public static void setTestCore() throws Exception {
        Create c = new Create("Create-1", 15.0);
        Process p1 = new PriorityQueueProcess("Чергові-лікарі", 2.0, 2);

        c.setNextElementsList(new ArrayList<>(){{
            add(p1);
        }});


        c.setDistribution("exp");
        p1.setDistribution("exp");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);

        Model model = new Model(list);
        model.simulate(100.0);
    }
    public static void setTaskCore() throws Exception {

    }
}
