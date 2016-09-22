package com.onshara.michael;

import java.util.Random;

public class MyDevice {

    public static String makeDeviceId() {
        String[] s = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        String str = "00000000-";
        Random rnd = new Random();

        str += s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+"-";
        str += s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+"-";
        str += s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+"-";
        str += s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)]+s[rnd.nextInt(15)];
        str += s[rnd.nextInt(15)]+s[rnd.nextInt(15)];

        return str;
    }
}
