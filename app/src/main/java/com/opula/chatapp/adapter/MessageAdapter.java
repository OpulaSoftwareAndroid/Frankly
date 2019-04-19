package com.opula.chatapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.model.AESUtils;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.User;
import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jp.wasabeef.blurry.Blurry;
import nl.changer.audiowife.AudioWife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public Activity mContext;
    static String TAG = "MessageAdapter";
    public static List<Chat> mChat;
    private String imageurl,strLoginUserName;
    String AES = "AES";
    private String downloadAudioPath;
    String strIsSecureChat;
    SharedPreference sharedPreference;
    public static FirebaseUser fuser;
    private BottomSheetDialog dialogMenu;
    String strChatReceiverUserName;
   private  int intOldSelectedPosition=-1;

    public static int i = 0;
    public static ForwardMessageAdapter newChatUserAdapter;
    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
    public    String strUriForAudio, strUrlPath;
    private LinearLayoutManager manager;
    private MediaController mediaControls;

    public MessageAdapter(Activity mContext, List<Chat> mChat, String imageurl,String strIsSecureChat,String strChatReceiverUserName
    ,String strLoginUserName,LinearLayoutManager manager)
    {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this. strIsSecureChat=strIsSecureChat;
        this.strChatReceiverUserName=strChatReceiverUserName;
        this.strLoginUserName=strLoginUserName;
        this.manager=manager;
        sharedPreference=new SharedPreference();
        mediaControls = new MediaController(mContext);

    }

