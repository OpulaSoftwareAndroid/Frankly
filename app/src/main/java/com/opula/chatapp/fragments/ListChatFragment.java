package com.opula.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import com.luseen.spacenavigation.SpaceNavigationView;
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

    private RecyclerView recyclerView, recycler_view1;
    private UserAdapter userAdapter, broadcastAdapter;
    private List<User> mUsers;
    private List<BroadcastUser> mBroadcast;
    FirebaseUser fuser;
    DatabaseReference reference;
    Chatlist chatlist;
    private List<Chatlist> usersList;
    private List<String> broadcastList;
    ConstraintLayout task_list;
    LinearLayout no_chat;
    String TAG="ListChatFragment";
    SpaceNavigationView spaceNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_chats, container, false);

        MainActivity.showpart1();
        MainActivity.showFloatingActionButton();
        WsConstant.ismain = "p";

        recyclerView = view.findViewById(R.id.recycler_view);
        recycler_view1 = view.findViewById(R.id.recycler_view1);
        task_list = view.findViewById(R.id.task_list);
        no_chat = view.findViewById(R.id.no_chat);
        spaceNavigationView = getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view1.setHasFixedSize(true);
        recycler_view1.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();
        broadcastList = new ArrayList<>();

        getChats();
        getBroadcast();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    public void getChats() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    usersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        chatlist = snapshot.getValue(Chatlist.class);
                        if (!("group".equalsIgnoreCase(snapshot.getKey()) || ("broadcast".equalsIgnoreCase(snapshot.getKey())))) {
                            Log.d(TAG,"jigar the message snapshot have "+ snapshot.getValue() + "//" + chatlist);
                            usersList.add(chatlist);
                        }
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

    public void getBroadcast() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child("broadcast");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    broadcastList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        broadcastList.add(snapshot.getKey());
                    }
                    broadcastList();
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
                            if (chatlist.getId().equalsIgnoreCase(user.getId())) {
                                mUsers.add(user);
                                Log.d(TAG,"jigar the message chat list  have "+ chatlist.getId());

                            }
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers, mBroadcast, true, true);
                    WsConstant.check = "fragment";
                    setAdapter(userAdapter, broadcastAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void broadcastList() {
        mBroadcast = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Broadcast");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    broadcastAdapter = new UserAdapter(getContext(), mUsers, mBroadcast, true, false);
                    WsConstant.check = "fragment";
                    setAdapter(userAdapter, broadcastAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAdapter(UserAdapter userAdapter, UserAdapter broadcastAdapter) {

        if (userAdapter.getItemCount() == 0 && broadcastAdapter.getItemCount() == 0) {
            task_list.setVisibility(View.GONE);
            no_chat.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(userAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
            recycler_view1.setAdapter(broadcastAdapter);
        }

//        if (userAdapter.getItemCount() > 0 || broadcastAdapter.getItemCount() > 0) {
//            recyclerView.setAdapter(userAdapter);
//            recycler_view1.setAdapter(broadcastAdapter);
//        } else {
//            task_list.setVisibility(View.GONE);
//            no_chat.setVisibility(View.VISIBLE);
//        }

       /* if (userAdapter.getItemCount() > 0) {
            if (broadcastAdapter.getItemCount() > 0) {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
                //recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(userAdapter);
            } else {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
               // recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(userAdapter);
            }
        } else if (broadcastAdapter.getItemCount() > 0) {
            if (userAdapter.getItemCount() > 0) {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
                //recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(userAdapter);
            } else {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
               // recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(userAdapter);
            }
        } else {
            // listView  empty
            task_list.setVisibility(View.GONE);
            no_chat.setVisibility(View.VISIBLE);
        }*/
    }

}
