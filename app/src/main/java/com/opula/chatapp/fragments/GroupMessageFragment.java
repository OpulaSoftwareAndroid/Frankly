package com.opula.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class GroupMessageFragment extends Fragment {

    DatabaseReference reference;
    SharedPreference sharedPreference;
    String groupUserId;
    RecyclerView recyclerView;
    RelativeLayout btn_send;
    CircleImageView imgUser;
    LinearLayout imgBack, groupName;
    EmojiconEditText text_send;
    ImageView emojiButton, send_image;
    TextView txtUserName, txtCheckActive;
    View rootView;
    StringBuilder commaSepValueBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_message, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);

        initViews(view);

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final GroupUser user = dataSnapshot.getValue(GroupUser.class);
                assert user != null;
                txtUserName.setText(user.getGroupName());
                txtCheckActive.setText(user.getMemberList().size()+" Members");
                if (user.getImageURL().equals("default")) {
                    imgUser.setImageResource(R.drawable.image_boy);
                } else {
                    try {
                        Log.d("Image", user.getImageURL());
                        Picasso.get().load(user.getImageURL())
                                .placeholder(R.drawable.image_boy).error(R.drawable.image_boy)
                                .into(imgUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

               /* for (int i = 0; i < user.getMemberList().size(); i++) {
                    commaSepValueBuilder = new StringBuilder();
                    final int finalI = i;
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(user.getMemberList().get(finalI)));
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            User u1 = dataSnapshot.getValue(User.class);
                            commaSepValueBuilder.append(u1.getEmail());
                            if (finalI != user.getMemberList().size()) {
                                commaSepValueBuilder.append(",");
                                txtCheckActive.setText(commaSepValueBuilder);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideFloatingActionButton();
                MainActivity.checkChatTheme(getContext());
                MainActivity.showpart2();

                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });


        return view;
    }

    private void initViews(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);
        btn_send = view.findViewById(R.id.btn_send);
        text_send = view.findViewById(R.id.text_send);
        imgBack = view.findViewById(R.id.imgBack);
        imgUser = view.findViewById(R.id.imgUser);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtCheckActive = view.findViewById(R.id.txtCheckActive);
        rootView = view.findViewById(R.id.root_view);
        emojiButton = view.findViewById(R.id.emoji_btn);
        send_image = view.findViewById(R.id.send_image);
        groupName = view.findViewById(R.id.groupName);
    }
}
