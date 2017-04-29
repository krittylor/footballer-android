package com.example.admin.footballer.SwipeSelector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.footballer.R;

import java.util.List;

class TimeAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private static final String STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION";
    private static final String TAG_CIRCLE = "TAG_CIRCLE";

    // For the left and right buttons when they're not visible
    private static final String TAG_HIDDEN = "TAG_HIDDEN";

    private final Context context;

    private final ViewPager viewPager;

    private OnSwipeItemSelectedListener onItemSelectedListener;
    private List<SwipeItem> items;
    private int currentPosition;
    private View currentView;
    public int currentIndex = -1;
    private int selectedColor;
    private int deselectedColor;
    private TimeAdapter(Builder builder) {
        context = builder.viewPager.getContext();

        viewPager = builder.viewPager;
        viewPager.addOnPageChangeListener(this);

        selectedColor = builder.selectedColor;
        deselectedColor = builder.deselectedColor;
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
        LinearLayout layout = (LinearLayout) View.inflate(context, R.layout.swipeselector_content_time_item, null);

        TextView description = (TextView) layout.findViewById(R.id.swipeselector_content_description);
        ImageView imgView = (ImageView)layout.findViewById(R.id.swipe_selector_ball);
        imgView.setImageResource(R.drawable.ball);
        if(currentIndex == position){
            description.setTextColor(selectedColor);
            imgView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);

            currentView = layout;
        } else {
            description.setTextColor(deselectedColor);
            imgView.setColorFilter(deselectedColor, PorterDuff.Mode.SRC_ATOP);
        }
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
        final String pos = String.valueOf(position);
        final int index = position;
        layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(currentView != null){
                    ImageView imgView = (ImageView)currentView.findViewById(R.id.swipe_selector_ball);
                    imgView.setImageResource(R.drawable.ball);

                    imgView.setColorFilter(deselectedColor, PorterDuff.Mode.SRC_ATOP);
                    TextView description = (TextView) currentView.findViewById(R.id.swipeselector_content_description);
                    description.setTextColor(deselectedColor);
                }
                ImageView imgView = (ImageView)v.findViewById(R.id.swipe_selector_ball);
                imgView.setImageResource(R.drawable.ball);

                imgView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                TextView description = (TextView) v.findViewById(R.id.swipeselector_content_description);
                description.setTextColor(selectedColor);
                currentIndex = index;
                currentView = v;
            }
        });
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
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    @Override
    public float getPageWidth(int position){
        return 0.2f;
    }

    @SuppressWarnings("deprecation")
    private void setAlpha(float alpha, Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            button.setAlpha(alpha);
        } else {
            button.setAlpha((int) (alpha * 255));
        }
    }

    static class Builder {
        private ViewPager viewPager;

        private int selectedColor;
        private int deselectedColor;

        Builder() {
        }

        Builder viewPager(ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        Builder selectedColor(int selectedColor){
            this.selectedColor = selectedColor;
            return this;
        }

        Builder deselectedColor(int deselectedColor){
            this.deselectedColor = deselectedColor;
            return this;
        }

        TimeAdapter build() {
            return new TimeAdapter(this);
        }
    }
}