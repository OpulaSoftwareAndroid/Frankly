package com.opula.chatapp.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.StatusAdapter;
import com.opula.chatapp.adapter.UserAdapter;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.BroadcastUser;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.Chatlist;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Token;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListStatusFragment extends Fragment {

    private RecyclerView recyclerView, recycler_view1;
    private StatusAdapter statusAdapter;
    private List<User> mUsers;
    FirebaseUser firebaseUser;
    ImageView imageViewProfileImage;
    private List<BroadcastUser> mBroadcast;
    FirebaseUser fuser;
    DatabaseReference reference;
    private StorageTask uploadTask;
    Chatlist chatlist;
    private List<Chatlist> usersList;
    private List<String> broadcastList;
    ConstraintLayout task_list;
    ImageView imageViewOpenCamera;
    Uri mImageUri = null;
    StorageReference storageReference;
    LinearLayout no_chat;
    public static StringBuilder sb;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();
    LinearLayout linearLayoutStatus;
    static SearchView searchViewChatList;
    String TAG="ListChatFragment";
    User user;
    SpaceNavigationView spaceNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_status, container, false);
        MainActivity.showpart1();
        MainActivity.showFloatingActionButton();
        WsConstant.ismain = "p";
        recyclerView = view.findViewById(R.id.recycler_view);
        recycler_view1 = view.findViewById(R.id.recycler_view1);
        searchViewChatList=view.findViewById(R.id.searchViewChatList);
//        searchViewChatList.setVisibility(View.GONE);
        task_list = view.findViewById(R.id.task_list);
        no_chat = view.findViewById(R.id.no_chat);
        linearLayoutStatus=view.findViewById(R.id.linearLayoutAddStatus);
        linearLayoutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
            }
        });
        imageViewOpenCamera=view.findViewById(R.id.imageViewOpenCamera);
        imageViewOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();

            }
        });
        imageViewProfileImage=view.findViewById(R.id.imageViewProfileImage);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        spaceNavigationView = getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view1.setHasFixedSize(true);
        recycler_view1.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        broadcastList = new ArrayList<>();
        getChats();
        checkImage();
        getStatusList();
        //getBroadcast();

        searchViewChatList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //if(usersList.contains(query))
                {
                    Log.d(TAG,"jigar the query to send search is "+query);
                    statusAdapter.getFilter().filter(query);
                }
//                else{
//                    Toast.makeText(getContext(), "No Match found",Toast.LENGTH_LONG).show();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.d(TAG,"jigar the query to send search is "+query);
                statusAdapter.getFilter().filter(query);
                return false;
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    public static void setSearchViewVisibility(boolean boolStatus){
        if(boolStatus) {
            searchViewChatList.setVisibility(View.VISIBLE);
        }else
        {
            searchViewChatList.setVisibility(View.GONE);
        }
    }
    private void checkImage() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(User.class);
                    assert user != null;
