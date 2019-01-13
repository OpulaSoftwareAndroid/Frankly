package com.opula.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.UserAdapter;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chatlist;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Token;

import java.util.ArrayList;
import java.util.List;


public class ListChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private List<BroadcastUser> mBroadcast;
    FirebaseUser fuser;
    DatabaseReference reference;
    Chatlist chatlist;
    private List<Chatlist> usersList;
    private List<String> broadcastList;
    LinearLayout no_chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_chats, container, false);

        MainActivity.showpart1();
        MainActivity.showFloatingActionButton();
        WsConstant.ismain = "p";

        recyclerView = view.findViewById(R.id.recycler_view);
        no_chat = view.findViewById(R.id.no_chat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();
        broadcastList = new ArrayList<>();

        getChats();



        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    public void getChats(){
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    usersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        chatlist = snapshot.getValue(Chatlist.class);
                        usersList.add(chatlist);
                    }

                    getBroadcast();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getBroadcast(){
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child("broadcast");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    broadcastList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        broadcastList.add(snapshot.getKey());
                    }
                    chatList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        mBroadcast = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        for (Chatlist chatlist : usersList) {
                            assert user != null;
                            if (user.getId().equals(chatlist.getId())) {
                                mUsers.add(user);
                            }
                        }
                    }

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Broadcast");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                mBroadcast.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    BroadcastUser user = snapshot.getValue(BroadcastUser.class);
                                    for (String list : broadcastList) {
                                        assert user != null;
                                        if (user.getBroadcastId().equals(list)) {
                                            mBroadcast.add(user);
                                        }
                                    }
                                }

                                userAdapter = new UserAdapter(getContext(), mUsers, mBroadcast,true);
                                WsConstant.check = "fragment";

                                setAdapter(userAdapter);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAdapter(UserAdapter userAdapter){
        if (userAdapter.getItemCount() > 0) {
            // listView not empty
            recyclerView.setVisibility(View.VISIBLE);
            no_chat.setVisibility(View.GONE);
            recyclerView.setAdapter(userAdapter);
        } else {
            // listView  empty
            recyclerView.setVisibility(View.GONE);
            no_chat.setVisibility(View.VISIBLE);
        }
    }

}
