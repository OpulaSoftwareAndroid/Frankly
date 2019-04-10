package com.opula.chatapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.BroadcastMessageFragment;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.UserProfileFragment;
import com.opula.chatapp.model.AESUtils;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.Chatlist;
import com.opula.chatapp.model.User;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.widget.Filter;
import android.widget.Filterable;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private List<User> mUsers;
    private List<Chatlist> mChatListDetails;
    private List<User> mUsersFilteredList;
    SpaceNavigationView spaceNavigationView;
    private List<BroadcastUser> mBroadcast;
    private boolean ischat, is;
    SharedPreference sharedPreference;
//    private int intNotificationCount=0;
    String theLastMessage, thetime;
    ArrayList<String> arrayListUserName;
    String AES = "AES";
    int intcount=0;

    static String TAG="UserAdapter";
    int intNotificationCount=0;
    ArrayList <String> arrayListUserLastMessageTime;

    public UserAdapter(Context mContext, List<User> mUsers,List<Chatlist> mChatListDetails, List<BroadcastUser> mBroadcast, boolean ischat, boolean is) {
        this.mUsers = mUsers;
        this.mUsersFilteredList = mUsers;
        this.mChatListDetails = mChatListDetails;
        this.mBroadcast = mBroadcast;
        this.mContext = mContext;
        this.ischat = ischat;
        this.is = is;
        arrayListUserLastMessageTime=new ArrayList<>();
    }

    public UserAdapter(Context mContext, List<User> mUsers, List<BroadcastUser> mBroadcast, boolean ischat, boolean is) {
        this.mUsers = mUsers;
        this.mUsersFilteredList = mUsers;
        this.mBroadcast = mBroadcast;
        this.mContext = mContext;
        this.ischat = ischat;
        this.is = is;
        arrayListUserLastMessageTime=new ArrayList<>();
    }
//    public UserAdapter(Context mContext,SpaceNavigationView spaceNavigationView, List<User> mUsers, List<BroadcastUser> mBroadcast, boolean ischat, boolean is) {
//        this.mUsers = mUsers;
//        this.mUsersFilteredList = mUsers;
//        this.mBroadcast = mBroadcast;
//        this.mContext = mContext;
//        this.ischat = ischat;
//        this.spaceNavigationView = spaceNavigationView;
//        this.is = is;
//        arrayListUserLastMessageTime=new ArrayList<>();
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        sharedPreference = new SharedPreference();

        if (is) {
            final User user = mUsersFilteredList.get(position);
            final Chatlist chatlist = mChatListDetails.get(position);

            String strUserName=user.getUsername();
            strUserName = strUserName.substring(0,1).toUpperCase() + strUserName.substring(1);
//            holder.username.setText(user.getUsername());
            holder.username.setText(strUserName);
            final Boolean aBooleanIsSecure= mChatListDetails.get(position).issecure;


            if (user.getImageURL().equals("default")) {
                holder.profile_image.setImageResource(R.drawable.image_boy);
            } else {
                Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            }

            if (ischat) {


                Log.d(TAG, "jigar the chat is not seen count before all is " + holder.textViewUnreadBadge.getText().toString());

                lastMessage(mUsersFilteredList,position,user.getId(), holder.last_msg, holder.time,holder.textViewUnreadBadge);
            } else {
                holder.last_msg.setVisibility(View.GONE);
            }

//            Log.d(TAG,"jigar the last time message is "+holder.time.getText().toString());

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
 //                    MainActivity.checkChatTheme(mContext);
                //    String strName=String.valueOf(aBooleanIsSecure);
                    MainActivity.showpart1();
                    holder.textViewUnreadBadge.setVisibility(View.GONE);

                    MessageFragment fragmentMessage=new MessageFragment();
                    Bundle args = new Bundle();
                    args.putString(WsConstant.IS_MESSAGE_SECURE, String.valueOf(chatlist.getIssecure()));
                    fragmentMessage.setArguments(args);
                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, fragmentMessage).addToBackStack(null).commit();

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

            if (ischat) {
                Log.d(TAG,"jigar the broadcast is called  "+user.getBroadcastId());

                lastMessageBroadcast(user.getBroadcastId(), holder.last_msg, holder.time);
            } else {
                holder.last_msg.setVisibility(View.GONE);
            }

            holder.click_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.hideFloatingActionButton();
                    sharedPreference.save(mContext, user.getBroadcastId(), WsConstant.broadcastId);
                    holder.textViewUnreadBadge.setVisibility(View.GONE);
