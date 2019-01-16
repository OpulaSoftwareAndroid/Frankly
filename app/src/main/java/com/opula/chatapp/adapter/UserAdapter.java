package com.opula.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.BroadcastMessageFragment;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.UserProfileFragment;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.User;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private List<BroadcastUser> mBroadcast;
    private boolean ischat,is;
    SharedPreference sharedPreference;
    String theLastMessage, thetime;
    String AES = "AES";

    public UserAdapter(Context mContext, List<User> mUsers , List<BroadcastUser> mBroadcast, boolean ischat, boolean is) {
        this.mUsers = mUsers;
        this.mBroadcast = mBroadcast;
        this.mContext = mContext;
        this.ischat = ischat;
        this.is = is;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        sharedPreference = new SharedPreference();

        if (is){
            final User user = mUsers.get(position);
            holder.username.setText(user.getUsername());
            if (user.getImageURL().equals("default")) {
                holder.profile_image.setImageResource(R.drawable.image_boy);
            } else {
                Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            }

            if (ischat) {
                lastMessage(user.getId(), holder.last_msg, holder.time);
            } else {
                holder.last_msg.setVisibility(View.GONE);
            }

            if (ischat) {
                if (user.getStatus().equals("online")) {
                    holder.img_on.setVisibility(View.VISIBLE);
                    holder.img_off.setVisibility(View.GONE);
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.VISIBLE);
                }
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);
            }

            holder.click_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.hideFloatingActionButton();
                    sharedPreference.save(mContext, user.getId(), WsConstant.userId);
                    MainActivity.checkChatTheme(mContext);
                    MainActivity.showpart1();
                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MessageFragment()).addToBackStack(null).commit();

                }
            });

            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedPreference.save(mContext, user.getId(), WsConstant.userId);

                    MainActivity.showpart2();

                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new UserProfileFragment()).addToBackStack(null).commit();

                }
            });
        } else {
            final BroadcastUser user = mBroadcast.get(position);
            holder.username.setText(user.getBroadcastName());

            holder.profile_image.setImageResource(R.drawable.broadcast);

            holder.click_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.hideFloatingActionButton();
                    sharedPreference.save(mContext, user.getBroadcastId(), WsConstant.broadcastId);
                    MainActivity.checkChatTheme(mContext);
                    MainActivity.showpart1();
                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new BroadcastMessageFragment()).addToBackStack(null).commit();

                }
            });
        }




    }

    @Override
    public int getItemCount() {
        if (is){
            return mUsers.size();
        } else {
            return mBroadcast.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg, time;
        LinearLayout click_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            time = itemView.findViewById(R.id.time);
            click_layout = itemView.findViewById(R.id.click_layout);

        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg, final TextView time) {
        theLastMessage = "default";
        thetime = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert firebaseUser != null;
                    firebaseUser.getUid();
                    if (chat != null) {
                        if (chat.getTo().equalsIgnoreCase("personal")){
                            if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                                theLastMessage = chat.getMessage();

                                String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
                                time.setText(tiime);

                            }
                        }

                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Messages");
                        break;

                    default:
                        /*String decMessage = null;
                        try {
                            decMessage = decrypt(theLastMessage,"Jenil");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String decrypt(String outputString, String Password) throws Exception{
        SecretKeySpec key = genrateKey(Password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE,key);
        byte[] encyptedValue = Base64.decode(outputString,Base64.DEFAULT);
        byte[] decValue = cipher.doFinal(encyptedValue);
        String decyptedValue = new String(decValue);
        return decyptedValue;
    }

    private SecretKeySpec genrateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return  secretKeySpec;
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

}
