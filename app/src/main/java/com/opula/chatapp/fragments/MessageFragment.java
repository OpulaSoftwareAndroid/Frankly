package com.opula.chatapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.api.APIService;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;
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
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MessageFragment extends Fragment {

    CircleImageView imgUser;
    LinearLayout imgBack;
    EmojiconTextView txtUserName;
    TextView txtCheckActive;
    public static FirebaseUser fuser;
    DatabaseReference reference;
    RelativeLayout btn_send;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    public static String userid;
    public static APIService apiService;
    SharedPreference sharedPreference;
    EmojiconEditText text_send;
    ImageView emojiButton, send_image;
    View rootView;
    EmojIconActions emojIcon;
    //pickimae
    StorageReference storageReference;
    private StorageTask uploadTask;
    Uri mImageUri = null;
    int GALLERY = 1;
    String CheckActive;
    public static boolean notify = false;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();
    public static StringBuilder sb;
    String AES = "AES";
    ValueEventListener mSendEventListner;
    RelativeLayout is_text;
    LinearLayout l1;
    //new library
    RecordView recordView;
    RecordButton recordButton;
    BottomSheetDialog dialogMenu;
    final static int PICK_PDF_CODE = 2342;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        MainActivity.hideFloatingActionButton();
//        MainActivity.checkChatTheme(getActivity());
        MainActivity.showpart1();

        sharedPreference = new SharedPreference();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        initViews(view);

        emojIcon = new EmojIconActions(getActivity(), rootView, text_send, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_24dp, R.drawable.ic_sentiment_satisfied_black_24dp);

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

        userid = sharedPreference.getValue(getActivity(), WsConstant.userId);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(getActivity(), fuser.getUid(), userid, msg, false, "default", "default");
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
                dialogMenu.show();
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showpart2();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new UserProfileFragment()).addToBackStack(null).commit();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    txtUserName.setText(user.getUsername());
                    CheckActive = user.getStatus();

                    if (user.getStatus().equalsIgnoreCase("online")) {
                        txtCheckActive.setText(user.getStatus());
                    } else {
                        String str = getDateCurrentTimeZone(Long.parseLong(user.getStatus()));
                        txtCheckActive.setText("Last seen : " + str);
                    }
                    if (user.getImageURL().equals("default")) {
                        imgUser.setImageResource(R.drawable.image_boy);
                    } else {
                        //and this
                        try {
                            Log.d("Image", user.getImageURL());
                            Picasso.get().load(user.getImageURL())
                                    .placeholder(R.drawable.image_boy).error(R.drawable.image_boy)
                                    .into(imgUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    readMesagges(fuser.getUid(), userid, user.getImageURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sendMessage(userid);

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

        KeyboardVisibilityEvent.setEventListener(Objects.requireNonNull(getActivity()),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            recordButton.setVisibility(View.GONE);
                            send_image.setVisibility(View.GONE);
                        } else {
                            recordButton.setVisibility(View.VISIBLE);
                            send_image.setVisibility(View.VISIBLE);
                        }
                    }
                });

        return view;
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
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

    public static String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = calendar.getTimeZone();//get your local time zone.
            calendar.setTimeInMillis(timestamp * 1000);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
            sdf.setTimeZone(tz);
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
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
        is_text = view.findViewById(R.id.is_text);
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
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = new Uri.Builder().build();
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            if (data != null) {
                mImageUri = data.getData();
                //image_profile.setImageURI(mImageUri);
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("image_upload", mImageUri + "//");
//                    uploadImage();
                }
            }
        }
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                String path = new File(Objects.requireNonNull(data.getData().getPath())).getAbsolutePath();
                if (path != null) {
                    uri = data.getData();
                    String filename;
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    if (cursor == null) { // Source is Dropbox or other similar local file path
                        filename = uri.getPath();
                    } else {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                        filename = cursor.getString(idx);
                        cursor.close();
                    }
                    String extension = filename.substring(filename.lastIndexOf("."));
                    Log.d("File_upload", extension + "/+" + path);
                    //uploading the file
                    //  uploadFile(data.getData(), extension);
                }
            } else {
                Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data, String ext) {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();
        pd.setCancelable(false);
//        storageReference = FirebaseStorage.getInstance().getReference("doc_files");
//        final StorageReference sRef = storageReference.child(System.currentTimeMillis() + ext);

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
                        sendMessage(getActivity(), fuser.getUid(), userid, "Document", false, "default", mUri);

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

//        sRef.putFile(data)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @SuppressWarnings("VisibleForTests")
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        pd.dismiss();
//                        Toast.makeText(getContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                        String doc_uri=sRef.getDownloadUrl().toString();
//                        Log.d("UploadFile",doc_uri);
//                        Toast.makeText(getContext(), doc_uri+"", Toast.LENGTH_SHORT).show();
////                        Upload upload = new Upload(editTextFilename.getText().toString(), taskSnapshot.getDownloadUrl().toString());
////                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
////                        sendMessage(getActivity(), fuser.getUid(), userid, "Document", false, taskSnapshot.getDownloadUrl());
//
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                })
//
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @SuppressWarnings("VisibleForTests")
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                        textViewStatus.setText((int) progress + "% Uploading...");
//                    }
//                });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

                        sendMessage(getActivity(), fuser.getUid(), userid, "Image", true, mUri, "default");

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

    public void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getTo().equalsIgnoreCase("personal")) {
                            if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isseen", true);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                } catch (Exception e) {
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

    private void sendMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSendEventListner != null) {
            reference.removeEventListener(mSendEventListner);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        seenMessage(userid);
    }

    private String encrypt(String Data, String Password) throws Exception {
        SecretKeySpec key = genrateKey(Password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = cipher.doFinal(Data.getBytes());
        String encyptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encyptedValue;
    }

    private String decrypt(String outputString, String Password) throws Exception {
        SecretKeySpec key = genrateKey(Password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encyptedValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = cipher.doFinal(encyptedValue);
        String decyptedValue = new String(decValue);
        return decyptedValue;
    }

    private SecretKeySpec genrateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public static void sendMessage(final Context context, final String sender, final String receiver, String message, boolean isimage, String uri, String docUri) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();
        randomString(9);
        /*String encMessage = null;
        try {
            encMessage = encrypt(message,"Jenil");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
            hashMap.put("isimage", isimage);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("storage_uri", "default");
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("table_id", reference.getKey());
        } else {
            hashMap.put("id", sb.toString());
            hashMap.put("to", "personal");
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("issend", false);
            hashMap.put("isseen", false);
            hashMap.put("isimage", isimage);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("storage_uri", "default");
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
                    if (notify) {
                        sendNotifiaction(context, receiver, user.getUsername(), msg);
                    }
                    notify = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void sendNotifiaction(final Context context, String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                                userid);

                        Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success != 1) {

                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
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

    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mchat.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        Log.d("DATAA33", snapshot.getKey() + "//" + snapshot.getValue());
                        assert chat != null;
                        if (chat.getTo().equalsIgnoreCase("personal")) {
                            if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                                mchat.add(chat);
                            }
                        } else if (chat.getTo().equalsIgnoreCase("broadcast")) {
                            for (int i = 0; i < chat.getBroadcast_receiver().size(); i++) {
                                if (chat.getBroadcast_receiver().get(i).equalsIgnoreCase(userid)) {
                                    mchat.add(chat);
                                }
                            }
                        }
                    }
                    messageAdapter = new MessageAdapter(getActivity(), mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
