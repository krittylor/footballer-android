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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.footballer.Models.Field;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Shows a list of posts.
 */
public class FieldsLocationFilterFragment extends Fragment {

    public static final String TAG = "FieldsLocationFilter";
    private String currentLocation = "";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<FieldLocationViewHolder> mAdapter;
    FieldsLocationFilterFragment.OnLocationFilterSelected mListener;
    public FieldsLocationFilterFragment() {
        // Required empty public constructor
    }

    public static FieldsLocationFilterFragment newInstance() {
        FieldsLocationFilterFragment fragment = new FieldsLocationFilterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fields_location_filter, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fields_location_filter_recycler_view);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Query allFieldsQuery = FirebaseUtil.getFieldsRef();
        mAdapter = getFirebaseRecyclerAdapter(allFieldsQuery);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        reload();
    }

    private FirebaseRecyclerAdapter<Field, FieldLocationViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Field, FieldLocationViewHolder>(
                Field.class, R.layout.field_location_item, FieldLocationViewHolder.class, query) {
            @Override
            public void populateViewHolder(final FieldLocationViewHolder FieldLocationViewHolder,
                                           final Field field, final int position) {
                setupField(FieldLocationViewHolder, field, position, null);
            }

            @Override
            public void onViewRecycled(FieldLocationViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupField(final FieldLocationViewHolder FieldLocationViewHolder, final Field field, final int position, final String inFieldKey) {
        FieldLocationViewHolder.setInfo(field.fieldName, currentLocation.equalsIgnoreCase(field.locationName));
        final String fieldKey;
        if (mAdapter instanceof FirebaseRecyclerAdapter) {
            fieldKey = field.locationName;
        } else {
            fieldKey = inFieldKey;
        }
        final int pos = position;
        FieldLocationViewHolder.setFieldClickListener(new FieldLocationViewHolder.FieldLocationClickListener() {
            @Override
            public void showFieldDetail() {
                currentLocation = fieldKey;
                mListener.onFilter(currentLocation);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    private int getRecyclerViewScrollPosition() {
        int scrollPosition = 0;
        // TODO: Is null check necessary?
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        return scrollPosition;
    }

    public interface OnLocationFilterSelected{
        public void onFilter(String location);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FieldsLocationFilterFragment.OnLocationFilterSelected) {
            mListener = (FieldsLocationFilterFragment.OnLocationFilterSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setCurrentLocation(String currentLocation){
        this.currentLocation = currentLocation;
        reload();
    }

    public void reload(){
        mAdapter.notifyDataSetChanged();
    }
}
