package com.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


public class Api {
    public static String getData(String endpoint) throws Exception { //gets data from API based on String url inputted into parameter
        URL url = new URI(endpoint).toURL(); //sets String url parameter to a URL object
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //makes connection to API
        connection.setRequestMethod("GET"); //gets API data instead of sending

        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) { //debug print
            throw new RuntimeException("HTTP error code: " + status);
        }

        StringBuilder content = new StringBuilder(); //uses StringBuilder to return all of the data from the API URL
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = buff.readLine()) != null) {
                content.append(inputLine);
            }
        }
        connection.disconnect();
        return content.toString(); //returns the data as a String
    }
}
