package com.opula.chatapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.SeenList;
import com.opula.chatapp.model.User;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private static final String TAG ="GroupMessageAdapter" ;
    FirebaseUser fuser;
    private Activity mContext;
    private List<Chat> mChat;
    DatabaseReference reference;
    GroupUser user;
     List<User> mUsers;
    private String imageurl;
    private String strGroupID;
    private BottomSheetDialog dialogMenu;
    private int memberListSize;

    public GroupMessageAdapter(Activity mContext, List<Chat> mChat, String imageurl, String strGroupID, int memberListSize) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.strGroupID = strGroupID;
        this.memberListSize = memberListSize;
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

//        String strTempSeenBy=" , "+chat.getIsseenby();
      //  String strArray[] =strTempSeenBy.split(" , ");
        List<String> arrayListSeenBy = new ArrayList<String>(Arrays.asList(chat.getIsseenby().split(",")));

        Log.d(TAG, "jigar the member size we have is  have of profile is " + memberListSize);
        Log.d(TAG, "jigar the member size arraylist have of profile pic y is " + arrayListSeenBy.size());

        if(position==mChat.size()-1) {
            if (memberListSize < 2) {
                holder.img_tick.setVisibility(View.VISIBLE);
                holder.img_dtick.setVisibility(View.GONE);
                holder.imageViewSeenAllTick.setVisibility(View.GONE);

            } else {
                if (memberListSize == (arrayListSeenBy.size())) {
                    holder.img_tick.setVisibility(View.GONE);
                    holder.img_dtick.setVisibility(View.GONE);
                    holder.imageViewSeenAllTick.setVisibility(View.VISIBLE);
                } else {
                    holder.img_tick.setVisibility(View.GONE);
                    holder.img_dtick.setVisibility(View.VISIBLE);
                    holder.imageViewSeenAllTick.setVisibility(View.GONE);
                }
            }

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

                final ImageView image =  dialogView.findViewById(R.id.image);

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


        holder.linear_chat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (chat.getSender().equals(fuser.getUid())) {
//                holder.linmain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_selecchat));
//                i = holder.getAdapterPosition();
                    //      MainActivity.showpart3();
                    Vibrator vv = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
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
                    String strTempSeenBy=" , "+chat.getIsseenby();
                    String strArray[] =strTempSeenBy.split(" , ");


                    Log.d(TAG, "jigar the group message id converted to String array" + strTempSeenBy);

                    //print elements of String array


                    showInfoSeenPopup(mContext, point,strArray);
                    assert vv != null;
                    vv.vibrate(50); // 5000 miliseconds = 5 seconds
                    return false;
                }
                return true;
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

        LinearLayout linear_chat;
        public ImageView profile_image, img_receive, img_tick, img_dtick,imageViewSeenAllTick;
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
            imageViewSeenAllTick= itemView.findViewById(R.id.imageViewSeenAllTick);
            relative_contact = itemView.findViewById(R.id.relative_contact);
            txtAddContact = itemView.findViewById(R.id.txtAddContact);
            linear_chat = itemView.findViewById(R.id.linear_chat);

            txtContactName = itemView.findViewById(R.id.txtContactName);
            txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
            pdfView = itemView.findViewById(R.id.pdfView);

        }
    }

    @Override
    public int getItemViewType(int position) {
       fuser = FirebaseAuth.getInstance().getCurrentUser();
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

    private void showInfoSeenPopup(final Activity context, Point p, final String[] arrayUserID) {

        final PopupWindow changeStatusPopUp;
        // Inflate the popup_layout.xml
//            linearLayoutMainHome.setBackground(getResources().getDrawable(R.drawable.transparent_dark_rectangle));
//            relativeLayoutViewPager.setBackground(getResources().getDrawable(R.drawable.transparent_dark_rectangle));

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.pop_up_info_chat_message, null);
        // Creating the PopupWindow
        LinearLayout viewGroup = (LinearLayout) layout.findViewById(R.id.linearLayoutInfo);
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
        int OFFSET_Y =  changeStatusPopUp.getHeight();
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

        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<SeenList> seenMessageList = new ArrayList<>();
                RecyclerView recyclerViewMessageSeenList;
                view = context.getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_seen_message, null);
                dialogMenu = new BottomSheetDialog(context);
                dialogMenu.setContentView(view);
                dialogMenu.setCancelable(true);
                dialogMenu.show();
                changeStatusPopUp.dismiss();

                recyclerViewMessageSeenList = (RecyclerView) view.findViewById(R.id.recyclerViewMessageSeenList);
                SeenMessageListAdapter seenMessageListAdapter=new SeenMessageListAdapter(seenMessageList,context);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                recyclerViewMessageSeenList.setLayoutManager(mLayoutManager);
                recyclerViewMessageSeenList.setItemAnimator(new DefaultItemAnimator());
                recyclerViewMessageSeenList.setAdapter(seenMessageListAdapter);
                groupMemberList(arrayUserID,seenMessageList,seenMessageListAdapter);

               // getMemberFromGroup();
                seenMessageListAdapter.notifyDataSetChanged();

