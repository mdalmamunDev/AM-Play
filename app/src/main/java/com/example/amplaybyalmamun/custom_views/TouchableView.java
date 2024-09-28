package com.example.amplaybyalmamun.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.LinearLayoutCompat;

public class TouchableView extends LinearLayoutCompat {

    public TouchableView(Context context) {
        super(context);
    }

    public TouchableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        // Handle the click event here
        // You can put your click logic here, or call a listener

        // Call super to handle accessibility
        return super.performClick();
    }
}

