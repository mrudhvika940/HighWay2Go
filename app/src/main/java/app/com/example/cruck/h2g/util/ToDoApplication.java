package app.com.example.cruck.h2g.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.firebase.client.Firebase;

/**
 * Created by cruck on 01-06-2016.
 */
public class ToDoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}