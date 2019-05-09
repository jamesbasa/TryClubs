package com.ucsd.tryclubs.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.R;

/**
 * Class DiscoverActivity sets the content to res/layout/activity_discover.xml
 * and this is the Bubble Picker "Discover" page.
 *
 */
public class DiscoverActivity extends AppCompatActivity {

    // BubblePicker library
    private BubblePicker picker;

    /**
     * UNDER CONSTRUCTION!!!
     *
     * DO NOT TOUCH
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

/*
        final String[] titles = getResources().getStringArray(R.array.countries);
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);
        final TypedArray images = getResources().obtainTypedArray(R.array.images);

        picker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(titles[position]);
                item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                item.setTextColor(ContextCompat.getColor(DiscoverActivity.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(DiscoverActivity.this, images.getResourceId(position, 0)));
                return item;
            }
        });

        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {

            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {

            }
        });11
        */
    }

    // TODO - NEED TO TEST
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        picker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        picker.onPause();
    }

    /**
     * Helper method which use Intent to go back to the Main Page (Timeline)
     */
    // TODO
    private void goToMainActivityHelper() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Animatoo.animateSlideUp(this); //fire the slide up animation
        finish();
    }
}
