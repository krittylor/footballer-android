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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FieldsActivity extends BaseActivity implements FieldsFragment.OnFieldSelectedListener
        ,NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "FieldsActivity";
    public static String fieldId = "";
    private FloatingActionButton mFab;
    public FieldsPagerAdapter adapter;
    public static String userId;
    public static int userType;
    public static String locationName;
    public static final int PLAYER_MODE = 0x02;
    public static final int OWNER_MODE = 0x01;
    public String locationFilter = "";
    FieldsFragment fieldsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userType = getIntent().getIntExtra("userType", PLAYER_MODE);
        userId = FirebaseUtil.getCurrentUserId();
        locationFilter = "";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.slidemenu);
        toolbar.setLogo(R.drawable.footballer);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fields_view_pager);
        adapter = new FieldsPagerAdapter(getSupportFragmentManager());
        fieldsFragment = FieldsFragment.newInstance(userType, locationFilter, userId);
        adapter.addFragment(fieldsFragment, "MURAL");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),   R.drawable.slidemenu, getTheme());
        //toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(drawable);
        drawer.setDrawerListener(toggle);
        toggle.setHomeAsUpIndicator(R.drawable.slidemenu);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(FieldsActivity.userType == PLAYER_MODE)
            getMenuInflater().inflate(R.menu.menu_fields_player_mode, menu);
        else
            getMenuInflater().inflate(R.menu.menu_fields_owner_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_filter){
            Intent intent = new Intent(FieldsActivity.this, FieldsLocationFilterActivity.class);

            startActivityForResult(intent, 2);
        }

        if(id == R.id.action_add_field){
            Intent intent = new Intent(FieldsActivity.this, AddFieldActivity.class);
            startActivityForResult(intent, 3);
        }

        return super.onOptionsItemSelected(item);
    }

    class FieldsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public FieldsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onFieldSelected(String fieldKey){
        //Toast.makeText(FieldsActivity.this, fieldKey, Toast.LENGTH_LONG).show();
        FieldsActivity.fieldId = fieldKey;
        if(FieldsActivity.userType == FieldsActivity.PLAYER_MODE) {
            Intent intent = new Intent(FieldsActivity.this, FieldDetailPlayerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(FieldsActivity.this, FieldDetailOwnerActivity.class);
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            // Handle the action
            Intent intent = new Intent(FieldsActivity.this, ProfileActivity.class);
            startActivityForResult(intent, 1);

        } else if (id == R.id.aboutus) {
            Intent intent = new Intent(FieldsActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                finish();
            }
        }

        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                locationFilter = data.getStringExtra("locationFilter");
                fieldsFragment.locationFilter = locationFilter;
                fieldsFragment.reload();
            }
        }

        if(requestCode == 3){
            if(resultCode == Activity.RESULT_OK){
                //fieldsFragment.reload();
            }
        }
    }
}
