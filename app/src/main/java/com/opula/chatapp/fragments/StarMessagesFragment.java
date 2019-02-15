package com.opula.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.StarMessageAdapter;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.StarMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StarMessagesFragment extends Fragment {
    RecyclerView recycler_view;
    List<StarMessage> mStarMsg;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StarMessageAdapter starMessageAdapter;
    LinearLayout imgBack;
    LinearLayout no_chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star_message, container, false);

        MainActivity.hideFloatingActionButton();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initViews(view);

        recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        recycler_view.setLayoutManager(linearLayoutManager);
        getAllStarMessages();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        return view;
    }

    private void initViews(View view) {
        recycler_view = view.findViewById(R.id.recycler_view);
        imgBack = view.findViewById(R.id.imgBack);
        no_chat = view.findViewById(R.id.no_chat);
    }

    private void getAllStarMessages() {
        mStarMsg = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("StarMessages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        StarMessage chat = snapshot.getValue(StarMessage.class);
                        assert chat != null;
                        if (chat.getId().equalsIgnoreCase(firebaseUser.getUid())) {
                            no_chat.setVisibility(View.GONE);
                            mStarMsg.add(chat);
                        } else {
                            no_chat.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                starMessageAdapter = new StarMessageAdapter(getContext(), mStarMsg);
                WsConstant.check = "fragment";
                recycler_view.setAdapter(starMessageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
