package com.example.admin.footballer.SwipeSelector;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.admin.footballer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by admin on 3/18/17.
 */

public class DateTimePicker extends LinearLayout {

    private Context mContext;
    private ViewPager monthPager;
    private ViewPager dayPager;
    private ViewPager timePager;
    private MonthAdapter monthAdapter;
    private DayAdapter dayAdapter;
    private TimeAdapter timeAdapter;
    private int year;
    private int month = 1;
    private int day = 1;
    private int time = 0;

    public DateTimePicker(Context context) {
        this(context, null);
    }

    public DateTimePicker(Context context, AttributeSet attrs){
        this(context, attrs, R.attr.layout);
    }

    public DateTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int selectedColor = Color.rgb(66, 165, 245);
        int deselectedColor = Color.rgb(0, 0, 0);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.datetime, this, true);
        monthPager = (ViewPager)findViewById(R.id.viewPager);
        Button leftBtn = (Button)findViewById(R.id.swipeLeftButton);
        Button rightBtn = (Button)findViewById(R.id.swipeRightButton);
        monthAdapter = new MonthAdapter.Builder()
                .viewPager(monthPager)
                .leftButton(leftBtn)
                .rightButton(rightBtn)
                .build();
        monthPager.setAdapter(monthAdapter);

        dayPager = (ViewPager)findViewById(R.id.dayPager);
        dayAdapter = new DayAdapter.Builder()
                .viewPager(dayPager)
                .selectedColor(selectedColor)
                .deselectedColor(deselectedColor)
                .build();
        dayPager.setAdapter(dayAdapter);

        timePager = (ViewPager)findViewById(R.id.timePager);

        timeAdapter = new TimeAdapter.Builder()
                .viewPager(timePager)
                .selectedColor(selectedColor)
                .deselectedColor(deselectedColor)
                .build();
        timePager.setAdapter(timeAdapter);

        year = GregorianCalendar.getInstance().get(Calendar.YEAR);
        month = 0;
        day = 0;
        time = 0;

        OnSwipeItemSelectedListener listener = new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                month = position;
                day = 0;
                time = 0;
                populateDays();
            }
        };

        monthAdapter.setOnItemSelectedListener(listener);
        populateMonths();
        populateDays();
        populateTimes();
    }

    private void populateDays(){
        //init day selector
        List<SwipeItem> dayItems = new ArrayList<SwipeItem>();
        List<String> dayOfWeeks = new ArrayList<String>();
        dayOfWeeks.add("Sun");
        dayOfWeeks.add("Mon");
        dayOfWeeks.add("Tue");
        dayOfWeeks.add("Wed");
        dayOfWeeks.add("Thu");
        dayOfWeeks.add("Fri");
        dayOfWeeks.add("Sat");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        int daysOfMonth = calendar.getActualMaximum(Calendar.DATE);
        for(int i = 1; i <= daysOfMonth; i++){
            Calendar dayTemp = new GregorianCalendar(year, month, i);
            int dayOfWeek = dayTemp.get(Calendar.DAY_OF_WEEK);
            dayItems.add(new SwipeItem(String.valueOf(i), dayOfWeeks.get(dayOfWeek - 1), String.valueOf(i)));
        }
        this.day = this.day < daysOfMonth ? this.day : daysOfMonth - 1;
        dayAdapter.currentIndex = this.day;
        dayAdapter.setItems(dayItems);
    }
    private void populateMonths() {

        //init month selector
        List<SwipeItem> pendingItems = new ArrayList<>();
        pendingItems.add(new SwipeItem("1", "title1", "January"));
        pendingItems.add(new SwipeItem("2", "title2", "February"));
        pendingItems.add(new SwipeItem("3", "title3", "March"));
        pendingItems.add(new SwipeItem("4", "title4", "April"));
        pendingItems.add(new SwipeItem("5", "title5", "May"));
        pendingItems.add(new SwipeItem("6", "title6", "June"));
        pendingItems.add(new SwipeItem("7", "title7", "July"));
        pendingItems.add(new SwipeItem("8", "title8", "August"));
        pendingItems.add(new SwipeItem("9", "title9", "September"));
        pendingItems.add(new SwipeItem("10", "title10", "October"));
        pendingItems.add(new SwipeItem("11", "title11", "November"));
        pendingItems.add(new SwipeItem("12", "title12", "December"));
        monthAdapter.setItems(pendingItems);
    }
    private void populateTimes(){
        //init time selector
        List<SwipeItem> timeItems = new ArrayList<SwipeItem>();
        for(int i = 5; i < 12; i++){
            timeItems.add(new SwipeItem(String.valueOf(i), "", String.valueOf(i) + ":00 PM"));
        }
        timeAdapter.currentIndex = this.time;
        timeAdapter.setItems(timeItems);
    }

    public int getYear(){
        return this.year;
    }

    public int getMonth(){
        return this.month + 1;
    }

    public int getDay(){
        this.day = dayAdapter.currentIndex;
        return this.day + 1;
    }
    public int getTime(){
        this.time = timeAdapter.currentIndex + 5;
        return this.time;
    }
}
