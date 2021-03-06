package com.opula.chatapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.luseen.spacenavigation.SpaceNavigationView;
import com.opula.chatapp.MainActivity;
import com.opula.chatapp.R;
import com.opula.chatapp.activity.LoginRegisterActivity;
import com.opula.chatapp.constant.AppGlobal;
import com.opula.chatapp.model.StarMessage;
import com.opula.chatapp.model.User;
import com.opula.chatapp.notifications.Token;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    LinearLayout imgBack;
    LinearLayout lin_close_account, lin_logout, lin_sync, lin_web, lin_star_message;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    ImageView image_profile;
    ImageView img_edit_profile;
    StorageReference storageReference;
    private StorageTask uploadTask;
    TextView txtName, txtMobile;
    Uri mImageUri = null;
    int GALLERY = 1, CAMERA = 2;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        MainActivity.hideFloatingActionButton();

        initViews(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("User_Profile");
        checkImage();

        img_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();

            }
        });

        lin_close_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCloseAccountDialog(getActivity());
            }
        });

        lin_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage("Syncing Contacts...");
                pd.show();

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "Contacts are updated...", Toast.LENGTH_SHORT).show();
                        pd.hide();
                    }
                }, 2500);
            }
        });

        lin_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = "https://shreemsanjeevani.com/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    getActivity().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    getActivity().startActivity(intent);
                }
            }
        });

        lin_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getActivity());
            }
        });

        lin_star_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getAllStarMessages();
                MainActivity.showpart2();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_mainactivity, new StarMessagesFragment()).addToBackStack(null).commit();

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());

                LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dailog_show_image, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setCancelable(true);

                final ImageView image = (ImageView) dialogView.findViewById(R.id.image);

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                image.setOnTouchListener(new ImageMatrixTouchHandler(dialogView.getContext()));

                String string = user.getImageURL();

                if (string.equals("default")) {
                    Toast.makeText(getActivity(), "No Image Found..!", Toast.LENGTH_SHORT).show();
                } else {
                    AppGlobal.showProgressDialog(getActivity());
                    Glide.with(getActivity()).load(string)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    AppGlobal.hideProgressDialog(getActivity());
                                    Toast.makeText(getActivity(), "No Image Found!" + model + "/" + e, Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    AppGlobal.hideProgressDialog(getActivity());
                                    alertDialog.show();
                                    return false;
                                }
                            })
                            .into(image);
                }
            }
        });

        return view;

    }



    private void checkImage() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    txtName.setText(user.getUsername());
                    txtMobile.setText(firebaseUser.getEmail());
                    if (user.getImageURL().equals("default")) {
                        image_profile.setImageResource(R.drawable.image_boy);
                    } else {
//                        Glide.with(getActivity())
//                                .load(user.getImageURL()).into(image_profile);
                        Picasso.get().load(user.getImageURL())
                                .fit()
                          //      .placeholder(R.drawable.progress_animation)
                                .into(image_profile);
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.child("imageURL").setValue("default");
        checkImage();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, CAMERA);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void initViews(View view) {
        image_profile = view.findViewById(R.id.profile_image);
        img_edit_profile = view.findViewById(R.id.img_edit_profile);
        lin_close_account = view.findViewById(R.id.lin_close_account);
        lin_sync = view.findViewById(R.id.lin_sync);
        lin_web = view.findViewById(R.id.lin_web);
        lin_star_message = view.findViewById(R.id.lin_star_message);
        imgBack = view.findViewById(R.id.imgBack);
        lin_logout = view.findViewById(R.id.lin_logout);
        txtName = view.findViewById(R.id.txtName);
        txtMobile = view.findViewById(R.id.txtMobile);
        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) getActivity().findViewById(R.id.space);
        spaceNavigationView.setVisibility(View.GONE);

    }

    public void showCloseAccountDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_close_account, null);
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
                closeAccount();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void showDialog(Context mContext) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setCustomTitle(View.inflate(mContext, R.layout.alert_back, null));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button btn_no = (Button) dialogView.findViewById(R.id.btn_no);
        Button btn_yes = (Button) dialogView.findViewById(R.id.btn_yes);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Logout Successfully!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getActivity(), LoginRegisterActivity.class);
                startActivity(it);
                getActivity().finish();
            }
        });
        alertDialog.show();
    }

    private void closeAccount() {

        deleteUser();
        deleteToken();
        deleteUserKey();
        deleteChatList();
    }

    private void deleteUserKey() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (firebaseUser.getUid().equalsIgnoreCase(child.getKey())) {
                            Log.d("CLose_Account_if", "/" + child.getKey());
                            child.getRef().removeValue();
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

    private void deleteToken() {
        reference = FirebaseDatabase.getInstance().getReference("Tokens");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (firebaseUser.getUid().equalsIgnoreCase(child.getKey())) {
                            Log.d("CLose_Account_if", "/" + child.getKey());
                            child.getRef().removeValue();
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

    private void deleteChatList() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (firebaseUser.getUid().equalsIgnoreCase(child.getKey())) {
                            Log.d("CLose_Account_if", "/" + child.getKey());
                            child.getRef().removeValue();
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

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AppGlobal.showProgressDialog(getActivity());
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().signOut();
                    AppGlobal.hideProgressDialog(getActivity());
                    Intent it = new Intent(getActivity(), LoginRegisterActivity.class);
                    startActivity(it);
                    getActivity().finish();
                    Toast.makeText(getActivity(), "Your account is closed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        pd.setCancelable(false);

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

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
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
                image_profile.setImageURI(mImageUri);

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
            image_profile.setImageBitmap(bitmap);
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
}
