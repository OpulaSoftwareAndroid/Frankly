package com.opula.chatapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.model.Chat;

import java.util.List;

public class UserSharedAdapter extends RecyclerView.Adapter<UserSharedAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChat;

    public UserSharedAdapter(Context mContext, List<Chat> mChat) {
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_user_shared_image, parent, false);
        return new UserSharedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);
        if (chat.getImage().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.image_boy);
        } else {
            holder.p_bar.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(chat.getImage())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.p_bar.setVisibility(View.GONE);
                            Toast.makeText(mContext, "No Image Found!" + model + "/" + e, Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.p_bar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.profile_image);
        }

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dailog_show_image, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setCancelable(true);

                final ImageView image = (ImageView) dialogView.findViewById(R.id.image);

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

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profile_image;
        public ProgressBar p_bar;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            p_bar = itemView.findViewById(R.id.p_bar);

        }
    }

}
