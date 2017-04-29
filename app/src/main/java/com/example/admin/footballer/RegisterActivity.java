package com.example.admin.footballer;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

/**
 * Created by Kevin on 2-Mar-17.
 */
public class RegisterActivity extends BaseActivity{

    private FirebaseAuth mAuth;
    private EditText editName, editEmail, editPassword;
    private final String TAG = "Register Activity";
    private int userType = ChooseUserTypeActivity.USER_PLAYER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

        Button registerBtn = (Button)findViewById(R.id.button_register_user);
        registerBtn.setOnClickListener(onClickListener);
        Button backBtn = (Button)findViewById(R.id.button_back_register);
        backBtn.setOnClickListener(onClickListener);
        editName = (EditText)findViewById(R.id.editName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        TextView textToSignIn = (TextView)findViewById(R.id.link_to_sign_in);
        textToSignIn.setOnClickListener(onClickListener);
        userType = getIntent().getIntExtra("userType", ChooseUserTypeActivity.USER_PLAYER);
        mAuth = FirebaseAuth.getInstance();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch(viewId){
                case R.id.button_register_user:
                    String email = editEmail.getText().toString();
                    String password = editPassword.getText().toString();

                    final String userName = editName.getText().toString();
                    if(email.equalsIgnoreCase("") || password.equalsIgnoreCase("") || userName.equalsIgnoreCase("")){
                        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                        alertDialog.setTitle("Footballer");
                        alertDialog.setMessage("Please input your information.");
                        alertDialog.setIcon(R.drawable.ball);

                        alertDialog.show();
                        return;
                    }
                    showProgressDialog("Registering...");
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    dismissProgressDialog();
                                    String userId = authResult.getUser().getUid();
                                    Intent fieldIntent = new Intent(RegisterActivity.this, FieldsActivity.class);
                                    FirebaseUtil.getUsersRef().child(userId).child("userName").setValue(userName);
                                    FirebaseUtil.getUsersRef().child(userId).child("userType").setValue(userType);
                                    fieldIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                                    finish();
                                    fieldIntent.putExtra("userType", userType);
                                    startActivity(fieldIntent);
                                }
                            }).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissProgressDialog();
                            Toast.makeText(RegisterActivity.this, "Unable to register.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
                        }
                    });
                    break;
                case R.id.button_back_register:
                    finish();
                    break;
                case R.id.link_to_sign_in:
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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