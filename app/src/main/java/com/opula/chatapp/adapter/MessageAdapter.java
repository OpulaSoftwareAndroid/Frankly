package com.opula.chatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.opula.chatapp.model.AESUtils;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.User;
import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;
import com.rygelouv.audiosensei.player.OnPlayerViewClickListener;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import nl.changer.audiowife.AudioWife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public Context mContext;
    String TAG = "MessageAdapter";
    public static List<Chat> mChat;
    private String imageurl;
    String AES = "AES";
    private String downloadAudioPath;
    String strIsSecureChat;
    SharedPreference sharedPreference;
    public static FirebaseUser fuser;
    public static int i = 0;
    public static ForwardMessageAdapter newChatUserAdapter;
    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
    public    String strUriForAudio, strUrlPath;
    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl,String strIsSecureChat)
    {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this. strIsSecureChat=strIsSecureChat;
        sharedPreference=new SharedPreference();
    }

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
        Log.d("Chat_Data", chat.getContact_number() + "/" + chat.getContact_number());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (!chat.getDoc_uri().equalsIgnoreCase("default")) {
            holder.show_message.setVisibility(View.GONE);
            holder.relative_contact.setVisibility(View.GONE);
            holder.pdfView.setVisibility(View.VISIBLE);
            try {
//                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                generateImageFromPdf(Uri.parse("/document/primary:text.pdf"), mContext);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!chat.getImage().equalsIgnoreCase("default")) {
            holder.img_receive.setVisibility(View.VISIBLE);
            holder.relative_contact.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);
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



            strUrlPath =chat.getAudio_uri();
            holder.audioSenseiPlayerView
                    .setAudioTarget(strUrlPath);



            holder.audioSenseiPlayerView.commitClickEvents();
            View playerRootView = holder.audioSenseiPlayerView.getPlayerRootView();

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
        if (chat.getImage().equalsIgnoreCase("default") && chat.getDoc_uri().equalsIgnoreCase("default") && chat.isIscontact() == false) {
            holder.img_receive.setVisibility(View.GONE);
            holder.relative.setVisibility(View.GONE);
            holder.relative_contact.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);


//            if(strIsSecureChat.equals("true"))
//            {
//                if (sharedPreference.getValue(mContext, WsConstant.IS_STORED_MESSAGE_SECURE).equals("true")) {
//                    String encrypted = chat.getMessage();
//
//                    String decrypted = "";
//                    try {
//                        decrypted = AESUtils.decrypt(encrypted);
//                        Log.d("TEST", "decrypted:" + decrypted);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    //  holder.show_message.setText(chat.getMessage());
//                    holder.show_message.setText(decrypted);
//                    if (holder.show_message.getText().equals("")) {
//                        holder.show_message.setText(chat.getMessage());
//                    }
//                }else
//                {
//                    holder.show_message.setText(chat.getMessage());
//                }
//            }else
//            {
//                holder.show_message.setText(chat.getMessage());
//            }
            if(chat.getIssecure())
            {
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

            }else
            {
                holder.show_message.setText(chat.getMessage());

            }
        }


        if (chat.isIsseen()) {
            holder.img_tick.setVisibility(View.GONE);
            holder.img_dtick.setVisibility(View.GONE);
            holder.img_dstick.setVisibility(View.VISIBLE);
        } else {
            if (chat.isIssend()) {
                holder.img_tick.setVisibility(View.GONE);
                holder.img_dtick.setVisibility(View.VISIBLE);
                holder.img_dstick.setVisibility(View.GONE);
            } else {
                holder.img_tick.setVisibility(View.VISIBLE);
                holder.img_dtick.setVisibility(View.GONE);
                holder.img_dstick.setVisibility(View.GONE);
            }
        }

        String str = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
        holder.show_time.setText(str);


        holder.imageViewPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        public TextView show_message, txtContactName, txtContactNumber, txtAddContact;
        public ImageView profile_image, img_receive, img_tick, img_dtick, img_dstick, img_download;
        public TextView show_time;
        public ProgressBar progress_circular;
        RelativeLayout relative, txt_seen, img_blur, relative_contact, relativeLayoutAudioPlayer;
        LinearLayout linear_chat;
        AudioSenseiPlayerView audioSenseiPlayerView;
        LinearLayout linmain;
        PDFView pdfView;
        TextView mRunTime, mTotalTime;
        SeekBar mMediaSeekBar;
        ImageView mPauseMedia, mPlayMedia,imageViewPlayAudio;
        AudioWife audioWife;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            audioWife = new AudioWife();
            profile_image = itemView.findViewById(R.id.profile_image);
            relativeLayoutAudioPlayer = itemView.findViewById(R.id.relativeLayoutAudioPlayer);

            imageViewPlayAudio=itemView.findViewById(R.id.imageViewPlayAudio);
            img_receive = itemView.findViewById(R.id.img_receive);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_time = itemView.findViewById(R.id.show_time);
            progress_circular = itemView.findViewById(R.id.progress_circular);
            relative = itemView.findViewById(R.id.relative);
            audioSenseiPlayerView = itemView.findViewById(R.id.audio_player);
            img_tick = itemView.findViewById(R.id.img_tick);
            img_dtick = itemView.findViewById(R.id.img_dtick);
            img_dstick = itemView.findViewById(R.id.img_dstick);
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

    public static void forwardMessage(final Context context) {
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

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                    newChatUserAdapter = new ForwardMessageAdapter(context, mUsers, mChat.get(i).isIsimage(), false, mChat.get(i).getMessage(), alertDialog, mChat.get(i).getImage());
                    WsConstant.check = "activity";
                    recyclerView.setAdapter(newChatUserAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        alertDialog.show();
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
                        if (chat.getId().equalsIgnoreCase(mChat.get(i).getId())) {
                            snapshot.getRef().removeValue();
                            Toast.makeText(context, "Message is deleted!", Toast.LENGTH_SHORT).show();
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