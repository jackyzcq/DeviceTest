package com.ztemt.test.basic.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.ztemt.test.basic.R;

public class TouchTest extends BaseTest implements OnTouchListener {

    private static final String TAG = "TouchTest";
    private static final String TAG_OK = "ok";
    private static final int MSG_CENTER_TOUCH = 1;
    private static final int MSG_END = 2;

    private static TouchTest sInstance;
    private static float sDensity;
    private static float sTouchTolerance;

    private LinearLayout mNorth;
    private LinearLayout mSouth;
    private LinearLayout mEast;
    private LinearLayout mWest;
    private TouchView mView;

    private boolean mTouchValid;
    private float mX = 0;
    private float mY = 0;
    private int mWidth;
    private int mHeight;

    public static TouchTest getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = getActivity().getWindowManager();
        wm.getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        sTouchTolerance = (sDensity = dm.density) * 17;

        // Use by class TouchView
        sInstance = this;
    }

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
        int wc = Math.round((20 * mWidth) / (320 * sDensity));
        int hc = Math.round((24 * mHeight) / (480 * sDensity));

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
        return true;
    }

    @Override
    public void onHandleMessage(final int index) {
        switch (index) {
        case MSG_CENTER_TOUCH:
            mView.setVisibility(View.VISIBLE);
            break;
        case MSG_END:
            clickPassButton();
            break;
        }
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
            mTouchValid = dx < sTouchTolerance && dy < sTouchTolerance;
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
        if (checkBorder(mNorth) && checkBorder(mSouth) && checkBorder(mEast)
                && checkBorder(mWest)) {
            // set center touch view visible
            setTimerTask(MSG_CENTER_TOUCH, 1000);
        }
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

        private static final int LINES_LENGTH = 10;
        private static final Point[][] POINTS = new Point[LINES_LENGTH][2];

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path   mPath;
        private Paint  mBitmapPaint;
        private Paint  mPaint;
        private Paint  mDashPaint;
        private Point  mPoint;

        private boolean mTouchValid;
        private boolean mFlag[] = new boolean[LINES_LENGTH];

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
            mPaint.setStrokeWidth(4 * sDensity);

            mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDashPaint.setStyle(Paint.Style.STROKE);
            mDashPaint.setColor(Color.LTGRAY);
            mDashPaint.setStrokeWidth(1);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            POINTS[0] = new Point[] { new Point(0, 0), new Point(w, h) };
            POINTS[1] = new Point[] { new Point(w, 0), new Point(0, h) };

            POINTS[2] = new Point[] { new Point(w / 3, 0), new Point(w, h * 2 / 3) };
            POINTS[3] = new Point[] { new Point(w * 2 / 3, 0), new Point(w, h / 3) };
            POINTS[4] = new Point[] { new Point(0, h / 3), new Point(w * 2 / 3, h) };
            POINTS[5] = new Point[] { new Point(0, h * 2 / 3), new Point(w / 3, h) };

            POINTS[6] = new Point[] { new Point(w * 2 / 3, 0), new Point(0, h * 2 / 3) };
            POINTS[7] = new Point[] { new Point(w / 3, 0), new Point(0, h / 3) };
            POINTS[8] = new Point[] { new Point(w, h / 3), new Point(w / 3, h) };
            POINTS[9] = new Point[] { new Point(w, h * 2 / 3), new Point(w * 2 / 3, h) };

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            for (int i = 0; i < LINES_LENGTH; i++) {
                canvas.drawLine(POINTS[i][0].x, POINTS[i][0].y, POINTS[i][1].x,
                        POINTS[i][1].y, mDashPaint);
            }

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
            if ((mPoint = locate(x, y)) != null) {
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
            }
        }

        private void touchMove(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            mTouchValid = dx < sTouchTolerance && dy < sTouchTolerance;

            if (mPoint != null && mTouchValid) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touchUp() {
            int index = indexOfLines(mPoint, locate(mX, mY));

            if (mTouchValid && index > -1) {
                mPath.lineTo(mX, mY);
                // commit the path to our offscreen
                mCanvas.drawPath(mPath, mPaint);
                // set pass flag
                mFlag[index] = true;
            }

            // kill this so we don't double draw
            mPath.reset();

            // pass?
            if (checkLines()) {
                getInstance().setTimerTask(MSG_END, 1000);
            }
        }

        private Point locate(float x, float y) {
            float distance = sDensity * 20;
            float dx, dy;

            for (int i = 0; i < LINES_LENGTH; i++) {
                dx = Math.abs(x - POINTS[i][0].x);
                dy = Math.abs(y - POINTS[i][0].y);
                if (dx < distance && dy < distance) {
                    return POINTS[i][0];
                }

                dx = Math.abs(x - POINTS[i][1].x);
                dy = Math.abs(y - POINTS[i][1].y);
                if (dx < distance && dy < distance) {
                    return POINTS[i][1];
                }
            }
            return null;
        }

        private int indexOfLines(Point start, Point end) {
            for (int i = 0; i < LINES_LENGTH; i++) {
                if (POINTS[i][0].equals(start) && POINTS[i][1].equals(end)
                        || POINTS[i][0].equals(end)
                        && POINTS[i][1].equals(start)) {
                    return i;
                }
            }
            return -1;
        }

        private boolean checkLines() {
            for (int i = 0; i < LINES_LENGTH; i++) {
                if (!mFlag[i]) return false;
            }
            return true;
        }
    }
}
