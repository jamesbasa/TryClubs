package com.ucsd.tryclubs.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucsd.tryclubs.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingClubsViewHolder extends RecyclerView.ViewHolder{

    public View mView;
    public CircleImageView mImageView;
    public TextView mTextView;
    public LinearLayout linearLayout;

    public FollowingClubsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mImageView = itemView.findViewById(R.id.clublist_circleImageView);
        mTextView = itemView.findViewById(R.id.clublist_clubdescription);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.clublist_linearlayout);

    }



}
