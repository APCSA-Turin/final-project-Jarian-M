package com.example;

import java.util.ArrayList;

public class ScoreStats {
    public static void sortList(ArrayList<Integer> list) { //sorts an ArrayList<Integer> inputted as a parameter
        for(int i = 1; i < list.size(); i++) { //insertion sort to sort the ArrayList
            int val = list.get(i);
            int prev = i - 1;

            while(prev >= 0 && list.get(prev) > val) {
                list.set(prev + 1, list.get(prev));
                prev = prev - 1;
            }
            list.set(prev + 1, val);
        }
    }

    public static double getAverage(ArrayList<Integer> list) { //returns the average value of the ArrayList<Integer> list as a double
        double sum = 0;
        for(int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        double avg = sum / list.size();
        return Math.round(avg * 100.0) / 100.0;
    }

    public static double getMedian(ArrayList<Integer> list) { //returns the median value of the ArrayList<Integer> list as a double
        sortList(list); //sorts the list first (median needs sorted list)
        int size = list.size();
        if(size == 0) {
            return 0;
        }
        if(size % 2 == 1) { //if list size is odd, return middle value
            return list.get(size / 2);
        } else {
            return (list.get(size / 2 - 1) + list.get(size / 2)) / 2.0; //returns sum of two middle values / 2.0
        }
    }
}
