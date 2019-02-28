package com.opula.chatapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.model.Chat;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    public GroupMessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_group_chat_item_right, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_group_chat_item_left, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageAdapter.ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);
        Log.d("Chat_Data", chat.getContact_number() + "/" + chat.getContact_name());

        if (chat.getSender_image().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            Glide.with(mContext).load(chat.getSender_image()).into(holder.profile_image);
        }

        holder.show_sender.setText(chat.getSender_username());
        if (!chat.getDoc_uri().equalsIgnoreCase("default")) {
            holder.show_message.setVisibility(View.GONE);
            holder.relative_contact.setVisibility(View.GONE);
            holder.pdfView.setVisibility(View.VISIBLE);

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
            //Glide.with(mContext).load(chat.getImage()).into(holder.img_receive);
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
            holder.show_message.setText(chat.getMessage());
        }

        if (chat.isIsseen()) {
            holder.img_tick.setVisibility(View.GONE);
            holder.img_dtick.setVisibility(View.VISIBLE);
        } else {
            holder.img_tick.setVisibility(View.VISIBLE);
            holder.img_dtick.setVisibility(View.GONE);
        }

        String str = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
        holder.show_time.setText(str);

        holder.img_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dailog_show_image, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setCancelable(true);

                final ImageView image = (ImageView) dialogView.findViewById(R.id.image);

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

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EmojiconTextView show_sender, show_message;
        public TextView show_time, txtContactName, txtContactNumber, txtAddContact;
        ;
        public ImageView profile_image, img_receive, img_tick, img_dtick;
        public ProgressBar progress_circular;
        public RelativeLayout relative, txt_seen, relative_contact;
        public PDFView pdfView;

        public ViewHolder(View itemView) {
            super(itemView);

            show_sender = itemView.findViewById(R.id.show_sender);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_receive = itemView.findViewById(R.id.img_receive);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_time = itemView.findViewById(R.id.show_time);
            progress_circular = itemView.findViewById(R.id.progress_circular);
            relative = itemView.findViewById(R.id.relative);
            img_tick = itemView.findViewById(R.id.img_tick);
            img_dtick = itemView.findViewById(R.id.img_dtick);
            relative_contact = itemView.findViewById(R.id.relative_contact);
            txtAddContact = itemView.findViewById(R.id.txtAddContact);
            txtContactName = itemView.findViewById(R.id.txtContactName);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
            pdfView = itemView.findViewById(R.id.pdfView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fuser != null;
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


}