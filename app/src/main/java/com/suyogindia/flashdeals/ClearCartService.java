package com.suyogindia.flashdeals;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.suyogindia.database.DataBaseHelper;

/**
 * Created by suyogcomputech on 19/10/16.
 */

public class ClearCartService extends IntentService {


    public ClearCartService() {
        super("ClearCartService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Clear Cart", "Called");

        DataBaseHelper helper = new DataBaseHelper(ClearCartService.this);
        helper.open();
        long row = helper.clearCart();
        helper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ClearCartService", "destryed");
    }
}
