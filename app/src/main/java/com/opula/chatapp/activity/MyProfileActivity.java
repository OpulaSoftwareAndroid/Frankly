package com.opula.chatapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;


public class MyProfileActivity extends AppCompatActivity {

    ImageView imgBack;
    LinearLayout lin_synccontact, lin_franklyweb, lin_close_account, lin_logout;
    String loginToken, userId;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        lin_close_account = findViewById(R.id.lin_close_account);
        imgBack = findViewById(R.id.imgBack);
        lin_logout = findViewById(R.id.lin_logout);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        lin_close_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCloseAccountDialog(MyProfileActivity.this);
            }
        });

        lin_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(MyProfileActivity.this);
            }
        });
    }

    public void showCloseAccountDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (MyProfileActivity.this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_close_account, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setCustomTitle(View.inflate(mContext, R.layout.alert_back, null));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button btn_no = dialogView.findViewById(R.id.btn_no);
        Button btn_yes = dialogView.findViewById(R.id.btn_yes);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAccount();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void showDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (MyProfileActivity.this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setCustomTitle(View.inflate(mContext, R.layout.alert_back, null));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button btn_no = (Button) dialogView.findViewById(R.id.btn_no);
        Button btn_yes = (Button) dialogView.findViewById(R.id.btn_yes);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        alertDialog.show();
    }

    private void closeAccount() {

        deleteUser();
        deleteToken();
        deleteUserKey();
        deleteChatList();
    }

    private void deleteUserKey() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (firebaseUser.getUid().equalsIgnoreCase(child.getKey())) {
                        Log.d("CLose_Account_if", "/" + child.getKey());
                        child.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteToken() {
        reference = FirebaseDatabase.getInstance().getReference("Tokens");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (firebaseUser.getUid().equalsIgnoreCase(child.getKey())) {
                        Log.d("CLose_Account_if", "/" + child.getKey());
                        child.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteChatList() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (firebaseUser.getUid().equalsIgnoreCase(child.getKey())) {
                        Log.d("CLose_Account_if", "/" + child.getKey());
                        child.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AppGlobal.showProgressDialog(MyProfileActivity.this);
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                    AppGlobal.hideProgressDialog(MyProfileActivity.this);
                    Intent it = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                    startActivity(it);
                    finish();
//                    Toast.makeText(MyProfileActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyProfileActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
