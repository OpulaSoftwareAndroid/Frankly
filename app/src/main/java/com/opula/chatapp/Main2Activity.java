package com.opula.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.opula.chatapp.activity.MyProfileActivity;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.fragments.ChatsFragment;

public class Main2Activity extends AppCompatActivity {

    private long mBackPressed;
    private static final int TIME_INTERVAL = 2000;
    public static TextView text, txt_list, txt_chats;
    ImageView img_setting;
    public static FragmentManager fragmentManager;
    SharedPreference sharedPreference;
    String userId, loginToken, mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txt_list = findViewById(R.id.txt_list);
        txt_chats = findViewById(R.id.txt_chats);
        img_setting = findViewById(R.id.img_setting);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ChatsFragment()).addToBackStack(null).commit();

        txt_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_list.setTextColor(getResources().getColor(R.color.white));
                txt_chats.setTextColor(getResources().getColor(R.color.black));
                txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_red);
                txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_white);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ChatsFragment()).addToBackStack(null).commit();
            }
        });

        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                startActivity(intent);
                //startActivityForResult(intent, 1001);
            }
        });

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

        if (frag instanceof ChatsFragment) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                Main2Activity.this.finishAffinity();
                return;
            } else {
                Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();
            }
        }
    }

}
