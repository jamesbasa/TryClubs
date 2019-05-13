package com.ucsd.tryclubs.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucsd.tryclubs.R;

public class FollowingClubsViewHolder extends RecyclerView.ViewHolder {

    public ImageView mImageView;
    public TextView mTextView;

    public FollowingClubsViewHolder(View view) {
        super(view);
        mImageView = view.findViewById(R.id.clublist_circleImageView);
        mTextView = view.findViewById(R.id.clublist_clubdescription);
    }

}
