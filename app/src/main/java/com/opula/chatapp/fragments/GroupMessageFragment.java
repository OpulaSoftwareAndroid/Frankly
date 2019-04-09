package com.opula.chatapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.GroupMessageAdapter;
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.api.APIService;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Client;
import com.opula.chatapp.notifications.Data;
import com.opula.chatapp.notifications.MyResponse;
import com.opula.chatapp.notifications.Sender;
import com.opula.chatapp.notifications.Token;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class GroupMessageFragment extends Fragment {

    DatabaseReference reference;
    SharedPreference sharedPreference;
    static String groupUserId;
    RecyclerView recyclerView;
    RelativeLayout btn_send;
    CircleImageView imgUser;
    LinearLayout imgBack, groupName;
    EmojiconEditText text_send;
    ImageView emojiButton, send_image;
    TextView txtCheckActive;
    EmojiconTextView txtUserName;
    View rootView;
    StringBuilder commaSepValueBuilder;
    EmojIconActions emojIcon;
    List<Chat> mchat;
    GroupMessageAdapter messageAdapter;
    ValueEventListener mSendEventListner;
    static FirebaseUser fuser;
    ValueEventListener seenListener;
    static String userusername;
    static String userimage;
    static APIService apiService;
    BottomSheetDialog dialogMenu;
    static String messageUniqueID;
    int AUDIO_FROM_GALLERY = 2;
    static String TAG = "GroupMessageFragment";
    //pickimage
    static ArrayList<String> arrayListGroupMemberList;
    static ArrayList<String> arrayListCountGroupMemberList;

    StorageReference storageReference;
    private StorageTask uploadTask;
    Uri mImageUri = null;
    int GALLERY = 1;
    public static StringBuilder sb;
    static boolean notify = false;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    GroupUser user;
    String strWhoViewedMessage;
    static SecureRandom rnd = new SecureRandom();

    static String userid;
    //new library
    RecordView recordView;
    RecordButton recordButton;
    RelativeLayout is_text;

    Uri mPDFUri = null;
    final static int PICK_PDF_CODE = 2342;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    boolean mStartRecording = true;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String fileName = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_message, container, false);

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        MainActivity.hideFloatingActionButton();
        MainActivity.showpart1();

        sharedPreference = new SharedPreference();

        arrayListGroupMemberList = new ArrayList<>();
        arrayListCountGroupMemberList = new ArrayList<>();

        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);
        userusername = sharedPreference.getValue(getActivity(), WsConstant.userUsername);
        userimage = sharedPreference.getValue(getActivity(), WsConstant.userImage);

        fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AudioRecording.mp3";
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        initViews(view);
        emojIcon = new EmojIconActions(getActivity(), rootView, text_send, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_24dp, R.drawable.ic_sentiment_satisfied_black_24dp);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideFloatingActionButton();
                MainActivity.showpart2();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideFloatingActionButton();
                MainActivity.showpart2();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });

        getCountMemberFromGroup();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessageToGrp(getActivity(), fuser.getUid(), groupUserId, msg, false, "default", "default", false, "default", "default");
                }
                text_send.setText("");
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
                dialogMenu = new BottomSheetDialog(getContext());
                dialogMenu.setContentView(view);
                dialogMenu.setCancelable(true);
                LinearLayout lin_document = dialogMenu.findViewById(R.id.lin_document);
                LinearLayout lin_camera = dialogMenu.findViewById(R.id.lin_camera);
                LinearLayout lin_gallery = dialogMenu.findViewById(R.id.lin_gallery);
                LinearLayout lin_audio = dialogMenu.findViewById(R.id.lin_audio);
                LinearLayout lin_location = dialogMenu.findViewById(R.id.lin_location);
                LinearLayout lin_contact = dialogMenu.findViewById(R.id.lin_contact);
                lin_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                        takePhotoFromCamera();
                    }
                });
                lin_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                        choosePhotoFromGallary();
                    }
                });
                lin_document.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                        getPDF();
                    }
                });
                lin_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                        chooseAudioFromGallary();
                    }
                });
                lin_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                        MainActivity.showpart2();
                        ContactListFragment ldf = new ContactListFragment();
                        Bundle args = new Bundle();
                        args.putString("Type", "GroupContact");
                        ldf.setArguments(args);
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().replace(R.id.frame_mainactivity, ldf).addToBackStack(null).commit();
                    }
                });
                dialogMenu.show();
            }
        });
        userid = sharedPreference.getValue(getActivity(), WsConstant.userId);

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(GroupUser.class);
                    assert user != null;
                    txtUserName.setText(user.getGroupName());
                    txtCheckActive.setText(user.getMemberList().size() + " Members");
                    sharedPreference.save(getContext(), user.getGroupAdmin(), WsConstant.groupadminId);
                    if (user.getImageURL().equals("default")) {
                        imgUser.setImageResource(R.drawable.image_boy);
                    } else {
                        try {
                            Log.d("Image", user.getImageURL());
                            Picasso.get().load(user.getImageURL())
                                    .placeholder(R.drawable.image_boy).error(R.drawable.image_boy)
                                    .into(imgUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

//                for (int i = 0; i < user.getMemberList().size(); i++) {
//                    commaSepValueBuilder = new StringBuilder();
//                    final int finalI = i;
//                    reference = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(user.getMemberList().get(finalI)));
//                    reference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            User u1 = dataSnapshot.getValue(User.class);
//                            assert u1 != null;
//                            commaSepValueBuilder.append(u1.getEmail());
//                            if (finalI != user.getMemberList().size()) {
//                                commaSepValueBuilder.append(",");
//                                txtCheckActive.setText(commaSepValueBuilder);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//
//                    Log.d("Group_dataa", commaSepValueBuilder + "//");
//                }


                    readMesagges(groupUserId, user.getImageURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //new library
        recordView = (RecordView) rootView.findViewById(R.id.record_view);
        recordButton = (RecordButton) rootView.findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                is_text.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);
                onRecord(mStartRecording);
                if (mStartRecording) {
//                    setText("Stop recording");
                } else {
//                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                //String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");
                //Log.d("RecordTime", time);
                is_text.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                is_text.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
                is_text.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
            }
        });

//        KeyboardVisibilityEvent.setEventListener(Objects.requireNonNull(getActivity()),
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        if (isOpen) {
//                            recordButton.setVisibility(View.GONE);
//                            send_image.setVisibility(View.GONE);
//                        } else {
//                            recordButton.setVisibility(View.VISIBLE);
//                            send_image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });


        return view;
    }

    private void getPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getContext().getPackageName()));
            startActivity(intent);
            return;
        }
        String[] mimeTypes =
                {"application/pdf", "text/plain"};
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
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
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) getActivity().finish();

    }

    public static String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private void initViews(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);
        btn_send = view.findViewById(R.id.btn_send);
        text_send = view.findViewById(R.id.text_send);
        imgBack = view.findViewById(R.id.imgBack);
        imgUser = view.findViewById(R.id.imgUser);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtCheckActive = view.findViewById(R.id.txtCheckActive);
        rootView = view.findViewById(R.id.root_view);
        emojiButton = view.findViewById(R.id.emoji_btn);
        send_image = view.findViewById(R.id.send_image);
        groupName = view.findViewById(R.id.groupName);
        is_text = view.findViewById(R.id.is_text);
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

    }

    private void readMesagges(final String groupid, final String imageurl) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mchat.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        if (chat.getTo().equalsIgnoreCase("group")) {
                            if (chat.getReceiver().equals(groupid)) {
                                for (int i = 0; i < user.getMemberList().size(); i++) {
                                    final int finalI = i;
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(user.getMemberList().get(finalI)));
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {
                                                User u1 = dataSnapshot.getValue(User.class);
                                                assert u1 != null;
                                                if (u1.getId().equalsIgnoreCase(chat.getSender())) {
                                                    chat.setSender_image(u1.getImageURL());
                                                    chat.setSender_username(u1.getUsername());
                                                    strWhoViewedMessage =chat.getIsseenby();
                                                    Log.d(TAG,"jigar the group message have been seen by is"+strWhoViewedMessage);

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
                                mchat.add(chat);
                            }
                        }

                        Log.d(TAG, "jigar the member array list before going inside we have is " + arrayListCountGroupMemberList.size());
                        messageAdapter = new GroupMessageAdapter(getActivity(), mchat, imageurl,groupid,arrayListCountGroupMemberList.size());
                        recyclerView.setAdapter(messageAdapter);
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

    public void chooseAudioFromGallary() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO_FROM_GALLERY);
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @SuppressLint("CheckResult")
    private void takePhotoFromCamera() {
        RxImagePicker.with(getActivity()).requestImage(Sources.CAMERA).subscribe(new io.reactivex.functions.Consumer<Uri>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Uri uri) throws Exception {
                mImageUri = uri;
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });

    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();
        pd.setCancelable(false);

        if (mImageUri != null) {

            storageReference = FirebaseStorage.getInstance().getReference("chats");

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
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

                        sendMessageToGrp(getActivity(), fuser.getUid(), groupUserId, "Image", true, mUri, "default", false, "default", "default");

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            if (data != null) {
                mImageUri = data.getData();
                //image_profile.setImageURI(mImageUri);
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        }
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                String path = new File(Objects.requireNonNull(data.getData().getPath())).getAbsolutePath();
                if (path != null) {
                    mPDFUri = data.getData();

                    String filename;
                    Cursor cursor = getActivity().getContentResolver().query(mPDFUri, null, null, null, null);
                    if (cursor == null) { // Source is Dropbox or other similar local file path
                        filename = mPDFUri.getPath();
                    } else {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                        filename = cursor.getString(idx);
                        cursor.close();
                    }
                    String extension = filename.substring(filename.lastIndexOf("."));
                    Log.d("File_upload", extension + "/+" + path);
//                    //uploading the file
                    uploadFile(data.getData(), extension);
                }
            } else {
                Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == AUDIO_FROM_GALLERY && null != data) {
            try {
                Uri audioFileUri = data.getData();
                String path1 = audioFileUri.getPath();
                Log.d("Audio_path", path1 + "/");
                uploadAudio(audioFileUri, "mp3");
//                String path2 = getAudioPath(audioFileUri);
//                File f = new File(path2);
//                long fileSizeInBytes = f.length();
//                long fileSizeInKB = fileSizeInBytes / 1024;
//                long fileSizeInMB = fileSizeInKB / 1024;
//                if (fileSizeInMB > 2) {
//                    Toast.makeText(getContext(), "sorry file size is large", Toast.LENGTH_SHORT).show();
//                } else {
////                        profilePicUrl = path2;
////                        isPicSelect = true;
//                    Toast.makeText(getContext(), "Upload success", Toast.LENGTH_SHORT).show();
//                }

            } catch (Exception e) {
                Toast.makeText(getContext(), "Unable to process,try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data, String ext) {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();
        pd.setCancelable(false);
        if (data != null) {
            storageReference = FirebaseStorage.getInstance().getReference("doc_files");

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ext);

            uploadTask = fileReference.putFile(data);
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
                        sendMessageToGrp(getActivity(), fuser.getUid(), groupUserId, "Document", false, "default", mUri, false, "default", "default");

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No File selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAudio(Uri data, String ext) {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();
        pd.setCancelable(false);
        if (data != null) {
            storageReference = FirebaseStorage.getInstance().getReference("audio");

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ext);

            uploadTask = fileReference.putFile(data);
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
//                        sendMessage(getActivity(), fuser.getUid(), userid, "Document", false, "default", mUri);
                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No File selected", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendMessageToGrp(final Context context, final String sender, final String receiverGroupID, final String message, boolean isimage, String uri, String docUri, boolean iscontact, String con_name, String con_num) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        randomString(9);

        Long tsLong = (System.currentTimeMillis() / 1000);
        String ts = tsLong.toString();
        HashMap<String, Object> hashMap = new HashMap<>();

        if (AppGlobal.isNetwork(context)) {
            hashMap.put("id", sb.toString());
            hashMap.put("to", "group");
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiverGroupID);
            hashMap.put("message", message);
            hashMap.put("issend", true);
            hashMap.put("isseen", false);
            hashMap.put("isseenby", "");
            hashMap.put("isimage", isimage);
            hashMap.put("iscontact", iscontact);
            hashMap.put("contact_number", con_num);
            hashMap.put("contact_name", con_name);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("storage_uri", "default");
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("sender_username", userusername);
            hashMap.put("sender_image", userimage);
        } else {
            hashMap.put("id", sb.toString());
            hashMap.put("to", "group");
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiverGroupID);
            hashMap.put("message", message);
            hashMap.put("issend", false);
            hashMap.put("isseen", false);
            hashMap.put("isseenby", "");
            hashMap.put("isimage", isimage);
            hashMap.put("iscontact", iscontact);
            hashMap.put("contact_number", con_num);
            hashMap.put("contact_name", con_name);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("storage_uri", "default");
            hashMap.put("sender_username", userusername);
            hashMap.put("sender_image", userimage);
        }


        reference.child("Chats").push().setValue(hashMap);
//

        String messageUniqueID = reference.getKey();

        getMemberFromGroup(context, message);

//        String topic="groupchat";
//
//        FirebaseMessaging.getInstance().subscribeToTopic(topic);
//
//        Toast.makeText(context,"Done With Topic",Toast.LENGTH_LONG).show();
//
//        groupMessageWithTopic(context);

    }

    //    context,strMemberID,message
    public static void getUserInfoAndSendNotification(final Context context, final ArrayList<String> receiverMemberID, final String message) {
        DatabaseReference reference;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                try {
//                                                final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {

                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        Log.d(TAG, "jigar the notification reciever member id is " + receiverMemberID.toString());
                        for (int i = 0; i < receiverMemberID.size(); i++) {
                            Log.d(TAG, "jigar the notification called with member id is " + receiverMemberID.get(i));

                            final String strRecieverMemberID = receiverMemberID.get(i);
                            final String strRecieverMemberName = user.getUsername();

                            //Do something after 100ms

                            sendNotifiaction(context, messageUniqueID,strRecieverMemberID, strRecieverMemberName, message);
                        }
                    }
                    notify = false;
//                                }}, 1000);


                } catch (Exception e) {
                    Log.d(TAG, "jigar the exception error in notification is " + e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
//    getMemberFromGroup(context,receiver, user.getUsername(), message);


    public static void getMemberFromGroup(final Context context, final String message) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot d1) {
                try {
                    if (d1.exists()) {
                        GroupUser groupUser = d1.getValue(GroupUser.class);
                        for (int i = 0; i < groupUser.getMemberList().size(); i++) {
                            String member = groupUser.getMemberList().get(i);
                            //         String strTempMemberID="";
                            if (!fuser.getUid().equals(member)) {
                                String strMemberID = member;
                                arrayListGroupMemberList.add(strMemberID);

                                //      strTempMemberID=strMemberID;
//                                sendNotifiaction(context,grp,userName,message);

                            }


//                            if (groupUser.getMemberList().size() <= 1) {
//                                ref.getRef().removeValue();
//                                Log.d("GroupChat2", "//");
//                            }
                        }
                        Log.d(TAG, "jigar the member of group we have is " + arrayListGroupMemberList.toString());

                        getUserInfoAndSendNotification(context, arrayListGroupMemberList, message);


                        // ref.child("memberList").setValue(grpList);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "jigar the exception member list error in notification is " + e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void getCountMemberFromGroup() {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot d1) {
                try {
                    if (d1.exists()) {
                        GroupUser groupUser = d1.getValue(GroupUser.class);
                        for (int i = 0; i < groupUser.getMemberList().size(); i++) {
                            String member = groupUser.getMemberList().get(i);
                            //         String strTempMemberID="";
                            //if (!fuser.getUid().equals(member))
                            {
                                String strMemberID = member;
                                arrayListCountGroupMemberList.add(strMemberID);


                            }



                        }
                        Log.d(TAG, "jigar the member of group we have is " + arrayListCountGroupMemberList.toString());



                        // ref.child("memberList").setValue(grpList);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "jigar the member of exception member list error in notification is " + e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "jigar the member of error database error is " + databaseError);

            }
        });
        Log.d(TAG, "jigar the member of size group member we have is " + arrayListCountGroupMemberList.size());

    }

    private static void sendNotifiaction(final Context context, final String messageUniqueID, String receiver, final String username, final String message) {
        Log.d(TAG, "jigar the we sending notification have receiver is " + receiver + " and user name is " + username
                + " and  message is  " + message);

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(fuser.getUid()
                                , R.mipmap.ic_launcher, username + ": " + message, "New Message",
                                userid,messageUniqueID);
                        Log.d(TAG, "jigar the we response before notification for token is  " + snapshot.getChildren().toString());
                        final Sender sender = new Sender(data, token.getToken());

                        //                        final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        Log.d(TAG, "jigar the on notification response we have in notification is " + response.code());
                                        Log.d(TAG, "jigar the on notification request we done is " + call.request().body());

                                        if (response.code() == 200) {

                                            if (response.body().success != 1) {

                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.d(TAG, "jigar the on notification failure  in notification is " + call.toString());

                                    }
                                });
//                                                        }}, 1000);


                    }
                } catch (Exception e) {
                    Log.d(TAG, "jigar the main exception in notification is " + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "jigar the notification cancelled is " + databaseError.getMessage());
            }
        });
    }

    public void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        Log.d(TAG, "jigar the seen message user id  we have is" + userid);

        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getTo().equalsIgnoreCase("group")) {
//                            if (chat.getReceiver().equals(groupUserId) && chat.getSender().equals(userid))
                                if (chat.getReceiver().equals(groupUserId) )
                                {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("isseen", true);
                                    System.out.println("jigar is seen by user are "+strWhoViewedMessage);
                                    if(strWhoViewedMessage==null)
                                    {
                                        hashMap.put("isseenby", userid);

                                    }else

                                    if (strWhoViewedMessage.contains(userid)) {

                                    System.out.println("jigar is I found the user id "+userid);

                                } else {
                                        System.out.println("jigar is i dnt find  found the user id "+userid);

                                        if(strWhoViewedMessage.equals(""))
                                    {
                                        hashMap.put("isseenby", userid);
                                    }else {
                                        hashMap.put("isseenby", strWhoViewedMessage + " , " + userid);
                                    }

                                }
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "jigar the exception we have is " + e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        };

        reference.addValueEventListener(valueEventListener);
        mSendEventListner = valueEventListener;

    }

    @Override
    public void onResume() {
        super.onResume();
//        if(messageAdapter!=null) {
//            recyclerView.setAdapter(messageAdapter);
//            messageAdapter.notifyDataSetChanged();
//        }
        seenMessage(fuser.getUid());
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mSendEventListner != null) {
            reference.removeEventListener(mSendEventListner);
        }
    }
}