//    public MessageAdapter(LinearLayoutManager manager)
//    {
//        this.manager=manager;
//    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_item_right, parent, false);

            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);
        Log.d(TAG,"jigar the Chat_Data we getting is "+ chat.getSender());

        if(chat.getIsstatus().equals("0") ||
                (chat.getSender().equals(fuser.getUid()) && chat.getIsstatus().equals("2"))
                || (chat.getReceiver().equals(fuser.getUid()) && chat.getIsstatus().equals("1")))
        {
            holder.linear_chat.setVisibility(View.VISIBLE);
            if (imageurl.equals("default")) {
                holder.profile_image.setImageResource(R.drawable.image_boy);
            } else {
                Glide.with(mContext).load(imageurl).into(holder.profile_image);
            }
            if (chat.isIsrepliedmessage()) {
                holder.textViewRepliedMessage.setVisibility(View.VISIBLE);
                holder.textViewUserName.setVisibility(View.VISIBLE);
                holder.linearLayoutRepliedMessage.setVisibility(View.VISIBLE);
                holder.textViewRepliedMessage.setText(chat.getRepliedmessage());
                holder.textViewUserName.setText(chat.isIsrepliedmessageby());

            } else {
                holder.textViewRepliedMessage.setVisibility(View.GONE);
                holder.textViewUserName.setVisibility(View.GONE);
                holder.linearLayoutRepliedMessage.setVisibility(View.GONE);
            }

            if (!chat.getDoc_uri().equalsIgnoreCase("default")) {
                holder.show_message.setVisibility(View.GONE);
                holder.show_message.setText(chat.getDoc_uri());
                holder.relative_contact.setVisibility(View.GONE);
                holder.pdfView.setVisibility(View.VISIBLE);
                holder.img_receive.setVisibility(View.GONE);
                Log.d(TAG,"jigar the pdf we have is "+chat.getDoc_uri());
                //try
                {
//                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    holder.pdfView.fromUri(Uri.parse(chat.getDoc_uri()));
//                    generateImageFromPdf(Uri.parse(chat.getDoc_uri()), mContext);
                }
//                catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
            }

            if (!chat.getImage().equalsIgnoreCase("default")
                    && chat.isIsvideo()) {
                holder.relative_contact.setVisibility(View.GONE);
                holder.show_message.setVisibility(View.GONE);
                // holder.videoView.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
                //holder.videoView.start();
                try
                {
                    if (chat.isIsvideo()) {
                        holder.relativeLayoutVideoThumbnail.setVisibility(View.VISIBLE);
                        holder.imageViewPlayButton.setVisibility(View.VISIBLE);
                        holder.imageViewThumbnail.setVisibility(View.VISIBLE);

                        Log.d(TAG,"jigar the thumbnail image we have is "+chat.getImage());
                        Glide.with(mContext).load(chat.getImage())
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                          //              holder.progress_circular.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                         //               holder.progress_circular.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(holder.imageViewThumbnail);
                        //                        holder.videoView.setVisibility(View.VISIBLE);
//                        // set the media controller in the VideoView
//                        holder.videoView.setMediaController(mediaControls);
//                        // set the uri of the video to be played
//                        holder.videoView.setVideoURI(Uri.parse(chat.getVideourl()));
                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }else if(!chat.getImage().equalsIgnoreCase("default"))
                {

                    holder.relativeLayoutVideoThumbnail.setVisibility(View.GONE);
                    holder.imageViewPlayButton.setVisibility(View.GONE);
                    holder.imageViewThumbnail.setVisibility(View.GONE);
                    holder.img_receive.setVisibility(View.VISIBLE);
                    holder.relative.setVisibility(View.VISIBLE);
                    holder.progress_circular.setVisibility(View.VISIBLE);

                    Glide.with(mContext).load(chat.getImage())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    holder.progress_circular.setVisibility(View.GONE);
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    holder.progress_circular.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.img_receive);
                }

                Log.d(TAG, "jigar the is audio is active or not " + chat.isIsaudio());
                Log.d(TAG, "jigar the is audio url we have is active or not " + chat.getAudio_uri());


                if (chat.isIsaudio()) {
                    holder.show_message.setVisibility(View.GONE);
                    holder.relativeLayoutAudioPlayer.setVisibility(View.VISIBLE);
                    strUrlPath = chat.getAudio_uri();
                    holder.audioSenseiPlayerView
                            .setAudioTarget(strUrlPath);
//
//
//
//            holder.audioSenseiPlayerView.commitClickEvents();
//
//            View playerRootView = holder.audioSenseiPlayerView.getPlayerRootView();

//            MyAudio myAudio = audioArrayList.get(position);
//            holder.audioSenseiPlayerView.setAudioTarget(strUrlPath);
//            holder.audioTitle.setText("hello");
                //            holder.audioSenseiPlayerView.setAudioTarget(strUrlPath);
                } else {
                holder.show_message.setVisibility(View.VISIBLE);
                holder.relativeLayoutAudioPlayer.setVisibility(View.GONE);

            }
            if (chat.isIscontact()) {
                holder.show_message.setVisibility(View.GONE);
                holder.relative_contact.setVisibility(View.VISIBLE);
                holder.txtContactNumber.setText(chat.getContact_number() + "");
                holder.txtContactName.setText(chat.getContact_name());
            }
            if (chat.getImage().equalsIgnoreCase("default")
                    && chat.getDoc_uri().equalsIgnoreCase("default") && chat.isIscontact() == false) {
                holder.img_receive.setVisibility(View.GONE);
                holder.relative.setVisibility(View.GONE);
                holder.relative_contact.setVisibility(View.GONE);
                holder.show_message.setVisibility(View.VISIBLE);

                if (chat.getIssecure()) {
                    String encrypted = chat.getMessage();

                    String decrypted = "";
                    try {
                        decrypted = AESUtils.decrypt(encrypted);
                        Log.d("TEST", "decrypted:" + decrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  holder.show_message.setText(chat.getMessage());
                    holder.show_message.setText(decrypted);

                } else {
                    holder.show_message.setText(chat.getMessage());
                }
            }


            if (chat.isIsseen()) {
                holder.img_tick.setVisibility(View.GONE);
                holder.img_dtick.setVisibility(View.GONE);
                holder.img_dstick.setVisibility(View.VISIBLE);
                holder.img_loading_tick.setVisibility(View.GONE);

            } else {
                if (chat.isIssend()) {
                    holder.img_tick.setVisibility(View.GONE);
                    holder.img_dtick.setVisibility(View.VISIBLE);
                    holder.img_dstick.setVisibility(View.GONE);
                    holder.img_loading_tick.setVisibility(View.GONE);
                } else {
                    holder.img_loading_tick.setVisibility(View.VISIBLE);
                    holder.img_dtick.setVisibility(View.GONE);
                    holder.img_dstick.setVisibility(View.GONE);

                }

            }

            if (!chat.isIsreceived()) {
                holder.img_tick.setVisibility(View.VISIBLE);
                holder.img_dtick.setVisibility(View.GONE);
                holder.img_dstick.setVisibility(View.GONE);
                holder.img_loading_tick.setVisibility(View.GONE);
            }

            String str = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
            holder.show_time.setText(str);



            holder.pdfView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            holder.imageViewPlayAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });


            holder.imageViewPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_show_video_player, null);
                    alertDialogBuilder.setView(dialogView);
                    alertDialogBuilder.setCancelable(true);

                    // initialize the VideoView
                    final VideoView  mVideoView = (VideoView)dialogView.findViewById(R.id.videoView);
                    final ImageView imageViewBackButton=dialogView.findViewById(R.id.imageViewBackButton);
           //         final LinearLayout relativeLayoutVideoPlayerMain=dialogView.findViewById(R.id.linearLayoutVideoPlayerMain);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    imageViewBackButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    //
//                    relativeLayoutVideoPlayerMain.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Blurry.with(mContext)
//                                    .radius(25)
//                                    .sampling(2)
//                                    .animate(500)
//                                    .onto(relativeLayoutVideoPlayerMain);
//                        }
//                    });

                    mVideoView.setVideoURI(Uri.parse(chat.getVideourl()));
                    mVideoView.start();
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaControls =  new MediaController(mContext){
                                @Override
                                public void hide() {
                                    this.show();
                                }
                            };
                            mVideoView.start();
                            mVideoView.setMediaController(mediaControls);

                            /*mV

                             * and set its position on screen
                             */

                            mediaControls.setAnchorView(mVideoView);
                            ((ViewGroup) mediaControls.getParent()).removeView(mediaControls);

                            ((FrameLayout) dialogView.findViewById(R.id.videoViewWrapper))
                                    .addView(mediaControls);
                            mediaControls.setVisibility(View.VISIBLE);

                            if (position == 0)
                            {
                                mVideoView.start();

                            }else
                            {
                                // if we come from a resumed activity, video playback will
                                // be paused
                                mVideoView.pause();
                            }
                            // TODO Auto-generated method stub
//                            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                                @Override
//                                public void onVideoSizeChanged(MediaPlayer mp,
//                                                               int width, int height) {
//                                    /*
//                                     * add media controller
//                                     */
//
//
//                                }
//                            });
                        }
                    });

