package com.example.iotkitchen;

import android.app.Application;
import android.os.Handler;

//General application class, highest functionality of all classes
public class MyApplication extends Application {

    public static MyApplication myApplication = new MyApplication();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Generalized callback used for bluetooth and set by the DatabaseMaster class
    static Handler.Callback realCallback = null;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (realCallback != null) {
                realCallback.handleMessage(msg);
            }
        };
    };
    public Handler getHandler() {
        return handler;
    }
    public void setCallBack(Handler.Callback callback) {
        this.realCallback = callback;
    }
}
