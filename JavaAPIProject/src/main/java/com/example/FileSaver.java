package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class FileSaver {
    public static void saveData(Map<String,String> data) { //saves a String String hashmap, the parameter, to a file
        File infoFile = new File("JavaApiProject/src/Data/info.txt"); //makes file
        String absolutePath = infoFile.getAbsolutePath(); //sets absolute file path of file to a String (I used absolute filepath because I'd have to keep changing the file name to the name of file on local computer otherwise since its path changed depending on local computer)
        try (FileWriter writer = new FileWriter(absolutePath)) { //writes hashmap info into a file info.txt
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveScores(Map<String, ArrayList<Integer>> data) { //saves a String ArrayList<Integer> hashmap, the parameter, to a file
        File infoFile = new File("JavaApiProject/src/Data/scores.txt"); //makes file
        String absolutePath = infoFile.getAbsolutePath(); //absolute path so no need to keep changing file name based on local path
        try (FileWriter writer = new FileWriter(absolutePath)) { //writes hashmap info into a file info.txt
            for (Map.Entry<String, ArrayList<Integer>> entry : data.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
