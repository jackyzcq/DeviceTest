package com.ztemt.test.basic.item.touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class BorderTouchView extends View {

    //private static final String TAG = "BorderTouchView";
    private static final int SIZE = 2;

    private Paint mPaint;
    private Rect  mRows[][];
    private Rect  mColumns[][];

    private OnTouchChangedListener mListener;

    private boolean mRowFlags[][];
    private boolean mColumnFlags[][];
    private boolean mTouchValid;

    private int mTouchTolerance;
    private int mX = 0;
    private int mY = 0;
    private int mRectWidth;
    private int mRectHeight;

    public BorderTouchView(Context context) {
        this(context, null);
    }

    public BorderTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mRectWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, dm);
        mRectHeight = (int) dm.density * 16;

        // Maximum distance between points
        mTouchTolerance = (int) dm.density * 21;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
    }

    public void setOnTouchChangedListener(OnTouchChangedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int rectsInRow = w / mRectHeight;
        int rectsInColumn = (h - 2 * mRectWidth) / mRectHeight;

        mRowFlags = new boolean[SIZE][rectsInRow];
        mColumnFlags = new boolean[SIZE][rectsInColumn];

        mRows = new Rect[SIZE][rectsInRow];
        mColumns = new Rect[SIZE][rectsInColumn];

        int l, r, t, b;
        for (int i = 0; i < SIZE; i++) {
            t = (h - mRectWidth) * i;
            b = t + mRectWidth;
            for (int j = 0; j < rectsInRow; j++) {
                l = mRectHeight * j;
                r = (j == rectsInRow - 1) ? w : l + mRectHeight;
                mRows[i][j] = new Rect(l + 1, t + 1, r - 1, b - 1);
            }

            l = (w - mRectWidth) * i;
            r = l + mRectWidth;
            for (int j = 0; j < rectsInColumn; j++) {
                t = mRectWidth + mRectHeight * j;
                b = (j == rectsInColumn - 1) ? h - mRectWidth : t + mRectHeight;
                mColumns[i][j] = new Rect(l + 1, t + 1, r - 1, b - 1);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < mRows[i].length; j++) {
                mPaint.setColor(mRowFlags[i][j] ? Color.GREEN : Color.WHITE);
                canvas.drawRect(mRows[i][j], mPaint);
            }
            for (int j = 0; j < mColumns[i].length; j++) {
                mPaint.setColor(mColumnFlags[i][j] ? Color.GREEN : Color.WHITE);
                canvas.drawRect(mColumns[i][j], mPaint);
            }
        }
    }

    private void touchStart(int x, int y) {
        mTouchValid = true;
        mX = x;
        mY = y;
    }

    private void touchMove(int x, int y) {
        int dx = Math.abs(x - mX);
        int dy = Math.abs(y - mY);

        mX = x;
        mY = y;

        if (mTouchValid) {
            mTouchValid = dx < mTouchTolerance && dy < mTouchTolerance;
        }

        if (mTouchValid) {
            setBorderFlag(x, y);
        }
    }

    private void touchUp() {
        if (mListener != null && checkBorders()) {
            mListener.onTouchFinish(this);
        }
    }

    private void setBorderFlag(int x, int y) {
        if (y < mRectWidth || y > getHeight() - mRectWidth) {
            int i = x / mRectHeight;
            if (i > -1 && i < mRowFlags[0].length) {
                if (y < mRectWidth) {
                    // mHorizontal[0]
                    mRowFlags[0][i] = true;
                } else if (y > getHeight() - mRectWidth) {
                    // mHorizontal[1]
                    mRowFlags[1][i] = true;
                }
            }
        } else if (y > mRectWidth && y < getHeight() - mRectWidth) {
            int i = (y - mRectWidth) / mRectHeight;
            if (i > -1 && i < mColumnFlags[0].length) {
                if (x < mRectWidth) {
                    // mVertical[0]
                    mColumnFlags[0][i] = true;
                } else if (x > getWidth() - mRectWidth) {
                    // mVertical[1]
                    mColumnFlags[1][i] = true;
                }
            }
        }
    }

    private boolean checkBorders() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < mRowFlags[i].length; j++) {
                if (!mRowFlags[i][j]) {
                    return false;
                }
            }
            for (int j = 0; j < mColumnFlags[i].length; j++) {
                if (!mColumnFlags[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }
}
