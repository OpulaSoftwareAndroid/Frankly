package com.opula.chatapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateBroadcastFragment extends Fragment {

    private ListView listMember;
    private ItemsListAdapter userAdapter;
    private ArrayList<User> mUsers;
    int count = 0;
    Button btnNext;
    MaterialEditText txt_broadcast_name;
    TextView txtSelectedCount;
    StringBuilder commaSepValueBuilder;

    List<String> myList;
    List<String> grpList;
    String gropuList;
    FirebaseUser fuser;
    StringBuilder sb;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_broadcast, container, false);

        MainActivity.hideFloatingActionButton();

        initViews(view);

        mUsers = new ArrayList<>();

        readUsers();


        txtSelectedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commaSepValueBuilder = new StringBuilder();
                for (int i = 0; i < mUsers.size(); i++) {
                    if (mUsers.get(i).isChecked()) {
                        commaSepValueBuilder.append(mUsers.get(i).getId());
                        if (i != mUsers.size()) {
                            commaSepValueBuilder.append(",");
                        }
                    }
                }
                if (commaSepValueBuilder.length() > 0) {
                    commaSepValueBuilder.deleteCharAt(commaSepValueBuilder.lastIndexOf(","));
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSelectedCount.performClick();
                if (commaSepValueBuilder.toString().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please select members!", Toast.LENGTH_SHORT).show();
                } else {
                    int i = commaSepValueBuilder.length();
                    if (!(i < 57)) {
                        if (txt_broadcast_name.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), "Please enter broadcast name!", Toast.LENGTH_SHORT).show();
                        } else {
                            createBroadcast();
                        }
                        /*CreateBroadcastDetailFragment ldf = new CreateBroadcastDetailFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        Bundle args = new Bundle();
                        args.putString("GrpList", commaSepValueBuilder.toString());
                        ldf.setArguments(args);
                        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, ldf).commit();*/
                    } else {
                        Toast.makeText(getActivity(), "Please select more than one member!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return view;

    }

    public String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private void createBroadcast() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        myList = new ArrayList<>(Arrays.asList(commaSepValueBuilder.toString().split(",")));
        grpList = new ArrayList<>();
        randomString(9);

        String broadcastName = txt_broadcast_name.getText().toString();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("broadcastName", broadcastName);
        hashMap.put("receiver", myList);
        hashMap.put("sender", fuser.getUid());
        hashMap.put("broadcastId", sb.toString());
        reference.child("Broadcast").child(sb.toString()).setValue(hashMap);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Chatlist")) {
                    // run some code
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                    final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(Objects.requireNonNull(dataSnapshot.getKey())).child("broadcast");
                                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            if (dataSnapshot1.getValue() == null) {
                                                grpList.add(sb.toString());
                                                for (String s : grpList) {
                                                    reference2.child(s).setValue(s);
                                                }
                                            } else {
                                                grpList.add(sb.toString());
                                                for (String s : grpList) {
                                                    reference2.child(s).setValue(s);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            } else if (!dataSnapshot.exists()) {
                                final DatabaseReference reference2 = FirebaseDatabase.getInstance()
                                        .getReference("Chatlist").child(fuser.getUid()).child("broadcast");
                                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                        if (dataSnapshot1.getValue() == null) {
                                            grpList.add(sb.toString());
                                            for (String s : grpList) {
                                                reference2.child(s).setValue(s);
                                            }
                                        } else {
                                            grpList.add(sb.toString());
                                            for (String s : grpList) {
                                                reference2.child(s).setValue(s);
                                            }
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
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                            final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(dataSnapshot.getKey()).child("broadcast");
                                            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                    if (dataSnapshot1.getValue() == null) {
                                                        grpList.add(sb.toString());
                                                        for (String s : grpList) {
                                                            reference2.child(s).setValue(s);
                                                        }
                                                    } else {
                                                        grpList.add(sb.toString());
                                                        for (String s : grpList) {
                                                            reference2.child(s).setValue(s);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    } else if (!dataSnapshot.exists()) {
                                        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child("broadcast");
                                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                if (dataSnapshot1.getValue() == null) {
                                                    grpList.add(sb.toString());
                                                    for (String s : grpList) {
                                                        reference2.child(s).setValue(s);
                                                    }
                                                } else {
                                                    grpList.add(sb.toString());
                                                    for (String s : grpList) {
                                                        reference2.child(s).setValue(s);
                                                    }
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

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();
    }

    private void initViews(View view) {
        listMember = view.findViewById(R.id.listMember);
        btnNext = view.findViewById(R.id.btnNext);
        txtSelectedCount = view.findViewById(R.id.txtSelectedCount);
        txt_broadcast_name = view.findViewById(R.id.txt_broadcast_name);
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);
    }


    static class ViewHolder {
        CheckBox checkBox;
        TextView txtName, txtNumber;
        CircleImageView item_profile;
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        assert firebaseUser != null;
                        assert user != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                    userAdapter = new ItemsListAdapter(getActivity(), mUsers);
                    WsConstant.check = "activity";
                    listMember.setAdapter(userAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class ItemsListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<User> oricoininfo;

        ItemsListAdapter(Context c, ArrayList<User> l) {
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.group_user_item, null);

                viewHolder.checkBox = rowView.findViewById(R.id.rowCheckBox);
                viewHolder.txtNumber = rowView.findViewById(R.id.txtNumber);
                viewHolder.txtName = rowView.findViewById(R.id.txtName);
                viewHolder.item_profile = rowView.findViewById(R.id.item_profile);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.checkBox.setChecked(oricoininfo.get(position).checked);
            viewHolder.txtName.setText(oricoininfo.get(position).getUsername());
            viewHolder.txtNumber.setText(oricoininfo.get(position).getEmail());
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
            Picasso.get().load(oricoininfo.get(position).getImageURL())
                    .placeholder(R.drawable.image_boy).error(R.drawable.image_boy)
                    .into(viewHolder.item_profile);
            return rowView;
        }


    }


}
