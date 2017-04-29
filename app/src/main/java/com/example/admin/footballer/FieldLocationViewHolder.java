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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FieldLocationViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private FieldLocationClickListener mListener;
    public ImageView imageCheck;
    public TextView locationTextView;
    public FieldLocationViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        imageCheck = (ImageView)mView.findViewById(R.id.image_check);
        locationTextView = (TextView)mView.findViewById(R.id.location_name);

        mView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mListener.showFieldDetail();
            }
        });
    }

    public void setInfo(String locationName, boolean checked){
        locationTextView.setText(locationName);
        if(checked)
            imageCheck.setVisibility(View.VISIBLE);
        else
            imageCheck.setVisibility(View.INVISIBLE);
    }

    public void setFieldClickListener(FieldLocationClickListener listener) {
        mListener = listener;
    }

    public interface FieldLocationClickListener {
        void showFieldDetail();
    }
}