package com.opula.chatapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.GroupMessageAdapter;
import com.opula.chatapp.api.APIService;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Data;
import com.opula.chatapp.notifications.MyResponse;
import com.opula.chatapp.notifications.Sender;
import com.opula.chatapp.notifications.Token;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class GroupMessageFragment extends Fragment {

    DatabaseReference reference;
    SharedPreference sharedPreference;
    String groupUserId;
    RecyclerView recyclerView;
    RelativeLayout btn_send;
    CircleImageView imgUser;
    LinearLayout imgBack, groupName;
    EmojiconEditText text_send;
    ImageView emojiButton, send_image;
    TextView txtUserName, txtCheckActive;
    View rootView;
    StringBuilder commaSepValueBuilder;
    EmojIconActions emojIcon;
    List<Chat> mchat;
    GroupMessageAdapter messageAdapter;
    FirebaseUser fuser;
    ValueEventListener seenListener;
    String userusername, userimage;
    APIService apiService;

    //pickimage
    StorageReference storageReference;
    private StorageTask uploadTask;
    Uri mImageUri = null;
    int GALLERY = 1;

    boolean notify = false;

    GroupUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_message, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);
        userusername = sharedPreference.getValue(getActivity(), WsConstant.userUsername);
        userimage = sharedPreference.getValue(getActivity(), WsConstant.userImage);


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
                MainActivity.checkChatTheme(getContext());
                MainActivity.showpart2();

                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideFloatingActionButton();
                MainActivity.checkChatTheme(getContext());
                MainActivity.showpart2();

                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), groupUserId, msg, false, "default");
                } else {
                    Toast.makeText(getActivity(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        text_send.addTextChangedListener(new TextWatcher() {

            boolean isTyping = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            private Timer timer = new Timer();
            private final long DELAY = 1500; // milliseconds

            @Override
            public void afterTextChanged(final Editable s) {
                Log.d("", "");
                if (!isTyping) {
                    Log.d("typing", "started typing");
                    // Send notification for start typing event
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                Log.d("typing", "stopped typing");
                                //send notification for stopped typing event
                            }
                        },
                        DELAY
                );
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(GroupUser.class);
                assert user != null;
                txtUserName.setText(user.getGroupName());
                txtCheckActive.setText(user.getMemberList().size() + " Members");
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
//
//
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
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
    }

    private void readMesagges(final String groupid, final String imageurl) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(groupid)) {
                        for (int i = 0; i < user.getMemberList().size(); i++) {
                            final int finalI = i;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(user.getMemberList().get(finalI)));
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d("Group_chat_data", dataSnapshot.getValue() + "//");
                                    User u1 = dataSnapshot.getValue(User.class);
                                    assert u1 != null;
                                    if (u1.getId().equalsIgnoreCase(chat.getSender())) {
                                        chat.setSender_image(u1.getImageURL());
                                        chat.setSender_username(u1.getUsername());
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        mchat.add(chat);

                    }

                    messageAdapter = new GroupMessageAdapter(getActivity(), mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Choose Image");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
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
        pd.setMessage("Uploading");
        pd.show();

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

                        sendMessage(fuser.getUid(), groupUserId, "Image", true, mUri);

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
    }

    private void sendMessage(String sender, final String receiver, String message, boolean isimage, String uri) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Long tsLong = (System.currentTimeMillis() / 1000 + 66600);
        String ts = tsLong.toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("isimage", isimage);
        hashMap.put("image", uri);
        hashMap.put("time", ts);
        hashMap.put("sender_username", userusername);
        hashMap.put("sender_image", userimage);

        reference.child("Chats").push().setValue(hashMap);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    //sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                            groupUserId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
