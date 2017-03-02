package com.example.wjf.uidetect;

import android.icu.text.DateFormat;
import android.icu.text.RuleBasedCollator;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by wjf on 17-3-2.
 * we use this class to do Monitor real work
 * we initialize one seperate thread for LogMonitor
 */

public class LogMonitor {
    private static final String TAG="LogMonitor";
    private static LogMonitor instance= new LogMonitor();
    private HandlerThread  mHandlerThread= new HandlerThread("LogMonitor");
    private Handler ioHandler;
    private static Runnable  logRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb=new StringBuilder();
            Looper mainLooper =Looper.getMainLooper();
            StackTraceElement[] ste=mainLooper.getThread().getStackTrace();


            for(StackTraceElement s:ste){
                sb.append(s.toString()+"\n");
            }
            Log.e(TAG,sb.toString());
        }
    };
    LogMonitor(){
        mHandlerThread.start();
        ioHandler=new Handler(mHandlerThread.getLooper());

    }
    public static LogMonitor getInstance(){


        return instance ;
    }
    boolean isHaveCallback(){
       // return  ioHandler.hasCallbacks(call);
        //我们用反射来调用这个内容

        final Handler local=ioHandler;
        Field mQueue= null;
        try {
            mQueue = local.getClass().getDeclaredField("mQueue");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            mQueue.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        MessageQueue queue= null;
        try {
            queue = (MessageQueue) mQueue.get(local);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Method hasMessage= null;
        try {
            hasMessage = queue.getClass().getDeclaredMethod("hasMessages",Handler.class, Runnable.class,Object.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        hasMessage.setAccessible(true);
        try {
            hasMessage.invoke(queue,local,logRunnable,null) ;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return true;
    }
    void startMonitor(){
        ioHandler.postDelayed(logRunnable,20);
    }
    void stopMonitor(){
        ioHandler.removeCallbacks(logRunnable);
    }
}
