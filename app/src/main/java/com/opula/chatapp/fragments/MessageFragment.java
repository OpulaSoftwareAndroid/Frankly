package com.opula.chatapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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
import android.text.format.DateFormat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
//import com.esafirm.imagepicker.features.ImagePicker;
//import com.esafirm.imagepicker.model.Image;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.MessageAdapter;
import com.opula.chatapp.api.APIService;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.LocationTrack;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.AESUtils;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Client;
import com.opula.chatapp.notifications.Data;
import com.opula.chatapp.notifications.MyResponse;
import com.opula.chatapp.notifications.Sender;
import com.opula.chatapp.notifications.Token;
import com.rygelouv.audiosensei.player.AudioSenseiListObserver;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
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

public class MessageFragment extends Fragment  {

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
    ImageView emojiButton, imageViewShareMediaDialog;
    public  static ImageView imageViewCloseChat;
    View rootView;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int REQUEST_CODE_PICKER = 11;
    private ArrayList<com.esafirm.imagepicker.model.Image> images = new ArrayList<>();
    Context context;
    VideoPicker videoPicker;
    final static String TAG="MessageFragment";
    EmojIconActions emojIcon;
    //pickimae
    StorageReference storageReference;
    private StorageTask uploadTask;
    Uri mImageUri = null;
    Uri mPDFUri = null;
    int GALLERY = 1;
    int AUDIO_FROM_GALLERY = 2;
    String CheckActive;
    public static boolean notify = false;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();
    public static StringBuilder sb;
    String AES = "AES";
    ValueEventListener mSendEventListner;
    RelativeLayout is_text;
    public static LinearLayout linearLayoutReplyMessage;
    //new library
    public static TextView textViewUserName;
    public static String strRepliedMessageID ="";
    public static  EmojiconTextView textViewReplyMessage;
    RecordView recordView;
    RecordButton recordButton;
    BottomSheetDialog dialogMenu;
    final static int PICK_PDF_CODE = 2342;
    String strIsSecureChat;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 201;


    private static String fileName = null;
    boolean mStartRecording = true;

    boolean isBlockedUser=false;
    String strLoginUserName;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    ImageView imageViewSettingPopUpIcon;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        //if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Record to the external cache directory for visibility
        String strFileName=  randomString(8);
        Date c = Calendar.getInstance().getTime();
        Log.d(TAG,"jigar the is secure whole current time is "+c.getTime());

        context=getContext();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String day          = (String) DateFormat.format("dd",   c); // 20
        String monthString  = (String) DateFormat.format("MMM",  c); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   c); // 06
        String year         = (String) DateFormat.format("yyyy", c); // 2013
        String formattedDate = String.valueOf(c.getTime());
        setRetainInstance(true);
        //        String formattedDate = day+monthString+year;

//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        fileName += "/franklyAudio"+formattedDate+"_"+strFileName+".3gp";


  //      fileName = getActivity().getExternalCacheDir().getAbsolutePath();

//
//        System.out.println("jigar the file name Current time => " + fileName);

        // Record to the external cache directory for visibility
//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        fileName += "/audiorecordtest.3gp";

//        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        fileName += "/audiorecordtest.3gp";
//        fileName = getContext().getExternalCacheDir().getAbsolutePath();
//        fileName += "/audiorecordtestagain4th.3gp";
   //     FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        MainActivity.hideFloatingActionButton();
        MainActivity.showpart1();
        try {
            if(getArguments()!=null) {
                strIsSecureChat = getArguments().getString(WsConstant.IS_MESSAGE_SECURE, "");
            }else
            {
                strIsSecureChat="false";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            strIsSecureChat="false"; }

        Log.d(TAG,"jigar the is secure intent have is clicked with status is "+strIsSecureChat);

        sharedPreference = new SharedPreference();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        initViews(view);

        emojIcon = new EmojIconActions(getActivity(), rootView, text_send, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_24dp, R.drawable.ic_sentiment_satisfied_black_24dp);
        AudioSenseiListObserver.getInstance().registerLifecycle(getLifecycle());
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
   //     messageAdapter=new MessageAdapter(linearLayoutManager);
        //        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                linearLayoutManager.scrollToPositionWithOffset(2, 20);
//            }
//        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        userid = sharedPreference.getValue(getActivity(), WsConstant.userId);
        String username = sharedPreference.getValue(getActivity(), WsConstant.userUsername);
//        getUserDetails();

        getBlockUserListOfReceiver();
        Log.d(TAG,"jigar the user name  we have STRAT in fragment is "+username);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG,"jigar the user id current user we have in fragment is "+fuser.getUid());


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();

                if (!msg.equals("")) {

                    if(linearLayoutReplyMessage.getVisibility()==View.VISIBLE)
                    {
                        //   Toast.makeText(getContext(),"message is: "+textViewReplyMessage.getText().toString()+" and "+textViewUserName.getText().toString(),Toast.LENGTH_LONG).show();
                        sendMessageToPersonal(getActivity(), strIsSecureChat,fuser.getUid(), userid, msg
                                , true, strRepliedMessageID,textViewReplyMessage.getText().toString()
                                ,textViewUserName.getText().toString()
                                , false, "default"
                                , false, "default"

                                , "default"
                                , false, "default", "default");
                        linearLayoutReplyMessage.setVisibility(View.GONE);
                    }else
                    {

                       // if(AppGlobal.isNetworkAvailable(getContext())) {
                            sendMessageToPersonal(getActivity(), strIsSecureChat, fuser.getUid(), userid, msg
                                    , false, "", "", ""
                                    ,false, "default"
                                    , false, "default"
                                    , "default", false, "default"
                                    , "default");
                       // }else
//                        {
                         //   Toast.makeText(getContext(),"Offline Mode",Toast.LENGTH_LONG).show();

  //                      }
                    }
                }
                text_send.setText("");
            }
        });



        imageViewShareMediaDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.fragment_item_bottom_share_whatsapp_android_media, null);
                dialogMenu = new BottomSheetDialog(getContext());
                dialogMenu.setContentView(view);
                dialogMenu.setCancelable(true);
                LinearLayout lin_document = dialogMenu.findViewById(R.id.lin_document);
                LinearLayout lin_camera = dialogMenu.findViewById(R.id.lin_camera);
                LinearLayout lin_gallery = dialogMenu.findViewById(R.id.lin_gallery);
                LinearLayout lin_audio = dialogMenu.findViewById(R.id.lin_audio);
                LinearLayout lin_location = dialogMenu.findViewById(R.id.lin_location);
                LinearLayout lin_contact = dialogMenu.findViewById(R.id.lin_contact);
                lin_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


