package com.ucsd.tryclubs.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ucsd.tryclubs.Model.Clubs;
import com.ucsd.tryclubs.R;

public class ClubPagePurposeSectionViewHolder extends RecyclerView.ViewHolder {

    public View mView;
    public TextView mPurpose_textView;

    public ClubPagePurposeSectionViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mView = itemView;
        mPurpose_textView = itemView.findViewById(R.id.club_profile_purpose_textView);
    }

    public void setPurpose(Clubs clubs) {
        String purpose = clubs.getPurpose();
        mPurpose_textView.setText(purpose);
    }
}
