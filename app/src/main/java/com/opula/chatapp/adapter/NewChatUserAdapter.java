package com.opula.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.fragments.MessageFragment;
import com.opula.chatapp.fragments.UserProfileFragment;
import com.opula.chatapp.model.User;

import java.util.List;
import java.util.Objects;

public class NewChatUserAdapter extends RecyclerView.Adapter<NewChatUserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;
    SharedPreference sharedPreference;

    public NewChatUserAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_new_chat_user, parent, false);
        return new NewChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        sharedPreference = new SharedPreference();

        if (Objects.requireNonNull(mUsers.get(position)).getId() != null)
            {

//        if (Objects.requireNonNull(user).getId() != null) {
            final User user = mUsers.get(position);

            String strUsername = user.getUsername().substring(0, 1).toUpperCase() + user.getUsername().substring(1);
            holder.username.setText(strUsername);
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
                    FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new MessageFragment()).commit();

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
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username,last_msg;
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
