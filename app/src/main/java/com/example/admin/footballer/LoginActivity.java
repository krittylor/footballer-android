package com.example.admin.footballer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.footballer.Models.User;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kevin on 2-Mar-17.
 */
public class LoginActivity extends BaseActivity{

    private FirebaseAuth mAuth;
    private EditText editEmail, editPassword;
    private final String TAG = "Register Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = (Button)findViewById(R.id.buttonLoginUser);
        loginBtn.setOnClickListener(onClickListener);
        Button backBtn = (Button)findViewById(R.id.buttonBackLogin);
        backBtn.setOnClickListener(onClickListener);
        editEmail = (EditText)findViewById(R.id.editLoginEmail);
        editPassword = (EditText)findViewById(R.id.editLoginPassword);
        TextView textToSignUp = (TextView)findViewById(R.id.linkToSignUp);
        textToSignUp.setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            final View view = v;
            switch(viewId){
                case R.id.buttonLoginUser:
                    String email = editEmail.getText().toString();
                    String password = editPassword.getText().toString();
                    if(email.equalsIgnoreCase("") || password.equalsIgnoreCase("")){
                        //Toast.makeText(v.getContext(), "Please enter your information", Toast.LENGTH_LONG).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("Footballer");
                        alertDialog.setMessage("Please input your information.");
                        alertDialog.setIcon(R.drawable.ball);

                        alertDialog.show();
                        return;
                    }
                    showProgressDialog("Logging in");
                    //mAuth.signInWithEmailAndPassword("dreammover88@outlook.com", "dream1")
                    mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUtil.getCurrentUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        User user = dataSnapshot.getValue(User.class);
                                        dismissProgressDialog();
                                        Intent fieldIntent = new Intent(LoginActivity.this, FieldsActivity.class);
                                        fieldIntent.putExtra("userType", user.getUserType());
                                        startActivity(fieldIntent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    dismissProgressDialog();
                                }
                            });

                        }
                    }).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissProgressDialog();
                            Toast.makeText(LoginActivity.this, "Unable to sign in.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
                        }
                    });
                    break;
                case R.id.buttonBackLogin:
                    finish();
                    break;
                case R.id.linkToSignUp:
                    Intent intent = new Intent(LoginActivity.this, ChooseUserTypeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
}