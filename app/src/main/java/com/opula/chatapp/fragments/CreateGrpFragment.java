package com.opula.chatapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGrpFragment extends Fragment {

    private ListView listMember;
    private ItemsListAdapter userAdapter;
    private ArrayList<User> mUsers;
    LinearLayout createNewGrpLayout;
    int count = 0;
    Button btnNext;
    TextView txtSelectedCount;
    StringBuilder commaSepValueBuilder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_grp, container, false);

        MainActivity.hideFloatingActionButton();

        initViews(view);

        mUsers = new ArrayList<>();

        readUsers();

        commaSepValueBuilder = new StringBuilder();
        txtSelectedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (commaSepValueBuilder.toString().equalsIgnoreCase("") ) {
                    Toast.makeText(getContext(), "Please select members", Toast.LENGTH_SHORT).show();
                } else {
                    CreateGroupDetailFragment ldf = new CreateGroupDetailFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Bundle args = new Bundle();
                    args.putString("GrpList", commaSepValueBuilder.toString());
                    ldf.setArguments(args);
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, ldf).addToBackStack(null).commit();
                }
            }
        });

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
        CircleImageView item_profile;
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }
                userAdapter = new ItemsListAdapter(getActivity(), mUsers);
                WsConstant.check = "activity";
                listMember.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<User> oricoininfo;
        private ArrayList<User> origPlanetList;
        private Filter planetFilter;

        ItemsListAdapter(Context c, ArrayList<User> l) {
            context = c;
            oricoininfo = l;
            origPlanetList = l;
        }

        @Override
        public int getCount() {
            return oricoininfo.size();
        }

        public void resetData() {
            oricoininfo = origPlanetList;
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
