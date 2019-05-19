package com.ucsd.tryclubs;

import java.util.Random;

public class getRandom {

    private static final Random RANDOM = new Random();

    public static int getRandomUCSDDrawable() {
        switch (RANDOM.nextInt(14)) {
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
            case 5:
                return R.drawable.arch;
            case 6:
                return R.drawable.breezeway;
            case 7:
                return R.drawable.cse_2;
            case 8:
                return R.drawable.geisel_3;
            case 9:
                return R.drawable.playground;
            case 10:
                return R.drawable.revelle_2;
            case 11:
                return R.drawable.rsom;
            case 12:
                return R.drawable.snake;
            case 13:
                return R.drawable.stringio;
        }
    }

    public static String getRandomColor() {
        switch (RANDOM.nextInt(7)) {
            default:
            case 0:
                return "#03A9F4";
            case 1:
                return "#0ca6f9";
            case 2:
                return "#a369ff";
            case 3:
                return "#0e56ff";
            case 4:
                return "#FF6A00";
            case 5:
                return "#ef0276";
            case 6:
                return "#FF0000";
            case 7:
                return "#11c612";
        }

    }
}
