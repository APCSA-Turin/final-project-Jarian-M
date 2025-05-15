package com.example;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Hello world!
 *
 */
public class App 
{
    private static int score;
    private static int highScore;
    private static int time;
    
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> pokeHigh = FileLoader.loadData("JavaAPIProject/src/data/info.txt", ":");
        if(pokeHigh != null) {
            highScore = Integer.parseInt(pokeHigh.get("highScore"));
        } else {
            highScore = 0;
        }
        // System.out.println("Who's that pokemon!");
        // String name = scan.next();
        String jsonString = Api.getData("https://pokeapi.co/api/v2/pokemon?offset=20&limit=200");
        //create the JSON object 
        //JSONObject pokemon = new JSONObject(jsonString);
        //you can get the value of the key by calling the getString(key) method of JSON Object
        //String name = pokemon.getString("name");
        //String type = pokemon.getString("type");
        //int level = pokemon.getInt("level");            
        //System.out.println(jsonString);
        //saveData(jsonString);
        JSONObject json = new JSONObject(jsonString);
        JSONArray results = json.getJSONArray("results");
        for(int i=0; i< results.length();i++){
            JSONObject item = (JSONObject) results.get(i);
            String name = item.getString("name");
            String url = item.getString("url");
            String s = Api.getData(url);
            JSONObject obj = new JSONObject(s);
            JSONObject sprites = obj.getJSONObject("sprites");
            String front_default = sprites.getString("front_default");
            System.out.println(front_default);
            //saveData();
        }

        score = 0;
        if(highScore < score) {
            highScore = score;
        }

        Map<String, String> pokedex = new HashMap<>();
        pokedex.put("score", "" + score);
        pokedex.put("highScore", "" + highScore);
        pokedex.put("time", "" + time);
        saveData(pokedex);
    }

    public static void saveData(Map<String,String> data) {
        try (FileWriter writer = new FileWriter("JavaAPIProject/src/data/info.txt")) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
