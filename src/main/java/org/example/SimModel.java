package org.example;

import java.util.ArrayList;

public class SimModel {

    public static void main(String[] args) throws Exception {
        setTaskCore();
    }

    public static void setTestCore() throws Exception {
        Create c = new Create("Create-1", 1.0, false);
        Process p1 = new Process("Process-1", 2.0, 10, 10, false);
        Process p2 = new Process("Process-2", 2.0, 90, 1, false);
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
    public static void setTaskCore() throws Exception {
        Create c = new Create("Create-1", 0.5, false);
        Process p1 = new Process("Process-1", 0.3, 10, 10, false);
        Process p2 = new Process("Process-2", 0.3, 90, 1, false);

        c.setNextElementsList(new ArrayList<>(){{
            add(p1);
            add(p2);
        }});


        p1.setMaxqueue(3);
        p2.setMaxqueue(3);

        //В КОЖНІЙ ЧЕРЗІ ВЖЕ ПО 2 КЛІЄНТА
        //ОБИДВА ЗАЙНЯТІ С ЗАМОГО ПОЧАТКУ НА ЯКИЙСЬ ЧАС
        p1.setBusyInStart();
        p2.setBusyInStart();

        c.setDistribution("exp");

        //НА ПОЧАТКУ НОРМАЛЬНИЙ РОЗПОДІЛ
        p1.setDelayMean(1.0);
        p1.setDelayDev(0.3);

        p2.setDelayMean(1.0);
        p2.setDelayDev(0.3);

        p1.setDistribution("norm");
        p2.setDistribution("norm");

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p1);
        list.add(p2);

        Model model = new Model(list);
        model.simulate(100.0);
    }
}
