package com.ztemt.test.auto.item;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ztemt.test.auto.R;

public class RecoveryTest extends BaseTest {

    private static final String LOG_TAG = "RecoveryTest";

    private Context mContext;

    public RecoveryTest(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void executeTest() {
        setSuccess();
        recovery();
        pause();
    }

    @Override
    public String getTestTitle() {
        return mContext.getString(R.string.recovery_test);
    }

    private void recovery() {
        setReboot(true);
        Log.d(LOG_TAG, "Performing recovery system...");
        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
        mContext.sendBroadcast(intent);
    }
}