//                        if (ContextCompat.checkSelfPermission(getActivity(),
//                                Manifest.permission.ACCESS_FINE_LOCATION)
//                                != PackageManager.PERMISSION_GRANTED) {
//
//                            // Permission is not granted
//                            // Should we show an explanation?
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                                getCurrentLocation();
//                                // Show an explanation to the user *asynchronously* -- don't block
//                                // this thread waiting for the user's response! After the user
//                                // sees the explanation, try again to request the permission.
//                            } else {
//                                // No explanation needed; request the permission
//
//                                ActivityCompat.requestPermissions(getActivity(),
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_READ_LOCATION);
//                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                                // app-defined int constant. The callback method gets the
//                                // result of the request.
//                            }
//                        } else {
//                            // Permission has already been granted
//                            getCurrentLocation();
//
//                        }

                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            }, MY_PERMISSIONS_REQUEST_READ_LOCATION);

                            dialogMenu.dismiss();

                        }else {
                            getCurrentLocation();
                            dialogMenu.dismiss();

                        }
                    }
                });
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
                    if(Objects.requireNonNull(user).getId()!=null) {

                    String str1 = user.getUsername();
                    String strUsername = str1.substring(0, 1).toUpperCase() + str1.substring(1);
                    txtUserName.setText(strUsername);
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
                            Log.d(TAG, "jigar the error in image exception we have in reference users is " + e);

                            e.printStackTrace();
                        }
                    }

                    readMesagges(fuser.getUid(), userid, user.getImageURL());
                }
                } catch (Exception e) {
                    Log.d(TAG,"jigar the error in exception we have in reference users is "+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"jigar the error on cancelled database error we have in reference users is "+databaseError);

            }
        });

        sendMessage(userid);

        //new library
        recordView = (RecordView) rootView.findViewById(R.id.record_view);
        recordButton = (RecordButton) rootView.findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);

        //        recordButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN)
