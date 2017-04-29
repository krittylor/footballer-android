package com.example.admin.footballer.SwipeSelector;

/*
 * SwipeSelector library for Android
 * Copyright (c) 2016 Iiro Krankka (http://github.com/roughike).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.footballer.R;

import java.util.List;

class MonthAdapter extends PagerAdapter implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION";
    private static final String TAG_CIRCLE = "TAG_CIRCLE";

    // For the left and right buttons when they're not visible
    private static final String TAG_HIDDEN = "TAG_HIDDEN";

    private final Context context;

    private final ViewPager viewPager;

    private final Button leftButton;
    private final Button rightButton;

    private OnSwipeItemSelectedListener onItemSelectedListener;
    private List<SwipeItem> items;
    private int currentPosition;
    private View currentView;
    private int currentIndex = -1;
    private Color selectedColor;
    private MonthAdapter(Builder builder) {
        context = builder.viewPager.getContext();

        viewPager = builder.viewPager;
        viewPager.addOnPageChangeListener(this);

        leftButton = builder.leftButton;

        rightButton = builder.rightButton;

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);

        leftButton.setTag(TAG_HIDDEN);
        leftButton.setClickable(false);

        selectedColor = builder.selectedColor;
        setAlpha(0.0f, leftButton);
    }

    /**
     * Protected methods used by SwipeSelector
     */
    void setOnItemSelectedListener(OnSwipeItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    void setItems(List<SwipeItem> items) {
        // If there are SwipeItems constructed using String resources
        // instead of Strings, loop through all of them and get the
        // Strings.
        this.items = items;
        currentPosition = 0;
        notifyDataSetChanged();
    }

    SwipeItem getSelectedItem() {
        return items.get(currentPosition);
    }

    void selectItemAt(int position, boolean animate) {
        if (position < 0 || position >= items.size()) {
            throw new IndexOutOfBoundsException("This SwipeSelector does " +
                    "not have an item at position " + position + ".");
        }

        viewPager.setCurrentItem(position, animate);
    }

    void selectItemWithValue(@NonNull String value, boolean animate) {
        boolean itemExists = false;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getValue().equals(value)) {
                viewPager.setCurrentItem(i, animate);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            throw new IllegalArgumentException("This SwipeSelector " +
                    "does not have an item with the given value " + value + ".");
        }
    }

    Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_CURRENT_POSITION, currentPosition);
        return bundle;
    }

    void onRestoreInstanceState(Bundle state) {
        viewPager.setCurrentItem(state.getInt(STATE_CURRENT_POSITION), false);
        notifyDataSetChanged();
    }

    /**
     * Override methods / listeners
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LinearLayout layout = (LinearLayout) View.inflate(context, R.layout.swipeselector_content_month_item, null);

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, LinearLayout.LayoutParams.WRAP_CONTENT);
        //layout.setLayoutParams(params);
        TextView description = (TextView) layout.findViewById(R.id.swipeselector_content_description);

        SwipeItem slideItem = items.get(position);

        if (slideItem.getDescription() == null) {
            description.setVisibility(View.GONE);
        } else {
            description.setVisibility(View.VISIBLE);
            description.setText(slideItem.getDescription());
        }

        layout.setPadding(5,
                5,
                5,
                5);

        container.addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void onPageSelected(int position) {
        if (getCount() == 0) return;

        handleLeftButtonVisibility(position);
        handleRightButtonVisibility(position);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(leftButton) && currentPosition >= 1) {
            viewPager.setCurrentItem(currentPosition - 1, true);
            onItemSelectedListener.onItemSelected(currentPosition - 1);
        } else if (v.equals(rightButton) && currentPosition <= getCount() - 1) {
            viewPager.setCurrentItem(currentPosition + 1, true);
            onItemSelectedListener.onItemSelected(currentPosition + 1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        onItemSelectedListener.onItemSelected(currentPosition);
    }

    @SuppressWarnings("deprecation")
    private void setTextAppearanceCompat(TextView textView, int appearanceRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(appearanceRes);
        } else {
            textView.setTextAppearance(textView.getContext(), appearanceRes);
        }
    }

    private int getGravity(int gravity) {
        if (gravity == -1)
            return -1;

        int realGravityValue;

        switch (gravity) {
            case 0:
                realGravityValue = Gravity.START;
                break;
            case 1:
                realGravityValue = Gravity.CENTER_HORIZONTAL;
                break;
            case 2:
                realGravityValue = Gravity.END;
                break;
            default:
                throw new IllegalArgumentException("Invalid value " +
                        "specified for swipe_descriptionGravity. " +
                        "Use \"left\", \"center\", \"right\" or leave " +
                        "blank for default.");
        }

        return realGravityValue;
    }

    private void handleLeftButtonVisibility(int position) {
        if (position < 1) {
            leftButton.setTag(TAG_HIDDEN);
            leftButton.setClickable(false);
            animate(0, leftButton);
        } else if (TAG_HIDDEN.equals(leftButton.getTag())) {
            leftButton.setTag(null);
            leftButton.setClickable(true);
            animate(1, leftButton);
        }
    }

    private void handleRightButtonVisibility(int position) {
        if (position == getCount() - 1) {
            rightButton.setTag(TAG_HIDDEN);
            rightButton.setClickable(false);
            animate(0, rightButton);
        } else if (TAG_HIDDEN.equals(rightButton.getTag())) {
            rightButton.setTag(null);
            rightButton.setClickable(true);
            animate(1, rightButton);
        }
    }

    private void animate(float alpha, Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            button.animate()
                    .alpha(alpha)
                    .setDuration(120)
                    .start();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            ObjectAnimator.ofFloat(button, "alpha",
                    alpha == 1 ? 0 : alpha, alpha == 1 ? alpha : 0)
                    .setDuration(120)
                    .start();
        } else {
            setAlpha(alpha, button);
        }
    }

    @Override
    public float getPageWidth(int position){
        return 1.0f;
    }

    @SuppressWarnings("deprecation")
    private void setAlpha(float alpha, Button button) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            button.setAlpha(alpha);
        } else {
            button.setAlpha((int) (alpha * 255));
        }
    }

    static class Builder {
        private ViewPager viewPager;

        private Button leftButton;
        private Button rightButton;

        private Color selectedColor;
        Builder() {
        }

        Builder viewPager(ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        Builder leftButton(Button leftButton) {
            this.leftButton = leftButton;
            return this;
        }

        Builder rightButton(Button rightButton) {
            this.rightButton = rightButton;
            return this;
        }

        Builder selectedColor(Color selectedColor){
            this.selectedColor = selectedColor;
            return this;
        }

        MonthAdapter build() {
            return new MonthAdapter(this);
        }
    }
}