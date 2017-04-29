package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.footballer.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends BaseActivity{
    private static final String TAG = "ProfileEditActivity";

    private TextView userNameView;
    private TextView newPasswordView;
    private TextView confirmPasswordView;
    private TextView birthdayView;
    private TextView phoneNumberView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userNameView = (TextView)findViewById(R.id.edit_user_name);
        newPasswordView = (TextView)findViewById(R.id.edit_new_password);
        confirmPasswordView = (TextView)findViewById(R.id.edit_confirm_password);
        birthdayView = (TextView)findViewById(R.id.edit_birthday);
        phoneNumberView = (TextView)findViewById(R.id.edit_phonenumber);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
        }
        if(id == R.id.save_profile){
            boolean updatePassword = true;
            boolean update = true;
            if(newPasswordView.getText().toString().equalsIgnoreCase("") && confirmPasswordView.getText().toString().equalsIgnoreCase("")){
                updatePassword = false;
            }
            if(!newPasswordView.getText().toString().equalsIgnoreCase(confirmPasswordView.getText().toString())){
                Toast.makeText(ProfileEditActivity.this, "Confirm password mismatch", Toast.LENGTH_LONG).show();
                update = false;
            }
            if(update == true){
                Map<String, Object> updateValues = new HashMap<String, Object>();
                String userId = FirebaseUtil.getCurrentUserId();
                updateValues.put("/users/" + userId + "/userName", userNameView.getText().toString());
                updateValues.put("/users/" + userId + "/birthday", birthdayView.getText().toString());
                updateValues.put("/users/" + userId + "/phoneNumber", phoneNumberView.getText().toString());
                if(updatePassword) FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPasswordView.getText().toString());
                FirebaseUtil.getBaseRef().updateChildren(updateValues, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(ProfileEditActivity.this, "An error occured while saving data", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
}