//                    txtName.setText(user.getUsername());
//                    txtMobile.setText(firebaseUser.getEmail());
                    if (user.getImageURL().equals("default")) {
                        imageViewProfileImage.setImageResource(R.drawable.image_boy);
                    } else {
                        Glide.with(getActivity()).load(user.getImageURL()).into(imageViewProfileImage);
                    }
                } catch (Exception e) {
                    Log.d(TAG,"jigar the exception in image is "+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"jigar the datbase error in image is "+databaseError.getMessage());
            }
        });
    }
    public static String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
    public static void uploadNewStatus(final Context context
            , final String sender, String message, boolean isimage
            , String uri, String docUri)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Status").push();
        randomString(9);

        /*String encMessage = null;
        try {
            encMessage = encrypt(message,"Jenil");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Long tsLong = (System.currentTimeMillis() / 1000);
        String ts = tsLong.toString();

        HashMap<String, Object> hashMap = new HashMap<>();

        if (AppGlobal.isNetwork(context)) {
            hashMap.put("id", sb.toString());
            hashMap.put("sender", sender);
            hashMap.put("message", message);
            hashMap.put("issend", true);
            hashMap.put("isseen", false);
            hashMap.put("isimage", isimage);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("storage_uri", "default");
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("table_id", reference.getKey());
        } else {
            hashMap.put("id", sb.toString());
            hashMap.put("sender", sender);
            hashMap.put("message", message);
            hashMap.put("issend", false);
            hashMap.put("isseen", false);
            hashMap.put("isimage", isimage);
            hashMap.put("image", uri);
            hashMap.put("time", ts);
            hashMap.put("table_id", reference.getKey());
            hashMap.put("audio_uri", "default");
            hashMap.put("doc_uri", docUri);
            hashMap.put("storage_uri", "default");
        }
        reference.setValue(hashMap);
    }
    private void takePhotoFromCamera() {
        RxImagePicker.with(getActivity()).requestImage(Sources.CAMERA).subscribe(new io.reactivex.functions.Consumer<Uri>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Uri uri) throws Exception {
                mImageUri = uri;
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }

            }
        });

    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();
        pd.setCancelable(false);


        if (mImageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("Status");

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        uploadNewStatus(getActivity(), fuser.getUid(), "Image", true, mUri, "default");
//                        public static void uploadNewStatus(final Context context
//            , final String sender, String message, boolean isimage
//            , String uri, String docUri)

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getTo().equalsIgnoreCase("personal")) {
                            if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isseen", true);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        };
        reference.addValueEventListener(valueEventListener);
     //   mSendEventListner = valueEventListener;
    }

    public void getChats() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    usersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        chatlist = snapshot.getValue(Chatlist.class);

                        if (!("group".equalsIgnoreCase(snapshot.getKey()) || ("broadcast".equalsIgnoreCase(snapshot.getKey())))) {
                            Log.d(TAG,"jigar the message snapshot have "+ snapshot.getValue() + "//" + chatlist);
                            usersList.add(chatlist);
                        }
                    }
                    chatList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });
    }

    public void getStatusList() {
        reference = FirebaseDatabase.getInstance().getReference("Status").child(fuser.getUid());
        Log.d(TAG,"jigar the url of status list we have "+ reference);

//        https://shreem-connect-814a3.firebaseio.com/Status
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                  //  usersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                 //       chatlist = snapshot.getValue(Chatlist.class);
                       // if (!("group".equalsIgnoreCase(snapshot.getKey()) || ("broadcast".equalsIgnoreCase(snapshot.getKey())))) {
                            Log.d(TAG,"jigar the status list have "+ snapshot.getValue() + "//" + chatlist);
           //                 usersList.add(chatlist);//}
                    }
           //         chatList();
                } catch (Exception e) {
                    Log.d(TAG,"jigar the status exception have "+e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"jigar the status database error have "+databaseError.getMessage());

            }

        });

    }

    public void getBroadcast() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child("broadcast");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    broadcastList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        broadcastList.add(snapshot.getKey());
                        Log.d(TAG,"jigar the message from broad cast chat list  have " );

                    }
                    broadcastList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        String strUserChatID=chatlist.getId();

                        for (Chatlist chatlist : usersList) {
                            assert user != null;

                            if (chatlist.getId().equalsIgnoreCase(user.getId())) {
                                mUsers.add(user);
                                strUserChatID=strUserChatID+","+chatlist.getId();
                            }
                            Log.d(TAG,"jigar the message chat list  have "+ strUserChatID);

                        }
                    }
                    statusAdapter = new StatusAdapter(getContext(), mUsers, mBroadcast, true, true);
//                    statusAdapter = new UserAdapter(getContext(),spaceNavigationView, mUsers, mBroadcast, true, true);
                    WsConstant.check = "fragment";
                    setAdapter(statusAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void broadcastList() {
        mBroadcast = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Broadcast");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mBroadcast.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BroadcastUser user = snapshot.getValue(BroadcastUser.class);
                        for (String list : broadcastList) {
                            assert user != null;
                            if (user.getBroadcastId().equals(list)) {
                                mBroadcast.add(user);
                            }
                        }
                    }
//                    broadcastAdapter = new UserAdapter(getContext(), mUsers, mBroadcast, true, false);
//                    WsConstant.check = "fragment";
//                    setAdapter(statusAdapter, broadcastAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAdapter(StatusAdapter userAdapter) {

        if (userAdapter.getItemCount() == 0
        //        && broadcastAdapter.getItemCount() == 0
        ) {
            task_list.setVisibility(View.GONE);
            no_chat.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(userAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
//            recycler_view1.setAdapter(broadcastAdapter);
        }

//        if (statusAdapter.getItemCount() > 0 || broadcastAdapter.getItemCount() > 0) {
//            recyclerView.setAdapter(statusAdapter);
//            recycler_view1.setAdapter(broadcastAdapter);
//        } else {
//            task_list.setVisibility(View.GONE);
//            no_chat.setVisibility(View.VISIBLE);
//        }
       /* if (statusAdapter.getItemCount() > 0) {
            if (broadcastAdapter.getItemCount() > 0) {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
                //recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(statusAdapter);
            } else {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
               // recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(statusAdapter);
            }
        } else if (broadcastAdapter.getItemCount() > 0) {
            if (statusAdapter.getItemCount() > 0) {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
                //recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(statusAdapter);
            } else {
                task_list.setVisibility(View.VISIBLE);
                no_chat.setVisibility(View.GONE);
               // recycler_view1.setAdapter(broadcastAdapter);
                recyclerView.setAdapter(statusAdapter);
            }
        } else {
            // listView  empty
            task_list.setVisibility(View.GONE);
            no_chat.setVisibility(View.VISIBLE);
        }*/
    }

}
