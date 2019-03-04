package com.opula.chatapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.HashMap;
import java.util.List;

public class AddMemberGroupFragment extends Fragment {
    private ListView listMember;
    private MemberListAdapter userAdapter;
    private ArrayList<User> userList;
    int count = 0;
    Button btnNext;
    TextView txtSelectedCount;
    StringBuilder commaSepValueBuilder;
    String groupUserId;
    SharedPreference sharedPreference;
    DatabaseReference reference;
    List<String> grpList;
    List<String> newMemberList;
    LinearLayout no_chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_grp_member, container, false);

        MainActivity.hideFloatingActionButton();
        grpList = new ArrayList<>();

        initViews(view);

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

        readUsers();

        txtSelectedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commaSepValueBuilder = new StringBuilder();
                newMemberList = new ArrayList<>();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).isChecked()) {
                        newMemberList.add(userList.get(i).getId());
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSelectedCount.performClick();
                grpList.addAll(newMemberList);
                Log.d("All_Member", grpList + "/");

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot d1) {
                        try {
                            if (d1.exists()) {
                                ref.child("memberList").setValue(grpList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                for (int i = 0; i < grpList.size(); i++) {
                    final int finalI = i;
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("Chatlist")) {
                                // run some code
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(grpList.get(finalI));
                                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                                Log.d("GroupChat", d1.getValue() + "//");
                                                final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(dataSnapshot.getKey()).child("group");
                                                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                        if (dataSnapshot1.getValue() == null) {
                                                            reference2.child(groupUserId).setValue(groupUserId);
                                                        } else {
                                                            reference2.child(groupUserId).setValue(groupUserId);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        } else if (!dataSnapshot.exists()) {
                                            final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(grpList.get(finalI)).child("group");
                                            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                    if (dataSnapshot1.getValue() == null) {
                                                        reference2.child(groupUserId).setValue(groupUserId);
                                                    } else {
                                                        reference2.child(groupUserId).setValue(groupUserId);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist");
                                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(grpList.get(finalI));
                                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                                        Log.d("GroupChat", d1.getValue() + "//");
                                                        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(dataSnapshot.getKey()).child("group");
                                                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                                if (dataSnapshot1.getValue() == null) {
                                                                    reference2.child(groupUserId).setValue(groupUserId);
                                                                } else {
                                                                    reference2.child(groupUserId).setValue(groupUserId);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                } else if (!dataSnapshot.exists()) {
                                                    final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(grpList.get(finalI)).child("group");
                                                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                            if (dataSnapshot1.getValue() == null) {
                                                                reference2.child(groupUserId).setValue(groupUserId);

                                                            } else {
                                                                reference2.child(groupUserId).setValue(groupUserId);

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                getActivity().onBackPressed();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });


        return view;


    }

    private void initViews(View view) {
        listMember = view.findViewById(R.id.listMember);
        btnNext = view.findViewById(R.id.btnNext);
        txtSelectedCount = view.findViewById(R.id.txtSelectedCount);
        no_chat = view.findViewById(R.id.no_chat);
    }


    static class ViewHolder {
        CheckBox checkBox;
        TextView txtName, txtNumber;
        ImageView item_profile;
    }

    private void readUsers() {
        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    GroupUser grpuser = dataSnapshot.getValue(GroupUser.class);
                    assert grpuser != null;
                    grpList.addAll(grpuser.getMemberList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        compareMember();

    }


    private void compareMember() {
        userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (!(grpList.contains(user.getId()))) {
                            userList.add(user);
                        }
                    }
                    userAdapter = new MemberListAdapter(getActivity(), userList);
                    WsConstant.check = "activity";


                    if (userList.size() > 0) {
                        // listView not empty
                        listMember.setVisibility(View.VISIBLE);
                        no_chat.setVisibility(View.GONE);
                        listMember.setAdapter(userAdapter);
                    } else {
                        // listView  empty
                        listMember.setVisibility(View.GONE);
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


    public class MemberListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<User> oricoininfo;
        private Filter planetFilter;

        MemberListAdapter(Context c, ArrayList<User> l) {
            context = c;
            oricoininfo = l;
        }

        @Override
        public int getCount() {
            return oricoininfo.size();
        }


        @Override
        public Object getItem(int position) {
            return oricoininfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return oricoininfo.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.group_user_item, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
                viewHolder.txtNumber = (TextView) rowView.findViewById(R.id.txtNumber);
                viewHolder.txtName = (TextView) rowView.findViewById(R.id.txtName);
                viewHolder.item_profile = (ImageView) rowView.findViewById(R.id.item_profile);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.checkBox.setChecked(oricoininfo.get(position).checked);
            viewHolder.txtName.setText(oricoininfo.get(position).getUsername() + "");
            viewHolder.txtNumber.setText(oricoininfo.get(position).getEmail() + "");
            if (oricoininfo.get(position).getImageURL().equals("default")) {
                viewHolder.item_profile.setImageResource(R.drawable.image_boy);
            } else {
                Glide.with(context).load(oricoininfo.get(position).getImageURL()).into(viewHolder.item_profile);
            }
            viewHolder.checkBox.setTag(position);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !oricoininfo.get(position).isChecked();
                    if (newState == true) {
                        count++;
                    } else if (newState == false) {
                        count--;
                    }
                    oricoininfo.get(position).checked = newState;
                    txtSelectedCount.setText(count + " Selected");
                }
            });
            return rowView;


        }
    }


}
