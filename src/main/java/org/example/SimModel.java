package org.example;

import java.util.ArrayList;

public class SimModel {
    public static int N = 2;
    public static void main(String[] args) throws Exception {
        simpleModelSet();
    }
    public static void simpleModelSet() throws Exception {
        Create c = new Create("Create-1", 2.0, true);
        c.setDistribution("exp");
        ArrayList<Element> list = new ArrayList<>();
        list.add(c);

        for(int i = 0; i<N; i++){
            String processName = "Process-" + (i + 1);
            Process p1 = new Process(processName, 1.0, 10, 3, true);
            p1.setMaxqueue(5);
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
