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

import com.opula.chatapp.MainActivity;
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

        MainActivity.hideFloatingActionButton();

        mImageView = (ImageView) view.findViewById(R.id.image_PersonalInfo_DP);


        return view;
    }
}
