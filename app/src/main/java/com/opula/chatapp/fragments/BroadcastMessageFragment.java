package com.opula.chatapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.luseen.spacenavigation.SpaceNavigationView;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.BroadcastMessageAdapter;
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.api.APIService;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.BroadcastUser;
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

public class BroadcastMessageFragment extends Fragment {

    CircleImageView imgUser;
    LinearLayout imgBack;
    EmojiconTextView txtUserName;
    TextView txtCheckActive;
    public static FirebaseUser fuser;
    DatabaseReference reference;
    RelativeLayout btn_send;
    BroadcastMessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    public static String userid, broadcastid;
    public static APIService apiService;
    SharedPreference sharedPreference;
    EmojiconEditText text_send;
    ImageView emojiButton, send_image;
    View rootView;
    EmojIconActions emojIcon;
    StorageReference storageReference;
    private StorageTask uploadTask;
    Uri mImageUri = null;
    Uri mPDFUri = null;
    int GALLERY = 1;
    public static boolean notify = false;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();
    public static StringBuilder sb;
    String AES = "AES";
    RelativeLayout is_text;
    //new library
    RecordView recordView;
    public static List<String> myList;
    RecordButton recordButton;
    BottomSheetDialog dialogMenu;
    final static int PICK_PDF_CODE = 2342;
    int AUDIO_FROM_GALLERY = 2;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    boolean mStartRecording = true;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    public static BroadcastUser user;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broadcast_message, container, false);

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
        broadcastid = sharedPreference.getValue(getActivity(), WsConstant.broadcastId);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessageFromBroadcast(getActivity(),broadcastid, msg, false, "default", "default", false, "default", "default");

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
                        args.putString("Type", "PersonalContact");
                        ldf.setArguments(args);
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().replace(R.id.frame_mainactivity, ldf).addToBackStack(null).commit();

//                        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ContactListFragment()).addToBackStack(null).commit();

                    }
                });
                dialogMenu.show();
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.showpart2();
                /*FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new UserProfileFragment()).addToBackStack(null).commit();*/
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Broadcast").child(broadcastid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    myList = new ArrayList<>();
                    user = dataSnapshot.getValue(BroadcastUser.class);
                    assert user != null;
                    txtUserName.setText(user.getBroadcastName());
                    txtCheckActive.setText(user.getReceiver().size() + " Members");
                    imgUser.setImageResource(R.drawable.broadcast);

                    String name = user.getReceiver().get(0);

                    myList.addAll(user.getReceiver());


                    readMesagges(broadcastid);
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

        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
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

    public static String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
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
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

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

    public void chooseAudioFromGallary() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO_FROM_GALLERY);
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
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("image_upload", mImageUri + "//");
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
                    //uploading the file
                    //  uploadFile(data.getData(), extension);
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

    private String getAudioPath(Uri uri) {
        String[] data = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(getActivity(), uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

                        sendMessageFromBroadcast(getActivity(),broadcastid, "Image", true, mUri, "default", false, "default", "default");

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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    public static void sendMessageFromBroadcast(final Context context, final String sender, String message, boolean isimage, String uri, String docUri, boolean iscontact, String con_name, String con_num){

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
            hashMap.put("to", "broadcast");
            hashMap.put("sender", sender);
            hashMap.put("broadcast_receiver", myList);
            hashMap.put("message", message);
            hashMap.put("issend", true);
            hashMap.put("isseen", false);
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
        } else {
            hashMap.put("id", sb.toString());
            hashMap.put("to", "broadcast");
            hashMap.put("sender", sender);
            hashMap.put("broadcast_receiver", myList);
            hashMap.put("message", message);
            hashMap.put("issend", false);
            hashMap.put("isseen", false);
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
        }
        reference.setValue(hashMap);

        for (int i = 0; i < user.getReceiver().size(); i++) {
            // add user to chat fragment
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(user.getReceiver().get(i)).child(user.getSender());

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (!dataSnapshot.exists()) {
                            chatRef.child("id").setValue(user.getSender());
                            chatRef.child("istyping").setValue(false);
                            chatRef.child("isnotification").setValue(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


//            final String msg = message;

          /*  reference = FirebaseDatabase.getInstance().getReference("Users").child((user.getReceiver().get(i)));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        if (notify) {
//                            sendNotifiaction(context,receiver, user.getUsername(), msg);
                        }
                        notify = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        }


    }

    /*private static void sendNotifiaction(final Context context,String receiver, final String username, final String message) {
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
                                                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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
    }*/

    private void readMesagges(final String groupid) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mchat.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getTo().equalsIgnoreCase("broadcast")) {
                            if (chat.getSender().equals(groupid)) {
                                mchat.add(chat);
                            }
                        }
                    }
                    messageAdapter = new BroadcastMessageAdapter(getActivity(), mchat);
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
