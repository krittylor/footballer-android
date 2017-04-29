package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.footballer.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.UUID;

public class ProfileActivity extends BaseActivity implements IPickResult{
    private static final String TAG = "ProfileActivity";

    private TextView userNameView;
    private TextView emailView;
    private TextView birthdayView;
    private TextView phoneNumberView;
    private ImageView avatarView;
    private ProfileActivity self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userNameView = (TextView)findViewById(R.id.profile_username);
        emailView = (TextView)findViewById(R.id.profile_email);
        birthdayView = (TextView)findViewById(R.id.profile_birthday);
        phoneNumberView = (TextView)findViewById(R.id.profile_mobile);
        avatarView = (ImageView)findViewById(R.id.profile_avatar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();

        Button logoutButton = (Button)findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
        self = this;
        avatarView.setOnClickListener(onAvatarClickListener);
    }

    protected void onImageViewClick() {
        PickSetup setup = new PickSetup();

        PickImageDialog.build(setup).show(this);
    }

    @Override
    public void onPickResult(final PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageReference = storageRef.child(UUID.randomUUID().toString());

            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            UploadTask uploadTask = imageReference.putFile(r.getUri());
            showProgressDialog("Uploading photo");
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    dismissProgressDialog();
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    FirebaseUtil.getCurrentUserRef().child("photoUrl").setValue(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    dismissProgressDialog();
                    // Handle unsuccessful uploads
                    Toast.makeText(ProfileActivity.this, "Uploading avatar failed", Toast.LENGTH_SHORT).show();
                }
            });
            //If you want the Bitmap.
            avatarView.setImageBitmap(r.getBitmap());

            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private View.OnClickListener onAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            onImageViewClick();
        }
    };

    private void loadData(){
        FirebaseUtil.getUsersRef().child(FirebaseUtil.getCurrentUserId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            User user = dataSnapshot.getValue(User.class);
                            userNameView.setText(user.getUserName());
                            emailView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            birthdayView.setText(user.getBirthday());
                            phoneNumberView.setText(user.getPhoneNumber());
                            if(user.getPhotoUrl() != null && user.getPhotoUrl() != ""){
                                GlideUtil.loadImage(user.getPhotoUrl(), avatarView);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        if(id == R.id.edit_profile){
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                loadData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}