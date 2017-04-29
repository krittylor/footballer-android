package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.footballer.Models.FieldSchedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ReservationDetailActivity extends BaseActivity{
    private static final String TAG = "ReservationDetailActivity";
    private String datetime;
    private int time;
    private TextView orderView;
    private TextView userNameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        datetime = getIntent().getStringExtra("datetime");
        time = getIntent().getIntExtra("time", 6);
        orderView = (TextView)findViewById(R.id.reservation_order_id_view);
        userNameView = (TextView)findViewById(R.id.reservation_user_name_view);
        Button doneButton = (Button)findViewById(R.id.reservation_done);
        doneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        FirebaseUtil.getFieldScheduleRef().child(FieldsActivity.fieldId).child(datetime).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    FieldSchedule schedule = dataSnapshot.getValue(FieldSchedule.class);
                    if(schedule.type == FieldSchedule.NOT_AVAILABLE){
                        orderView.setText("Not available");
                        userNameView.setText("");
                    } else {
                        orderView.setText("Order ID: " + schedule.orderId);
                        userNameView.setText("User Name: "  + schedule.userName);
                    }
                } else{
                    orderView.setText("Not yet ordered");
                    userNameView.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservation_details, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}