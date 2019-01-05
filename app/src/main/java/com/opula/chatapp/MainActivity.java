package com.opula.chatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opula.chatapp.fragments.ListChatFragment;
import com.opula.chatapp.fragments.ListGroupChatFragment;
import com.opula.chatapp.fragments.ListUserFragment;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.MyProfileFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private long mBackPressed;
    private static final int TIME_INTERVAL = 2000;
    public static TextView text, txt_list, txt_chats;
    ImageView img_setting, img_search;
    public static FragmentManager fragmentManager;
    public static FirebaseUser firebaseUser;
    public static DatabaseReference reference;
    public static FloatingActionButton fab;
    public static LinearLayout part1, part2;
    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txt_list = findViewById(R.id.txt_list);
        txt_chats = findViewById(R.id.txt_chats);
        img_setting = findViewById(R.id.img_setting);
        img_search = findViewById(R.id.img_search);
        fab = findViewById(R.id.fab);
        part1 = findViewById(R.id.part1);
        part2 = findViewById(R.id.part2);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        showFloatingActionButton();
        showpart1();

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
                txt_list.setTextColor(getResources().getColor(R.color.white));
                txt_chats.setTextColor(getResources().getColor(R.color.black));
                txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_red);
                txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_white);
                showFloatingActionButton();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();
            }
        });

        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingActionButton();
                showpart2();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MyProfileFragment()).addToBackStack(null).commit();
            }
        });
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingActionButton();
                showpart2();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ListGroupChatFragment()).addToBackStack(null).commit();
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void showpart1() {
        part1.setVisibility(View.VISIBLE);
        part2.setVisibility(View.GONE);
    }

    public static void showpart2() {
        part1.setVisibility(View.GONE);
        part2.setVisibility(View.VISIBLE);
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

        if (frag instanceof ListChatFragment) {
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
        status("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    public static void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    public static void hideFloatingActionButton() {
        fab.setVisibility(View.GONE);
        fab.hide();
    }

    public static void showFloatingActionButton() {
        fab.show();
    }
}