//                    linearLayoutMainHome.setBackgroundResource(0);
//                    relativeLayoutViewPager.setBackgroundResource(0);
            }
        });
    }
    private void getMemberFromGroup() {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(strGroupID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot d1) {
                try {
                    if (d1.exists()) {
                        GroupUser groupUser = d1.getValue(GroupUser.class);
                        for (int i = 0; i < groupUser.getMemberList().size(); i++) {
                            String member = groupUser.getMemberList().get(i);
                            //         String strTempMemberID="";
                            if (!fuser.getUid().equals(member))
                            {
                                String strMemberID = member;
                                Log.d(TAG, "jigar the member of group adapter name we have is " + member);

                                //  arrayListGroupMemberList.add(strMemberID);

                                //      strTempMemberID=strMemberID;
//                                sendNotifiaction(context,grp,userName,message);

                            }


//                            if (groupUser.getMemberList().size() <= 1) {
//                                ref.getRef().removeValue();
//                                Log.d("GroupChat2", "//");
//                            }
                        }

//                        getUserInfoAndSendNotification(context, arrayListGroupMemberList, message);


                        // ref.child("memberList").setValue(grpList);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "jigar the exception member list error in notification is " + e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void groupMemberList(final String[] stringArrayUserID
            , final List<SeenList> seenMessageList
            , final SeenMessageListAdapter seenMessageListAdapter)
    {
//                    System.out.println(arrayUserID[i]);
//

// here we start

//        for(int i=0; i < stringArrayUserID.length; i++){
//            if(!stringArrayUserID[i].equals("")) {
//                Log.d(TAG, "jigar the group message size we have is " + stringArrayUserID.length);
//                SeenList seenListAdapter = new SeenList(stringArrayUserID[i], "Action & Adventure");
//                seenMessageList.add(seenListAdapter);
//                seenMessageListAdapter.notifyDataSetChanged();
//            }
//        }
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final List<String> arrayListGroupMemberID = new ArrayList<String>(Arrays.asList(stringArrayUserID)); //new ArrayList is only needed if you absolutely need an ArrayList
                    ArrayList <String>arrayListGroupMemberName=new ArrayList<>();
                    ArrayList <String>arrayListGroupMemberImageURL=new ArrayList<>();
                    arrayListGroupMemberName.clear();

                    mUsers.clear();
                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                        User u1 = d1.getValue(User.class);
                   //     Log.d(TAG, "jigar the group message new direct seen by name is " +u1.getUsername());

                        if(arrayListGroupMemberID.contains(u1.getId()))
                        {
                            arrayListGroupMemberImageURL.add(u1.getImageURL());
                            arrayListGroupMemberName.add(u1.getUsername());
                        }
//                        for (int i = 0; i < user.getMemberList().size(); i++) {
//                            String id = user.getMemberList().get(i);
//                            if (u1.getId().equals(id)) {
//
//                                mUsers.add(u1);
//                            }
//                        }
                    }

                    for(int i=0;i<arrayListGroupMemberName.size();i++) {
                        Log.d(TAG, "jigar the group message seen by name is " + arrayListGroupMemberName.get(i));
                        SeenList seenListAdapter = new SeenList(arrayListGroupMemberName.get(i), arrayListGroupMemberImageURL.get(i));
                        seenMessageList.add(seenListAdapter);
                        seenMessageListAdapter.notifyDataSetChanged();

                    }
//                    userAdapter = new GroupParticipantAdapter(getContext(), mUsers);
//                    WsConstant.check = "fragment";
//                    recycler_view_member.setAdapter(userAdapter);

                } catch (Exception e) {
                    Log.d(TAG, "jigar the data error exception in group message seen by name is " +e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "jigar the data error in group message seen by name is " +databaseError.getMessage());

            }
        });
    }


}

