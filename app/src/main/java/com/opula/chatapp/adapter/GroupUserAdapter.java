package com.opula.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.opula.chatapp.fragments.GroupMessageFragment;
import com.opula.chatapp.fragments.GroupProfileFragment;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.ViewHolder> {

    private Context mContext;
    private List<GroupUser> mUsers;
    private boolean ischat;
    SharedPreference sharedPreference;
    String theLastMessage, thetime;
    String strSenderUserID;
    int intcount=0;
    String TAG="GroupUserAdapter";

    public GroupUserAdapter(Context mContext,String strSenderUserID,List<GroupUser> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.strSenderUserID=strSenderUserID;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_user_item, parent, false);
        return new GroupUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        sharedPreference = new SharedPreference();

        final GroupUser user = mUsers.get(position);
        holder.username.setText(user.getGroupName());

        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat) {
            Log.d(TAG, "jigar the group chat is not seen count before all is " + holder.textViewUnreadBadge.getText().toString());

            //            lastMessage(mUsersFilteredList,position,user.getId(), holder.last_msg, holder.time,holder.textViewUnreadBadge);

            Log.d(TAG, "jigar the sender user id we getting in adapter is " + strSenderUserID);
            lastMessage(user.getGroupId(), holder.last_msg, holder.time,holder.textViewUnreadBadge);

        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreference.save(mContext, user.getGroupId(), WsConstant.groupUserId);
                MainActivity.hideFloatingActionButton();
//                MainActivity.checkChatTheme(mContext);
                MainActivity.showpart1();
                holder.textViewUnreadBadge.setVisibility(View.GONE);

                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupMessageFragment()).addToBackStack(null).commit();

            }
        });

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreference.save(mContext, user.getGroupId(), WsConstant.groupUserId);
                sharedPreference.save(mContext, user.getGroupAdmin(), WsConstant.groupadminId);
                MainActivity.showpart2();
                holder.textViewUnreadBadge.setVisibility(View.GONE);

                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new GroupProfileFragment()).addToBackStack(null).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg,time,textViewUnreadBadge;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            time = itemView.findViewById(R.id.time);
            textViewUnreadBadge=itemView.findViewById(R.id.textViewUnreadBadge);
        }
    }

    //check for last message
private void lastMessage(final String userid, final TextView last_msg, final TextView time, final TextView textViewUnreadBadge) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        final ArrayList<String> arrayListUnreadChatID=new ArrayList();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getTo().equalsIgnoreCase("group")){
                        if (firebaseUser != null && chat != null) {
                            if (chat.getReceiver().equals(userid)) {
                                theLastMessage = chat.getMessage();
                                String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
                                time.setText(tiime);

                                Log.d(TAG,"jigar the chat is read ? "+chat.isIsseen());

                                if(!chat.isIsseen())
                                {
                                    Log.d(TAG,"jigar the chat not read id is "+chat.getId());

                                    if(!arrayListUnreadChatID.contains(chat.getId()))
                                    {
                                        arrayListUnreadChatID.add(chat.getId());
                                    }
                                    intcount=(intcount+1)-intcount;
                                    Log.d(TAG,"jigar the the unread arraylist  chat time is with "+arrayListUnreadChatID.size());

                                    if(!chat.getSender().equals(userid)) {
                                        textViewUnreadBadge.setVisibility(View.GONE);
                                    }else
                                    {
                                        textViewUnreadBadge.setText(" "+arrayListUnreadChatID.size()+" ");
                                        textViewUnreadBadge.setVisibility(View.VISIBLE);
                                    }
                                }else
                                {
                                    textViewUnreadBadge.setVisibility(View.GONE);
                                }

                            }
                        }
                    }

                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Messages");
                        break;

                    default:
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
