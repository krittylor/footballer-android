package com.example.admin.footballer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.admin.footballer.Models.Field;
import com.example.admin.footballer.SwipeSelector.DateTimePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FieldDetailPlayerActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private Spinner durationDropDown;
    private double latitude;
    private double longitude;
    private TextView priceView;
    private TextView locationNameView;
    private int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail_player);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showProgressDialog("Loading");
        priceView = (TextView)findViewById(R.id.price_text);
        locationNameView = (TextView)findViewById(R.id.field_location_name);
        durationDropDown = (Spinner)findViewById(R.id.duration_dropdown);
        durationDropDown.setOnItemSelectedListener(new ItemSelectedListener());
        Button viewMapButton = (Button)findViewById(R.id.view_map_button);
        viewMapButton.setOnClickListener(onMapClickListener);
        Button selectButton = (Button)findViewById(R.id.select_date_time_button);
        selectButton.setOnClickListener(onOrderClickListener);

        final Activity context = this;
        FirebaseUtil.getFieldsRef().child(FieldsActivity.fieldId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dismissProgressDialog();
                if(dataSnapshot.exists()){
                    Field field = dataSnapshot.getValue(Field.class);
                    longitude = field.longitude;
                    latitude = field.latitude;
                    price = field.price;
                    locationNameView.setText(field.locationName);
                    priceView.setText("Price: " + String.valueOf(price ) + " SAR");
                    getSupportActionBar().setTitle(field.fieldName);
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
                dismissProgressDialog();
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

    private View.OnClickListener onOrderClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            DateTimePicker dateTimePicker = (DateTimePicker)findViewById(R.id.date_time_picker);
            int year = dateTimePicker.getYear();
            int month = dateTimePicker.getMonth();
            int day = dateTimePicker.getDay();
            int time = dateTimePicker.getTime();
            int duration = durationDropDown.getSelectedItemPosition();
            String fieldId = FieldsActivity.fieldId;
            Intent intent = new Intent(FieldDetailPlayerActivity.this, OrderActivity.class);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", day);
            intent.putExtra("time", time);
            intent.putExtra("duration", duration);
            intent.putExtra("fieldId", fieldId);
            startActivityForResult(intent, 1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
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

    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        //get strings of first item
        String firstItem = String.valueOf(durationDropDown.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(durationDropDown.getSelectedItem()))) {
                // ToDo when first item is selected
            } else {
//                Toast.makeText(parent.getContext(),
//                        "You have selected : " + parent.getItemAtPosition(pos).toString(),
//                        Toast.LENGTH_LONG).show();

                // Todo when item is selected by the user
            }
            priceView.setText("Price: " + String.valueOf((pos + 1) * price ) + " SAR");
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }
    }

    private View.OnClickListener onMapClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FieldDetailPlayerActivity.this, MapsActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            startActivity(intent);
        }
    };
}