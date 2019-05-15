package com.ucsd.tryclubs;

import java.util.Random;

public class getRandomPicture {

    private static final Random RANDOM = new Random();

    public static int getRandomCheeseDrawable() {
        switch (RANDOM.nextInt(5)) {
            default:
            case 0:
                return R.drawable.geisel_2;
            case 1:
                return R.drawable.cse;
            case 2:
                return R.drawable.housing;
            case 3:
                return R.drawable.revelle;
            case 4:
                return R.drawable.eng;
        }
    }
}
