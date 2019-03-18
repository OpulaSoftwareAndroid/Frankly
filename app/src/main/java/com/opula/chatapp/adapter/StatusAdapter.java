package com.opula.chatapp.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devlomi.circularstatusview.CircularStatusView;
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
import com.opula.chatapp.activity.MainShowStatusActivity;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.ShowStatusFragment;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.POJOStatus;
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

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private  ArrayList<String> arrayListStatusSenderID;
    private  ArrayList<String> arrayListStatusImageList;
    private  ArrayList<Integer> arrayListStatusToBeRemoved;

    private RecyclerView recyclerViewStatus;
    private List<POJOStatus> pojoStatusList;
    private List<POJOStatus> mStatusFilteredList;
  //  SpaceNavigationView spaceNavigationView;
    // private boolean ischat, is;
    SharedPreference sharedPreference;
    private int intNotificationCount=0;
    String theLastMessage, thetime;
    ArrayList<String> arrayListUserName;
    String AES = "AES";
    static String TAG="UserAdapter";

  //  ArrayList <String> arrayListUserLastMessageTime;
//    public StatusAdapter(Context mContext, List<POJOStatus> mUsers, List<BroadcastUser> mBroadcast, boolean ischat, boolean is) {
//        this.pojoStatusList = mUsers;
//        this.mStatusFilteredList = mUsers;
//     //   this.mBroadcast = mBroadcast;
//        this.mContext = mContext;
////        this.ischat = ischat;
////        this.is = is;
//    //    arrayListUserLastMessageTime=new ArrayList<>();
//    }

    public StatusAdapter(Context mContext, List<POJOStatus> mPojoStatusList,RecyclerView recyclerViewStatus) {
        this.pojoStatusList = mPojoStatusList;
        this.mStatusFilteredList = mPojoStatusList;
        this.mContext = mContext;
        this.recyclerViewStatus=recyclerViewStatus;
        arrayListStatusSenderID=new ArrayList<>();
        arrayListStatusImageList=new ArrayList<>();
        arrayListStatusToBeRemoved=new ArrayList<>();

//       Log.d(TAG,"jigar the adapter filter constructor have "+mStatusFilteredList.get(0).getSenderDisplayName());
    }
