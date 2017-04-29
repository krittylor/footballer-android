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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FieldViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private FieldClickListener mListener;
    public DatabaseReference mFieldRef;
    public ValueEventListener mFieldListener;

    private ImageView mPhotoView;
    private TextView mFieldNameView;
    private TextView mLocationNameView;
    private TextView mPriceView;
    public String mFieldKey;

    public FieldViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mListener.showFieldDetail();
            }
        });
        mPhotoView = (ImageView) itemView.findViewById(R.id.fieldImage);
        mFieldNameView = (TextView) itemView.findViewById(R.id.fieldName);
        mLocationNameView = (TextView) itemView.findViewById(R.id.fieldLocation);
        mPriceView = (TextView) itemView.findViewById(R.id.fieldPrice);
    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, mPhotoView);
    }

    public void setInfos(String fieldName, String cityName, int price){
        mFieldNameView.setText(fieldName);
        mLocationNameView.setText(cityName);
        mPriceView.setText(String.valueOf(price));
    }

    public void setFieldClickListener(FieldClickListener listener) {
        mListener = listener;
    }

    public interface FieldClickListener {
        void showFieldDetail();
    }
}