//                    try
//                    {
//
//                        // set the media controller in the VideoView
//                        myVideoView.setMediaController(mediaControls);
//
//                        // set the uri of the video to be played
//                        myVideoView.setVideoURI(Uri.parse(chat.getVideourl()));
//
//                    } catch (Exception e)
//                    {
//                        Log.e("Error", e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                    myVideoView.requestFocus();
//
//                    // we also set an setOnPreparedListener in order to know when the video
//                    // file is ready for playback
//
//                    myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
//                    {
//
//                        public void onPrepared(MediaPlayer mediaPlayer)
//                        {
//                            // if we have a position on savedInstanceState, the video
//                            // playback should start from here
//                            myVideoView.seekTo(position);
//
//                            System.out.println("vidio is ready for playing");
//
//                            if (position == 0)
//                            {
//                                myVideoView.start();
//                            } else
//                            {
//                                // if we come from a resumed activity, video playback will
//                                // be paused
//                                myVideoView.pause();
//                            }
//                        }
//                    });


                }
            });

            holder.img_receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dailog_show_image, null);
                    alertDialogBuilder.setView(dialogView);
                    alertDialogBuilder.setCancelable(true);

                    final ImageView image = dialogView.findViewById(R.id.image);
                    image.setOnTouchListener(new ImageMatrixTouchHandler(dialogView.getContext()));
                    AppGlobal.showProgressDialog(mContext);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    String string = chat.getImage();

                    Glide.with(mContext).load(string)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    AppGlobal.hideProgressDialog(mContext);
                                    Toast.makeText(mContext, "No Image Found!" + model + "/" + e, Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    AppGlobal.hideProgressDialog(mContext);
                                    alertDialog.show();
                                    return false;
                                }
                            })
                            .into(image);
//
//                Glide.with(image.getContext()).load(string).asBitmap().into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        image.setImage(ImageSource.bitmap(resource));
//                    }
//                });
                }
            });

            holder.linear_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d(TAG,"jigar the position in message adapter we have is "+chat.getRepliedmessage());

                    for(int i=0;i<mChat.size();i++)
                    {
                        if(mChat.get(i).getId().equals(chat.isIsrepliedmessageid()))
                        {
                            manager.scrollToPosition(i);
                            final Animation anim = new AlphaAnimation(0.0f, 1.0f);
                            anim.setDuration(50); //You can manage the blinking time with this parameter
                            anim.setStartOffset(20);
                            anim.setRepeatMode(Animation.REVERSE);
                            anim.setRepeatCount(Animation.INFINITE);

//                            manager.findViewByPosition(i).performClick();
                            Log.d(TAG,"jigar the position on top we have is "+manager.findFirstVisibleItemPosition());
                            final int finalI = i;
                            final int[] intCount = {0};
                            view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onDraw() {
                                    // TODO Auto-generated method stub

                                    if(intCount[0] ==0) {

                                        intCount[0] = intCount[0] +1;


                                        manager.findViewByPosition(finalI).setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));

                                        Timer timer_interact=new Timer();
                                        timer_interact.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                mContext.runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        manager.findViewByPosition(finalI). setBackgroundColor(0);
                                                    }
                                                });
                                            }
                                        }, 100);
                                        //creating runnable
                                    }
                                }
                            });


