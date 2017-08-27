package com.example.jules.familytrackr;

/**
 * Created by jules on 26-Aug-17.
 */

public class variables {

    public static String[] names = new String[100];
    public static String[] links = new String[100];

    public static String getNames(int index) {
        return names[index];
    }

    public static void setNames(String name, int index) {
        names[index] = name;
    }

    public static String getLinks(int index) {
        return links[index];
    }

    public static void setLinks(String link, int index) {
        links[index] = link;
    }
}
