package org.example;

import java.util.ArrayList;
import java.util.List;

public class SimModel {
    public static void main(String[] args) throws Exception {
        ArrayList<Long> timeList = new ArrayList<>();
        for(int i=1; i<=100; i=i*10) {
            long workingTime = simpleModelSet(i);
            System.out.println(i + " Working Time: " + workingTime);
            timeList.add(workingTime);
            Element.resetIds();
        }

        int timeIndex = 0;
        for(int i=1; i<=100; i=i*10) {
            System.out.println();
            System.out.println(i + " Working Time: " + timeList.get(timeIndex));
            timeIndex++;
        }
    }
    public static long simpleModelSet(int N) throws Exception {
        Create c = new Create("Create-1", 2.0, true);
        c.setDistribution("exp");
        c.setMaxValue(N+1);
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
        long startTime = System.nanoTime();
        model.simulate(Double.MAX_VALUE);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}
