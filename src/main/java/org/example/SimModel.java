package org.example;

import java.util.ArrayList;

public class SimModel {

    public static void main(String[] args) throws Exception {
        setTestCore();
    }

    public static void setTestCore() throws Exception {
        Create c = new Create("Create", 15.0);
        Process p1 = new PriorityQueueProcess("Чергові-лікарі", 2.0, 2, ProcessTypes.DUTY);
        Process p2 = new Process("Супровідні", 2.0, 3, ProcessTypes.ACCOMPANYING);
        Process p3 = new Process("Перехід в Лабу", 2.0, 10, ProcessTypes.LAB_TRANSFER);
        Process p4 = new Process("Реєстрація в Реєстратурі", 2.0, 1, ProcessTypes.REGISTRATION);
        Process p5 = new Process("Сдача Аналізів", 2.0, 2, ProcessTypes.TAKING_TEST);
        Process p6 = new Process("Повернення до приймального відділення", 2.0, 10, ProcessTypes.BACK_TRANSFER);

        c.setNextElementsList(new ArrayList<>(){{
            add(p1);
        }});
        p1.setNextElementsList(new ArrayList<>(){{
            add(p2);
            add(p3);
        }});

        p3.setNextElementsList(new ArrayList<>(){{
            add(p4);
        }});

        p4.setNextElementsList(new ArrayList<>(){{
            add(p5);
        }});

        p5.setNextElementsList(new ArrayList<>(){{
            add(p6);
        }});

        p6.setNextElementsList(new ArrayList<>(){{
            add(p1);
        }});

        c.setDistribution("exp");

        p1.setDistribution("exp");

        p2.setDistribution("unif");
        p2.setDelayMean(3.0);
        p2.setDelayDev(8.0);

        p3.setDistribution("unif");
        p3.setDelayMean(2.0);
        p3.setDelayDev(5.0);

        p4.setDistribution("erlang");
        p4.setDelayMean(4.5);
        p4.setDelayDev(3.0);

        p5.setDistribution("erlang");
        p5.setDelayMean(4.0);
        p5.setDelayDev(2.0);

        p6.setDistribution("unif");
        p6.setDelayMean(2.0);
        p6.setDelayDev(5.0);

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);

        Model model = new Model(list);
        model.simulate(1000.0);
    }
    public static void setTaskCore() throws Exception {

    }
}
