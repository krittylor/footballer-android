package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by Kevin on 2-Mar-17.
 */
public class ChooseUserTypeActivity extends Activity{


    public static int USER_OWNER = 0x01;
    public static int USER_PLAYER = 0x02;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_type_layout);

        Button registerBtn = (Button)findViewById(R.id.button_register_owner);

        registerBtn.setOnClickListener(ownerPlayerListener);
        Button backBtn = (Button)findViewById(R.id.button_register_player);
        backBtn.setOnClickListener(ownerPlayerListener);
    }
    private View.OnClickListener ownerPlayerListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChooseUserTypeActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            if(v.getId() == R.id.button_register_owner)
                intent.putExtra("userType", USER_OWNER);
            if(v.getId() == R.id.button_register_player)
                intent.putExtra("userType", USER_PLAYER);
            startActivity(intent);
            finish();
        }
    };
}