package com.opula.chatapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class ShowStatusFragment extends Fragment {
    private ArrayList<User> userList;
    int count = 0;
    StringBuilder commaSepValueBuilder;
    String groupUserId;
    SharedPreference sharedPreference;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_status, container, false);

        MainActivity.hideFloatingActionButton();

        initViews(view);

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

        return view;


    }

    private void initViews(View view) {

    }



}
