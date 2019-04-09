package com.opula.chatapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.User;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;

public class MainRecordTestActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    //pickimae
    StorageReference storageReference;
    private StorageTask uploadTask;
    FirebaseUser fuser;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();
    SharedPreference sharedPreference;
    String userid;
    private RecordButton recordButton = null;
    private MediaRecorder recorder = null;
    static String TAG="MainRecordTestActivity";
    private PlayButton   playButton = null;
    private MediaPlayer   player = null;
    public static StringBuilder sb;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {

            startRecording();

        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        Uri audioFileUri=Uri.fromFile(new File(fileName));

        uploadAudio(audioFileUri,"3gp");

        recorder.stop();
        recorder.release();
        recorder = null;
    }

    class RecordButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        LinearLayout ll = new LinearLayout(this);
        recordButton = new RecordButton(this);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreference = new SharedPreference();
        userid = sharedPreference.getValue(this, WsConstant.userId);

        ll.addView(recordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        playButton = new PlayButton(this);
        ll.addView(playButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        setContentView(ll);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
            }

    private void uploadAudio(Uri data, String ext) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        pd.setCancelable(false);
        if (data != null) {
            storageReference = FirebaseStorage.getInstance().getReference("audio");

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ext);

            uploadTask = fileReference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //     Toast.makeText(getContext(),"congratulation audio uploaded successful "+taskSnapshot.getMetadata().getPath(),Toast.LENGTH_LONG).show();
                    Log.d(TAG,"jigar the audio success is having  is "+taskSnapshot.getBytesTransferred());

                }
            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        Log.d("UploadFile", mUri);
                        sendAudioToPersonal(MainRecordTestActivity.this, fuser.getUid(), userid, "Voice Message"
                                , false,mUri, "default", true, "default"
                                , "default");
//                        sendMessage(getActivity(), fuser.getUid(), userid, "Document", false, "default", mUri);
                        pd.dismiss();
                    } else {
                        Toast.makeText(MainRecordTestActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainRecordTestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(MainRecordTestActivity.this, "No File selected", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendAudioToPersonal(final Context context
            , final String sender, final String receiver
            , String message, boolean isimage, String uri, String docUri
            , boolean isAudio, String con_name, String con_num) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();
        randomString(9);

        //        /*String encMessage = null;
//        try {
//            encMessage = encrypt(message,"Jenil");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }*/

        Long tsLong = (System.currentTimeMillis() / 1000);
        String ts = tsLong.toString();
        HashMap<String, Object> hashMap = new HashMap<>();

        if (AppGlobal.isNetwork(context)) {
            hashMap.put("id", sb.toString());
            hashMap.put("to", "personal");
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("issend", true);
            hashMap.put("isseen", false);
            hashMap.put("isrepliedmessage", false);
            hashMap.put("isrepliedmessageid", "");
            hashMap.put("isseentime", "");
            hashMap.put("isimage", isimage);
            hashMap.put("iscontact", false);
            hashMap.put("isaudio", isAudio);
            hashMap.put("contact_number", con_num);
            hashMap.put("contact_name", con_name);
            hashMap.put("image","default" );
            hashMap.put("time", ts);
            hashMap.put("storage_uri", "default");
            hashMap.put("audio_uri", uri);
            hashMap.put("doc_uri", docUri);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("isstatus", "0");

        } else {
            hashMap.put("id", sb.toString());
            hashMap.put("to", "personal");
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("issend", false);
            hashMap.put("isseen", false);
            hashMap.put("isrepliedmessage",false);
            hashMap.put("isrepliedmessageid","");
            hashMap.put("isseentime", "");
            hashMap.put("isimage", isimage);
            hashMap.put("iscontact", false);
            hashMap.put("isaudio", isAudio);
            hashMap.put("contact_number", con_num);
            hashMap.put("contact_name", con_name);
            hashMap.put("image", "default");
            hashMap.put("time", ts);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("audio_uri",uri );
            hashMap.put("doc_uri", docUri);
            hashMap.put("storage_uri", "default");
            hashMap.put("isstatus", "0");
        }
        reference.setValue(hashMap);

        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(sender).child(receiver);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (!dataSnapshot.exists()) {
                        chatRef.child("id").setValue(receiver);
                        chatRef.child("istyping").setValue(false);
                        chatRef.child("isnotification").setValue(false);
                        chatRef.child("issecure").setValue(false);
                        chatRef.child("issender").setValue(true);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist").child(receiver).child(sender);
            chatRefReceiver.child("id").setValue(sender);
            chatRefReceiver.child("istyping").setValue(false);
            chatRefReceiver.child("isnotification").setValue(true);
            chatRefReceiver.child("issecure").setValue(false);
            chatRefReceiver.child("issender").setValue(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(sender);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                   boolean notify=true;
                    if (notify) {
                      //  sendNotifiaction(context, receiver, user.getUsername(), msg);
                    }
                    notify = false;
                } catch (Exception e) {
                    Log.d(TAG,"jigar the exception is main reference in "+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"jigar the oncancelled error is main reference in "+databaseError.getMessage());

            }
        });
    }

    public static String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}