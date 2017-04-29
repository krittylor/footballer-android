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
public class FieldsFragment extends Fragment {

    public static final String TAG = "FieldsFragment";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private int mRecyclerViewPosition = 0;
    private int userType = 0x01;
    private OnFieldSelectedListener mListener;
    public String locationFilter = "";
    public String userFilter = "";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<FieldViewHolder> mAdapter;

    public FieldsFragment() {
        // Required empty public constructor

    }

    public static FieldsFragment newInstance(int userType, String locationFilter, String userFilter) {
        FieldsFragment fragment = new FieldsFragment();
        Bundle args = new Bundle();
        args.putInt("userType", userType);
        args.putString("locationFilter", locationFilter);
        args.putString("userFilter", userFilter);
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
        userType = getArguments().getInt("userType");
        locationFilter = getArguments().getString("locationFilter");
        userFilter = getArguments().getString("userFilter");
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_fields, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fields_recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
            // TODO: RecyclerView only restores position properly for some tabs.
        }

        reload();
    }

    private FirebaseRecyclerAdapter<Field, FieldViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Field, FieldViewHolder>(
                Field.class, R.layout.field_item, FieldViewHolder.class, query) {
            @Override
            public void populateViewHolder(final FieldViewHolder fieldViewHolder,
                                           final Field post, final int position) {
                setupField(fieldViewHolder, post, position, null);
            }

            @Override
            public void onViewRecycled(FieldViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupField(final FieldViewHolder fieldViewHolder, final Field field, final int position, final String inFieldKey) {
        if(field.photoUrls != null)fieldViewHolder.setPhoto(field.photoUrls.get(0));
        fieldViewHolder.setInfos(field.fieldName, field.cityName, field.price);
        final String fieldKey;
        if (mAdapter instanceof FirebaseRecyclerAdapter) {
            fieldKey = ((FirebaseRecyclerAdapter) mAdapter).getRef(position).getKey();
        } else {
            fieldKey = inFieldKey;
        }

        fieldViewHolder.setFieldClickListener(new FieldViewHolder.FieldClickListener() {
            @Override
            public void showFieldDetail() {
                Log.d(TAG, "field position: " + position);
                mListener.onFieldSelected(fieldKey);
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
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     */
    public interface OnFieldSelectedListener {
        void onFieldSelected(String fieldKey);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFieldSelectedListener) {
            mListener = (OnFieldSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void reload(){
        Query allFieldsQuery = FirebaseUtil.getFieldsRef();
        if(userType == FieldsActivity.PLAYER_MODE && !locationFilter.equalsIgnoreCase(""))
            allFieldsQuery = FirebaseUtil.getFieldsRef().orderByChild("locationName").equalTo(locationFilter);
        if(userType == FieldsActivity.OWNER_MODE && userFilter != "")
            allFieldsQuery = FirebaseUtil.getFieldsRef().orderByChild("fieldOwner").equalTo(userFilter);
        mAdapter = getFirebaseRecyclerAdapter(allFieldsQuery);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
