package com.example.wjf.uidetect;

import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
/*
    we use this class to detect UI stuck problem,we use Looper loop method
* */
public class MainActivity extends AppCompatActivity {
    private final String TAG="wangjf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Looper.getMainLooper().setMessageLogging(new Printer() {
            private final String START=">>>>> Dispatching to ";
            private final String END="<<<<< Finished to ";
/*
* 接管Looper的消息处理
*
* */
            @Override
            public void println(String s) {
                if (s.startsWith(START)){
                    //LogMonitor.getInstance().startMonitor();
                   // Log.d(TAG,s);
                }
                if(s.startsWith(END)){
                    //LogMonitor.getInstance().startMonitor();
                    //Log.d(TAG,s);
                }
            }
        });

        //确认Choreographer的调用间隔。
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback(){
          public void doFrame(long l){
                Log.d(TAG,"call one doFrame");
              if(LogMonitor.getInstance().isHaveCallback()){
                  LogMonitor.getInstance().stopMonitor();
              }
              LogMonitor.getInstance().startMonitor();
              Choreographer.getInstance().postFrameCallback(this);
          }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
