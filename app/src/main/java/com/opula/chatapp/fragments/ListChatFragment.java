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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chatlist;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListChatFragment extends Fragment {

    private RecyclerView recyclerView, recycler_view1;
    private UserAdapter userAdapter, broadcastAdapter;
    private List<User> mUsers;
    private List<BroadcastUser> mBroadcast;
    FirebaseUser fuser;
    DatabaseReference reference;
    Chatlist chatlist;
    ArrayList <String> arrayListIsSecure;
    private List<Chatlist> usersList;
    private List<String> broadcastList;
    ConstraintLayout task_list;
    LinearLayout no_chat;
    static SearchView searchViewChatList;
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
        searchViewChatList=view.findViewById(R.id.searchViewChatList);
        searchViewChatList.setVisibility(View.GONE);
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
        arrayListIsSecure=new ArrayList<>();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String strFragmentName = bundle.getString(WsConstant.FRAGMENT_NAME, "");
            Log.d(TAG,"jigar the fragment we came is "+strFragmentName);
            searchViewChatList.setVisibility(View.VISIBLE);
        }
        getChats();
        getBroadcast();

        searchViewChatList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //if(usersList.contains(query))
                {
                    Log.d(TAG,"jigar the query to send search is "+query);
                    userAdapter.getFilter().filter(query);
                }
//                else{
//                    Toast.makeText(getContext(), "No Match found",Toast.LENGTH_LONG).show();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.d(TAG,"jigar the query to send search is "+query);
                if(userAdapter!=null) {
                    userAdapter.getFilter().filter(query);
                }
                return false;

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }
    public static void setSearchViewVisibility(boolean boolStatus){
        if(boolStatus) {
          //  searchViewChatList.setVisibility(View.VISIBLE);
        }else
        {
        //    searchViewChatList.setVisibility(View.GONE);
        }
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
                        //    Log.d(TAG,"jigar the message snapshot have "+ snapshot.getValue() + "//" + chatlist);
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid()).child("broadcast");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    broadcastList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        broadcastList.add(snapshot.getKey());
                        Log.d(TAG,"jigar the message from broad cast chat list  have " );
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
                        String strUserChatID=chatlist.getId();

                        for (Chatlist chatlist : usersList) {
                            assert user != null;
                            if (chatlist.getId().equalsIgnoreCase(user.getId())) {
                                mUsers.add(user);
                                arrayListIsSecure.add(String.valueOf(chatlist.getIssecure()));
                                strUserChatID=strUserChatID+","+chatlist.getId();
//                                String strisChatSecure=chatlist.getIssecure();
                                Log.d(TAG,"jigar the chat is secure in chat list have "+chatlist.getIssecure());
                            }
                            Log.d(TAG,"jigar the message chat list  have "+ strUserChatID);
                        }
                    }

//                    userAdapter = new UserAdapter(getContext(), mUsers, mBroadcast, true, true);
                    userAdapter = new UserAdapter(getContext(), mUsers,usersList, mBroadcast, true, true);

//                   userAdapter = new UserAdapter(getContext(),spaceNavigationView, mUsers, mBroadcast, true, true);

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
//                    broadcastAdapter = new UserAdapter(getContext(), mUsers,broadcastList, mBroadcast, true, true);

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

////        if (userAdapter.getItemCount() > 0 || broadcastAdapter.getItemCount() > 0) {
////            recyclerView.setAdapter(userAdapter);
////            recycler_view1.setAdapter(broadcastAdapter);
////        } else {
////            task_list.setVisibility(View.GONE);
////            no_chat.setVisibility(View.VISIBLE);
////        }
//       /* if (userAdapter.getItemCount() > 0) {
//            if (broadcastAdapter.getItemCount() > 0) {
//                task_list.setVisibility(View.VISIBLE);
//                no_chat.setVisibility(View.GONE);
//                //recycler_view1.setAdapter(broadcastAdapter);
//                recyclerView.setAdapter(userAdapter);
//            } else {
//                task_list.setVisibility(View.VISIBLE);
//                no_chat.setVisibility(View.GONE);
//               // recycler_view1.setAdapter(broadcastAdapter);
//                recyclerView.setAdapter(userAdapter);
//            }
//        } else if (broadcastAdapter.getItemCount() > 0) {
//            if (userAdapter.getItemCount() > 0) {
//                task_list.setVisibility(View.VISIBLE);
//                no_chat.setVisibility(View.GONE);
//                //recycler_view1.setAdapter(broadcastAdapter);
//                recyclerView.setAdapter(userAdapter);
//            } else {
//                task_list.setVisibility(View.VISIBLE);
//                no_chat.setVisibility(View.GONE);
//               // recycler_view1.setAdapter(broadcastAdapter);
//                recyclerView.setAdapter(userAdapter);
//            }
//        } else {
//            // listView  empty
//            task_list.setVisibility(View.GONE);
//            no_chat.setVisibility(View.VISIBLE);
//        }*/
    }

}