//                            Toast.makeText(mContext,"jigar the item position we have is "+i ,Toast.LENGTH_LONG).show();

                        }


                    }

//                    manager.scrollToPosition();
                }
            });

            holder.linear_chat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));
                    i = holder.getAdapterPosition();
                    MainActivity.showpart3();
                    Vibrator vv = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    assert vv != null;
                    vv.vibrate(50); // 5000 miliseconds = 5 seconds
                    return false;
                }
            });


            holder.linear_chat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

//                if(intOldSelectedPosition!=-1)
//                {
//                    holder.linmain.getChildAt(intOldSelectedPosition).setBackgroundResource(0);
//                }

                    holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));

                    i = holder.getAdapterPosition();
                    MainActivity.showpart3();
                    Vibrator vv = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    assert vv != null;
                    vv.vibrate(50); // 5000 miliseconds = 5 seconds
                    intOldSelectedPosition = position;
                    Boolean isSender;
                    Point point = new Point();
                    int[] location = new int[2];
//                          point.x=140;
//                        point.y=140;

                    holder.linear_chat.getLocationOnScreen(location);

                    point.x = location[0];
                    point.y = location[1] - 180;
//                    Log.d(TAG, "jigar the location of profile pic x is " + point.x);
//                    Log.d(TAG, "jigar the location of profile pic y is " + point.y);
                    // get first string
                    String strTempSeenBy = " , " + chat.getIsseenby();
                    String strArray[] = strTempSeenBy.split(" , ");

                    if (chat.getSender().equals(fuser.getUid())) {
//                holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));
//                i = holder.getAdapterPosition();
                        //      MainActivity.showpart3();

                        String strChatDeliveredTime = chat.getTime();
                        String strChatSeenTime = chat.getIsseentime();

                        Log.d(TAG, "jigar the group message id converted to String array" + strChatSeenTime);

                        //print elements of String array


                        isSender = true;

                        showInfoSeenPopup(mContext, point, strChatDeliveredTime, strChatSeenTime, isSender, holder.linearLayoutRepliedMessage, position);
                        assert vv != null;
                        vv.vibrate(50); // 5000 miliseconds = 5 seconds
                        return false;
                    } else {
                        point.y = location[1] - 40;
                        isSender = false;
                        showInfoSeenPopup(mContext, point, "", "", isSender, holder.linearLayoutRepliedMessage, position);
                        assert vv != null;
                        vv.vibrate(50); // 5000 miliseconds = 5 seconds
                        return false;
                    }

                }
            });
            holder.linmain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    i = position;
                    holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));
                    Toast.makeText(mContext, position + "/", Toast.LENGTH_SHORT).show();
                    MainActivity.showpart3();
                    Vibrator vv = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    assert vv != null;
                    vv.vibrate(50); // 5000 miliseconds = 5 seconds
                    return false;
                }
            });

            holder.img_receive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));
                    i = holder.getAdapterPosition();
                    MainActivity.showpart3();
                    Vibrator vv = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    assert vv != null;
                    vv.vibrate(50); // 5000 miliseconds = 5 seconds
                    return true;
                }
            });
            holder.txtAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    contactIntent
                            .putExtra(ContactsContract.Intents.Insert.NAME, chat.getContact_name())
                            .putExtra(ContactsContract.Intents.Insert.PHONE, chat.getContact_number());
                    ((Activity) mContext).startActivity(contactIntent);
                }
            });
        }else
        {
            holder.linear_chat.setVisibility(View.GONE);
        }
    }