//    public UserAdapter(Context mContext,SpaceNavigationView spaceNavigationView, List<User> pojoStatusList, List<BroadcastUser> mBroadcast, boolean ischat, boolean is) {
//        this.pojoStatusList = pojoStatusList;
//        this.mStatusFilteredList = pojoStatusList;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_status_item, parent, false);
        return new StatusAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, final int position) {


    sharedPreference = new SharedPreference();

        //if (is)
        {
            final POJOStatus user = mStatusFilteredList.get(position);
            String strUserName=user.getSenderDisplayName();
            strUserName = strUserName.substring(0,1).toUpperCase() + strUserName.substring(1);
//            holder.username.setText(user.getUsername());
            holder.username.setText(strUserName);
            Log.d(TAG,"jigar the status adapter list image is "+user.getImageUrl());
            if (user.getImageUrl().equals("default")) {
                holder.profile_image.setImageResource(R.drawable.image_boy);
            } else {
                Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
            }
            long longCurrentTime=System.currentTimeMillis();
            long longStatusTime=Long.valueOf(user.getTime())*1000;
            long longDifferenceTime=longCurrentTime-longStatusTime;
            long seconds = longDifferenceTime / 1000;
            long longStatusAgoSeconds = seconds % 60;
            long longStatusAgoMinute = (seconds / 60) % 60;
            long longStatusAgoHour = (seconds / (60 * 60)) % 24;

            Log.d(TAG,"jigar the current time is " +longCurrentTime );
            Log.d(TAG,"jigar the statuss time is " +longStatusTime);
            Log.d(TAG,"jigar the differr time is " +longStatusAgoHour);

            if(longStatusAgoHour>0)
            {
                holder. textViewStatusTime.setText(longStatusAgoHour+"h Ago");
            }else if(longStatusAgoMinute>0)
            {
                holder. textViewStatusTime.setText(longStatusAgoMinute+"m Ago");
            }
            else if(longStatusAgoSeconds>0)
            {
                holder. textViewStatusTime.setText(longStatusAgoSeconds+" Sec Ago");
            }

            Log.d(TAG,"jigar the status upload ALL MAIN list have is " +user.getSenderID()+" and position is "+position);

            if(!arrayListStatusSenderID.contains(user.getSenderID()))
            {
                arrayListStatusSenderID.add(user.getSenderID());
                arrayListStatusImageList.add(user.getImageUrl());
                holder.circularStatusView.setPortionsCount(arrayListStatusImageList.size());
            } else
            {
                //mStatusFilteredList.remove(position);
                arrayListStatusToBeRemoved.add(arrayListStatusSenderID.indexOf(user.getSenderID()));
               final int intIndexPosition= arrayListStatusSenderID.indexOf(user.getSenderID());
               String strImageUrlList=arrayListStatusImageList.get(intIndexPosition);
               strImageUrlList=strImageUrlList+","+user.getImageUrl();
               arrayListStatusImageList.add(intIndexPosition,strImageUrlList);
               holder.circularStatusView.setPortionsCount(arrayListStatusImageList.size());

               //              int intIndexToDelete= mStatusFilteredList.indexOf(arrayListStatusSenderID.get(intIndexPosition));
//               mStatusFilteredList.remove(intIndexToDelete);

               Log.d(TAG,"jigar the status upload from main match list is " +user.getSenderID()+" and position is "+arrayListStatusSenderID.indexOf(user.getSenderID()));
               Log.d(TAG,"jigar the status upload before index from main match list is " +mStatusFilteredList.indexOf(mStatusFilteredList.get(position).getSenderID())+" and current position is "+position);

                Log.d(TAG,"jigar the status upload match found at " +intIndexPosition
                       +" and id  is "+arrayListStatusSenderID.get(intIndexPosition));
           //    mStatusFilteredList.remove(position);

                recyclerViewStatus.post(new Runnable()
                {
                    @Override
                    public void run() {
                        Log.d(TAG,"jigar the status upload removing list have is " +arrayListStatusToBeRemoved.toString());

                        for(int i=0;i<arrayListStatusToBeRemoved.size();i++)
                        {
                            mStatusFilteredList.remove(arrayListStatusToBeRemoved.get(i));
                            //                        for(int i=0;i<arrayListStatusSenderID.size();i++)
//                        {
//                            mStatusFilteredList.remove(arrayListStatusImageList.get(i));
//                            //                            mStatusFilteredList.remove(1);
////                            mStatusFilteredList.remove(2);
                          //  notifyDataSetChanged();
                        }
                    }
                });
            }
//            Log.d(TAG,"jigar the status adapter list image url we have "+ arrayListStatusImageList.size()
//                    +" and "+arrayListStatusImageList.toString());

//                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(longDifferenceTime),
//                                        TimeUnit.MILLISECONDS.toMinutes(longDifferenceTime) % TimeUnit.HOURS.toMinutes(1),
//                                        TimeUnit.MILLISECONDS.toSeconds(longDifferenceTime) % TimeUnit.MINUTES.toSeconds(1));
            //holder.textViewStatusTime.setText(user.getTime());


//            if (ischat) {
//           //     lastMessage(mStatusFilteredList,position,user.getId(), holder.textViewStatus,holder.textViewUnreadBadge);
//            } else {
//                holder.textViewStatus.setVisibility(View.GONE);
//            }
            //            Log.d(TAG,"jigar the last time message is "+holder.time.getText().toString());

//            if (ischat) {
//                if (user.getStatus().equals("online"))
//                {
//                    holder.img_on.setVisibility(View.VISIBLE);
//                    holder.img_off.setVisibility(View.GONE);
//                } else {
//                    holder.img_on.setVisibility(View.GONE);
//                    holder.img_off.setVisibility(View.VISIBLE);
//                }
//            } else {
//                holder.img_on.setVisibility(View.GONE);
//                holder.img_off.setVisibility(View.GONE);
//            }
            holder.click_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.hideFloatingActionButton();
                    sharedPreference.save(mContext, user.getId(), WsConstant.userId);
//                    MainActivity.checkChatTheme(mContext);
                    MainActivity.showpart1();
                    //                    ArrayList<String> arrayListStatusImageUrl=new ArrayList<>();
//                    arrayListStatusImageUrl.add(user.getImageUrl());
                    Intent myIntent = new Intent(mContext, MainShowStatusActivity.class);
                    myIntent.putExtra(WsConstant.STATUS_IMAGE_URL,arrayListStatusImageList.get(position));
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(mContext, R.anim.fade_in, R.anim.fade_out);
                    mContext.startActivity(myIntent, options.toBundle());
                    //  mContext.startActivity(myIntent);
//                    holder.textViewUnreadBadge.setVisibility(View.GONE);

//                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ShowStatusFragment()).addToBackStack(null).commit();
                }
            });
            //
