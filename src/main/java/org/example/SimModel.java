package org.example;

import java.util.ArrayList;
import java.util.List;

public class SimModel {
    public static void main(String[] args) throws Exception {

        List<Integer> numberList = List.of(10, 100);
        ArrayList<Long> times = new ArrayList<>();
        for (Integer N : numberList) {
            long startTime = System.nanoTime();
            simpleModelSet(N);
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            times.add(elapsedTime);
        }

        System.out.println();
        for(int i = 0; i<numberList.size(); i++){
            System.out.println("Time for " + numberList.get(i) + " processes: " + times.get(i));
        }

        simpleModelSet(100);
    }
    public static void simpleModelSet(int N) throws Exception {
        Create c = new Create("Create-1", 2.0, true);
        c.setDistribution("exp");
        ArrayList<Element> list = new ArrayList<>();
        list.add(c);

        for(int i = 0; i<N; i++){
            String processName = "Process-" + (i + 1);
            Process p1 = new Process(processName, 1.0, 10, 3, true);
            p1.setDistribution("exp");
            list.add(p1);
            if(i==0){
                c.setNextElementsList(new ArrayList<>(){{
                    add(p1);
                }});
            }
            else{
                int lastIndex = list.size() - 2;
                list.get(lastIndex).setNextElementsList(new ArrayList<>(){{
                    add(p1);
                }});
            }
        }


        Model model = new Model(list);
        model.simulate(100.0);
    }
}
