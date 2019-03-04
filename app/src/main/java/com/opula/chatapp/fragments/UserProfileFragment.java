package com.opula.chatapp.fragments;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.adapter.NewChatUserAdapter;
import com.opula.chatapp.adapter.UserSharedAdapter;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.Chatlist;
import com.opula.chatapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    SharedPreference sharedPreference;
    String userid;
    TextView txtName, txtEmail, text_no_image, txtViewAll;
    TextView txtOnOff;
    LinearLayout imgBack,linearLayoutMainMedia;
//    CircleImageView image_PersonalInfo_DP;
    ImageView image_PersonalInfo_DP;
    Switch chkNotification;
    DatabaseReference reference;
    RecyclerView recycler_image;
    private List<Chat> mchat;
    private UserSharedAdapter userSharedAdapter;
    FirebaseUser fuser;
    public static FirebaseUser firebaseUser;
    String image;
    ImageView imageViewChat;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userprofile, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        userid = sharedPreference.getValue(getActivity(), WsConstant.userId);
        initViews(view);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_image.setLayoutManager(horizontalLayoutManagaer);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Log.d("DATAA", dataSnapshot.getValue() + "/");
                    String email = dataSnapshot.child("email").getValue().toString();
                    image = dataSnapshot.child("imageURL").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    txtEmail.setText(email);
                    txtName.setText(username);
                    if (image.equals("default")) {
                        image_PersonalInfo_DP.setImageResource(R.drawable.image_boy);
                    } else {

                        Picasso.get().load(image)
                                .fit()
                                .into(image_PersonalInfo_DP);
//                        Picasso.with(getContext())
//                                .load(image)
//                                .fitCenter()
//                                .into(image_PersonalInfo_DP);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MessageFragment()).addToBackStack(null).commit();
            }
        });
        image_PersonalInfo_DP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());

                LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dailog_show_image, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setCancelable(true);

                final ImageView image2 = (ImageView) dialogView.findViewById(R.id.image);

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

                if (image.equals("default")) {
                    Toast.makeText(getActivity(), "No Image Found..!", Toast.LENGTH_SHORT).show();
                } else {
                    AppGlobal.showProgressDialog(getActivity());
                    Glide.with(getActivity()).load(image)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    AppGlobal.hideProgressDialog(getActivity());
                                    Toast.makeText(getActivity(), "No Image Found!" + model + "/" + e, Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    AppGlobal.hideProgressDialog(getActivity());
                                    alertDialog.show();
                                    return false;
                                }
                            })
                            .into(image2);
                }
            }
        });

        txtViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showpart2();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ViewAllSharedMediaFragment()).addToBackStack(null).commit();

            }
        });
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Chatlist chatlist = dataSnapshot.getValue(Chatlist.class);
//                assert chatlist != null;
//                if (chatlist.getIsnotification()){
//                    chkNotification.setChecked(true);
//                } else {
//                    chkNotification.setChecked(false);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        chkNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (chkNotification.isChecked()){
//                    final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(fuser.getUid());
//                    chatRefReceiver.child("id").setValue(fuser.getUid());
//                    chatRefReceiver.child("isnotification").setValue(false);
//
//                    txtOnOff.setText("Off");
//                } else {
//
//                    final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(fuser.getUid());
//                    chatRefReceiver.child("id").setValue(fuser.getUid());
//                    chatRefReceiver.child("isnotification").setValue(true);
//
//                    txtOnOff.setText("On");
//                }
//            }
//        });
        chkNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    /*final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(fuser.getUid());
                    chatRefReceiver.child("id").setValue(fuser.getUid());
                    chatRefReceiver.child("isnotification").setValue(true);*/

                    txtOnOff.setText("On");
                } else {

                    /*final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(fuser.getUid());
                    chatRefReceiver.child("id").setValue(fuser.getUid());
                    chatRefReceiver.child("isnotification").setValue(false);*/

                    txtOnOff.setText("Off");
                }
            }
        });

        readMesagges(fuser.getUid(), userid);

        return view;
    }

    private void initViews(View view) {
        chkNotification = view.findViewById(R.id.chkNotification);
        txtOnOff = view.findViewById(R.id.txtOnOff);
        imgBack = view.findViewById(R.id.imgBack);
        image_PersonalInfo_DP = view.findViewById(R.id.image_PersonalInfo_DP);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        recycler_image = view.findViewById(R.id.recycler_image);
        txtViewAll = view.findViewById(R.id.txtViewAll);
        text_no_image = view.findViewById(R.id.text_no_image);
        linearLayoutMainMedia=view.findViewById(R.id.linearLayoutMainMedia);
        imageViewChat=view.findViewById(R.id.imageViewChat);
    }

    private void readMesagges(final String myid, final String userid) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mchat.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;

                        if (chat.getTo().equalsIgnoreCase("personal")) {
                            if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                                if (chat.isIsimage()) {
                                    mchat.add(chat);
                                }
                            }
                        }
                        userSharedAdapter = new UserSharedAdapter(getActivity(), mchat);
                        recycler_image.setAdapter(userSharedAdapter);

                        if (userSharedAdapter.getItemCount() > 0) {
                            // listView not empty
                            linearLayoutMainMedia.setVisibility(View.VISIBLE);
                            txtViewAll.setText("View All");
                            recycler_image.setVisibility(View.VISIBLE);
                            text_no_image.setVisibility(View.GONE);
                            recycler_image.setAdapter(userSharedAdapter);
                        } else {
                            // listView  empty
                            linearLayoutMainMedia.setVisibility(View.GONE);
                            recycler_image.setVisibility(View.GONE);
                            text_no_image.setVisibility(View.VISIBLE);
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
    }

}
