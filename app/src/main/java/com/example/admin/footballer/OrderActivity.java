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
import android.widget.Toast;

import com.example.admin.footballer.Models.Field;
import com.example.admin.footballer.Models.FieldSchedule;
import com.example.admin.footballer.Models.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends BaseActivity{
    private static final String TAG = "OrderActivity";

    private TextView cardHolderName;
    private TextView cardNumber;
    private TextView cardType;
    private TextView expirationDate;
    private TextView cvc;
    private TextView orderDuration;
    private TextView orderAmount;
    private int year;
    private int month;
    private int day;
    private int time;
    private int duration;
    private String fieldId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        cardHolderName = (TextView)findViewById(R.id.card_holder_name);
        cardNumber = (TextView)findViewById(R.id.card_number);
        cardType = (TextView)findViewById(R.id.card_type);
        expirationDate = (TextView)findViewById(R.id.expiration_date);
        cvc = (TextView)findViewById(R.id.card_cvc);
        orderDuration = (TextView)findViewById(R.id.order_duration);
        orderAmount = (TextView)findViewById(R.id.order_amount);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button)findViewById(R.id.order_pay_button);
        button.setOnClickListener(onPayClickListener);

        year = getIntent().getIntExtra("year", 0);
        month = getIntent().getIntExtra("month", 0);
        day = getIntent().getIntExtra("day", 0);
        time = getIntent().getIntExtra("time", 0);
        duration = getIntent().getIntExtra("duration", 0);
        fieldId = getIntent().getStringExtra("fieldId");
        orderDuration.setText("Duration: " + (duration == 0 ? "60 minutes" : "120 minutes"));
        FirebaseUtil.getFieldsRef().child(fieldId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Field field = dataSnapshot.getValue(Field.class);
                    orderAmount.setText("Amount: " + String.valueOf(field.price * (duration + 1)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_field_order, menu);
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
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener onPayClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            FirebaseUtil.getFieldsRef().child(fieldId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Field field = dataSnapshot.getValue(Field.class);
                        int price = field.price;
                        int amount = price * (duration + 1);
                        String userId = FirebaseUtil.getCurrentUserId();
                        String datetime = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) +
                                "-" + String.valueOf(time);
                        final Order order = new Order(userId,
                                fieldId,
                                amount,
                                cardHolderName.getText().toString(),
                                cardNumber.getText().toString(),
                                cardType.getText().toString(),
                                cvc.getText().toString(),
                                datetime,
                                duration,
                                expirationDate.getText().toString()
                                );
                        showProgressDialog("Ordering");
                        FirebaseUtil.getBaseRef().child("orderNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final int orderNumber = dataSnapshot.getValue(Integer.class);

                                    FirebaseUtil.getCurrentUserRef().child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {
                                                final String userName = dataSnapshot.getValue(String.class);
                                                //check the availability of the selected time
                                                final String datetime = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(time);
                                                final String datetime1 = String.valueOf(year)+ "-" + String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(time + 1);
                                                FirebaseUtil.getFieldScheduleRef().child(fieldId).child(datetime).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){
                                                            Toast.makeText(OrderActivity.this, "Already ordered or unavailable", Toast.LENGTH_LONG).show();
                                                            dismissProgressDialog();
                                                        } else {

                                                            Map<String, Object> updateValues = new HashMap<String, Object>();
                                                            updateValues.put("/orders/" + String.valueOf(orderNumber), order.toMap());
                                                            updateValues.put("/orderNumber", orderNumber + 1);

                                                            updateValues.put("/field-schedule/" + fieldId + "/" + datetime + "/type", FieldSchedule.AVAILABLE);
                                                            updateValues.put("/field-schedule/" + fieldId + "/" + datetime + "/orderId", String.valueOf(orderNumber));
                                                            updateValues.put("/field-schedule/" + fieldId + "/" + datetime + "/fieldId", fieldId);
                                                            updateValues.put("/field-schedule/" + fieldId + "/" + datetime + "/userName", userName);

                                                            if(duration == 1){
                                                                updateValues.put("/field-schedule/" + fieldId + "/" + datetime1 + "/type", FieldSchedule.AVAILABLE);
                                                                updateValues.put("/field-schedule/" + fieldId + "/" + datetime1 + "/orderId", String.valueOf(orderNumber));
                                                                updateValues.put("/field-schedule/" + fieldId + "/" + datetime1 + "/fieldId", fieldId);
                                                                updateValues.put("/field-schedule/" + fieldId + "/" + datetime1 + "/userName", userName);
                                                            }

                                                            FirebaseUtil.getBaseRef().updateChildren(updateValues, new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                    dismissProgressDialog();
                                                                    if (databaseError == null) {

                                                                        Intent intent = new Intent(OrderActivity.this, OrderConfirmActivity.class);
                                                                        intent.putExtra("orderId", orderNumber);
                                                                        intent.putExtra("year", year);
                                                                        intent.putExtra("month", month);
                                                                        intent.putExtra("day", day);
                                                                        intent.putExtra("time", time);
                                                                        intent.putExtra("duration", duration);
                                                                        intent.putExtra("fieldId", fieldId);
                                                                        startActivityForResult(intent, 1);
                                                                    } else {
                                                                        Intent intent = new Intent();
                                                                        setResult(Activity.RESULT_OK, intent);
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        dismissProgressDialog();
                                                        Toast.makeText(OrderActivity.this, "An error occured while processing your order", Toast.LENGTH_LONG).show();
                                                    }
                                                });



                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            dismissProgressDialog();
                                            Toast.makeText(OrderActivity.this, "An error occured while processing your order", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                dismissProgressDialog();
                                Toast.makeText(OrderActivity.this, "An error occured while processing your order", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dismissProgressDialog();
                    Toast.makeText(OrderActivity.this, "An error occured while processing your order", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}