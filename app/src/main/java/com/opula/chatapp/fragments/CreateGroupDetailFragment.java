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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreateGroupDetailFragment extends Fragment {
    List<String> myList;
    List<String> grpList;
    String gropuList;
    TextView txtaddPhoto;
    Button btnCreate;
    MaterialEditText txt_name;
    ImageView add_image, imgUpload;
    Uri mImageUri = null;
    int GALLERY = 1, CAMERA = 2;
    StorageReference storageReference;
    private StorageTask uploadTask;
    String mUri;
    StringBuilder sb;
    FirebaseUser fuser;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static SecureRandom rnd = new SecureRandom();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_grp_detail, container, false);

        MainActivity.hideFloatingActionButton();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        gropuList = getArguments().getString("GrpList");
        myList = new ArrayList<>(Arrays.asList(gropuList.split(",")));
        myList.add(fuser.getUid());
        grpList = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference("Group_profile");
        initViews(view);

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }

            }
        });

        return view;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Choose Image");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
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
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, CAMERA);
    }

    public String randomString(int len) {
        sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
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
                add_image.setImageURI(mImageUri);
                txtaddPhoto.setVisibility(View.GONE);
                imgUpload.setVisibility(View.GONE);

            }
        }
        if (requestCode == CAMERA && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            final Bitmap bitmap = (Bitmap) bundle.get("data");
            mImageUri = getImageUri(getContext(), bitmap);
            add_image.setImageBitmap(bitmap);
            txtaddPhoto.setVisibility(View.GONE);
            imgUpload.setVisibility(View.GONE);

        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Creating Group..");
        pd.show();
        randomString(9);

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
                        mUri = downloadUri.toString();

//                        group chat

                        String grpName = txt_name.getText().toString();
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("groupName", grpName);
                        hashMap.put("memberList", myList);
                        hashMap.put("imageURL", mUri);
                        hashMap.put("groupAdmin", fuser.getUid());
                        hashMap.put("groupId", sb.toString());
                        reference.child("Groups").child(sb.toString()).setValue(hashMap);

                        for (int i = 0; i < myList.size(); i++) {

                            final int finalI = i;

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("Chatlist")) {
                                        // run some code
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myList.get(finalI));
                                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                                        Log.d("GroupChat", d1.getValue() + "//");
                                                        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(dataSnapshot.getKey()).child("group");
                                                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                                if (dataSnapshot1.getValue() == null) {
                                                                    grpList.add(sb.toString());
                                                                    for (String s : grpList) {
                                                                        reference2.child(s).setValue(s);
                                                                    }
                                                                } else {
                                                                    grpList.add(sb.toString());
                                                                    for (String s : grpList) {
                                                                        reference2.child(s).setValue(s);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                } else if (!dataSnapshot.exists()) {
                                                    final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myList.get(finalI)).child("group");
                                                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                            if (dataSnapshot1.getValue() == null) {
                                                                grpList.add(sb.toString());
                                                                for (String s : grpList) {
                                                                    reference2.child(s).setValue(s);
                                                                }
                                                            } else {
                                                                grpList.add(sb.toString());
                                                                for (String s : grpList) {
                                                                    reference2.child(s).setValue(s);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
//                                                }
//                                            }
//                                        }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist");
                                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myList.get(finalI));
                                                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                                                Log.d("GroupChat", d1.getValue() + "//");
                                                                final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(dataSnapshot.getKey()).child("group");
                                                                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                                        if (dataSnapshot1.getValue() == null) {
                                                                            grpList.add(sb.toString());
                                                                            for (String s : grpList) {
                                                                                reference2.child(s).setValue(s);
                                                                            }
                                                                        } else {
                                                                            grpList.add(sb.toString());
                                                                            for (String s : grpList) {
                                                                                reference2.child(s).setValue(s);
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        } else if (!dataSnapshot.exists()) {
                                                            final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myList.get(finalI)).child("group");
                                                            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                                    if (dataSnapshot1.getValue() == null) {
                                                                        grpList.add(sb.toString());
                                                                        for (String s : grpList) {
                                                                            reference2.child(s).setValue(s);
                                                                        }
                                                                    } else {
                                                                        grpList.add(sb.toString());
                                                                        for (String s : grpList) {
                                                                            reference2.child(s).setValue(s);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
//                                                }
//                                            }
//                                        }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new ListChatFragment()).addToBackStack(null).commit();

                        pd.dismiss();
                    } else

                    {
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
        } else

        {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
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

    private void initViews(View view) {
        txt_name = view.findViewById(R.id.txt_name);
        add_image = view.findViewById(R.id.add_image);
        txtaddPhoto = view.findViewById(R.id.txtaddPhoto);
        btnCreate = view.findViewById(R.id.btnCreate);
        imgUpload = view.findViewById(R.id.imgUpload);
    }
}
