package com.opula.chatapp.adapter;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.model.User;

import java.util.List;

public class ForwardMessageAdapter extends RecyclerView.Adapter<ForwardMessageAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat, isimage,isAudio,isContact;

    String strContactName;
    String strContactNumber;
    String strDocumentUrl;
    SharedPreference sharedPreference;
    FirebaseUser fuser;
    String meg, url;
    boolean isVideo;
    String strVideoUrl;
    String TAG="ForwardMessageAdapter";
    AlertDialog alertDialog;
    String strAudioUrl;

    public ForwardMessageAdapter(Context mContext, List<User> mUsers, boolean isimage
            , boolean ischat, String meg, AlertDialog alertDialog, String url,boolean isAudio,String strAudioUrl
            ,boolean isContact,String strContactName,String strContactNumber
            ,boolean isVideo,String strVideoUrl,String strDocumentUrl) {

        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.isimage = isimage;
        this.isAudio = isAudio;
        this.meg = meg;
        this.url = url;
        this.strAudioUrl=strAudioUrl;
        this.alertDialog = alertDialog;
        this.isContact=isContact;
        this.strContactName=strContactName;
        this.strContactNumber=strContactNumber;
        this.isVideo=isVideo;
        this.strVideoUrl=strVideoUrl;
        this.strDocumentUrl=strDocumentUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_new_chat_user, parent, false);
        return new ForwardMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        sharedPreference = new SharedPreference();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        holder.last_msg.setText(user.getEmail());
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        Log.d(TAG,"jigar the forward message we have is "+meg);

        holder.click_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideFloatingActionButton();
                sharedPreference.save(mContext, user.getId(), WsConstant.userId);
//                MainActivity.checkChatTheme(mContext);
                MainActivity.showpart1();
                if(isAudio)
                {
                    MessageFragment.sendAudioToPersonal(mContext, fuser.getUid(),  user.getId(), "Voice Message"
                            , false,strAudioUrl, "default", true, "default"
                            , "default");
                }else {
                    if(isContact)
                    {
                        Log.d(TAG,"jigar the contact name we have is "+isContact);
                        MessageFragment.sendMessageToPersonal(mContext,"false", fuser.getUid(), user.getId(),
                                "contact", false,"",""
                                ,"",false, "default"
                                ,false, "default"
                                , "default", true, strContactName
                                , strContactNumber);

                    }else {
                        if(isVideo)
                        {
                            MessageFragment.sendMessageToPersonal(mContext,"false", fuser.getUid(),  user.getId()
                                    , "Video",
                                    false,"","",""
                                    ,true, url,true,strVideoUrl, "default"
                                    , false, "default", "default");


                        }else {
                            if(!strDocumentUrl.equalsIgnoreCase("default"))
                            {
                                MessageFragment.sendMessageToPersonal(mContext,"false", fuser.getUid(), user.getId()
                                        , meg,
                                        false,"","",""
                                        ,false, "default"
                                        ,false, "default",
                                        strDocumentUrl,false,"default", "default");

                                //                                public static void sendMessageToPersonal(final Context context, final String strIsSecureChat
//            , final String sender, final String receiver
//            , String message,boolean isRepliedMessage,String strRepliedMessageID,String strRepliedMessage
//                                    ,String strRepliedUserName, boolean isimage ,String uri,boolean isVideo,String strVideoUri
//                                    , String docUri
//            , boolean iscontact, String con_name, String con_num) {

                            }else
                            {
                                MessageFragment.sendMessageToPersonal(mContext, "false", fuser.getUid(), user.getId(), meg,
                                        false, "", "", "", isimage, url
                                        , false, "default"
                                        , "default", false, "default", "default");
                            }
                        }
                    }
                }

                alertDialog.dismiss();
                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MessageFragment()).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, last_msg;
        public ImageView profile_image;
        LinearLayout click_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            click_layout = itemView.findViewById(R.id.click_layout);
            last_msg = itemView.findViewById(R.id.last_msg);

        }
    }

}
