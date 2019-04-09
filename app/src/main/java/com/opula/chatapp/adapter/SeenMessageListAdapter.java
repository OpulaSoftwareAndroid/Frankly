package com.opula.chatapp.adapter;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.model.SeenList;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SeenMessageListAdapter extends RecyclerView.Adapter<SeenMessageListAdapter.MyViewHolder> {

    private List<SeenList> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUserName;
        public ImageView imageViewUserProfileImage;

        public MyViewHolder(View view) {
            super(view);
            textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
            imageViewUserProfileImage=(ImageView) view.findViewById(R.id.imageViewProfileImage);
        }
    }

    public SeenMessageListAdapter(List<SeenList> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bottom_sheet_dialog, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        SeenList seenList = moviesList.get(position);
        holder.textViewUserName.setText(seenList.getTextViewUserName());
        Glide.with(context).load(seenList.getProfileImageUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
              //          AppGlobal.hideProgressDialog(context);
                        holder.imageViewUserProfileImage.setImageResource(R.drawable.avatar);
                        Toast.makeText(context, "No Image Found!" + model + "/" + e, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    //    AppGlobal.hideProgressDialog(context);
                //        alertDialog.show();
                        return false;
                    }
                })
                .into(holder.imageViewUserProfileImage);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}