//            holder.profile_image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    sharedPreference.save(mContext, user.getId(), WsConstant.userId);
//
//                    MainActivity.showpart2();
//
//                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new UserProfileFragment()).addToBackStack(null).commit();
//
//                }
//            });
        }
        //        else {
//
//            final BroadcastUser user = mBroadcast.get(position);
//            holder.username.setText(user.getBroadcastName());
//
//            holder.profile_image.setImageResource(R.drawable.broadcast);
//
//            if (ischat) {
//
////                lastMessageBroadcast(user.getBroadcastId(), holder.textViewStatus, holder.time);
//            } else {
//                holder.textViewStatus.setVisibility(View.GONE);
//            }
//            holder.click_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    MainActivity.hideFloatingActionButton();
//                    sharedPreference.save(mContext, user.getBroadcastId(), WsConstant.broadcastId);
//                    holder.textViewUnreadBadge.setVisibility(View.GONE);
////                    MainActivity.checkChatTheme(mContext);
//                    MainActivity.showpart1();
//                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new BroadcastMessageFragment()).addToBackStack(null).commit();
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
//        if (is) {
//            return pojoStatusList.size();
        return mStatusFilteredList.size();
    }
//        else {
//            return mBroadcast.size();
//        }

    public void removeAt(int position) {
        mStatusFilteredList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mStatusFilteredList.size());
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mStatusFilteredList = pojoStatusList;
                } else {
                    List<POJOStatus> filteredList = new ArrayList<>();
                    for (POJOStatus row : pojoStatusList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        Log.d(TAG,"jigar the query for search is "+charString.toString());

//                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
//                            filteredList.add(row);
//                            Log.d(TAG,"jigar the query match for search is "+row.getUsername());
//
//                        }
                    }

                    mStatusFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mStatusFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mStatusFilteredList = (ArrayList<POJOStatus>) filterResults.values;

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
        private TextView textViewStatusTime,textViewUnreadBadge;
        LinearLayout click_layout;
        CircularStatusView circularStatusView;
        SpaceNavigationView spaceNavigationView;
        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            textViewStatusTime = itemView.findViewById(R.id.textViewStatusTime);
            click_layout = itemView.findViewById(R.id.click_layout);
            textViewUnreadBadge=itemView.findViewById(R.id.textViewUnreadBadge);
            circularStatusView=itemView.findViewById(R.id.circular_status_view);

        }
    }

    //check for last message
    private void lastMessage(final List<User> mUsersFilteredList, final int position,final String userid
            , final TextView textViewStatusTime, final TextView textViewUnreadBadge) {
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
                        if (chat.getTo().equalsIgnoreCase("personal")) {
                            if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                                theLastMessage = chat.getMessage();
                                String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));

                                long longCurrentTime=System.currentTimeMillis();
                                long longStatusTime=Long.valueOf(chat.getTime())*1000;

                                long longDifferenceTime=longCurrentTime-longStatusTime;
                                long seconds = longDifferenceTime / 1000;
                                long longStatusAgoSeconds = seconds % 60;
                                long longStatusAgoMinute = (seconds / 60) % 60;
                                long longStatusAgoHour = (seconds / (60 * 60)) % 24;
