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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddMemberGroupFragment extends Fragment {
    private ListView listMember;
    private MemberListAdapter userAdapter;
    private ArrayList<User> userList;
    LinearLayout createNewGrpLayout;
    int count = 0;
    Button btnNext;
    TextView txtSelectedCount;
    StringBuilder commaSepValueBuilder;
    String groupUserId;
    SharedPreference sharedPreference;
    DatabaseReference reference;
    List<String> grpList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_grp_member, container, false);

        MainActivity.hideFloatingActionButton();
        grpList = new ArrayList<>();
        initViews(view);

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);

        readUsers();

        commaSepValueBuilder = new StringBuilder();

//        txtSelectedCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < userList.size(); i++) {
//                    if (userList.get(i).isChecked()) {
//                        commaSepValueBuilder.append(userList.get(i).getId());
//                        if (i != userList.size()) {
//                            commaSepValueBuilder.append(",");
//                        }
//                    }
//                }
//                if (commaSepValueBuilder.length() > 0) {
//                    commaSepValueBuilder.deleteCharAt(commaSepValueBuilder.lastIndexOf(","));
//                }
//
//            }
//        });
//
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                txtSelectedCount.performClick();
//                if (commaSepValueBuilder.toString().equalsIgnoreCase("")) {
//                    Toast.makeText(getContext(), "Please select members", Toast.LENGTH_SHORT).show();
//                } else {
//                    CreateGroupDetailFragment ldf = new CreateGroupDetailFragment();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    Bundle args = new Bundle();
//                    args.putString("GrpList", commaSepValueBuilder.toString());
//                    ldf.setArguments(args);
//                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, ldf).addToBackStack(null).commit();
//                }
//            }
//        });


        return view;


    }

    private void initViews(View view) {
        listMember = view.findViewById(R.id.listMember);
        btnNext = view.findViewById(R.id.btnNext);
        txtSelectedCount = view.findViewById(R.id.txtSelectedCount);
        createNewGrpLayout = view.findViewById(R.id.createNewGrpLayout);
    }


    static class ViewHolder {
        CheckBox checkBox;
        TextView txtName, txtNumber;
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
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.checkBox.setChecked(oricoininfo.get(position).checked);
            viewHolder.txtName.setText(oricoininfo.get(position).getUsername() + "");
            viewHolder.txtNumber.setText(oricoininfo.get(position).getEmail() + "");
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