//                {
//
//                }
//                else if(motionEvent.getAction()== MotionEvent.ACTION_)
//                return false;
//            }
//        });

       recordView.setOnRecordListener(new OnRecordListener() {


           @Override
            public void onStart() {
               fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
               Date c = Calendar.getInstance().getTime();
               String strCurrentTime=String.valueOf(c.getTime());
               fileName += "/audiofrankly"+strCurrentTime+".3gp";
                //Start Recording..
                Log.d("RecordView", "onStart");
                is_text.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);
                mStartRecording=true;
                onRecord(mStartRecording);
                if (mStartRecording) {
             //       setText("Stop recording");
                } else {
               //     setText("Start recording");
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
                Log.d("RecordView", "onFinish");
                is_text.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
                Uri audioFileUri=Uri.fromFile(new File(fileName));
                mStartRecording=false;

                onRecord(mStartRecording);
                Log.d(TAG, "jigar the audio uri in internal memory is "+audioFileUri);
                fileName="";
                //uploadAudio(audioFileUri,"3gp");
           //     uploadmp3formatdemo(audioFileUri,"mp3");

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


public void getCurrentLocation () {
    LocationTrack locationTrack = new LocationTrack(getContext());


    if (locationTrack.canGetLocation()) {


        double longitude = locationTrack.getLongitude();
        double latitude = locationTrack.getLatitude();

        if(longitude!=0.0 && latitude!=0.0)
        {
            String strLocationMessage="https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude;

            sendMessageToPersonal(getActivity(), strIsSecureChat, fuser.getUid(), userid, strLocationMessage
                    , false, "", "",""
                    , false, "default"
                    , false, "default"
                    , "default", false, "default", "default");



        }
//        Toast.makeText(getContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
    } else {

        locationTrack.showSettingsAlert();

    }
}
    private void showFilterPopup(View v) {
        final PopupMenu popup = new PopupMenu(getContext(), v);
        // Inflate the menu from xml
        popup.inflate(R.menu.popup_filters);
        popup.show();
        // Setup menu item selection
        popup.getMenu().getItem(1).setVisible(false);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_item_block:
                        new FancyAlertDialog.Builder(getActivity())
                                .setTitle("Are you sure! you want Block "+txtUserName.getText() +"?")
                                .setBackgroundColor(getResources().getColor(R.color.colorDarkRed))  //Don't pass R.color.colorvalue
                                .setMessage("You won't be able to chat!")
                                .setNegativeBtnText("Cancel")
                                .setPositiveBtnBackground(getResources().getColor(R.color.colorDarkRed))  //Don't pass R.color.colorvalue
                                .setPositiveBtnText("Yes, Block")
                                .setNegativeBtnBackground(getResources().getColor(R.color.colorPrimary))  //Don't pass R.color.colorvalue
                                .setIcon(R.drawable.ic_action_block_white, Icon.Visible)
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .OnPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Log.d(TAG,"jigar the user name we are getting  is "+fuser.getUid());
                                        Log.d(TAG,"jigar the receiver user name we are getting  is "+userid);
                                        String strLoginUserId=fuser.getUid();
                                        String strReceiverId=userid;
                                        popup.getMenu().getItem(0).setVisible(false);
                                        popup.getMenu().getItem(1).setVisible(true);

                                        popup.show();

                                        //                                        MenuItem menuItem= popup.getMenu().findItem(R.id.menu_item_block);
//                                        menuItem.setTitle("Unblock");
//                                        popup.getMenu().getItem(0).setVisible(false);
//                                        popup.getMenu().add("Unblock");

                                        blockUser();

                                    }
                                })
                                .OnNegativeClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        //Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build();
                        return true;
                    case R.id.menu_item_clear_chat:

                        new FancyAlertDialog.Builder(getActivity())
                                .setTitle("Are you sure! you want delete this chat?")
                                .setBackgroundColor(getResources().getColor(R.color.colorPrimary))  //Don't pass R.color.colorvalue
                                .setMessage("You won't be able to recover this chat!")
                                .setNegativeBtnText("Cancel")
                                .setPositiveBtnBackground(getResources().getColor(R.color.colorPrimary))  //Don't pass R.color.colorvalue
                                .setPositiveBtnText("Delete")
                                .setNegativeBtnBackground(getResources().getColor(R.color.colorPrimaryone))  //Don't pass R.color.colorvalue
                                .setAnimation(Animation.SLIDE)
                                .isCancellable(true)
                                .setIcon(R.drawable.sym_keyboard_delete_holo_dark, Icon.Visible)
                                .OnPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        Log.d(TAG,"jigar the user name we are getting  is "+fuser.getUid());
                                        Log.d(TAG,"jigar the receiver user name we are getting  is "+userid);
                                        String strLoginUserId=fuser.getUid();
                                        String strReceiverId=userid;
                                        deleteMessageList(strLoginUserId,strReceiverId);
//                                  //    Toast.makeText(getContext(),"Delete",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .OnNegativeClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        //Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build();
//                        Toast.makeText(getContext(), "Clear Chat", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.menu_view_profile:

                        FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new UserProfileFragment()).addToBackStack(null).commit();

                    default:
                        return false;
                }
            }
        });
    }
    private void onRecord(boolean start) {

        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void getBlockUserListOfReceiver() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (Objects.requireNonNull(user).getId() != null) {

                            if (user.getId().equals(fuser.getUid())) {
                                Log.d(TAG, "jigar the blocked user list we have is " + user.getBlockedby());


                                String strBlockedUserList = user.getBlockedby();

                                if (strBlockedUserList.contains(userid)) {
                                    isBlockedUser = true;
                                    Toast.makeText(getContext(), "You have been blocked by user ", Toast.LENGTH_LONG).show();
                                } else {
                                    isBlockedUser = false;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(valueEventListener);
        mSendEventListner = valueEventListener;
    }



    public void blockUser()
    {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                         try {
                                                             for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                 User user = snapshot.getValue(User.class);
                                                                 if (Objects.requireNonNull(user).getId() != null) {

                                                                     Log.d(TAG, "jigar the username we have is " + user.getUsername());
                                                                     if (user.getId().equals(userid)) {
                                                                         String strWhoBlockedUser = user.getBlockedby();

                                                                         HashMap<String, Object> hashMap = new HashMap<>();

                                                                         if (strWhoBlockedUser == null) {
                                                                             hashMap.put("blockedby", fuser.getUid());
                                                                             snapshot.getRef().updateChildren(hashMap);

                                                                         } else if (strWhoBlockedUser.contains(fuser.getUid())) {

                                                                             System.out.println("jigar the already blocked user is " + strWhoBlockedUser);

                                                                         } else {

                                                                             if (strWhoBlockedUser.equals("")) {
                                                                                 System.out.println("jigar is new blocked user id " + strWhoBlockedUser);
                                                                                 hashMap.put("blockedby", fuser.getUid());
                                                                                 snapshot.getRef().updateChildren(hashMap);

                                                                             } else {
                                                                                 System.out.println("jigar is already blocked user are : " + strWhoBlockedUser);
                                                                                 hashMap.put("blockedby", strWhoBlockedUser + " , " + fuser.getUid());
                                                                                 snapshot.getRef().updateChildren(hashMap);
                                                                                 System.out.println("jigar is already blocked user are : " + strWhoBlockedUser);
                                                                             }
                                                                         }
                                                                         Log.d(TAG, "jigar the blocking username we have is " + user.getUsername());
                                                                     }

                                                                 }
                                                             }
                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                                     }
        }
        );
    }

    public void unBlockUser()
    {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (Objects.requireNonNull(user).getId() != null) {

                            Log.d(TAG, "jigar the username we have is " + user.getUsername());
                            if (user.getId().equals(userid)) {
                                String strWhoBlockedUser = user.getBlockedby();

                                HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put("blockedby", true);
//                            snapshot.getRef().updateChildren(hashMap);

                                if (strWhoBlockedUser.contains(fuser.getUid())) {

                                    strWhoBlockedUser = strWhoBlockedUser.replace(fuser.getUid(), "");
                                    hashMap.put("blockedby", strWhoBlockedUser);
                                    snapshot.getRef().updateChildren(hashMap);
                                    System.out.println("jigar the already blocked user is " + strWhoBlockedUser);

                                }
                                Log.d(TAG, "jigar the blocking username we have is " + user.getUsername());
                            }
//                            if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)
//                                    && !chat.isIsseen()) {
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                Long tsLong = (System.currentTimeMillis() / 1000);
//                                String ts = tsLong.toString();
//                                hashMap.put("isseen", true);
//                                hashMap.put("issend", true);
//                                hashMap.put("isreceived", true);
//                                hashMap.put("isseentime", ts);
//                                snapshot.getRef().updateChildren(hashMap);
//                            }
//
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteMessageList(final String strLoginUserId,final String strReceiverID) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if(Objects.requireNonNull(chat).getId()!=null) {

                            if (chat.getTo().equalsIgnoreCase("personal")) {

                                if ((chat.getSender().equals(strLoginUserId) || chat.getSender().equals(strReceiverID))
                                        && (chat.getReceiver().equals(strLoginUserId) || chat.getReceiver().equals(strReceiverID))) {
                                    HashMap<String, Object> hashMap = new HashMap<>();

                                    if (chat.getReceiver().equals(strLoginUserId)) {
                                        if (chat.getIsstatus().equals("0")) {
                                            hashMap.put("isstatus", "2");
                                        } else if (chat.getIsstatus().equals("1")) {
                                            hashMap.put("isstatus", "3");
                                        }
                                        Log.d(TAG, "jigar the login user is receiver and id is : " + strReceiverID + " and message is " + chat.getMessage());
                                        snapshot.getRef().updateChildren(hashMap);
                                        //                                    && chat.getSender().equals(userid)
//                                    && !chat.isIsseen()) {
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                Long tsLong = (System.currentTimeMillis() / 1000);
//                                String ts = tsLong.toString();

//                                hashMap.put("isseen", true);
//                                hashMap.put("issend", true);
//                                hashMap.put("isreceived", true);
//                                hashMap.put("isseentime", ts);
//                                snapshot.getRef().updateChildren(hashMap);
                                    } else {
                                        if (chat.getIsstatus().equals("0")) {
                                            hashMap.put("isstatus", "1");
                                        } else if (chat.getIsstatus().equals("2")) {
                                            hashMap.put("isstatus", "3");
                                        }
                                        Log.d(TAG, "jigar the login user is sender and id is : " + strLoginUserId + " and message is " + chat.getMessage());
                                        snapshot.getRef().updateChildren(hashMap);
                                    }

                                }
                            }
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
////        ValueEventListener valueEventListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                //
////            }
////        };
//        reference.addValueEventListener(valueEventListener);
//        mSendEventListner = valueEventListener;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    public static boolean showReplyMessageDialog() {
        //        TranslateAnimation animate = new TranslateAnimation(0,0,0,-linearLayoutReplyMessage.getHeight());
//        animate.setDuration(500);
//        animate.setFillAfter(true);
//        linearLayoutReplyMessage.startAnimation(animate);
        linearLayoutReplyMessage.setVisibility(View.VISIBLE);
        //        linearLayoutReplyMessage.animate()
//                .translationY(-linearLayoutReplyMessage.getHeight())
//                .alpha(0.0f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        linearLayoutReplyMessage.setVisibility(View.VISIBLE);
//                    }
//                });
//        linearLayoutReplyMessage.setVisibility(View.VISIBLE);
//        textViewUserName.setVisibility(View.VISIBLE);
//        textViewReplyMessage.setVisibility(View.VISIBLE);
        return true;
    }


    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, "jigar the prepare() failed at start with exception "+e);

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
            recorder.start();
        } catch (IOException e ) {
            Log.e(LOG_TAG, "jigar the prepare () failed"+e);
        }
        catch (IllegalStateException ex){
            Log.e(LOG_TAG, "jigar the prepare () failed"+ex);

        }


    }

    private void stopRecording() {
        try {
            Uri audioFileUri = Uri.fromFile(new File(fileName));
            mStartRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            uploadAudio(audioFileUri, "3gp");
        }catch (IllegalStateException ex)
        {

        }
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
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    getCurrentLocation();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }


                return;
            }
        }

        if (!permissionToRecordAccepted) getActivity().finish();

    }
    private void getUserDetails() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user;
                    user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    strLoginUserName=user.getUsername();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