//                    MainActivity.checkChatTheme(mContext);
                    MainActivity.showpart1();
                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new BroadcastMessageFragment()).addToBackStack(null).commit();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (is) {
//            return mUsers.size();
            return mUsersFilteredList.size();

        } else {
            return mBroadcast.size();
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mUsersFilteredList = mUsers;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : mUsers) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        Log.d(TAG,"jigar the query for search is "+charString.toString());

                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                            Log.d(TAG,"jigar the query match for search is "+row.getUsername());

                        }
                    }

                    mUsersFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mUsersFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mUsersFilteredList = (ArrayList<User>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg, time,textViewUnreadBadge;
        LinearLayout click_layout;
        SpaceNavigationView spaceNavigationView;
        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            time = itemView.findViewById(R.id.time);
            click_layout = itemView.findViewById(R.id.click_layout);
            textViewUnreadBadge=itemView.findViewById(R.id.textViewUnreadBadge);

        }
    }

    //check for last message
    private void lastMessage(final List<User> mUsersFilteredList, final int position,final String userid
            , final TextView last_msg, final TextView time, final TextView textViewUnreadBadge) {
        theLastMessage = "default";
        thetime = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
         String strCurrentUserId = "";
        final ArrayList <String> arrayListUnreadChatID=new ArrayList();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //intNotificationCount=0;
                    Chat chat = snapshot.getValue(Chat.class);
                    intNotificationCount=0;
                    assert firebaseUser != null;
                    firebaseUser.getUid();
                    if (chat != null) {
                        if (chat.getTo().equalsIgnoreCase("personal")) {

                            if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {

                                if(chat.getIsstatus().equals("0") ||
                                        (chat.getSender().equals(firebaseUser.getUid()) && chat.getIsstatus().equals("2"))
                                        || (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getIsstatus().equals("1")))
                                {
                                if (chat.getIssecure()) {

                                    String encrypted = chat.getMessage();
                                    String decrypted = "";
                                    try {
                                        decrypted = AESUtils.decrypt(encrypted);
                                        Log.d("TEST", "decrypted:" + decrypted);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    theLastMessage = decrypted;

                                } else {
                                    theLastMessage = chat.getMessage();
                                }
                                String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
                                time.setText(tiime);
                                arrayListUserLastMessageTime.add(tiime);
                                Log.d(TAG, "jigar the chat time is " + chat.getTime() + " and " + userid);

                                if (!chat.isIsseen()) {
                                    if (!arrayListUnreadChatID.contains(chat.getId())) {
                                        arrayListUnreadChatID.add(chat.getId());
                                    }
                                    intcount = (intcount + 1) - intcount;
                                    Log.d(TAG, "jigar the the unread arraylist  chat time is with " + arrayListUnreadChatID.size());

                                    if (!chat.getSender().equals(userid)) {
                                        textViewUnreadBadge.setVisibility(View.GONE);
                                    } else {
                                        textViewUnreadBadge.setText(" " + arrayListUnreadChatID.size() + " ");
                                        textViewUnreadBadge.setVisibility(View.VISIBLE);
                                    }
                                    User user = mUsersFilteredList.get(position);
                                    mUsersFilteredList.remove(position);
                                    mUsersFilteredList.add(0, user);
                                    notifyItemMoved(position, 0);
                                } else {
                                    textViewUnreadBadge.setVisibility(View.GONE);
                                }
                            }else
                                {
                                   last_msg.setText("Message has been deleted");
                                }
                            }
                        } else if (chat.getTo().equalsIgnoreCase("broadcast"))
                        {
                            for (int i = 0; i < chat.getBroadcast_receiver().size(); i++)
                            {
                                if (chat.getBroadcast_receiver().get(i).equalsIgnoreCase(userid))
                                {
                                    theLastMessage = chat.getMessage();
                                    String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
                                    time.setText(tiime);
                                    arrayListUserLastMessageTime.add(tiime);
                                }
                            }
                          //  Log.d(TAG,"jigar the user broadcast last message list have is "+arrayListUserLastMessageTime.toString());
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

    private void lastMessageBroadcast(final String userid, final TextView last_msg, final TextView time) {
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
                        if (chat.getTo().equalsIgnoreCase("broadcast")) {
                            if (chat.getSender().equalsIgnoreCase(userid)) {
                                theLastMessage = chat.getMessage();
                                Log.d(TAG,"jigar is the image sent in chat ? "+chat.isIsimage());
                                Log.d(TAG,"jigar is the  last message is  "+chat.getMessage());

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
