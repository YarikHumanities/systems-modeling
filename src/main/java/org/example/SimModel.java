package org.example;

import java.util.ArrayList;
import java.util.List;

public class SimModel {
    public static void printStructure(Element element, int depth) {
        // Print current element
        for (int i = 0; i < depth; i++) {
            System.out.print("  "); // Adjust indentation for depth
        }
        System.out.println(element.getName());

        // Print next elements recursively
        if (element.getNextElementsList() != null) {
            for (Element nextElement : element.getNextElementsList()) {
                printStructure(nextElement, depth + 1);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<Integer> Ns = List.of(10, 50, 100, 125, 150);
        ArrayList<Long> timeList = new ArrayList<>();
        double lambda = 1.0;
        for(Integer i: Ns) {
            long workingTime = simpleModelSet(i);
            //long workingTime = complexModelSet(i);
            System.out.println(i + " Working Time: " + workingTime);
            timeList.add(workingTime);
            Element.resetIds();
        }

        int timeIndex = 0;
        for(Integer i: Ns) {
            System.out.println();
            System.out.println(i + " Working Time: " + (double) timeList.get(timeIndex) / 1_000_000_000.0);
            System.out.println("Complexity: " + (((double) timeList.get(timeIndex) / 1_000_000_000.0) * 20 * lambda) + " n");
            timeIndex++;
        }


        //complexModelSet(6);
    }
    public static Long complexModelSet(int N) throws Exception {
        Create c = new Create("Create-1", 2.0, true);
        c.setDistribution("exp");
        c.setMaxValue(N+1);
        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        c.setNextElementsList(new ArrayList<>());

        Element currentElement = null;
        int currentMainIndex = 2;
        for(int i = 0; i<N; i++){

            String processName = "Process-" + (i + 1);
            Process p1 = new Process(processName, 1.0, 50, 3, true);

            p1.setDistribution("exp");

            list.add(p1);
            p1.setNextElementsList(new ArrayList<>());

            if(i==0 || i==1){
                c.addNextElement(p1);
            }

            else{
                if(i==currentMainIndex){
                    currentElement = p1;

                    list.get(list.size() - 2).addNextElement(p1);

                    list.get(list.size() - 3).addNextElement(p1);
                    currentMainIndex+=3;
                }
               else{
                   currentElement.addNextElement(p1);
                }
            }
        }

        //printStructure(c, 5);
        Model model = new Model(list);
        long startTime = System.nanoTime();
        System.out.println("LIST SIZE: " + list.size());
        model.simulate(Double.MAX_VALUE);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static Long simpleModelSet(int N) throws Exception {
        Create c = new Create("Create-1", 5.0, true);
        c.setDistribution("exp");
        c.setMaxValue(N+1);
        ArrayList<Element> list = new ArrayList<>();
        list.add(c);

        for(int i = 0; i<N; i++){
            String processName = "Process-" + (i + 1);
            Process p1 = new Process(processName, 1.0, 50, 3, true);
            p1.setDistribution("exp");
            list.add(p1);
            if(i==0){
                c.setNextElementsList(new ArrayList<>(){{
                    add(p1);
                }});
            }
            else{
                //2 BECAUSE of index start from 0
                int lastIndex = list.size() - 2;
                list.get(lastIndex).setNextElementsList(new ArrayList<>(){{
                    add(p1);
                }});
            }
        }


        //printStructure(c, 5);
        Model model = new Model(list);
        long startTime = System.nanoTime();
        System.out.println("LIST SIZE: " + list.size());
        model.simulate(Double.MAX_VALUE);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}