//        String[] mimeTypes =
//                {"application/pdf", "text/plain"};
        String[] mimeTypes =
                {"application/pdf", "text/plain", "application/msword","application/vnd.ms-powerpoint","application/vnd.ms-excel"};

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


//        final FilePicker filePicker = new FilePicker(MessageFragment.this);
//// filePicker.allowMultiple();
//// filePicker.
//        filePicker.setFilePickerCallback(new FilePickerCallback() {
//            @Override
//            public void onFilesChosen(List<ChosenFile> files) {
//                // Display Files
//
//                Log.d(TAG,"jigar the file picker have selected is "+files.get(0).getExtension());
//                Log.d(TAG,"jigar the file picker have selected is "+files.get(0).getOriginalPath());
//
//            }
//
//            @Override
//            public void onError(String message) {
//                Log.d(TAG,"jigar the file picker error is "+files.get(0).getOriginalPath());
//
//                // Handle errors
//            }
//        });
//
//        filePicker.pickFile();

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
            Log.d(TAG,"jigar the error in exception we current time is "+e);

        }
        return "";
    }

    private void initViews(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);
        btn_send = view.findViewById(R.id.btn_send);
        text_send = view.findViewById(R.id.text_send);

//
//        text_send.setKeyListener(new KeyListener() {
//            @Override
//            public int getInputType() {
//
//
//
//                return 0;
//            }
//
//            @Override
//            public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
//                return false;
//            }
//
//            @Override
//            public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
//                return false;
//            }
//
//            @Override
//            public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
//                return false;
//            }
//
//            @Override
//            public void clearMetaKeyState(View view, Editable editable, int i) {
//
//            }
//        });
        imgBack = view.findViewById(R.id.imgBack);
        imgUser = view.findViewById(R.id.imgUser);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtCheckActive = view.findViewById(R.id.txtCheckActive);
        rootView = view.findViewById(R.id.root_view);
        emojiButton = view.findViewById(R.id.emoji_btn);
        imageViewShareMediaDialog = view.findViewById(R.id.imageViewShareMediaDialog);
        imageViewSettingPopUpIcon = view.findViewById(R.id.imageViewSettingPopUpIcon);
        imageViewCloseChat=view.findViewById(R.id.imageViewCloseChat);
        linearLayoutReplyMessage=view.findViewById(R.id.linearLayoutReplyMessage);
        linearLayoutReplyMessage.setVisibility(View.GONE);
        textViewUserName=view.findViewById(R.id.textViewUserName);
        textViewReplyMessage=view.findViewById(R.id.textViewReplyMessage);
        imageViewCloseChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                linearLayoutReplyMessage.animate()
