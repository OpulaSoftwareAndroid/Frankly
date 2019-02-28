package com.opula.chatapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.comix.rounded.RoundedCornerImageView;
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
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ViewAllSharedMediaFragment extends Fragment {
    SharedPreference sharedPreference;
    String userid;
    FirebaseUser fuser;
    RecyclerView recycler_image;
    private List<Chat> mchat;
    DatabaseReference reference;
    ViewAllSharedMediaAdapter viewAllSharedMediaAdapter;
    LinearLayout imgBack;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewall_sharedmedia, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        userid = sharedPreference.getValue(getActivity(), WsConstant.userId);
        initViews(view);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recycler_image.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        readMesagges(fuser.getUid(), userid);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    private void initViews(View view) {
        recycler_image = view.findViewById(R.id.recycler_image);
        imgBack = view.findViewById(R.id.imgBack);
    }

    private void readMesagges(final String myid, final String userid) {
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mchat.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;

                        if (chat.getTo().equalsIgnoreCase("personal")) {
                            if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                    chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                                if (chat.isIsimage()) {
                                    mchat.add(chat);
                                }
                            }
                        }
                        viewAllSharedMediaAdapter = new ViewAllSharedMediaAdapter(getActivity(), mchat);
                        recycler_image.setAdapter(viewAllSharedMediaAdapter);

                        if (viewAllSharedMediaAdapter.getItemCount() > 0) {
                            // listView not empty
                            recycler_image.setVisibility(View.VISIBLE);
//                            text_no_image.setVisibility(View.GONE);
                            recycler_image.setAdapter(viewAllSharedMediaAdapter);
                        } else {
                            // listView  empty
                            recycler_image.setVisibility(View.GONE);
//                            text_no_image.setVisibility(View.VISIBLE);
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

    public class ViewAllSharedMediaAdapter extends RecyclerView.Adapter<ViewAllSharedMediaAdapter.ViewHolder> {

        private Context mContext;
        private List<Chat> mChat;

        public ViewAllSharedMediaAdapter(Context mContext, List<Chat> mChat) {
            this.mChat = mChat;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_viewall_sharedmedia, parent, false);
            return new ViewAllSharedMediaAdapter.ViewHolder(view);
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

            public RoundedCornerImageView profile_image;
            public ProgressBar p_bar;

            public ViewHolder(View itemView) {
                super(itemView);
                profile_image = itemView.findViewById(R.id.img_main);
                p_bar = itemView.findViewById(R.id.p_bar);

            }
        }

    }
}
