package com.example;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FileLoader { 
    public static Map<String,String> loadData(String filePath, String delimiter) { //takes in a file path and delimeter
        Map<String, String> data = new HashMap<>(); //makes new String String hashmap
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) { //if the line in the file isn't null, the information in the line of the file is split and put into the data hashmap
                String[] parts = line.split(delimiter);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    data.put(key, value);
                } else {
                    System.err.println("Skipping invalid line: " + line);
                }
            }
            return data; //returns the new hashmap to be used by the MainFrame game
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage()); //debug message
            return null;
        }
    }

    public static Map<String, ArrayList<Integer>> loadScores(String filePath, String delimiter) { //takes in a file path and delimeter
        Map<String, ArrayList<Integer>> data = new HashMap<>(); //makes new String ArrayList<Integer> hashmap
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) { //if the line in the file isn't null, the information in the line of the file is split and put into the data hashmap
                //if(line.trim().isEmpty()) continue; 

                String[] parts = line.split(delimiter, 2);
                if(parts.length != 2) {
                    System.out.println("Skipping Invalid Line (Missing Delimiter): " + line); //debug to prevent error with reading and loading
                    continue;
                }

                String key = parts[0].trim(); //the String key of the String Arraylist<Integer> hashmap
                String scorePart = parts[1].trim().replaceAll("^\\[|\\]$", ""); //removes "[]" from each integer in the arraylist in the file (I had a problem with the file being read because of the brackets)
                String[] scoreTokens = scorePart.split(","); //ArrayList integers in the file split by "," for each integer in the arraylist
                ArrayList<Integer> scores = new ArrayList<>(); //makes a new Integer ArrayList

                for(String token : scoreTokens) { //for each integer in the ArrayList, if the String representing the integer isn't empty, it adds the Integer that the String represents to the scores ArrayList
                    String scoreStr = token.trim();
                    if(!scoreStr.isEmpty()) {
                        try {
                            scores.add(Integer.parseInt(scoreStr));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid Score Skipped: \"" + scoreStr + "\" in line: " + line); //print for debugging
                        }
                    }
                }
                data.put(key, scores); //initializes the data hashmap to the String key and scores ArrayList
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage()); //print for debugging
            e.printStackTrace();
            return null;
        }
        return data; //returns the data hashmap
    }
}