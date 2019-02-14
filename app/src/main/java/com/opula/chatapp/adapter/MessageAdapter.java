package com.opula.chatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
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
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.User;

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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public Context mContext;
    public static List<Chat> mChat;
    private String imageurl;
    String AES = "AES";
    public static FirebaseUser fuser;
    public static int i = 0;
    public static ForwardMessageAdapter newChatUserAdapter;


    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
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
        Log.d("Chat_Data", chat.getImage() + "/");

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (!chat.getImage().equalsIgnoreCase("default")) {
            holder.img_receive.setVisibility(View.VISIBLE);
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
        if (chat.getImage().equalsIgnoreCase("default")) {
            holder.img_receive.setVisibility(View.GONE);
            holder.relative.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);

           /* String decMessage = null;
            try {
                decMessage = decrypt(chat.getMessage(),"Jenil");
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            holder.show_message.setText(chat.getMessage());
        }


        if (chat.isIsseen()) {
            //holder.txt_seen.setText("seen");
            holder.img_tick.setVisibility(View.GONE);
            holder.img_dtick.setVisibility(View.GONE);
            holder.img_dstick.setVisibility(View.VISIBLE);
        } else {
            if (chat.isIssend()) {
                //holder.txt_seen.setText("delivered");
                holder.img_tick.setVisibility(View.GONE);
                holder.img_dtick.setVisibility(View.VISIBLE);
                holder.img_dstick.setVisibility(View.GONE);
            } else {
                //holder.txt_seen.setText("send");
                holder.img_tick.setVisibility(View.VISIBLE);
                holder.img_dtick.setVisibility(View.GONE);
                holder.img_dstick.setVisibility(View.GONE);
            }
        }

        String str = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
        holder.show_time.setText(str);

//        holder.img_download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.progress_circular.setVisibility(View.VISIBLE);
//                FirebaseStorage storageRef = FirebaseStorage.getInstance();
//                StorageReference dateRef = storageRef.getReferenceFromUrl(chat.getImage());
//
//                URL url = null;
//                InputStream input = null;
//                try {
//                    url = new URL(chat.getImage());
//                    input = url.openStream();
//                    File storagePath = Environment.getExternalStorageDirectory();
//                    File imageDirectory = new File(storagePath+"/Shreem Connect/images");
//                    if(!imageDirectory.exists()) {
//                        imageDirectory.mkdirs();
//                    }
//                    File file = new File(imageDirectory,  "/"+System.currentTimeMillis()+".png");
//                    Log.i("filepath:", " " + file);
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").child(chat.getTable_id());
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("storage_uri", "" + file);
//                    reference.updateChildren(map);
//                    OutputStream output = new FileOutputStream(file);
//                    try {
//                        byte[] buffer = new byte[2024];
//                        int bytesRead = 0;
//                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
//                            output.write(buffer, 0, bytesRead);
//                        }
//                    } finally {
//                        output.close();
//                        input.close();
//                        Toast.makeText(mContext, "successfully Saved", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri downloadUrl) {
//                        Toast.makeText(mContext, "successfully Download", Toast.LENGTH_SHORT).show();
//                        holder.progress_circular.setVisibility(View.INVISIBLE);
//                    }
//                });
//            }
//        });


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
            public boolean onLongClick(View v) {
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
                holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));
                i = holder.getAdapterPosition();
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

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image, img_receive, img_tick, img_dtick, img_dstick, img_download;
        public TextView show_time;
        public ProgressBar progress_circular;
        public RelativeLayout relative, txt_seen, img_blur;
        public LinearLayout linear_chat;
        public LinearLayout linmain;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_receive = itemView.findViewById(R.id.img_receive);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_time = itemView.findViewById(R.id.show_time);
            progress_circular = itemView.findViewById(R.id.progress_circular);
            relative = itemView.findViewById(R.id.relative);
            img_tick = itemView.findViewById(R.id.img_tick);
            img_dtick = itemView.findViewById(R.id.img_dtick);
            img_dstick = itemView.findViewById(R.id.img_dstick);
            linear_chat = itemView.findViewById(R.id.linear_chat);
            linmain = itemView.findViewById(R.id.linmain);
            img_download = itemView.findViewById(R.id.img_download);
            img_blur = itemView.findViewById(R.id.img_blur);
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

                    newChatUserAdapter = new ForwardMessageAdapter(context, mUsers, mChat.get(i).isIsimage(),false, mChat.get(i).getMessage(), alertDialog, mChat.get(i).getImage());
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

        if (AppGlobal.isNetwork(context)) {
            hashMap.put("id", fuser.getUid());
            hashMap.put("sender", mChat.get(i).getSender());
            hashMap.put("receiver", mChat.get(i).getReceiver());
            hashMap.put("message", mChat.get(i).getMessage());
            hashMap.put("isimage", mChat.get(i).isIsimage());
            hashMap.put("image", mChat.get(i).getImage());
            hashMap.put("table_id", reference.getKey());
        }

        reference.setValue(hashMap);
        Toast.makeText(context, "Message has been stared..!", Toast.LENGTH_SHORT).show();
    }

}