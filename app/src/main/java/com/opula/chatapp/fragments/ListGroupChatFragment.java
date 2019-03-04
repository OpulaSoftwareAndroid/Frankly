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
import com.luseen.spacenavigation.SpaceNavigationView;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.GroupUserAdapter;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.GroupUser;

import java.util.ArrayList;
import java.util.List;

public class ListGroupChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupUserAdapter userAdapter;
    private List<GroupUser> mUsers;
    FirebaseUser fuser;
    DatabaseReference reference;
    private List<String> usersList;
    LinearLayout no_chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_group_chats, container, false);

        MainActivity.showpart1();
        MainActivity.hideFloatingActionButton();
        WsConstant.ismain = "g";

        recyclerView = view.findViewById(R.id.recycler_view);
        no_chat = view.findViewById(R.id.no_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mUsers = new ArrayList<>();
        usersList = new ArrayList<>();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Chatlist");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshott) {
                try {
                    if (dataSnapshott.hasChild(fuser.getUid())) {
                        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child("group");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                usersList.clear();
                                try {
                                    if (dataSnapshott.child(fuser.getUid()).hasChild("group")) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//                                        for (DataSnapshot d1 : snapshot.getChildren()) {
                                            usersList.add(snapshot.getKey());
//                                        }
                                            groupList();
                                        }
                                    } else {
                                        if (mUsers.size() > 0) {
                                            // listView not empty
                                            recyclerView.setVisibility(View.VISIBLE);
                                            no_chat.setVisibility(View.GONE);
                                        } else {
                                            // listView  empty
                                            recyclerView.setVisibility(View.GONE);
                                            no_chat.setVisibility(View.VISIBLE);
                                        }
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        if (mUsers.size() > 0) {
                            // listView not empty
                            recyclerView.setVisibility(View.VISIBLE);
                            no_chat.setVisibility(View.GONE);
                        } else {
                            // listView  empty
                            recyclerView.setVisibility(View.GONE);
                            no_chat.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void groupList() {
        mUsers = new ArrayList<>();
        DatabaseReference referenceq = FirebaseDatabase.getInstance().getReference("Groups");
        referenceq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        mUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GroupUser user = snapshot.getValue(GroupUser.class);
                            for (String list : usersList) {
                                if (!list.equalsIgnoreCase("id")) {
                                    assert user != null;
                                    if (user.getGroupId().equals(list)) {
                                        Log.d("GroupChat", list + "/");
                                        mUsers.add(user);
                                    }
                                }
                            }
                        }
                    }
                    userAdapter = new GroupUserAdapter(getContext(), mUsers, true);
                    WsConstant.check = "fragment";

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
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
