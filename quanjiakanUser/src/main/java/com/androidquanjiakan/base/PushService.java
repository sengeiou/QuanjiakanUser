package com.androidquanjiakan.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PushService extends Service {

    public PushService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
