package com.ucsd.tryclubs.Login;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.ucsd.tryclubs.MainActivity;
import com.ucsd.tryclubs.R;

import org.jetbrains.annotations.NotNull;

/**
 * Class DiscoverActivity sets the content to res/layout/activity_discover.xml
 * and this is the Bubble Picker "Discover" page.
 *
 */
public class DiscoverActivity extends AppCompatActivity {

    // BubblePicker library
    private BubblePicker picker;

    // view stuff
    private TextView mDiscover;
    private TextView mSubtitle;
    private TextView mTapToChoose;
    private Button mNextButton;

    // font stuff
    Typeface type;

    // Firebase stuff
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        final String[] titles = getResources().getStringArray(R.array.all_clubs_suggestions);
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);
        final TypedArray images = getResources().obtainTypedArray(R.array.bubblepicker_images);

        picker = findViewById(R.id.picker);
        picker.setMaxSelectedCount(3);

        mDiscover = findViewById(R.id.bubblepicker_discover);
        mSubtitle = findViewById(R.id.bubblepicker_subtitle);
        mTapToChoose = findViewById(R.id.bubblepicker_tapTochoose);
        mNextButton = findViewById(R.id.bubblepicker_next_button);

        type =  Typeface.createFromAsset(getApplicationContext().getAssets(), "font/googlesans_regular.ttf");
        mDiscover.setTypeface(type);
        mSubtitle.setTypeface(type);
        mTapToChoose.setTypeface(type);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                goToMainActivityHelper();
            }
        });

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
                item.setTypeface(type);
                item.setTextColor(ContextCompat.getColor(DiscoverActivity.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(DiscoverActivity.this, images.getResourceId(position, 0)));
                return item;
            }
        });

        colors.recycle();
        images.recycle();
        picker.setBubbleSize(20);

        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {
                // TODO
                if (picker.getSelectedItems().size() > 0) {
                    mNextButton.setText("FINISH");
                }
                Toast.makeText(getApplicationContext(),"Hi", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {
                // TODO
            }
        });

    }

    /**
     * Override system method onBackPress() which defines the "back" button behaviour
     * in the Action Bar of Android
     */
    @Override
    public void onBackPressed() {
        // NO GO BACK IS ALLOWED!
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
