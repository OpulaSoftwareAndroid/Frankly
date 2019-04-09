package com.opula.chatapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class chatapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

