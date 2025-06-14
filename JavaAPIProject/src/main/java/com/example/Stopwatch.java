package com.example;

import java.util.concurrent.TimeUnit;

public class Stopwatch {

    private static long startTime = 0; //instance variable for start time
    private static long endTime = 0; //instance variable for end time
    private static boolean running = false; //instance variable for boolean to determine if watch is running

    public static void start() { //starts stopwatch timer
        if (running) {
            return; //terminates if already running
        }
        running = true;
        startTime = System.nanoTime(); //startTime initialized to current time
    }

    public static void stop() { //stops stopwatch timer
        if (!running) {
            return; // terminates before stopping if its not running
        }
        running = false; //stops the time
        endTime = System.nanoTime(); //endTime is initialized to time when the watch is stopped
    }

    public static void reset() { //resets the instance variables to their original values
        startTime = 0;
        endTime = 0;
        running = false;
    }

    public static int getElapsedTimeSeconds() { //returns the time that has elapsed since the watch started in seconds as an int
        long sec = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
        if (running) {
             sec = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
        return (int) sec;
    }
    
}
