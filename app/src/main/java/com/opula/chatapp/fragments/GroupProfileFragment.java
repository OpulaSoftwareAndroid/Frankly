package com.opula.chatapp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.adapter.GroupParticipantAdapter;
import com.opula.chatapp.adapter.UserSharedAdapter;
import com.opula.chatapp.constant.RoundCornersTransformation;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.Chat;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class GroupProfileFragment extends Fragment {

    LinearLayout imgBack;
    ImageView mImageView, groupimg_edit, img_edit_profile, img_add_person;
    TextView txtMember, txtParticipant;
    EmojiconTextView txtGrpName;
    Button btnExit;
    RecyclerView recycler_view_member, recycler_image;
    SharedPreference sharedPreference;
    String groupUserId, groupAdminID;
    DatabaseReference reference;
    GroupUser user;
    List<User> mUsers;
    GroupParticipantAdapter userAdapter;
    Uri mImageUri = null;
    private StorageTask uploadTask;
    int GALLERY = 1, CAMERA = 2;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    ProgressBar progress_circular;
    List<String> grpList;

    TextView text_no_image;
    private List<Chat> mchat;
    private UserSharedAdapter userSharedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_profile, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);
        groupAdminID = sharedPreference.getValue(getContext(), WsConstant.groupadminId);
        grpList = new ArrayList<>();

        initViews(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Group_profile");


        if (groupAdminID.equals(firebaseUser.getUid())) {
            img_add_person.setVisibility(View.VISIBLE);
        }

        recycler_view_member.setHasFixedSize(true);
        recycler_view_member.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_image.setLayoutManager(horizontalLayoutManagaer);

        checkImage();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        groupimg_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        img_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());

                LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dailog_rename_group, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setCancelable(true);

                final EditText text = dialogView.findViewById(R.id.text);
                final Button save = dialogView.findViewById(R.id.btn_yes);
                final Button cancle = dialogView.findViewById(R.id.btn_no);

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
                        ref.child("groupName").setValue(text.getText().toString());

                        Toast.makeText(getActivity(), "Group Updated!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }
                });

                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog(getContext());
            }
        });

        readMesagges(groupUserId);

        img_add_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sharedPreference.save(getContext(), user.getGroupId(), WsConstant.groupUserId);
                MainActivity.hideFloatingActionButton();
                MainActivity.checkChatTheme(getContext());
                MainActivity.showpart2();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new AddMemberGroupFragment()).addToBackStack(null).commit();
            }
        });
        return view;
    }


    public void showExitDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_exit_group, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setCustomTitle(View.inflate(mContext, R.layout.alert_back, null));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button btn_no = dialogView.findViewById(R.id.btn_no);
        Button btn_yes = dialogView.findViewById(R.id.btn_yes);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGroup();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void exitGroup() {
        deleteGroupFromChatList();
        deleteMemebeFromGroup();

        MainActivity.checkListTheme(Objects.requireNonNull(getContext()));

        FragmentManager fragmentPersonal = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentPersonal.beginTransaction().replace(R.id.frame_mainactivity, new ListGroupChatFragment()).addToBackStack(null).commit();

    }

    private void deleteMemebeFromGroup() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot d1) {
                try {
                    if (d1.exists()) {
                        GroupUser groupUser = d1.getValue(GroupUser.class);
                        for (int i = 0; i < groupUser.getMemberList().size(); i++) {
                            String member = groupUser.getMemberList().get(i);
                            if (!firebaseUser.getUid().equals(member)) {
                                String grp = member;
                                grpList.add(grp);
                            }
                            if(groupUser.getMemberList().size()<=1)
                            {
                                ref.getRef().removeValue();
                                Log.d("GroupChat2",   "//");
                            }
                        }
                        ref.child("memberList").setValue(grpList);
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

    private void deleteGroupFromChatList() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child("group");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                            String id = d1.getKey();
                            assert id != null;
                            if (id.equalsIgnoreCase(groupUserId)) {
                                Log.d("Remove_data", d1.getValue() + "/");
                                d1.getRef().removeValue();
                            }
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

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Choose Image");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera",
                "Remove Photo"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                            case 2:
                                removePhoto1();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void removePhoto1() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
        ref.child("imageURL").setValue("default");
        checkImage();
    }

    private void checkImage() {
        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(GroupUser.class);
                    assert user != null;
                    txtGrpName.setText(user.getGroupName());
                    txtMember.setText(user.getMemberList().size() + " member");
                    txtParticipant.setText(user.getMemberList().size() + " Participants");
                    sharedPreference.save(getContext(), user.getGroupAdmin(), WsConstant.groupadminId);
                    sharedPreference.save(getContext(), user.getGroupId(), WsConstant.groupId);
                    if (user.getImageURL().equals("default")) {
                        mImageView.setImageResource(R.drawable.img2);
                    } else {
                        try {
                            progress_circular.setVisibility(View.VISIBLE);
                            Log.d("Image", user.getImageURL());
                            Picasso.get()
                                    .load(user.getImageURL())
                                    .transform(new RoundCornersTransformation(50, 0, true, false))
                                    .into(mImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            progress_circular.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            progress_circular.setVisibility(View.GONE);
                                            mImageView.setImageResource(R.drawable.img2);
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    groupMember();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            if (data != null) {
                mImageUri = data.getData();
                mImageView.setImageURI(mImageUri);
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        }
        if (requestCode == CAMERA && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            final Bitmap bitmap = (Bitmap) bundle.get("data");
            mImageUri = getImageUri(getContext(), bitmap);
            mImageView.setImageBitmap(bitmap);
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (mImageUri != null) {
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

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups").child(groupUserId);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", "" + mUri);
                        reference1.updateChildren(map);

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

    private void groupMember() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    mUsers.clear();
                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                        User u1 = d1.getValue(User.class);
                        for (int i = 0; i < user.getMemberList().size(); i++) {
                            String id = user.getMemberList().get(i);
                            if (u1.getId().equals(id)) {

                                mUsers.add(u1);
                            }
                        }
                    }

                    userAdapter = new GroupParticipantAdapter(getContext(), mUsers);
                    WsConstant.check = "fragment";
                    recycler_view_member.setAdapter(userAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews(View view) {
        mImageView = view.findViewById(R.id.image_PersonalInfo_DP);
        imgBack = view.findViewById(R.id.imgBack);
        txtParticipant = view.findViewById(R.id.txtParticipant);
        img_add_person = view.findViewById(R.id.imgaddperson);
        txtMember = view.findViewById(R.id.txtMember);
        groupimg_edit = view.findViewById(R.id.groupimg_edit);
        img_edit_profile = view.findViewById(R.id.img_edit_profile);
        btnExit = view.findViewById(R.id.btnExit);
        txtGrpName = view.findViewById(R.id.txtName);
        recycler_view_member = view.findViewById(R.id.recycler_view_member);
        recycler_image = view.findViewById(R.id.recycler_image);
        progress_circular = view.findViewById(R.id.progress_circular);
        text_no_image = view.findViewById(R.id.text_no_image);
    }

    private void readMesagges(final String userid) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mchat.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        if (chat.getReceiver().equals(userid)) {
                            if (chat.getIsimage()) {
                                mchat.add(chat);
                            }
                        }

                        userSharedAdapter = new UserSharedAdapter(getActivity(), mchat);
                        recycler_image.setAdapter(userSharedAdapter);

                        if (userSharedAdapter.getItemCount() > 0) {
                            // listView not empty
                            recycler_image.setVisibility(View.VISIBLE);
                            text_no_image.setVisibility(View.GONE);
                            recycler_image.setAdapter(userSharedAdapter);
                        } else {
                            // listView  empty
                            recycler_image.setVisibility(View.GONE);
                            text_no_image.setVisibility(View.VISIBLE);
                        }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