//    ViewTreeObserver.OnGlobalLayoutListener



    private void showInfoSeenPopup(final Context context, Point p,
                                   final String strChatDeliveredTimeMiliSec, final String strChatSeenTimeMiliSec
            , final Boolean isSender, final LinearLayout linearLayoutRepliedMessage,final  int position) {

        final PopupWindow changeStatusPopUp;
        // Inflate the popup_layout.xml
//            linearLayoutMainHome.setBackground(getResources().getDrawable(R.drawable.transparent_dark_rectangle));
//            relativeLayoutViewPager.setBackground(getResources().getDrawable(R.drawable.transparent_dark_rectangle));

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.pop_up_info_chat_message, null);
        // Creating the PopupWindow
        LinearLayout linearLayoutInfo = (LinearLayout) layout.findViewById(R.id.linearLayoutInfo);


        if(isSender) {
            linearLayoutInfo.setVisibility(View.VISIBLE);
        }else
        {
            linearLayoutInfo.setVisibility(View.GONE);
        }

        LinearLayout linearLayoutReply = (LinearLayout) layout.findViewById(R.id.linearLayoutReply);

        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        //   new DrawView(mContext);
        Log.d(TAG,"jigar the height of linear layout is "+changeStatusPopUp.getHeight());

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = 20;
//            int OFFSET_Y = -(changeStatusPopUp.getHeight()+10);
        int OFFSET_Y =  changeStatusPopUp.getHeight()+100;
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());
        //Clear the default translucent background
        //  changeStatusPopUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_dark_roundcorner));
//            if(changeStatusPopUp.getHeight()<p.y) {
//                // Displaying the popup at the specified location, + offsets.
//                changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, (p.y) + OFFSET_Y);
//            }else
        {
            changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y - OFFSET_Y);
        }
        changeStatusPopUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                    linearLayoutMainHome.setBackgroundResource(0);
//                    relativeLayoutViewPager.setBackgroundResource(0);
            }
        });

        linearLayoutReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            boolean strMessageType=MessageFragment.showReplyMessageDialog();
            if(strMessageType)
            {
                linearLayoutRepliedMessage.setVisibility(View.VISIBLE);
                final Chat chat = mChat.get(position);
                MessageFragment.textViewReplyMessage.setText(chat.getMessage());
                Log.d(TAG,"jigar the message in chat is "+chat.getSender());
                Log.d(TAG,"jigar the message ID for chat is "+chat.getId());
                Log.d(TAG,"jigar the message user log in chat is "+fuser.getDisplayName());

                if(chat.getSender().equals(fuser.getUid()))
                {
                    MessageFragment.textViewUserName.setText(strLoginUserName);
                    MessageFragment.strRepliedMessageID =chat.getId();

                }else
                {
                    MessageFragment.textViewUserName.setText(strChatReceiverUserName);
                    MessageFragment.strRepliedMessageID =chat.getId();

                }
             //   textViewRepliedMessage.setText(chat.getMessage());
            }else
            {
                linearLayoutRepliedMessage.setVisibility(View.GONE);
            }
                changeStatusPopUp.dismiss();
            }
        });
        linearLayoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = mContext.getLayoutInflater().inflate(R.layout.bottom_sheet_private_seen_message, null);
                dialogMenu = new BottomSheetDialog(context);
                dialogMenu.setContentView(view);
                dialogMenu.setCancelable(true);
                dialogMenu.show();
                TextView textViewSeenTime=view.findViewById(R.id.textViewSeenTime);
                TextView textViewDeliveredTime=view.findViewById(R.id.textViewDeliveredTime);
                ImageView imageViewSeenDot=view.findViewById(R.id.imageViewSeenDot);
                ImageView imageViewDeliveredDot=view.findViewById(R.id.imageViewDeliveredDot);

                if(strChatDeliveredTimeMiliSec.equals(""))
                {
                    imageViewDeliveredDot.setVisibility(View.VISIBLE);
                    textViewDeliveredTime.setVisibility(View.GONE);
                }else
                {
                    String strChatDeliveredTime= getDateCurrentTimeZone(Long.parseLong(strChatDeliveredTimeMiliSec));

                    imageViewDeliveredDot.setVisibility(View.GONE);
                    textViewDeliveredTime.setVisibility(View.VISIBLE);
                    textViewDeliveredTime.setText(strChatDeliveredTime);

                }

                if(strChatSeenTimeMiliSec.equals(""))
                {
                    imageViewSeenDot.setVisibility(View.VISIBLE);
                    textViewSeenTime.setVisibility(View.GONE);
                }else
                {
                    imageViewSeenDot.setVisibility(View.GONE);
                    textViewSeenTime.setVisibility(View.VISIBLE);
                    String strChatSeenTime=getDateCurrentTimeZone(Long.parseLong(strChatSeenTimeMiliSec));
                    textViewSeenTime.setText(strChatSeenTime);

                }
                changeStatusPopUp.dismiss();
                //                recyclerViewMessageSeenList = (RecyclerView) view.findViewById(R.id.recyclerViewMessageSeenList);
