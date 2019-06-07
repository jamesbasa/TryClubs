package com.ucsd.tryclubs.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ucsd.tryclubs.Model.ClubMembers;
import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.Model.Tags;
import com.ucsd.tryclubs.R;

import java.util.Random;

import org.w3c.dom.Text;

import java.util.Set;

/**
 * class ClubCreationActivity is the "Club Creation" page in the App.
 */
public class ClubCreationActivity extends AppCompatActivity {

    private static final String TAG = "ClubCreationActivity";

    Chip mAcademic, mArt, mBussiness, mComedy, mCommunity_service, mCulture, mDance, mEngineering, mEthnic, mGaming, mGreek, mLifestyle, mMartial_arts,
            mMedia, mMedical, mMusic, mPerforming_arts, mProgramming, mReligion, mScience, mSelf_improvement, mSocial, mSports, mTechnology;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAllClubsRef;
    private DatabaseReference mUserRef;

    private TextInputEditText mClubName;
    private TextInputEditText mPurpose;
    private ImageView mExit;
    private MaterialButton mDoneBtn;

    private Clubs club = new Clubs();
    private ClubMembers clubMembers = new ClubMembers();
    private Tags tags = new Tags(0);
    private ChipGroup cp;
    String clubName = "";

    private int checkCount = 0;

