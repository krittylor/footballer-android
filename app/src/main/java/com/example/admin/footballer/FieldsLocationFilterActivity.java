/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.admin.footballer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.admin.footballer.Models.Field;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FieldsLocationFilterActivity extends BaseActivity{
    private static final String TAG = "FieldsLocationFilterActivity";
    public static String userId;
    public static String locationName;
    public String currentFilter;
    ListView locationListView;
    ArrayAdapter<String> adapter;
    List<String> locations = new ArrayList<String>(){};
    int currentIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_filter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.footballer);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button saveFilterButton = (Button)findViewById(R.id.save_filter);
        saveFilterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(currentIndex == -1) {
                    Toast.makeText(FieldsLocationFilterActivity.this, "Select Location First", Toast.LENGTH_LONG).show();
                    return;
                }
                currentFilter = locations.get(currentIndex);
                intent.putExtra("locationFilter", currentFilter);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button clearFilterButton = (Button)findViewById(R.id.clear_filter);
        clearFilterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("locationFilter", "");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        locationListView = (ListView)findViewById(R.id.list_locations);
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                currentIndex = position;
            }
        });
        FirebaseUtil.getFieldsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot fieldSnapShot : dataSnapshot.getChildren()) {
                    Field field = fieldSnapShot.getValue(Field.class);
                    //check if location is already added
                    boolean addFlag = true;
                    for (String location : locations) {
                        if(location.equalsIgnoreCase(field.locationName))
                            addFlag = false;
                    }

                    if(addFlag)
                        locations.add(field.locationName);
                }
                String[] locationStrList = new String[locations.size()];
                locationStrList = locations.toArray(locationStrList);

                adapter = new ArrayAdapter<String>(FieldsLocationFilterActivity.this, android.R.layout.simple_list_item_single_choice,
                        locations);
                locationListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_field_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_close){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