//                SeenMessageListAdapter seenMessageListAdapter=new SeenMessageListAdapter(seenMessageList,context);
//                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
//                recyclerViewMessageSeenList.setLayoutManager(mLayoutManager);
//                recyclerViewMessageSeenList.setItemAnimator(new DefaultItemAnimator());
//                recyclerViewMessageSeenList.setAdapter(seenMessageListAdapter);
//                groupMemberList(arrayUserID,seenMessageList,seenMessageListAdapter);

                // getMemberFromGroup();
//                seenMessageListAdapter.notifyDataSetChanged();

//                    linearLayoutMainHome.setBackgroundResource(0);
//                    relativeLayoutViewPager.setBackgroundResource(0);

            }
        });
    }


    private String extractFilename(String urlDownloadLink){
        if(urlDownloadLink.equals("")){
            return "";
        }
        String newFilename = "";
        if(urlDownloadLink.contains("/")){
            int dotPosition = urlDownloadLink.lastIndexOf("/");
            newFilename = urlDownloadLink.substring(dotPosition + 1, urlDownloadLink.length());
        }
        else{
            newFilename = urlDownloadLink;
        }
        return newFilename;
    }

    public void generateImageFromPdf(Uri pdfUri, Context context) throws FileNotFoundException {

        //        int pageNumber = 0;
//        PdfiumCore pdfiumCore = new PdfiumCore(context);
//        try {
//            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
//            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
//            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
//            pdfiumCore.openPage(pdfDocument, pageNumber);
//            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
//            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
//            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
//            saveImage(bmp);
//            pdfiumCore.closeDocument(pdfDocument); // important!
//        } catch (Exception e) {
//            Log.d("Exceptoin",e.toString());
//            //todo with exception
//        }


//        ImageView iv = (ImageView) findViewById(R.id.imageView);

        ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
        int pageNum = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);

            //if you need to render annotations and form fields, you can use
            //the same method above adding 'true' as last param

