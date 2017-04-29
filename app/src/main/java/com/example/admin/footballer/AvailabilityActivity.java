package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin.footballer.Models.FieldSchedule;
import com.example.admin.footballer.SwipeSelector.DateTimePickerRed;

public class AvailabilityActivity extends BaseActivity{
    private static final String TAG = "AvailabilityActivity";
    DateTimePickerRed dateTimePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_availability);
        dateTimePicker = (DateTimePickerRed)findViewById(R.id.date_time_schedule);
        EditText description = (EditText)findViewById(R.id.editText);
        description.setKeyListener(null);
        Button availabilityButton = (Button)findViewById(R.id.field_availability_button);
        availabilityButton.setOnClickListener(onAvailabilityListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private View.OnClickListener onAvailabilityListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int year = dateTimePicker.getYear();
            int month = dateTimePicker.getMonth();
            int day = dateTimePicker.getDay();
            int time = dateTimePicker.getTime();
            String datetime = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(time);

            FieldSchedule schedule = new FieldSchedule(FieldSchedule.NOT_AVAILABLE, "", "", "");
            FirebaseUtil.getFieldScheduleRef().child(FieldsActivity.fieldId).child(datetime).setValue(schedule.toMap());
            finish();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
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