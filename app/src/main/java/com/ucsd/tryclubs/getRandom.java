package com.ucsd.tryclubs;

import java.util.Random;

/**
 * class getRandom returns random UCSD pictures or color for elements in the App.
 */
public class getRandom {

    private static final Random RANDOM = new Random();

    /**
     * method getRandomUCSDDrawable returns a random UCSD picture
     *
     * @return a random picture in resource file
     */
    public static int getRandomUCSDDrawable() {
        switch (RANDOM.nextInt(22)) {
            default:
                return R.drawable.cse;
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
            case 14:
                return R.drawable.ucsd_64_night;
            case 15:
                return R.drawable.ucsd_medical;
            case 16:
                return R.drawable.ucsd_nc_1;
            case 17:
                return R.drawable.ucsd_nch;
            case 18:
                return R.drawable.ucsd_people;
            case 19:
                return R.drawable.ucsd_people_2;
            case 20:
                return R.drawable.ucsd_people_3;
            case 21:
                return R.drawable.ucsd_roosevelt;
        }
    }

    /**
     * method getRandomColor returns a random color
     *
     * @return a random color
     */
    public static String getRandomColor() {
        switch (RANDOM.nextInt(8)) {
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
