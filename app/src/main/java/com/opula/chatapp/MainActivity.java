package com.opula.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.adapter.UserAdapter;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.ListChatFragment;
import com.opula.chatapp.fragments.ListGroupChatFragment;
import com.opula.chatapp.fragments.ListUserFragment;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.MyProfileFragment;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.Chatlist;
import com.opula.chatapp.model.User;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private long mBackPressed;
    private static final int TIME_INTERVAL = 2000;
    public static TextView text, txt_list, txt_chats;
    ImageView img_setting, img_chat;
    public static FragmentManager fragmentManager;
    public static FirebaseUser firebaseUser;
    public static DatabaseReference reference;
    public static FloatingActionButton fab;
    public static LinearLayout part1, part2, part3;
    FirebaseUser mUser;
    FirebaseDatabase mFirebaseInstance;
    FirebaseAuth firebaseAuth;
    SharedPreference sharedPreference;
    LinearLayout imgBack,imgTrash,imgCopy,imgForward,imgStar;
    public static FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            Drawable background = getResources().getDrawable(R.drawable.gradient_bg_splash); //bg_gradient is your gradient.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreference = new SharedPreference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        txt_list = findViewById(R.id.txt_list);
        txt_chats = findViewById(R.id.txt_chats);
        img_setting = findViewById(R.id.img_setting);
        img_chat = findViewById(R.id.img_chat);
        fab = findViewById(R.id.fab);
        part1 = findViewById(R.id.part1);
        part2 = findViewById(R.id.part2);

        part3 = findViewById(R.id.part3);
        imgBack = findViewById(R.id.imgBack);
        imgTrash = findViewById(R.id.imgTrash);
        imgCopy = findViewById(R.id.imgCopy);
        imgForward = findViewById(R.id.imgForward);
        imgStar = findViewById(R.id.imgStar);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();

        showFloatingActionButton();
        showpart1();

        //save userdata
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    sharedPreference.save(MainActivity.this,user.getUsername(),WsConstant.userUsername);
                    sharedPreference.save(MainActivity.this,user.getImageURL(),WsConstant.userImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //sendMessage();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart1();
            }
        });

        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart1();
                MessageAdapter.copyMessage(MainActivity.this);
            }
        });

        imgTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart1();
                MessageAdapter.deletemessage(MainActivity.this);
            }
        });

        imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart1();
            }
        });

        imgForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart1();
                MessageAdapter.forwardMessage(MainActivity.this);
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingActionButton();
                showpart2();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ListUserFragment()).addToBackStack(null).commit();
            }
        });


        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();


        txt_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkListTheme(MainActivity.this);
                String ismain = WsConstant.ismain;
                if (ismain.equalsIgnoreCase("p")) {
                    FragmentManager fragmentGroup = getSupportFragmentManager();
                    fragmentGroup.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();
                } else if (ismain.equalsIgnoreCase("g")) {
                    FragmentManager fragmentPersonal = getSupportFragmentManager();
                    fragmentPersonal.beginTransaction().replace(R.id.frame_mainactivity, new ListGroupChatFragment()).addToBackStack(null).commit();
                }

            }
        });

        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart2();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MyProfileFragment()).addToBackStack(null).commit();
            }
        });

        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkListTheme(MainActivity.this);
                String ismain = WsConstant.ismain;
                if (ismain.equalsIgnoreCase("p")) {
                    img_chat.setImageDrawable(getResources().getDrawable(R.drawable.personal));
                    showpart1();
                    FragmentManager fragmentGroup = getSupportFragmentManager();
                    fragmentGroup.beginTransaction().replace(R.id.frame_mainactivity, new ListGroupChatFragment()).addToBackStack(null).commit();
                } else if (ismain.equalsIgnoreCase("g")) {
                    img_chat.setImageDrawable(getResources().getDrawable(R.drawable.groupchat));
                    showpart1();
                    FragmentManager fragmentPersonal = getSupportFragmentManager();
                    fragmentPersonal.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();
                }
            }
        });
    }

    public static void showpart1() {
        part1.setVisibility(View.VISIBLE);
        part2.setVisibility(View.GONE);
        part3.setVisibility(View.GONE);
    }

    public static void showpart2() {
        part1.setVisibility(View.GONE);
        part2.setVisibility(View.VISIBLE);
        part3.setVisibility(View.GONE);
    }

    public static void showpart3() {
        part1.setVisibility(View.GONE);
        part2.setVisibility(View.GONE);
        part3.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MessageFragment()).addToBackStack(null).commit();
            }
        }
    }

    public static void checkChatTheme(Context context) {
        txt_list.setTextColor(context.getResources().getColor(R.color.black));
        txt_chats.setTextColor(context.getResources().getColor(R.color.white));
        txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_white);
        txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_red);
    }

    public static void checkListTheme(Context context) {
        txt_list.setTextColor(context.getResources().getColor(R.color.white));
        txt_chats.setTextColor(context.getResources().getColor(R.color.black));
        txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_red);
        txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_white);
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_mainactivity);

        if (frag instanceof ListChatFragment || frag instanceof ListGroupChatFragment) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                status("offline");
                MainActivity.this.finishAffinity();
                return;
            } else {
                Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();
            }
        } else {
            txt_list.setTextColor(getResources().getColor(R.color.white));
            txt_chats.setTextColor(getResources().getColor(R.color.black));
            txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_red);
            txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_white);
            showFloatingActionButton();
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Long tsLong = (System.currentTimeMillis() / 1000);
        String ts = tsLong.toString();
        status(ts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Long tsLong = (System.currentTimeMillis() / 1000);
        String ts = tsLong.toString();
        status(ts);
    }

    public static void status(String status) {
        try {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideFloatingActionButton() {
        fab.setVisibility(View.GONE);
        fab.hide();
    }

    public static void showFloatingActionButton() {
        fab.show();
    }

    /*private void sendMessage() {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(fuser.getUid())) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("issend", true);
                            snapshot.getRef().updateChildren(hashMap);
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
    }*/


}
