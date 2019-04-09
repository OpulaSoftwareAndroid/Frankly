package com.opula.chatapp.notifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.constant.WsConstant;

import java.util.List;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    String TAG="MyFirebaseMessaging";
    DatabaseReference reference;
    ValueEventListener mSendEventListner;


    @Override
public void onMessageReceived(RemoteMessage remoteMessage)
{
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String strMessageUniqueID=remoteMessage.getData().get("messageuniqueid");
        Log.d(TAG,"jigar the message unique id we have received is "+strMessageUniqueID);
        setMessageReceivedYes(strMessageUniqueID);
        if(sented!=null)
        {

            if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (isForeground(getApplicationContext())) {
                        //if in forground then your operation
                        // if app is running them
                        } else {
                        //if in background then perform notification operation
                            sendOreoNotification(remoteMessage);
                        }
                    } else {
                        if (isForeground(getApplicationContext())) {
                        //if in forground then your operation
                        // if app is running them
                        } else {
                        //if in background then perform notification operation
                            sendNotification(remoteMessage);
                        }
                    }
                }
            }
        }

}

    public void setMessageReceivedYes(final String messageID) {
        try {
            reference = FirebaseDatabase.getInstance().getReference("Chats");
            reference.child(messageID).child("isreceived").setValue(true);
        }catch (Exception ex)
        {
            Log.d(TAG,"jigar the exception in updating mesage received is  "+ex);

        }
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Chat chat = snapshot.getValue(Chat.class);
//                         {
////                            if (chat.getId().equals(messageID))
//                            {
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                hashMap.put("isreceived", true);
//                                snapshot.getRef().updateChildren(hashMap);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG,"jigar the exception we got is "+e);
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //
//                Log.d(TAG,"jigar the exception database error we got is "+databaseError.getMessage());
//
//            }
//        };
//        reference.addValueEventListener(valueEventListener);
//        mSendEventListner = valueEventListener;
    }
private static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance
                    && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
}

private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(WsConstant.user_id_notification, user);
        bundle.putString(WsConstant.FRAGMENT_NAME, WsConstant.FRAGMENT_CHAT);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if (j > 0) {
            i = j;
        }
        oreoNotification.getManager().notify(i, builder.build());
}

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putString("userid", user);
        bundle.putString(WsConstant.user_id_notification, user);

        bundle.putString(WsConstant.FRAGMENT_CHAT, WsConstant.FRAGMENT_CHAT);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int i = 0;
        if (j > 0){
            i = j;
        }

        noti.notify(i, builder.build());
    }
}
