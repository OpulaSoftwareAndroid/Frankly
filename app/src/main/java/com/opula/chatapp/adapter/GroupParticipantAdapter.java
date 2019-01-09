package com.opula.chatapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.UserProfileFragment;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class GroupParticipantAdapter extends RecyclerView.Adapter<GroupParticipantAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    String groupAdminID, groupID;
    FirebaseUser firebaseUser;
    SharedPreference sharedPreference;
    List<String> grpList;


    public GroupParticipantAdapter(Context mContext, List<User> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.participant_user_item, parent, false);
        return new GroupParticipantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        sharedPreference = new SharedPreference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        groupAdminID = sharedPreference.getValue(mContext, WsConstant.groupadminId);
        groupID = sharedPreference.getValue(mContext, WsConstant.groupId);

        final User user = mUsers.get(position);

        if (!user.getId().equals(firebaseUser.getUid()) && !(user.getId().equals(groupAdminID))) {
            holder.username.setText(user.getUsername());
        }
        if (user.getId().equals(firebaseUser.getUid())) {
            holder.username.setText("you");
            holder.last_msg.setVisibility(View.INVISIBLE);
        }
        if (user.getId().equals(groupAdminID)) {
            holder.username.setText(user.getUsername() + "  (admin)");
        }
        if (user.getId().equals(groupAdminID) && user.getId().equals(firebaseUser.getUid())) {
            holder.username.setText("you (admin)");
        }

        holder.last_msg.setText(user.getEmail());
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onLongClick(View view) {
                String id = user.getId();
                String name = user.getSearch();
                if (!id.equals(firebaseUser.getUid())) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dailog_remove_member, null);
                    alertDialogBuilder.setView(dialogView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    TextView txtMsg = dialogView.findViewById(R.id.txtMsg);
                    TextView txtViewProfile = dialogView.findViewById(R.id.txtViewProfile);
                    TextView txtRemove = dialogView.findViewById(R.id.txtRemove);

                    if (firebaseUser.getUid().equals(groupAdminID)) {
                        txtRemove.setVisibility(View.VISIBLE);
                    }

                    txtMsg.setText("Message " + name);
                    txtViewProfile.setText("View profile " + name);
                    txtRemove.setText("Remove " + name);

                    txtMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            MsgUser(user.getId());

                        }
                    });

                    txtViewProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            ViewUser(user.getId());
                        }
                    });

                    txtRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            RemoveUser(user.getId());
                        }
                    });
                    alertDialog.show();
                }

                return false;
            }
        });

    }

    private void RemoveUser(String id) {
        deleteGroupFromChatList(id);
        deleteMemebeFromGroup(id);
        Toast.makeText(mContext, "Removed successfully", Toast.LENGTH_SHORT).show();
    }

    private void deleteMemebeFromGroup(final String id) {
        grpList = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot d1) {
                if (d1.exists()) {
                    Log.d("GroupChat_dataa", d1.getValue() + "//");
                    GroupUser groupUser = d1.getValue(GroupUser.class);
                    for (int i = 0; i < groupUser.getMemberList().size(); i++) {
                        String member = groupUser.getMemberList().get(i);
                        if (!id.equals(member)) {
                            String grp = member;
                            Log.d("GroupChat2", grp + "//");
                            grpList.add(grp);
                        }
                    }
                    ref.child("memberList").setValue(grpList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteGroupFromChatList(String id) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(id).child("group");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                        String id = d1.getKey();
                        if (id.equalsIgnoreCase(groupID)) {
                            Log.d("Remove_member", d1.getValue() + "/");
                            d1.getRef().removeValue();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ViewUser(String id) {
        sharedPreference.save(mContext, id, WsConstant.userId);
        MainActivity.showpart2();
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new UserProfileFragment()).addToBackStack(null).commit();

    }

    private void MsgUser(String id) {
        MainActivity.hideFloatingActionButton();
        sharedPreference.save(mContext, id, WsConstant.userId);
        MainActivity.checkChatTheme(mContext);
        MainActivity.showpart1();
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MessageFragment()).addToBackStack(null).commit();


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profile_image;
        private EmojiconTextView last_msg, username;
        LinearLayout click_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msg);
            click_layout = itemView.findViewById(R.id.click_layout);

        }
    }
//
//    //check for last message
//    private void lastMessage(final String userid, final TextView last_msg, final TextView time) {
//        theLastMessage = "default";
//        thetime = "";
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (firebaseUser != null && chat != null) {
//                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
//                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
//                            theLastMessage = chat.getMessage();
//
//                            String tiime = getDateCurrentTimeZone(Long.parseLong(chat.getTime()));
//                            time.setText(tiime);
//
//                        }
//                    }
//                }
//
//                switch (theLastMessage) {
//                    case "default":
//                        last_msg.setText("No Message");
//                        break;
//
//                    default:
//                        last_msg.setText(theLastMessage);
//                        break;
//                }
//
//                theLastMessage = "default";
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    public String getDateCurrentTimeZone(long timestamp) {
//        try {
//            Calendar calendar = Calendar.getInstance();
//            TimeZone tz = TimeZone.getDefault();
//            calendar.setTimeInMillis(timestamp * 1000);
//            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//            Date currenTimeZone = (Date) calendar.getTime();
//            return sdf.format(currenTimeZone);
//        } catch (Exception e) {
//        }
//        return "";
}

