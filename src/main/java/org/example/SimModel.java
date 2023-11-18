package org.example;

import java.util.ArrayList;

public class SimModel {

    public static void main(String[] args) throws Exception {
        setTestCore();
    }

    public static void setTestCore() throws Exception {
        Create c = new Create("Create-1", 15.0);
        Process p1 = new PriorityQueueProcess("Чергові-лікарі", 2.0, 2, ProcessTypes.DUTY);
        Process p2 = new PriorityQueueProcess("Супровідні", 2.0, 3, ProcessTypes.ACCOMPANYING);

        c.setNextElementsList(new ArrayList<>(){{
            add(p1);
        }});
        p1.setNextElementsList(new ArrayList<>(){{
            add(p2);
        }});

        c.setDistribution("exp");

        p1.setDistribution("exp");

        p2.setDistribution("unif");
        p2.setDelayMean(3.0);
        p2.setDelayDev(8.0);

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);

        Model model = new Model(list);
        model.simulate(100.0);
    }
    public static void setTaskCore() throws Exception {

    }
}
