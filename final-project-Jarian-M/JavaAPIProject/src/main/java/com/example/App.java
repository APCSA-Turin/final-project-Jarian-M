package com.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class App
{
    private static int score; //testing only, not the real app
    private static int highScore;
    private static int time;
    private static Map<String, String> pokeHigh = new HashMap<String, String>();
    private static ArrayList<Integer> nums = new ArrayList<Integer>();
    private static ArrayList<String> names = new ArrayList<String>();
    private static ArrayList<String> urls = new ArrayList<String>();

    public App() {
    }

    public int getHigh() {
        return highScore;
    }

    public int getScore() {
        return score;
    }

    public int getTime() {
        return time;
    }

    public static ArrayList<Integer> getNums() {
        return nums;
    }

    public static ArrayList<String> getNames() {
        return names;
    }

    public static ArrayList<String> getUrls() {
        return urls;
    }
   
    public static void main( String[] args ) throws Exception
    {
        /*Map<String, String> pokeHigh = FileLoader.loadData("JavaAPIProject/src/Data/info.txt", ":");
        if(pokeHigh != null) {
            highScore = Integer.parseInt(pokeHigh.get("highScore"));
        } else {
            highScore = 0;
        } */

        pokeHigh = FileLoader.loadData("JavaAPIProject/src/Data/info.txt", ":");
        if(pokeHigh != null) {
            highScore = Integer.parseInt(pokeHigh.get("highScore"));
            score = Integer.parseInt(pokeHigh.get("score"));
            time = Integer.parseInt(pokeHigh.get("time"));
        } else {
            highScore = 0;
            score = 0;
            time = 0;
        }

        String jsonString = Api.getData("https://pokeapi.co/api/v2/pokemon?limit=100&offset=0");

        JSONObject json = new JSONObject(jsonString);
        JSONArray results = json.getJSONArray("results");
        for(int i = 0; i < 10; i++) {
            int num = (int)(Math.random() * 100);
            while(nums.contains(num)) {
                num = (int)(Math.random() * 100);
            }
            nums.add(num);
            JSONObject item = (JSONObject) results.get(num);
            String name = item.getString("name");
            names.add(name);
            String url = item.getString("url");
            urls.add(url);
            String s = Api.getData(url);
            JSONObject obj = new JSONObject(s);
            JSONObject sprites = obj.getJSONObject("sprites");
            String front_default = sprites.getString("front_default");
            System.out.println(front_default);
        }

        score = 0;
        highScore = 0;
        if(highScore < score) {
            highScore = score;
        }


        Map<String, String> pokedex = new HashMap<>();
        pokedex.put("score", "" + score);
        pokedex.put("highScore", "" + highScore);
        pokedex.put("time", "" + time);
        FileSaver.saveData(pokedex);
    }

}