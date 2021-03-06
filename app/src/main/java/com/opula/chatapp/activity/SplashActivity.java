package com.opula.chatapp.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.notifications.MyFirebaseMessaging;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    public static int REQUEST_CAMERA = 101;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    SharedPreference sharedPreference;
    PackageInfo pInfo;
    DatabaseReference ref;
    ValueEventListener listener ;
    String TAG="SplashActivity";
    MyFirebaseMessaging myFirebaseMessaging;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        sharedPreference = new SharedPreference();

//        myFirebaseMessaging.noti

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();
        try {
            pInfo = SplashActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.d(TAG,"jigar the current version in mobile is "+pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

         ref = FirebaseDatabase.getInstance().getReference("Version").child("code");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot d1) {
                try {
                    Log.d("code",d1.getValue().toString());
                        String verCode = String.valueOf(pInfo.versionCode);
                    Log.d(TAG,"jigar the data base version we have ar server is "+d1.getValue().toString());

                    if (verCode.equalsIgnoreCase(d1.getValue().toString()))
                    {
                        if (firebaseUser != null){
                            auth.signInWithEmailAndPassword(Objects.requireNonNull(firebaseUser.getEmail())
                                    , sharedPreference.getValue(SplashActivity.this,WsConstant.password))
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            AppGlobal.hideProgressDialog(SplashActivity.this);
                                            if (task.isSuccessful()){
                                               initView("1");
                                            } else {
                                                if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                                                        != PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                                                                != PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)
                                                                != PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                                                                != PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                                != PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                                != PackageManager.PERMISSION_GRANTED ||
                                                        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)
                                                                != PackageManager.PERMISSION_GRANTED) {

                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                                                Manifest.permission.ACCESS_NETWORK_STATE,
                                                                Manifest.permission.READ_CONTACTS,
                                                                Manifest.permission.ACCESS_WIFI_STATE,
                                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                                Manifest.permission.READ_PHONE_STATE}, REQUEST_CAMERA);
                                                    }
                                                } else {
                                                    initView("0");
                                                }
                                            }
                                        }
                                    });
                        } else {
                            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                                            != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)
                                            != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                                            != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)
                                            != PackageManager.PERMISSION_GRANTED) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.ACCESS_NETWORK_STATE,
                                            Manifest.permission.READ_CONTACTS,
                                            Manifest.permission.ACCESS_WIFI_STATE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_PHONE_STATE}, REQUEST_CAMERA);
                                }
                            } else {
                                initView("0");
                            }
                        }
                    } else {
                        initView("2");
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

        public void ValidateUserLogin()
    {
        String txt_email = firebaseUser.getEmail();
        final String txt_password = sharedPreference.getValue(SplashActivity.this,WsConstant.password);

        if (AppGlobal.isNetwork(SplashActivity.this)){
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(SplashActivity.this, "All fileds are required", Toast.LENGTH_SHORT).show();
            } else {
                AppGlobal.showProgressDialog(SplashActivity.this);
                auth.signInWithEmailAndPassword(txt_email, txt_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                AppGlobal.hideProgressDialog(SplashActivity.this);
                                if (task.isSuccessful()){
                                    sharedPreference.save(SplashActivity.this,txt_password,WsConstant.password);
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                                    startActivity(intent);
                                    SplashActivity.this.finish();
                                    Toast.makeText(SplashActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(SplashActivity.this, "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE}, REQUEST_CAMERA);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                initView("0");
            } else {
                getPermission();
            }
        }
    }


    private void initView(final String session) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.equalsIgnoreCase("1")) {
                    ValidateUserLogin();
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                } else if (session.equalsIgnoreCase("0")){
                    Intent intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } else if (session.equalsIgnoreCase("2")){
                    showDialog(SplashActivity.this);
                }
            }
        }, 2500);
    }

    public void showDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (SplashActivity.this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setCustomTitle(View.inflate(mContext, R.layout.alert_back, null));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        TextView txt = dialogView.findViewById(R.id.txt);
        Button btn_yes = (Button) dialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button) dialogView.findViewById(R.id.btn_no);

        btn_no.setVisibility(View.GONE);

        txt.setText("You are using old version.Please get the updated version from provider...");

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   ref.removeEventListener(valueEventListener);
    }
}
