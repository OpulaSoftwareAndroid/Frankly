package com.opula.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opula.chatapp.R;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.model.StarMessage;
import com.opula.chatapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StarMessageAdapter extends RecyclerView.Adapter<StarMessageAdapter.ViewHolder> {

    private Context mContext;
    private List<StarMessage> mUsers;
    private boolean ischat;
    SharedPreference sharedPreference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public StarMessageAdapter(Context mContext, List<StarMessage> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_star_message_item, parent, false);
        return new StarMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreference = new SharedPreference();

        final StarMessage starMessage = mUsers.get(position);
        holder.show_message.setText(starMessage.getMessage());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        //Sender
                        if (firebaseUser.getUid().equals(starMessage.getSender())) {
                            holder.txtsender.setText("You");
                            Log.d("Image_url_if", user.getImageURL() + "/");

                            if (user.getImageURL().equals("default")) {
                                holder.user_profile.setImageResource(R.drawable.image_boy);
                            } else {
                                Glide.with(mContext).load(user.getImageURL()).into(holder.user_profile);
                            }

                        }
                        if (user.getId().equals(starMessage.getSender())) {
                            holder.txtsender.setText(user.getUsername());
                            Log.d("Image_url_else", user.getImageURL() + "/");

                            if (user.getImageURL().equals("default")) {
                                holder.user_profile.setImageResource(R.drawable.image_boy);
                            } else {
                                Glide.with(mContext).load(user.getImageURL()).into(holder.user_profile);
                            }

                        }

                        //Receiver
                        if (firebaseUser.getUid().equals(starMessage.getReceiver())) {
                            holder.txtreceiver.setText("You");

                        } else if (user.getId().equals(starMessage.getReceiver())) {
                            holder.txtreceiver.setText(user.getUsername());
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, txtsender, txtreceiver;
        public ImageView profile_image, img_receive, img_tick, img_dtick, img_dstick;
        public TextView show_time;
        public RelativeLayout relative, txt_seen;
        public LinearLayout linear_chat;
        public LinearLayout linmain;
        public CircleImageView user_profile;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_receive = itemView.findViewById(R.id.img_receive);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_time = itemView.findViewById(R.id.show_time);
            relative = itemView.findViewById(R.id.relative);
            img_tick = itemView.findViewById(R.id.img_tick);
            img_dtick = itemView.findViewById(R.id.img_dtick);
            img_dstick = itemView.findViewById(R.id.img_dstick);
            linear_chat = itemView.findViewById(R.id.linear_chat);
            linmain = itemView.findViewById(R.id.linmain);
            txtsender = itemView.findViewById(R.id.txtsender);
            txtreceiver = itemView.findViewById(R.id.txtreceiver);
            user_profile = itemView.findViewById(R.id.user_profile);

        }
    }

}