//                        .translationY(0)
//                        .alpha(0.0f)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                linearLayoutReplyMessage.setVisibility(View.GONE);
//                            }
//                        });
                linearLayoutReplyMessage.setVisibility(View.GONE);
//                slideDown(linearLayoutReplyMessage);
            }
        });
        imageViewSettingPopUpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterPopup(view);
            }
        });

        is_text = view.findViewById(R.id.is_text);
        SpaceNavigationView spaceNavigationView=getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

    }


    public void choosePhotoFromGallary() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(galleryIntent, GALLERY);

//        ImagePicker.create(this) // Activity or Fragment
//                .start(REQUEST_CODE_PICKER);



        //----------comment for video picker
//
        //----------comment for video picker end

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_show_gallery_video, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(true);

        // initialize the VideoView
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        final TextView textViewSelectImage=dialogView.findViewById(R.id.textViewSelectImage);
        final TextView textViewSelectVideo=dialogView.findViewById(R.id.textViewSelectVideo);


        textViewSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(MessageFragment.this)
                .returnAfterFirst(true) // set whether pick or camera action should return immediate result or not. For pick image only work on single mode
                .folderMode(false) // folder mode (false by default)
               // .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // image selection title
                .single() // single mode
                .multi() // multi mode (default mode)
                .limit(70) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image ("Camera" folder by default)
                .origin(images) // original selected images, used in multi mode
           //     .useExternalPickers(true) // show external image pickers in the toolbar (Google photos... )
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code

            alertDialog.dismiss();
            }
        });


        textViewSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                videoPicker = new VideoPicker(MessageFragment.this);
        videoPicker.setVideoPickerCallback(new VideoPickerCallback(){
            @Override
            public void onVideosChosen(List<ChosenVideo> list) {
                Log.d(TAG,"jigar the selected video uri we have is "+list.get(0).getOriginalPath());

//                Toast.makeText(context,"jigar the path we have is "+list.get(0).getOriginalPath(),Toast.LENGTH_LONG).show();
//                Uri uri =Uri.fromFile(new File((list.get(0).getOriginalPath())));
                Uri uriThumbnailImage =Uri.fromFile(new File((list.get(0).getPreviewThumbnail())));
                Uri uriVideo =Uri.fromFile(new File((list.get(0).getOriginalPath())));

                uploadMultipleVideo(uriThumbnailImage,uriVideo);
                alertDialog.dismiss();

            }

            @Override
            public void onError(String message) {
                // Do error handling
                Log.d(TAG,"jigar the selected video error "+message);
            }
        }
        );
// videoPicker.allowMultiple(); // Default is false
// videoPicker.shouldGenerateMetadata(false); // Default is true
// videoPicker.shouldGeneratePreviewImages(false); // Default is true
        videoPicker.pickVideo();

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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_VIDEO_DEVICE) {
                if (videoPicker == null) {
                    videoPicker = new VideoPicker(this);
                    videoPicker.setVideoPickerCallback(new VideoPickerCallback() {
                        @Override
                        public void onVideosChosen(List<ChosenVideo> list) {
                            Log.d(TAG,"jigar the selected video uri we have is "+list.get(0).getOriginalPath());

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                }

                videoPicker.submit(data);
            }
        }

            if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {

//        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
//        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
////            mPath = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
//            Log.d(TAG, "onActivityResult: ");
//
//           // if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {

           ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
//            List<String> images = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Log.d(TAG,"jigar the selected image uri we have is "+images.toString());

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            imagesEncodedList = new ArrayList<String>();

                if (images.size() != 0)
                {
                   // ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < images.size(); i++) {
                //        Log.d(TAG,"jigar the uri we have is "+images.get(i).getPath());
//                        Uri uri = Uri.parse(images.get(i).getPath());
                        Uri uri =Uri.fromFile(new File((images.get(i).getPath())));
                        mArrayUri.add(uri);
                        //                        Uri uri =Uri.fromFile(new File((images.get(i))));

                     //   Log.d(TAG,"jigar the uri after we have is "+uri.getPath());

                        // Get the cursor
//                        Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
//                        // Move to first row
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        imageEncoded  = cursor.getString(columnIndex);
//                        imagesEncodedList.add(imageEncoded);
//                        cursor.close();
                    }
                    for(int i=0;i<mArrayUri.size();i++)
                    {
                        uploadMultipleImage(mArrayUri.get(i),context);
                    }
              //      Log.v(TAG, "jigar the multiple  Selected size Images are " + mArrayUri.size());
               //     Log.v(TAG, "jigar the multiple  Selected Images are " + mArrayUri.toString());
                }

        }




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
                    Log.d(TAG,"jigar the gallery selected file we  have is  "+ mImageUri + "and path we have is "+mImageUri.getPath());
                    uploadImage();
                }
            }

        }

        //        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
