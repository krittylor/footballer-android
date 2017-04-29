package com.example.admin.footballer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.admin.footballer.Models.Field;
import com.example.admin.footballer.SwipeSelector.SwipeItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderConfirmActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    private static final String TAG = "OrderConfirmationActivity";
    private SliderLayout mDemoSlider;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        context = this;
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button gotoHomeButton = (Button)findViewById(R.id.confirm_to_home_button);
        gotoHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        int year = getIntent().getIntExtra("year", 0);
        int month = getIntent().getIntExtra("month", 0);
        int day = getIntent().getIntExtra("day", 0);
        int time = getIntent().getIntExtra("time", 0);
        int duration = getIntent().getIntExtra("duration", 0);
        int orderId = getIntent().getIntExtra("orderId", 0);

        //show Order Id
        TextView orderView = (TextView)findViewById(R.id.order_id);
        orderView.setText("Order Id: " + String.valueOf(orderId));

        //get the month string
        String[] monthArray = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String monthStr = monthArray[month - 1];

        //get the dayOfWeek from the current date
        Calendar dayTemp = new GregorianCalendar(year, month, day);
        int dayOfWeek = dayTemp.get(Calendar.DAY_OF_WEEK);
        List<String> dayOfWeeks = new ArrayList<String>();
        dayOfWeeks.add("Sun");
        dayOfWeeks.add("Mon");
        dayOfWeeks.add("Tue");
        dayOfWeeks.add("Wed");
        dayOfWeeks.add("Thu");
        dayOfWeeks.add("Fri");
        dayOfWeeks.add("Sat");
        String dayOfWeekString = dayOfWeeks.get(dayOfWeek - 1);
        TextView orderDateView = (TextView)findViewById(R.id.duration_date);
        orderDateView.setText(dayOfWeekString + " - " + monthStr + " " + String.valueOf(day));

        String firstSuffix = time > 12 ? "PM" : "AM";
        String timeStr = String.valueOf(time) + ":00 " + "PM";
        time = (time + 1) % 24;
        String secondSuffix = time > 12 ? "PM" : "AM";
        timeStr = timeStr + " - " + String.valueOf(time) + ":00 " + "PM";
        TextView durationTimeView = (TextView)findViewById(R.id.duration_time);
        durationTimeView.setText(timeStr);

        //get fieldId from the intent and set to the image slider
        String fieldId = getIntent().getStringExtra("fieldId");
        FirebaseUtil.getFieldsRef().child(fieldId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Field field = dataSnapshot.getValue(Field.class);
                    if(field.photoUrls != null){
                        for (String photoUrl : field.photoUrls) {
                            DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                            defaultSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                            defaultSliderView.image(photoUrl);
                            mDemoSlider.addSlider(defaultSliderView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        Button leftBtn = (Button)findViewById(R.id.leftArrowButton);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.movePrevPosition(true);
            }
        });
        Button rightBtn = (Button)findViewById(R.id.rightArrowButton);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDemoSlider.moveNextPosition(true);
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

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}