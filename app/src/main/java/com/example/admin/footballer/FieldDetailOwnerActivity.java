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

public class FieldDetailOwnerActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private double latitude;
    private double longitude;
    private TextView priceView;
    private TextView locationNameView;
    private int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail_owner);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showProgressDialog("Loading");
        priceView = (TextView)findViewById(R.id.price_text);
        locationNameView = (TextView)findViewById(R.id.field_location_name);
        Button viewMapButton = (Button)findViewById(R.id.view_map_button);
        viewMapButton.setOnClickListener(onClickListener);
        Button editFieldButton = (Button)findViewById(R.id.field_edit_button);
        editFieldButton.setOnClickListener(onClickListener);
        Button scheduleButton = (Button)findViewById(R.id.field_schedule_button);
        scheduleButton.setOnClickListener(onClickListener);
        Button availabilityButton = (Button)findViewById(R.id.field_availability_button);
        availabilityButton.setOnClickListener(onClickListener);
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
                    priceView.setText("Price: " + String.valueOf(price ) + " SAR");
                    locationNameView.setText(field.locationName);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                finish();
            }
        }
        if(requestCode == 3){
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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()){
                case R.id.view_map_button:
                    intent = new Intent(FieldDetailOwnerActivity.this, MapsActivity.class);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    startActivityForResult(intent, 2);
                    break;
                case R.id.field_edit_button:
                    intent = new Intent(FieldDetailOwnerActivity.this, EditFieldActivity.class);
                    startActivityForResult(intent, 3);
                    break;
                case R.id.field_schedule_button:
                    intent = new Intent(FieldDetailOwnerActivity.this, ScheduleActivity.class);
                    startActivity(intent);
                    break;
                case R.id.field_availability_button:
                    intent = new Intent(FieldDetailOwnerActivity.this, AvailabilityActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}