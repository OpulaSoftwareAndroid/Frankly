package com.opula.chatapp.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.opula.chatapp.R;

public class GroupProfileFragment extends Fragment {
    Resources mResources;
    Bitmap mBitmap;
    ImageView mImageView;
    float cornerRadius = 40.0f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_profile, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image_PersonalInfo_DP);
        mResources = getResources();
        mBitmap = BitmapFactory.decodeResource(mResources, R.drawable.img2);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                mResources,
                mBitmap
        );

        roundedBitmapDrawable.setCornerRadius(cornerRadius);
        roundedBitmapDrawable.setAntiAlias(true);
        mImageView.setImageDrawable(roundedBitmapDrawable);
        return view;
    }
}
