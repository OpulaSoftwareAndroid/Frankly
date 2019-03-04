package com.opula.chatapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.ListChatFragment;
import com.opula.chatapp.fragments.ListGroupChatFragment;
import com.opula.chatapp.fragments.ListUserFragment;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.MyProfileFragment;
import com.opula.chatapp.model.User;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private long mBackPressed;
    private static final int TIME_INTERVAL = 2000;
    public static TextView text;
    public static LinearLayout txt_globle, txt_my_wallate;
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
    SpaceNavigationView spaceNavigationView;
    LinearLayout imgBack, imgTrash, imgCopy, imgForward, imgStar;
    public static FirebaseUser fuser;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

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

        txt_globle = findViewById(R.id.txt_globle);
        txt_my_wallate = findViewById(R.id.txt_my_wallate);
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
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.setSpaceBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.shouldShowFullBadgeText(true);
//        spaceNavigationView.addSpaceItem(new SpaceItem("Status", R.drawable.ic_bottom_chat));
//        spaceNavigationView.addSpaceItem(new SpaceItem("Call", R.drawable.ic_bottom_call));
//        spaceNavigationView.addSpaceItem(new SpaceItem("Chats", R.drawable.ic_bottom_chat));
//        spaceNavigationView.addSpaceItem(new SpaceItem("Search", R.drawable.ic_bottom_chat));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_action_status));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_action_call));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_action_chat));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_action_search));

//        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onCentreButtonClick() {
//                Log.d("onCentreButtonClick ", "onCentreButtonClick");
//                if (checkSelfPermission(Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.CAMERA},
//                            MY_CAMERA_PERMISSION_CODE);
//                } else {
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                }
//            }
//
//            @Override
//            public void onItemClick(int itemIndex, String itemName) {
//                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
//            }
//
//            @Override
//            public void onItemReselected(int itemIndex, String itemName) {
//                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
//            }
//        });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                spaceNavigationView.changeCenterButtonIcon( R.drawable.ic__bottom_camera);

                spaceNavigationView.shouldShowFullBadgeText(true);
                spaceNavigationView.showBadgeAtIndex(2, 3, Color.RED);
                spaceNavigationView.setInActiveCentreButtonIconColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryone));
                spaceNavigationView.changeCurrentItem(2);
            }
        }, 700);

     //   spaceNavigationView.showBadgeAtIndex(1,3,getResources().getColor(R.color.colorRed));
 //       spaceNavigationView.showBadgeAtIndex(int itemIndexToShowBadge, int badgeCountText, int badgeBackgroundColor);

//        spaceNavigationView.addSpaceItem(new SpaceItem("Settings", R.drawable.ic_double_tick_send_indicator));

     //   showFloatingActionButton();
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
                    sharedPreference.save(MainActivity.this, user.getUsername(), WsConstant.userUsername);
                    sharedPreference.save(MainActivity.this, user.getImageURL(), WsConstant.userImage);
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
                MessageAdapter.back(MainActivity.this);
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
                MessageAdapter.starMessage(MainActivity.this);
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



        txt_my_wallate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                checkMyTheme(MainActivity.this);
//                String ismain = WsConstant.ismain;
//                if (ismain.equalsIgnoreCase("p")) {
//                    FragmentManager fragmentGroup = getSupportFragmentManager();
//                    fragmentGroup.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();
//                } else if (ismain.equalsIgnoreCase("g")) {
//                    FragmentManager fragmentPersonal = getSupportFragmentManager();
//                    fragmentPersonal.beginTransaction().replace(R.id.frame_mainactivity, new ListGroupChatFragment()).addToBackStack(null).commit();
//                }
                Toast.makeText(MainActivity.this, "Coming Soon..!", Toast.LENGTH_SHORT).show();
            }
        });

        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpart2();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MyProfileFragment()).addToBackStack(null).commit();
                spaceNavigationView.setVisibility(View.GONE);

            }
        });

        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkListTheme(MainActivity.this);
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
                    spaceNavigationView.setVisibility(View.GONE);
                    fragmentPersonal.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }

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
                spaceNavigationView.setVisibility(View.GONE);

            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

   /* public static void checkGlobleTheme(Context context) {
        txt_globle.setTextColor(context.getResources().getColor(R.color.black));
        txt_chats.setTextColor(context.getResources().getColor(R.color.white));
        txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_white);
        txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_red);
    }

    public static void checkMyTheme(Context context) {
        txt_list.setTextColor(context.getResources().getColor(R.color.white));
        txt_chats.setTextColor(context.getResources().getColor(R.color.black));
        txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_red);
        txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_white);
    }*/

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
            /*txt_list.setTextColor(getResources().getColor(R.color.white));
            txt_chats.setTextColor(getResources().getColor(R.color.black));
            txt_list.setBackgroundResource(R.drawable.toolbar_backgroung_list_red);
            txt_chats.setBackgroundResource(R.drawable.toolbar_backgroung_chats_white);*/
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
