package com.ztemt.test.basic.item;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ztemt.test.basic.R;

public class TouchTest extends BaseTest {

    // API 4.0 level
    private static final String POINTER_LOCATION = "pointer_location";
    //private static final String SHOW_TOUCHES = "show_touches";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.touch, container, false);
        v.findViewById(R.id.touch_text).setBackgroundDrawable(
                getResources().getDrawable(android.R.drawable.gallery_thumb));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setPointerVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setPointerVisibility(false);
    }

    @Override
    public String getTestName() {
        return getContext().getString(R.string.touch_title);
    }

    @Override
    public boolean isNeedTest() {
        return getSystemProperties("touch", true);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        setPointerVisibility(isButtonVisible());
        setButtonVisibility(!isButtonVisible());
        return true;
    }

    private void setPointerVisibility(boolean visible) {
        Settings.System.putInt(getActivity().getContentResolver(),
                POINTER_LOCATION, visible ? 1 : 0);
    }
}