//                && null != data) {
//            // Get the Image from data
//
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//            imagesEncodedList = new ArrayList<String>();
//            if(data.getData()!=null){
//
//                Uri mImageUri=data.getData();
//
//                // Get the cursor
//                Cursor cursor = getContext().getContentResolver().query(mImageUri,
//                        filePathColumn, null, null, null);
//                // Move to first row
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                imageEncoded  = cursor.getString(columnIndex);
//                cursor.close();
//
//            } else {
//                if (data.getClipData() != null) {
//                    ClipData mClipData = data.getClipData();
//                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                    for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                        ClipData.Item item = mClipData.getItemAt(i);
//                        Uri uri = item.getUri();
//                        mArrayUri.add(uri);
//                        // Get the cursor
//                        Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
//                        // Move to first row
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        imageEncoded  = cursor.getString(columnIndex);
//                        imagesEncodedList.add(imageEncoded);
//                        cursor.close();
//
//                    }
//                    for(int i=0;i<mArrayUri.size();i++)
//                    {
//                        uploadMultipleImage(mArrayUri.get(i));
//
//                    }
//                    Log.v(TAG, "jigar the multiple  Selected size Images are " + mArrayUri.size());
//                    Log.v(TAG, "jigar the multiple  Selected Images are " + mArrayUri.toString());
//
//                }
//            }
//        } else {
//        //    Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
//        }


        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                String path = new File(Objects.requireNonNull(data.getData().getPath())).getAbsolutePath();

                Log.d(TAG,"jigar the file upload first have is " + data.getData());

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
                    Log.d(TAG,"jigar the file upload have is "+ extension + "/+" + filename);
                    //uploading the file
                      uploadFile(data.getData(),filename, extension);
                }
            } else {
                Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == AUDIO_FROM_GALLERY && null != data) {
            try {
                Uri audioFileUri = data.getData();
                String path1 = audioFileUri.getPath();
                Log.d(TAG,"jigar the audio path we have is "+audioFileUri.getPath());
                Log.d("Audio_path", path1 + "/");

             //   fileName=fileName+"1";

                uploadAudio(audioFileUri, "3gp");
                //uploadmp3formatdemo(audioFileUri, "mp3");

//
//  String path2 = getAudioPath(audioFileUri);
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

//    private void uploadmp3formatdemo(final Uri data, String ext)
//    {
//        // File or Blob
//        storageReference = FirebaseStorage.getInstance().getReference("chats");
//
//        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() +"."+ ext);
//
//
//       Uri file=data;
//
//// Create the file metadata
//      StorageMetadata  metadata = new StorageMetadata.Builder()
//                .setContentType("audio/mpeg")
//                .build();
//
//// Upload file and metadata to the path 'audio/audio.mp3'
//        uploadTask = storageReference.child("audio/"+file.getLastPathSegment()).putFile(file);
//
//// Listen for state changes, errors, and completion of the upload.
//        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                System.out.println("jigar Upload is " + progress + "% done");
//            }
//        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                System.out.println("jigar Upload is paused");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                System.out.println("jigar the error in Upload is " + exception);
//
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // Handle successful uploads on complete
//              //  String downloadUrl =taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
//                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        String downloadUrl = uri.toString();
//                        Log.d(TAG,"jigar the audio download url on succes is "+downloadUrl);
//
//                        sendAudioToPersonal(getActivity(), fuser.getUid(), userid, "Voice Message"
//                                , false,downloadUrl, "default", true, "default"
//                                , "default");
//                    }
//                });
//                return;
//
////                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
////                Uri downloadUri = taskSnapshot.getMetadata();
//  //              String mUri = downloadUri.toString();
//            }
//        });
//    }
//    private void uploadAudio(final Uri data, String ext) {
//
//
//        final ProgressDialog pd = new ProgressDialog(getContext());
//        pd.setMessage("Uploading Audio...");
//        pd.show();
//        pd.setCancelable(false);
//        if (data != null) {
//            storageReference = FirebaseStorage.getInstance().getReference("chats");
//
//            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() +"."+ ext);
//            StorageMetadata metadata = new StorageMetadata.Builder()
//                    .setContentType("image/jpg")
//                    .build();
//
//            fileReference.putFile(data,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                }
//            });
//
//            uploadTask = fileReference.putFile(data);
//
//            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//
//                        Log.d(TAG,"jigar the error in upload audio is "+task.getException());
//                        throw task.getException();
//                    }
//
//                    return fileReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    Log.d(TAG,"jigar the on audio complete in upload audio is "+task.getResult());
//
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        String mUri = downloadUri.toString();
//                        Log.d("UploadFile", mUri);
////                        sendMessage(userid);
//                        notify = true;
//                        sendAudioToPersonal(getActivity(), fuser.getUid(), userid, "Voice Message"
//                                , false,mUri, "default", true, "default"
//                                , "default");
//
//                        //getActivity(), fuser.getUid(), userid, "Document", false, "default", mUri);
//                        pd.dismiss();
//                    } else {
//                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
//                        pd.dismiss();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    pd.dismiss();
//                }
//            });
//        } else {
//            Toast.makeText(getContext(), "No File selected", Toast.LENGTH_SHORT).show();
//        }
//    }


private void uploadAudio(Uri data, String ext) {
    final ProgressDialog pd = new ProgressDialog(getContext());
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
                    sendAudioToPersonal(getActivity(), fuser.getUid(), userid, "Voice Message"
                                , false,mUri, "default", true, "default"
                                , "default");
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

    //
//    private String getAudioPath(Uri uri) {
//        String[] data = {MediaStore.Audio.Media.DATA};
//        CursorLoader loader = new CursorLoader(getActivity(), uri, data, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    private void uploadFile(Uri data, final String strFileName, final String ext) {

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
                        sendMessageToPersonal(getActivity(),strIsSecureChat, fuser.getUid(), userid, strFileName,
                                false,"","",""
                                ,false, "default"
                                ,false, "default",
                                mUri,false,"default", "default");

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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
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

                        sendMessageToPersonal(getActivity(),strIsSecureChat, fuser.getUid(), userid, "Image"
                                , false,
                                "","","",true
                                , mUri,false,"default", "default", false, "default", "default");

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

    private void uploadMultipleImage(Uri strImageUri,Context context) {

     //      final ProgressDialog pd = new ProgressDialog(getContext());
//        pd.setMessage("Uploading...");
//        pd.show();
//        pd.setCancelable(false);

        if (strImageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("chats");

            Log.d(TAG,"jigar the uri we get  before extension is "+strImageUri);
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(strImageUri));
            //
//            String strImageURL=String.valueOf(strImageUri);
//            String extension = String.valueOf(strImageURL).substring(strImageURL.lastIndexOf("."));
//            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
//                    + "." + extension);

            uploadTask = fileReference.putFile(strImageUri);
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

                        sendMessageToPersonal(getActivity(),strIsSecureChat, fuser.getUid(), userid, "Image",
                                false,"","",""
                                ,true, mUri,false,"default", "default", false, "default", "default");

           //             pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
           //             pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            //        pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadMultipleVideo(Uri strThumbNailImage, final Uri strVideoUri) {
        //      final ProgressDialog pd = new ProgressDialog(getContext());
//        pd.setMessage("Uploading...");
//        pd.show();
//        pd.setCancelable(false);
        if (strVideoUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("chats");
//            Log.d(TAG,"jigar the uri we get  before extension is "+strVideoUri);
//            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
//                    + "." + getFileExtension(strVideoUri));
            String strThumbNailImageURL=String.valueOf(strThumbNailImage);
            String extension = String.valueOf(strThumbNailImageURL).substring(strThumbNailImageURL.lastIndexOf("."));
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "" + extension);

            uploadTask = fileReference.putFile(strThumbNailImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();

                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        final String strThumbNailUri = downloadUri.toString();

                        String strVideoURL=String.valueOf(strVideoUri);
                        String extension = String.valueOf(strVideoURL).substring(strVideoURL.lastIndexOf("."));
                        final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                + "" + extension);

                        uploadTask = fileReference.putFile(strVideoUri);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();

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

                                    sendMessageToPersonal(getActivity(),strIsSecureChat, fuser.getUid(), userid, "Video",
                                            false,"","",""
                                            ,true, strThumbNailUri,true,mUri, "default"
                                            , false, "default", "default");

                                    //             pd.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                    //             pd.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                //        pd.dismiss();
                            }
                        });

                        //             pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        //             pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    //        pd.dismiss();
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
                        if(Objects.requireNonNull(chat).getId()!=null) {

                            if (chat.getTo().equalsIgnoreCase("personal")) {
                                if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)
                                        && !chat.isIsseen()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    Long tsLong = (System.currentTimeMillis() / 1000);
                                    String ts = tsLong.toString();
                                    hashMap.put("isseen", true);
                                    hashMap.put("issend", true);
                                    hashMap.put("isreceived", true);
                                    hashMap.put("isseentime", ts);
                                    snapshot.getRef().updateChildren(hashMap);
                                }
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
                        if(Objects.requireNonNull(chat).getId()!=null) {
                            Log.d(TAG, "jigar the user in firebase have is " + fuser.getUid());
                            Log.d(TAG, "jigar the user in sender we have is  " + userid);
                            Log.d(TAG, "jigar the user in get reciever we have is  " + chat.getReceiver());
                            Log.d(TAG, "jigar the user in get sender we have is  " + chat.getSender());
                            Log.d(TAG, "jigar the user in database chat id we have is  " + chat.getId());
                            Log.d(TAG, "jigar the user in database table id we have is  " + chat.getTable_id());

                            if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("issend", true);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG,"jigar the error in exception we have in send message is "+e);

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
        //        if(messageAdapter!=null) {
//            recyclerView.setAdapter(messageAdapter);
//            messageAdapter.notifyDataSetChanged();
//        }
        seenMessage(userid);
    }

    //    private String encrypt(String Data, String Password) throws Exception {
