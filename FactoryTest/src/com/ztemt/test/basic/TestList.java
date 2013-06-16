package com.ztemt.test.basic;

import android.content.Context;

import com.ztemt.test.basic.item.BacklightTest;
import com.ztemt.test.basic.item.BaseTest;
import com.ztemt.test.basic.item.EarphoneTest;
import com.ztemt.test.basic.item.FmRadioTest;
import com.ztemt.test.basic.item.KeyTest;
import com.ztemt.test.basic.item.LCDTest;
import com.ztemt.test.basic.item.LoopbackTest;
import com.ztemt.test.basic.item.ReceiverTest;
import com.ztemt.test.basic.item.SpeakerTest;
import com.ztemt.test.basic.item.TouchTest;
import com.ztemt.test.basic.item.UnknownTest;
import com.ztemt.test.basic.item.VersionTest;
import com.ztemt.test.basic.item.VibratorTest;

public class TestList {

    // Add new test item here
    private static final BaseTest[] ALL_ITEMS = {
        new VersionTest(),
        new LCDTest(),
        new BacklightTest(),
        new ReceiverTest(),
        new SpeakerTest(),
        new EarphoneTest(),
        new FmRadioTest(),
        new KeyTest(),
        new VibratorTest(),
        new TouchTest(),
        new LoopbackTest(),
    };

    // Need to test items
    private static BaseTest[] sItems;

    // Unknown test item
    private static BaseTest sUnknownTest = new UnknownTest();

    static void updateItems(Context context) {
        sUnknownTest.setContext(context);

        int size = 0;
        for (BaseTest t : ALL_ITEMS) {
            t.setContext(context);
            if (t.isNeedTest()) {
                size++;
            }
        }
        sItems = new BaseTest[size];

        int i = 0;
        for (BaseTest t : ALL_ITEMS) {
            if (t.isNeedTest()) {
                sItems[i] = t;
                i++;
            }
        }
    }

    static int getCount() {
        return sItems.length;
    }

    static BaseTest get(int position) {
        if (position >= 0 && position < getCount()) {
            return sItems[position];
        } else {
            return sUnknownTest;
        }
    }
}
