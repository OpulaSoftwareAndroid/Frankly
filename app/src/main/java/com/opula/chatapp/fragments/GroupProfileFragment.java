package com.opula.chatapp.fragments;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
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
import com.opula.chatapp.constant.RoundCornersTransformation;
import com.opula.chatapp.constant.SharedPreference;
import com.opula.chatapp.constant.WsConstant;
import com.opula.chatapp.model.GroupUser;
import com.opula.chatapp.model.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class GroupProfileFragment extends Fragment {

    ImageView mImageView, imgBack, groupimg_edit;
    TextView txtName, txtMember, txtParticipant;
    Button btnExit;
    RecyclerView recycler_view_member, recycler_view_media;
    SharedPreference sharedPreference;
    String groupUserId;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_profile, container, false);

        MainActivity.hideFloatingActionButton();

        sharedPreference = new SharedPreference();
        groupUserId = sharedPreference.getValue(getActivity(), WsConstant.groupUserId);

        initViews(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Group_Profile");

        recycler_view_member.setHasFixedSize(true);
        recycler_view_member.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler_view_media.setHasFixedSize(true);
        recycler_view_media.setLayoutManager(new LinearLayoutManager(getContext()));

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

        return view;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
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
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(GroupUser.class);
                assert user != null;
                txtName.setText(user.getGroupName());
                txtMember.setText(user.getMemberList().size() + " member");
                txtParticipant.setText(user.getMemberList().size() + " Participants");
                if (user.getImageURL().equals("default")) {
                    mImageView.setImageResource(R.drawable.image_boy);
                } else {
                    try {
                        Log.d("Image", user.getImageURL());
                        Picasso.get()
                                .load(user.getImageURL())
                                .transform(new RoundCornersTransformation(8, 00, true, false))
                                .placeholder(R.drawable.image_boy).error(R.drawable.image_boy)
                                .into(mImageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                groupMember();
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
                    Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
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
                Log.d("Group_dataa", mUsers + "/ ");
                userAdapter = new GroupParticipantAdapter(getContext(), mUsers);
                WsConstant.check = "fragment";
                recycler_view_member.setAdapter(userAdapter);
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
        txtMember = view.findViewById(R.id.txtMember);
        groupimg_edit = view.findViewById(R.id.groupimg_edit);
        btnExit = view.findViewById(R.id.btnExit);
        txtName = view.findViewById(R.id.txtName);
        recycler_view_member = view.findViewById(R.id.recycler_view_member);
        recycler_view_media = view.findViewById(R.id.recycler_view_media);
    }
}
