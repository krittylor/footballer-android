package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by Kevin on 2-Mar-17.
 */
public class JoinCommunityActivity extends Activity {

    public static int ACTION_LOGIN = 0x01;
    public static int ACTION_REGISTER = 0x02;
    public static int actionType = ACTION_LOGIN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_community);
        Button loginBtn = (Button) findViewById(R.id.button_login);
        Button registerBtn = (Button) findViewById(R.id.button_register);
        loginBtn.setOnClickListener(loginListener);
        registerBtn.setOnClickListener(registerListener);

    }
    private View.OnClickListener loginListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(JoinCommunityActivity.this, LoginActivity.class);
            startActivity(intent);
            JoinCommunityActivity.actionType = JoinCommunityActivity.ACTION_LOGIN;
        }
    };

    private View.OnClickListener registerListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(JoinCommunityActivity.this, ChooseUserTypeActivity.class);
            startActivity(intent);
            JoinCommunityActivity.actionType = JoinCommunityActivity.ACTION_REGISTER;
        }
    };
}