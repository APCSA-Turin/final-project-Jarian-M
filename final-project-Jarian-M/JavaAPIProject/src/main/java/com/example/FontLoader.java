package com.example;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontLoader {
    public static Font loadFont(String path, float size) { //takes in file path (where font download is) and Font size and loads the Font
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path)); //makes a new font object
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); //needed to add/register custom font to program
            ge.registerFont(font);
            return font.deriveFont(size); //returns the new custom font with size size
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Comic Sans MS", Font.PLAIN, (int) size); //debug, font becomes "Comic Sans MS" if custom font doesn't work
        }
    }
}
