package com.opula.chatapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;

public class LoginRegisterActivity extends AppCompatActivity {

    Button btn_login, btn_register;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    SharedPreference sharedPreference;
    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreference = new SharedPreference();
        auth = FirebaseAuth.getInstance();

        //check if user is null
        if (firebaseUser != null) {
            ValidateUserLogin();
//            Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
        }
    }

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
        setContentView(R.layout.activity_login_register);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginRegisterActivity.this, LoginActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginRegisterActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginRegisterActivity.this.finish();
    }
    public void ValidateUserLogin()
    {
        String txt_email = firebaseUser.getEmail();
        final String txt_password = sharedPreference.getValue(LoginRegisterActivity.this,WsConstant.password);

        if (AppGlobal.isNetwork(LoginRegisterActivity.this)){
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(LoginRegisterActivity.this, "All fileds are required", Toast.LENGTH_SHORT).show();
            } else {
                AppGlobal.showProgressDialog(LoginRegisterActivity.this);
                auth.signInWithEmailAndPassword(txt_email, txt_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                AppGlobal.hideProgressDialog(LoginRegisterActivity.this);
                                if (task.isSuccessful()){
                                    sharedPreference.save(LoginRegisterActivity.this,txt_password, WsConstant.password);
                                    Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginRegisterActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(LoginRegisterActivity.this, "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }

    }
}
