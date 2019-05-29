package com.ucsd.tryclubs.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.ucsd.tryclubs.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public View mView;
    public TextView mClubName;
    public TextView mDateTime;
    public TextView mTitle;
    public TextView mDescription;
    public TextView mLocation;
    public ImageView mTimelineImg;
    public LikeButton mLike;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        mClubName = (TextView) itemView.findViewById(R.id.timeline_clubName);
        mClubName.setSelected(true);
        mDateTime = (TextView) itemView.findViewById(R.id.timeline_date);
        mTitle = (TextView) itemView.findViewById(R.id.timeline_title);
        mDescription = (TextView) itemView.findViewById(R.id.timeline_description);
        mLocation = (TextView) itemView.findViewById(R.id.timeline_location);
        mTimelineImg = (ImageView) itemView.findViewById(R.id.timeline_img);
        mLike = (LikeButton) itemView.findViewById(R.id.timeline_like);
    }
}
