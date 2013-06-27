package com.ztemt.test.basic.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.ztemt.test.basic.R;

public class TouchTest extends BaseTest implements OnTouchListener {

    private static final String TAG = "TouchTest";
    private static final String TAG_OK = "ok";
    private static final int MSG_CENTER_TOUCH = 1;
    private static final float TOUCH_TOLERANCE = 50;

    private LinearLayout mNorth;
    private LinearLayout mSouth;
    private LinearLayout mEast;
    private LinearLayout mWest;
    private TouchView mView;

    private boolean mTouchValid;

    private float mX = 0;
    private float mY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.touch, container, false);

        mView = (TouchView) v.findViewById(R.id.touch_center);

        mNorth = (LinearLayout) v.findViewById(R.id.touch_north);
        mNorth.setOnTouchListener(this);
        mSouth = (LinearLayout) v.findViewById(R.id.touch_south);
        mSouth.setOnTouchListener(this);
        mEast = (LinearLayout) v.findViewById(R.id.touch_east);
        mEast.setOnTouchListener(this);
        mWest = (LinearLayout) v.findViewById(R.id.touch_west);
        mWest.setOnTouchListener(this);

        // Base on 320 x 480, density = 1.0, block is 20 x 24
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wc = Math.round((20 * dm.widthPixels) / (320 * dm.density));
        int hc = Math.round((24 * dm.heightPixels) / (480 * dm.density));
        addBorder(mNorth, wc);
        addBorder(mSouth, wc);
        addBorder(mEast, hc);
        addBorder(mWest, hc);

        return v;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touchStart(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            touchMove(x, y);
            break;
        case MotionEvent.ACTION_UP:
            touchUp();
            break;
        }
        return !isBorderChecked();
    }

    @Override
    public void onHandleMessage(final int index) {
        switch (index) {
        case MSG_CENTER_TOUCH:
            mView.setVisibility(View.VISIBLE);
            break;
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        setButtonVisibility(!isButtonVisible());
        return true;
    }

    @Override
    public String getTestName() {
        return getContext().getString(R.string.touch_title);
    }

    @Override
    public boolean isNeedTest() {
        return getSystemProperties("touch", true);
    }

    private void touchStart(float x, float y) {
        mTouchValid = true;
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        mX = x;
        mY = y;

        if (mTouchValid) {
            mTouchValid = dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE;
        }

        if (mTouchValid) {
            fillBorder(mNorth, x, y);
            fillBorder(mSouth, x, y);
            fillBorder(mEast, x, y);
            fillBorder(mWest, x, y);
        } else {
            Log.w(TAG, "Moving distance is too large");
        }
    }

    private void touchUp() {
        if (isBorderChecked()) {
            // set center touch view visible
            setTimerTask(MSG_CENTER_TOUCH, 1000);
        }
    }

    private boolean isBorderChecked() {
        return checkBorder(mNorth) && checkBorder(mSouth) && checkBorder(mEast)
                && checkBorder(mWest);
    }

    private void fillBorder(LinearLayout ll, float x, float y) {
        if (ll.getTag() != null && ll.getTag().equals(TAG_OK)) {
            return;
        }

        int location[] = new int[2];
        for (int i = 0; i < ll.getChildCount(); i++) {
            View v = ll.getChildAt(i);
            v.getLocationOnScreen(location);

            if (x >= location[0] && x <= location[0] + v.getMeasuredWidth()
                    && y >= location[1]
                    && y <= location[1] + v.getMeasuredHeight()) {
                v.setBackgroundColor(Color.GREEN);
                v.setTag(TAG_OK);
            }
        }
    }

    private void addBorder(LinearLayout ll, int count) {
        for (int i = 0; i < count; i++) {
            View v = new View(getActivity());
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, 1);
            lp.setMargins(1, 1, 1, 1);
            v.setBackgroundColor(Color.WHITE);
            v.setLayoutParams(lp);
            ll.addView(v);
        }
    }

    private boolean checkBorder(LinearLayout ll) {
        if (ll.getTag() == null || !ll.getTag().equals(TAG_OK)) {
            int count = ll.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = ll.getChildAt(i);
                if (v.getTag() == null || !v.getTag().equals(TAG_OK)) {
                    return false;
                }
            }
            ll.setTag(TAG_OK);
        }
        return true;
    }

    public static class TouchView extends View {

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path   mPath;
        private Paint  mBitmapPaint;
        private Paint  mPaint;
        private Paint  mLinePaint;

        private boolean mTouchValid;

        private float mX, mY;

        public TouchView(Context context) {
            this(context, null);
        }

        public TouchView(Context context, AttributeSet attrs) {
            super(context, attrs);

            mPath = new Path();

            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);

            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setStyle(Paint.Style.STROKE);
            mLinePaint.setColor(Color.LTGRAY);
            mLinePaint.setStrokeWidth(1);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();

            canvas.drawColor(Color.WHITE);

            canvas.drawLine(0, 0, w, h, mLinePaint);
            canvas.drawLine(w, 0, 0, h, mLinePaint);

            canvas.drawLine(w / 3, 0, w, h * 2 / 3, mLinePaint);
            canvas.drawLine(w * 2 / 3, 0, w, h / 3, mLinePaint);
            canvas.drawLine(0, h / 3, w * 2 / 3, h, mLinePaint);
            canvas.drawLine(0, h * 2 / 3, w / 3, h, mLinePaint);

            canvas.drawLine(w * 2 / 3, 0, 0, h * 2 / 3, mLinePaint);
            canvas.drawLine(w / 3, 0, 0, h / 3, mLinePaint);
            canvas.drawLine(w, h / 3, w / 3, h, mLinePaint);
            canvas.drawLine(w, h * 2 / 3, w * 2 / 3, h, mLinePaint);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            }
            return true;
        }

        private void touchStart(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touchMove(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            mTouchValid = dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE;

            if (mTouchValid) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touchUp() {
            if (mTouchValid) {
                mPath.lineTo(mX, mY);
                // commit the path to our offscreen
                mCanvas.drawPath(mPath, mPaint);
            }

            // kill this so we don't double draw
            mPath.reset();
        }
    }
}
