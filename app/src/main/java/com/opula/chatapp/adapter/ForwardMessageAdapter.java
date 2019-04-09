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
    private boolean ischat, isimage,isAudio;
    SharedPreference sharedPreference;
    FirebaseUser fuser;
    String meg, url;
    String TAG="ForwardMessageAdapter";
    AlertDialog alertDialog;
    String strAudioUrl;

    public ForwardMessageAdapter(Context mContext, List<User> mUsers, boolean isimage
            , boolean ischat, String meg, AlertDialog alertDialog, String url,boolean isAudio,String strAudioUrl) {

        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.isimage = isimage;
        this.isAudio = isAudio;
        this.meg = meg;
        this.url = url;
        this.strAudioUrl=strAudioUrl;
        this.alertDialog = alertDialog;
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

        holder.click_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideFloatingActionButton();
                sharedPreference.save(mContext, user.getId(), WsConstant.userId);
//                MainActivity.checkChatTheme(mContext);
                MainActivity.showpart1();
                Log.d(TAG,"jigar the forward message we have is "+meg);
                if(isAudio)
                {
                    MessageFragment.sendAudioToPersonal(mContext, fuser.getUid(),  user.getId(), "Voice Message"
                            , false,strAudioUrl, "default", true, "default"
                            , "default");
                }else {
                    MessageFragment.sendMessageToPersonal(mContext, "false", fuser.getUid(), user.getId(), meg,
                            false, "", "", isimage, url
                            , "default", false, "default", "default");
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