//                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(longDifferenceTime),
//                                        TimeUnit.MILLISECONDS.toMinutes(longDifferenceTime) % TimeUnit.HOURS.toMinutes(1),
//                                        TimeUnit.MILLISECONDS.toSeconds(longDifferenceTime) % TimeUnit.MINUTES.toSeconds(1));
                                Log.d(TAG,"jigar the current time is " +longCurrentTime );
                                Log.d(TAG,"jigar the statuss time is " +longStatusTime);
                                Log.d(TAG,"jigar the differr time is " +longStatusAgoHour);

                                if(longStatusAgoHour>0)
                                {
                                    textViewStatusTime.setText(longStatusAgoHour+"h Ago");
                                }else if(longStatusAgoMinute>0)
                                {
                                    textViewStatusTime.setText(longStatusAgoMinute+"m Ago");
                                }
                                else if(longStatusAgoSeconds>0)
                                {
                                    textViewStatusTime.setText(longStatusAgoSeconds+" Sec Ago");
                                }

//                                textViewStatusTime.setText(tiime);
                       //         arrayListUserLastMessageTime.add(tiime);
//                                Log.d(TAG,"jigar the user last message list have is "
//                                        +arrayListUserLastMessageTime.toString()+"and user is "+userid);
                                //                                items.removeAt(currentPosition).also {
//                                    items.add(currentPosition - 1, it)
//                                }
//                                notifyItemMoved(currentPosition, currentPosition - 1)
                                if(!chat.isIsseen())
                                {
                                    intNotificationCount=intNotificationCount+1;
                                    textViewUnreadBadge.setVisibility(View.VISIBLE);
                                    textViewUnreadBadge.setText("     ");
                                    User user=mUsersFilteredList.get(position);
                                    mUsersFilteredList.remove(position);
                                    mUsersFilteredList.add(0,user);
                                    notifyItemMoved(position, 0);
//                                    items.removeAt(currentPosition).also {
//                                    items.add(currentPosition - 1, it)
//                                }
//                                notifyItemMoved(currentPosition, currentPosition - 1)

//                                    spaceNavigationView.shouldShowFullBadgeText(true);
//                                    spaceNavigationView.showBadgeAtIndex(2, intNotificationCount, Color.RED);
                                }else
                                {
                           //         intNotificationCount=intNotificationCount-1;
                                    textViewUnreadBadge.setVisibility(View.GONE);
                                }
                            }
                        }
//                        else if (chat.getTo().equalsIgnoreCase("broadcast")) {
//                            for (int i = 0; i < chat.getBroadcast_receiver().size(); i++) {
//                                if (chat.getBroadcast_receiver().get(i).equalsIgnoreCase(userid)) {
//                                    theLastMessage = chat.getMessage();
//                                    String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
//                                    time.setText(tiime);
//                                    arrayListUserLastMessageTime.add(tiime);
//                                }
//                            }
//                          //  Log.d(TAG,"jigar the user broadcast last message list have is "+arrayListUserLastMessageTime.toString());
//                        }

                    }
                }

                switch (theLastMessage) {
                    case "default":
                        textViewStatusTime.setText("No Messages");
                        break;

                    default:
                        /*String decMessage = null;
                        try {
                            decMessage = decrypt(theLastMessage,"Jenil");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

//                        last_msg.setText(time.toString());
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }
    private void lastMessageBroadcast(final String userid, final TextView textViewStatus, final TextView time) {
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
                        textViewStatus.setText("No Messages");
                        break;

                    default:
                        /*String decMessage = null;
                        try {
                            decMessage = decrypt(theLastMessage,"Jenil");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        textViewStatus.setText(time.toString());
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
