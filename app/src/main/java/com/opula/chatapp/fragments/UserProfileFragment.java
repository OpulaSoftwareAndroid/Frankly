package com.opula.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    SharedPreference sharedPreference;
    String userid;
    TextView txtName, txtEmail;
    TextView txtOnOff;
    ImageView imgBack;
    CircleImageView image_PersonalInfo_DP;

    Switch chkNotification;
    DatabaseReference reference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userprofile, container, false);

        sharedPreference = new SharedPreference();
        userid = sharedPreference.getValue(getActivity(), WsConstant.userId);
        initViews(view);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DATAA", dataSnapshot.getValue() + "/");
                String email = dataSnapshot.child("email").getValue().toString();
                String image = dataSnapshot.child("imageURL").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                txtEmail.setText(email);
                txtName.setText(username);
                if (image.equals("default")) {
                    image_PersonalInfo_DP.setImageResource(R.drawable.image_boy);
                } else {
                    Glide.with(getContext()).load(image).into(image_PersonalInfo_DP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chkNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                    txtOnOff.setText("On");
                else if (isChecked == false)
                    txtOnOff.setText("Off");
            }
        });

        return view;
    }

    private void initViews(View view) {
        chkNotification = view.findViewById(R.id.chkNotification);
        txtOnOff = view.findViewById(R.id.txtOnOff);
        imgBack = view.findViewById(R.id.imgBack);
        image_PersonalInfo_DP = view.findViewById(R.id.image_PersonalInfo_DP);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
    }
}