//            iv.setImageBitmap(bitmap);

            printInfo(pdfiumCore, pdfDocument);

            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printInfo(PdfiumCore core, PdfDocument doc) {
        PdfDocument.Meta meta = core.getDocumentMeta(doc);
        Log.e("TAG", "title = " + meta.getTitle());
        Log.e("TAG", "author = " + meta.getAuthor());
        Log.e("TAG", "subject = " + meta.getSubject());
        Log.e("TAG", "keywords = " + meta.getKeywords());
        Log.e("TAG", "creator = " + meta.getCreator());
        Log.e("TAG", "producer = " + meta.getProducer());
        Log.e("TAG", "creationDate = " + meta.getCreationDate());
        Log.e("TAG", "modDate = " + meta.getModDate());

        printBookmarksTree(core.getTableOfContents(doc), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("TAG", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, txtContactName, txtContactNumber, txtAddContact,textViewRepliedMessage,textViewUserName;
        public ImageView profile_image, img_receive, img_tick, img_dtick, img_dstick, img_download,img_loading_tick;
        public TextView show_time,audioTitle;
        public ProgressBar progress_circular;
        RelativeLayout relative, txt_seen, img_blur, relative_contact, relativeLayoutAudioPlayer,relativeLayoutVideoThumbnail;
        LinearLayout linear_chat,linearLayoutRepliedMessage;
        AudioSenseiPlayerView audioSenseiPlayerView;
        LinearLayout linmain;
        //public VideoView videoView;

        PDFView pdfView;
        TextView mRunTime, mTotalTime;
        SeekBar mMediaSeekBar;
        ImageView mPauseMedia, mPlayMedia,imageViewPlayAudio,imageViewThumbnail,imageViewPlayButton;
        AudioWife audioWife;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            audioWife = new AudioWife();
            profile_image = itemView.findViewById(R.id.profile_image);
            relativeLayoutAudioPlayer = itemView.findViewById(R.id.relativeLayoutAudioPlayer);
            textViewRepliedMessage=itemView.findViewById(R.id.textViewRepliedMessage);
            textViewUserName=itemView.findViewById(R.id.textViewUserName);
            linearLayoutRepliedMessage=itemView.findViewById(R.id.linearLayoutRepliedMessage);
            imageViewPlayAudio=itemView.findViewById(R.id.imageViewPlayAudio);
            img_receive = itemView.findViewById(R.id.img_receive);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_time = itemView.findViewById(R.id.show_time);
            progress_circular = itemView.findViewById(R.id.progress_circular);
            relative = itemView.findViewById(R.id.relative);
            audioSenseiPlayerView = itemView.findViewById(R.id.audio_player);
            audioTitle=itemView.findViewById(R.id.audio_name);
            img_tick = itemView.findViewById(R.id.img_tick);
            img_dtick = itemView.findViewById(R.id.img_dtick);
            img_dstick = itemView.findViewById(R.id.img_dstick);
            img_loading_tick = itemView.findViewById(R.id.img_loading_tick);
            linear_chat = itemView.findViewById(R.id.linear_chat);
            linmain = itemView.findViewById(R.id.linmain);
            img_download = itemView.findViewById(R.id.img_download);
            img_blur = itemView.findViewById(R.id.img_blur);
            pdfView = itemView.findViewById(R.id.pdfView);
            relative_contact = itemView.findViewById(R.id.relative_contact);
            txtAddContact = itemView.findViewById(R.id.txtAddContact);
            txtContactName = itemView.findViewById(R.id.txtContactName);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
            mPlayMedia = itemView.findViewById(R.id.play);
            mPauseMedia = itemView.findViewById(R.id.pause);
            mMediaSeekBar = (SeekBar) itemView.findViewById(R.id.media_seekbar);
            mRunTime = (TextView) itemView.findViewById(R.id.run_time);
            mTotalTime = (TextView) itemView.findViewById(R.id.total_time);
     //       videoView = (VideoView)itemView.findViewById(R.id.videoView);
            relativeLayoutVideoThumbnail=itemView.findViewById(R.id.relativeLayoutVideoThumbnail);
            imageViewPlayButton=itemView.findViewById(R.id.imageViewPlayButton);
            imageViewThumbnail=itemView.findViewById(R.id.imageViewThumbnail);
        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = calendar.getTimeZone();//get your local time zone.
            calendar.setTimeInMillis(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(tz);
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
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

    public static void copyMessage(Context context) {
        AppGlobal.copyData(context, mChat.get(i).getMessage());
    }

    public static void back(Context context) {

    }
    public static void forwardMessage1(final Context context) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_forward_message, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final RecyclerView recyclerView;
        final LinearLayout imgBack;

        final List<User> mUsers;

        recyclerView = dialogView.findViewById(R.id.recycler_viewq);
        imgBack = dialogView.findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showpart1();
                alertDialog.dismiss();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mUsers = new ArrayList<>();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        Log.d(TAG,"jigar the chat to be forwarded is "+user.getId());
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                    newChatUserAdapter = new ForwardMessageAdapter(context, mUsers, mChat.get(i).isIsimage()
                            , true, mChat.get(i).getMessage(), alertDialog, mChat.get(i).getImage()
                            ,mChat.get(i).isIsaudio(),mChat.get(i).getAudio_uri());

                    WsConstant.check = "activity";
                    recyclerView.setAdapter(newChatUserAdapter);
                } catch (Exception e) {
                    Log.d(TAG, "jigar the main exception in forward message is  "+e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        alertDialog.show();
    }


//    public static void forwardMessage(final Context context) {
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dailog_forward_message, null);
//        alertDialogBuilder.setView(dialogView);
//        alertDialogBuilder.setCancelable(true);
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        final RecyclerView recyclerView;
//        final LinearLayout imgBack;
//
//        final List<User> mUsers;
//
//        recyclerView = dialogView.findViewById(R.id.recycler_viewq);
//        imgBack = dialogView.findViewById(R.id.imgBack);
//
//        imgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.showpart1();
//                alertDialog.dismiss();
//            }
//        });
//
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        mUsers = new ArrayList<>();
//
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    mUsers.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        User user = snapshot.getValue(User.class);
//                        Log.d(TAG,"jigar the chat to be forwarded is "+user.getId());
//                        if (!user.getId().equals(firebaseUser.getUid())) {
//                            mUsers.add(user);
//                        }
//                    }
//                        newChatUserAdapter = new ForwardMessageAdapter(context, mUsers, mChat.get(i).isIsimage()
//                                , true, mChat.get(i).getMessage(), alertDialog, mChat.get(i).getImage()
//                                ,mChat.get(i).isIsaudio(),mChat.get(i).getAudio_uri());
//
//                    WsConstant.check = "activity";
//                    recyclerView.setAdapter(newChatUserAdapter);
//                } catch (Exception e) {
//                    Log.d(TAG, "jigar the main exception in forward message is  "+e);
//
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        alertDialog.show();
//    }
//    public static void deleteMessageList(final String strLoginUserId,final String strReceiverID) {


public static void deleteMessageList(final Context context) {

        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);

                        if (Objects.requireNonNull(chat).getId() != null) {

                            if (chat.getTo().equalsIgnoreCase("personal")) {

                                   if (chat.getId().equalsIgnoreCase(mChat.get(i).getId())) {
                                       snapshot.getRef().removeValue();
                                       Log.d(TAG,"jigar the chat to be deleted is "+chat.getId());
                                       Toast.makeText(context, "Message is deleted!", Toast.LENGTH_SHORT).show();
                                   }
//                                if ((chat.getSender().equals(strLoginUserId) || chat.getSender().equals(strReceiverID))
//                                        && (chat.getReceiver().equals(strLoginUserId)
//                                        || chat.getReceiver().equals(strReceiverID))) {
//                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    //                                    if (chat.getReceiver().equals(strLoginUserId)) {
//                                        if (chat.getIsstatus().equals("0")) {
//                                            hashMap.put("isstatus", "2");
//                                        } else if (chat.getIsstatus().equals("1")) {
//                                            hashMap.put("isstatus", "3");
//                                        }
//                                        Log.d(TAG, "jigar the login user is receiver and id is : " + strReceiverID + " and message is " + chat.getMessage());
//                                        snapshot.getRef().updateChildren(hashMap);
//                                        //                                    && chat.getSender().equals(userid)
////                                    && !chat.isIsseen()) {
////                                HashMap<String, Object> hashMap = new HashMap<>();
////                                Long tsLong = (System.currentTimeMillis() / 1000);
////                                String ts = tsLong.toString();
//
////                                hashMap.put("isseen", true);
////                                hashMap.put("issend", true);
////                                hashMap.put("isreceived", true);
////                                hashMap.put("isseentime", ts);
////                                snapshot.getRef().updateChildren(hashMap);
//                                    } else {
//                                        if (chat.getIsstatus().equals("0")) {
//                                            hashMap.put("isstatus", "1");
//                                        } else if (chat.getIsstatus().equals("2")) {
//                                            hashMap.put("isstatus", "3");
//                                        }
//                                        Log.d(TAG, "jigar the login user is sender and id is : " + strLoginUserId + " and message is " + chat.getMessage());
//                                        snapshot.getRef().updateChildren(hashMap);
                                //    }
                               // }
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
    }

public static void deletemessage(final Context context) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        Log.d(TAG,"jigar the chat to be deleted is "+chat.getId());
                        if (chat.getId().equalsIgnoreCase(mChat.get(i).getId())) {
                            snapshot.getRef().removeValue();
                            Toast.makeText(context, "Message is deleted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG,"jigar the exception in  delete is "+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void starMessage(Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("StarMessages").push();
        HashMap<String, Object> hashMap = new HashMap<>();

        if (mChat.get(i).getTo().equalsIgnoreCase("broadcast")) {

            DatabaseReference referenceq = FirebaseDatabase.getInstance().getReference("Groups");
            referenceq.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                BroadcastUser user = snapshot.getValue(BroadcastUser.class);
                                if (user.getBroadcastId().equalsIgnoreCase(mChat.get(i).getSender())) {

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
        } else if (AppGlobal.isNetwork(context)) {
            hashMap.put("id", fuser.getUid());
            hashMap.put("to", mChat.get(i).getTo());
            hashMap.put("sender", mChat.get(i).getSender());
            hashMap.put("receiver", mChat.get(i).getReceiver());
            hashMap.put("message", mChat.get(i).getMessage());
            hashMap.put("issend", mChat.get(i).isIssend());
            hashMap.put("isseen", mChat.get(i).isIsseen());
            hashMap.put("isimage", mChat.get(i).isIsimage());
            hashMap.put("iscontact", mChat.get(i).isIscontact());
            hashMap.put("contact_number", mChat.get(i).getContact_number());
            hashMap.put("contact_name", mChat.get(i).getContact_name());
            hashMap.put("image", mChat.get(i).getImage());
            hashMap.put("time", mChat.get(i).getTime());
            hashMap.put("doc_uri", mChat.get(i).getDoc_uri());
            hashMap.put("table_id", reference.getKey());
        }
        reference.setValue(hashMap);
        Toast.makeText(context, "Message has been stared..!", Toast.LENGTH_SHORT).show();
    }


}