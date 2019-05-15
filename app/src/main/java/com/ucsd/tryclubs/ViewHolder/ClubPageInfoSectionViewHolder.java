package com.ucsd.tryclubs.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ucsd.tryclubs.R;

public class ClubPageInfoSectionViewHolder extends RecyclerView.ViewHolder {

    public View mView;
    public TextView mName_textView;
    public TextView mEmail_textView;

    public ClubPageInfoSectionViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        mName_textView = itemView.findViewById(R.id.club_profile_info_name_textView);
        mEmail_textView = itemView.findViewById(R.id.club_profile_info_email_textView);
    }
}