    private static final Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_creation);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAllClubsRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_clubs_tag));
        mUserRef = mFirebaseDatabase.getReference().child(getApplicationContext().getString(R.string.firebase_users_tag)).child(mAuth.getCurrentUser().getUid());

        mClubName = (TextInputEditText) findViewById(R.id.club_creation_clubname);
        mClubName.setInputType(InputType.TYPE_CLASS_TEXT);
        mPurpose = (TextInputEditText) findViewById(R.id.club_creation_purpose);
        mExit = (ImageView) findViewById(R.id.club_creation_exit_imageView);
        mDoneBtn = (MaterialButton) findViewById(R.id.club_creation_done_MaterialButton);
        cp = (ChipGroup) findViewById(R.id.club_creation_chipGroup);

        mAcademic = (Chip) findViewById(R.id.academic_tag);
        mArt = (Chip) findViewById(R.id.art_tag);
        mBussiness = (Chip) findViewById(R.id.business_tag);
        mComedy = (Chip) findViewById(R.id.comedy_tag);
        mCommunity_service = (Chip) findViewById(R.id.community_service_tag);
        mCulture = (Chip) findViewById(R.id.culture_tag);
        mDance = (Chip) findViewById(R.id.dance_tag);
        mEngineering = (Chip) findViewById(R.id.engineering_tag);
        mEthnic = (Chip) findViewById(R.id.ethnic_tag);
        mGaming = (Chip) findViewById(R.id.gaming_tag);
        mGreek = (Chip) findViewById(R.id.greek_tag);
        mLifestyle = (Chip) findViewById(R.id.lifestyle_tag);
        mMartial_arts = (Chip) findViewById(R.id.martial_arts_tag);
        mMedia = (Chip) findViewById(R.id.media_tag);
        mMedical = (Chip) findViewById(R.id.medical_tag);
        mMusic = (Chip) findViewById(R.id.music_tag);
        mPerforming_arts = (Chip) findViewById(R.id.performing_arts_tag);
        mProgramming = (Chip) findViewById(R.id.programming_tag);
        mReligion = (Chip) findViewById(R.id.religion_tag);
        mScience = (Chip) findViewById(R.id.science_tag);
        mSelf_improvement = (Chip) findViewById(R.id.self_improvement_tag);
        mSocial = (Chip) findViewById(R.id.social_tag);
        mSports = (Chip) findViewById(R.id.sports_tag);
        mTechnology = (Chip) findViewById(R.id.technology_tag);

        SetSingleChipListener(mAcademic);
        SetSingleChipListener(mArt);
        SetSingleChipListener(mBussiness);
        SetSingleChipListener(mComedy);
        SetSingleChipListener(mCommunity_service);
        SetSingleChipListener(mCulture);
        SetSingleChipListener(mDance);
        SetSingleChipListener(mEngineering);
        SetSingleChipListener(mEthnic);
        SetSingleChipListener(mGaming);
        SetSingleChipListener(mGreek);
        SetSingleChipListener(mLifestyle);
        SetSingleChipListener(mMartial_arts);
        SetSingleChipListener(mMedia);
        SetSingleChipListener(mMedical);
        SetSingleChipListener(mMusic);
        SetSingleChipListener(mPerforming_arts);
        SetSingleChipListener(mProgramming);
        SetSingleChipListener(mReligion);
        SetSingleChipListener(mScience);
        SetSingleChipListener(mSelf_improvement);
        SetSingleChipListener(mSocial);
        SetSingleChipListener(mSports);
        SetSingleChipListener(mTechnology);

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClubName.getText().toString().trim().equals(".") || mClubName.getText().toString().trim().equals("#") || mClubName.getText().toString().trim().equals("$")
                        || mClubName.getText().toString().trim().equals("[") || mClubName.getText().toString().trim().equals("]")) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid symbol", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    snackbar.show();
                    return;
                }
                if (checkCount == 0) {
                    //Toast.makeText(getApplicationContext(), "Please Select At Least One Tag.", Toast.LENGTH_LONG).show();
                    Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please Select At Least One Tag.", Snackbar.LENGTH_LONG);
                    View view = sn.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.parseColor("#FFD700"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    sn.show();
                } else {
                    addMyClub();
                }
            }
        });

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * addMyClub methods creates new a club in Firebase
     */
    private void addMyClub() {

        if (TextUtils.isEmpty(mClubName.getText().toString().trim()) || TextUtils.isEmpty(mPurpose.getText().toString().trim())) {
            Log.d(TAG, "adding my club" + " if statement");
            //Toast.makeText(getApplicationContext(), "Please Make Sure All Text Fields Are Filled.", Toast.LENGTH_LONG).show();
            Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please Make Sure All Text Fields Are Filled.", Snackbar.LENGTH_LONG);
            View view = sn.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.parseColor("#FFD700"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            sn.show();
        } else {
            Log.d(TAG, "adding my club now" +  mClubName.getText().toString().trim());

            clubName = mClubName.getText().toString().trim();

            mAllClubsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(clubName)) {
                        Snackbar sn = Snackbar.make(findViewById(android.R.id.content), "Club name conflicted. Choose another name.", Snackbar.LENGTH_LONG);
                        View view = sn.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#FFD700"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                        sn.show();
                    } else {
                        String purpose = mPurpose.getText().toString().trim();
                        int randomID = RANDOM.nextInt(100000-5000)+5000;
                        clubMembers.setName(mAuth.getCurrentUser().getDisplayName());
                        clubMembers.setEmail(mAuth.getCurrentUser().getEmail());

                        club.setId(Integer.toString(randomID));
                        club.setClub_name(clubName);
                        club.setPurpose(purpose);

                        mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(clubName).setValue(club).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DatabaseReference newClubRef = mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(clubName);
                                newClubRef.child(getApplicationContext().getString(R.string.firebase_tags_tag)).setValue(tags);
                                newClubRef.child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(mAuth.getCurrentUser().getDisplayName()).setValue(clubMembers);
                            }
                        });
                        mUserRef.child(getApplicationContext().getString(R.string.firebase_my_club_tag)).child(clubName).child(getApplicationContext().getString(R.string.firebase_lower_case_club_name_tag)).setValue(clubName.toLowerCase());

                        mAllClubsRef.child(clubName).setValue(club).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mAllClubsRef.child(clubName).child(getApplicationContext().getString(R.string.firebase_tags_tag)).setValue(tags);
                                mAllClubsRef.child(clubName).child(getApplicationContext().getString(R.string.firebase_clubmembers_tag)).child(mAuth.getCurrentUser().getDisplayName()).setValue(clubMembers);
                                mAllClubsRef.child(clubName).child(getApplicationContext().getString(R.string.firebase_lower_case_club_name_tag)).setValue(clubName.toLowerCase());
                                checkIfFollowingClubsIndexExist();

                                mAllClubsRef.child(clubName).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d(TAG, "followthisclub, club name: " + dataSnapshot);
                                        Clubs club = dataSnapshot.getValue(Clubs.class);
                                        mUserRef.child(getApplicationContext().getString(R.string.firebase_following_clubs_tag)).child(clubName).setValue(club);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Intent clubProfilePage = new Intent(getApplicationContext(), ClubProfileActivity.class);
                                clubProfilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.d(TAG, "mDoneBtn.setOnClickListener Intent Extra: " + clubName);
                                clubProfilePage.putExtra(ClubProfileActivity.EXTRA, clubName);
                                startActivity(clubProfilePage);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * SetSingleChipListener adds Listener to all tags
     *
     * @param mChip [a specific Chip]
     */
    private void SetSingleChipListener(final Chip mChip) {
        mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String tagName = buttonView.getText().toString().substring(1);

                switch (tagName) {
                    default:
                    case "academic":
                        if (isChecked) {
                            tags.setAcademic(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setAcademic(false);
                            checkCount--;
                        }
                        break;
                    case "art":
                        if (isChecked) {
                            tags.setArt(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setArt(false);
                            checkCount--;
                        }
                        break;
                    case "business":
                        if (isChecked) {
                            tags.setBusiness(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setBusiness(false);
                            checkCount--;
                        }
                        break;
                    case "comedy":
                        if (isChecked) {
                            tags.setComedy(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setComedy(false);
                            checkCount--;
                        }
                        break;
                    case "community_service":
                        if (isChecked) {
                            tags.setCommunity_service(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setCommunity_service(false);
                            checkCount--;
                        }
                        break;
                    case "culture":
                        if (isChecked) {
                            tags.setCulture(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setCulture(false);
                            checkCount--;
                        }
                        break;
                    case "dance":
                        if (isChecked) {
                            tags.setDance(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setDance(false);
                            checkCount--;
                        }
                        break;
                    case "engineering":
                        if (isChecked) {
                            tags.setEngineering(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setEngineering(false);
                            checkCount--;
                        }
                        break;
                    case "ethnic":
                        if (isChecked) {
                            tags.setEthnic(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setEthnic(false);
                            checkCount--;
                        }
                        break;
                    case "gaming":
                        if (isChecked) {
                            tags.setGaming(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setGaming(false);
                            checkCount--;
                        }
                        break;
                    case "greek":
                        if (isChecked) {
                            tags.setGreek(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setGreek(false);
                            checkCount--;
                        }
                        break;
                    case "lifestyle":
                        if (isChecked) {
                            tags.setLifestyle(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setLifestyle(false);
                            checkCount--;
                        }
                        break;
                    case "martial_arts":
                        if (isChecked) {
                            tags.setMartial_arts(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setMartial_arts(false);
                            checkCount--;
                        }
                        break;
                    case "media":
                        if (isChecked) {
                            tags.setMedia(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setMedia(false);
                            checkCount--;
                        }
                        break;
                    case "medical":
                        if (isChecked) {
                            tags.setMedical(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setMedical(false);
                            checkCount--;
                        }
                        break;
                    case "music":
                        if (isChecked) {
                            tags.setMusic(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setMusic(false);
                            checkCount--;
                        }
                        break;
                    case "performing_arts":
                        if (isChecked) {
                            tags.setPerforming_arts(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setPerforming_arts(false);
                            checkCount--;
                        }
                        break;
                    case "programming":
                        if (isChecked) {
                            tags.setProgramming(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setProgramming(false);
                            checkCount--;
                        }
                        break;
                    case "religion":
                        if (isChecked) {
                            tags.setReligion(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setReligion(false);
                            checkCount--;
                        }
                        break;
                    case "science":
                        if (isChecked) {
                            tags.setScience(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setScience(false);
                            checkCount--;
                        }
                        break;
                    case "self_improvement":
                        if (isChecked) {
                            tags.setSelf_improvement(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setSelf_improvement(false);
                            checkCount--;
                        }
                        break;
                    case "social":
                        if (isChecked) {
                            tags.setSocial(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setSocial(false);
                            checkCount--;
                        }
                        break;
                    case "sports":
                        if (isChecked) {
                            tags.setSports(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setSports(false);
                            checkCount--;
                        }
                        break;
                    case "technology":
                        if (isChecked) {
                            tags.setTechnology(true);
                            checkCount++;
                        }
                        if (!isChecked) {
                            tags.setTechnology(false);
                            checkCount--;
                        }
                        break;
                }
            }
        });
    }

    private void MaximumChip(int checkCount, Chip chip) {
        if (checkCount > 5) {
            chip.setChecked(false);
            Snackbar sn = Snackbar.make(findViewById(android.R.id.content), "Maximum 5 tags are allowed", Snackbar.LENGTH_LONG);
            View view = sn.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.parseColor("#FFD700"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            sn.show();
            this.checkCount--;
        }
    }

    /**
     * checkIfFollowingClubsIndexExist checks if the following_clubs index under user's profile exists
     */
    private void checkIfFollowingClubsIndexExist() {
        Log.d(TAG, "checkIfFollowingClubsIndexExist");

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getApplicationContext().getString(R.string.firebase_following_clubs_tag))) {
                    mUserRef.child(getApplication().getString(R.string.firebase_following_clubs_tag)).setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