//        SecretKeySpec key = genrateKey(Password);
//        Cipher cipher = Cipher.getInstance(AES);
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        byte[] encVal = cipher.doFinal(Data.getBytes());
//        String encyptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
//        return encyptedValue;
//    }
//
//    private String decrypt(String outputString, String Password) throws Exception {
//        SecretKeySpec key = genrateKey(Password);
//        Cipher cipher = Cipher.getInstance(AES);
//        cipher.init(Cipher.DECRYPT_MODE, key);
//        byte[] encyptedValue = Base64.decode(outputString, Base64.DEFAULT);
//        byte[] decValue = cipher.doFinal(encyptedValue);
//        String decyptedValue = new String(decValue);
//        return decyptedValue;
//    }

    private SecretKeySpec genrateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public static void sendMessageToPersonal(final Context context, final String strIsSecureChat
            , final String sender, final String receiver
            , String message,boolean isRepliedMessage,String strRepliedMessageID,String strRepliedMessage
            ,String strRepliedUserName, boolean isimage ,String uri,boolean isVideo,String strVideoUri
            , String docUri
            , boolean iscontact, String con_name, String con_num) {

        if(strIsSecureChat.equals("true")) {
            String encrypted = message;
            String sourceStr = "This is any source string";
            try {
                encrypted = AESUtils.encrypt(encrypted);
                Log.d("TEST", "encrypted:" + encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }

            message = encrypted;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").push();


//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.keepSynced(true);
        randomString(9);


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
            hashMap.put("isseentime", "");
            hashMap.put("isreceived", false);
            hashMap.put("isrepliedmessage", isRepliedMessage);
            hashMap.put("isrepliedmessageid", strRepliedMessageID);
            hashMap.put("repliedmessage", strRepliedMessage);
            hashMap.put("isrepliedmessageby", strRepliedUserName);
            hashMap.put("isimage", isimage);
            hashMap.put("isvideo", isVideo);
            hashMap.put("videourl", strVideoUri);
            hashMap.put("iscontact", iscontact);
            hashMap.put("isaudio", false);
            hashMap.put("issecure",Boolean.valueOf(strIsSecureChat));
            hashMap.put("contact_number", con_num);
            hashMap.put("contact_name", con_name);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("storage_uri", "default");
            hashMap.put("audio_uri", "default");
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
            hashMap.put("isseentime", "");
            hashMap.put("isreceived", false);
            hashMap.put("isrepliedmessage", isRepliedMessage);
            hashMap.put("isrepliedmessageid", strRepliedMessageID);
            hashMap.put("repliedmessage", strRepliedMessage);
            hashMap.put("isrepliedmessageby", strRepliedUserName);
            hashMap.put("isimage", isimage);
            hashMap.put("isvideo", isVideo);
            hashMap.put("videourl", strVideoUri);
            hashMap.put("iscontact", iscontact);
            hashMap.put("isaudio", false);
            hashMap.put("issecure", Boolean.valueOf(strIsSecureChat));
            hashMap.put("contact_number", con_num);
            hashMap.put("contact_name", con_name);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("storage_uri", "default");
            hashMap.put("isstatus", "0");


        }
        reference.setValue(hashMap);

        final String messageUniqueID = reference.getKey();
        Log.d(TAG,"jigar the last inserted data id is "+messageUniqueID);
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
                    Log.d(TAG,"jigar the error in exception we have in personal message is "+e);
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
                    assert user != null;
                    if(Objects.requireNonNull(user.getUsername())!=null) {

                        if (notify) {
                            sendNotifiaction(context, messageUniqueID, receiver, user.getUsername(), msg);
                        }
                        notify = false;
                    }
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
            hashMap.put("isreceived", false);
            hashMap.put("isrepliedmessage", false);
            hashMap.put("isrepliedmessageid", "");
            hashMap.put("repliedmessage", "");
            hashMap.put("isseentime", "");
            hashMap.put("isvideo", false);
            hashMap.put("videourl", "default");
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
            hashMap.put("isreceived", false);
            hashMap.put("isseen", false);
            hashMap.put("isrepliedmessage",false);
            hashMap.put("isrepliedmessageid","");
            hashMap.put("repliedmessage", "");
            hashMap.put("isseentime", "");
            hashMap.put("isimage", isimage);
            hashMap.put("iscontact", false);
            hashMap.put("isaudio", isAudio);
            hashMap.put("isvideo", false);
            hashMap.put("videourl", "default");
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
        final String messageUniqueID = reference.getKey();
        Log.d(TAG,"jigar the last inserted data id is "+messageUniqueID);

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
                    if(Objects.requireNonNull(user).getUsername()!=null) {

                        notify = true;
                        if (notify) {
                            sendNotifiaction(context, messageUniqueID, receiver, user.getUsername(), msg);
                        }
                        notify = false;
                    }
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
    private static void sendNotifiaction(final Context context, final String strMessageUniqueID
            , String receiver, final String username, final String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(fuser.getUid()
                                , R.mipmap.ic_launcher,username + ": " + message, "New Message", userid,strMessageUniqueID);

                        if(Objects.requireNonNull(token).getToken()!=null) {

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            Log.d(TAG, "jigar the on notification response we have in notification is " + response.code());

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
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG,"jigar the main exception in notification is "+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"jigar the notification cancelled is "+databaseError.getMessage());
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
                        if(Objects.requireNonNull(chat).getId()!=null) {

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
                    }
                    strLoginUserName = sharedPreference.getValue(getActivity(), WsConstant.userUsername);

                    messageAdapter = new MessageAdapter(getActivity(), mchat, imageurl,strIsSecureChat
                            ,txtUserName.getText().toString(),strLoginUserName,linearLayoutManager);

                    Log.d(TAG,"jigar the user login has name is the "+fuser.getDisplayName());

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
