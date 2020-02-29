package com.example.handlerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class CountDown extends AppCompatActivity {

    public static final int COUNTDOWN_TIME_CODE = 10001;
    public static final int DELAY_MILLIS = 1000;
    public static final int MAX_COUNT = 10;
    private TextView mtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        mtv=findViewById(R.id.tv_countdown);
       CountDownTimeHandler handler=new CountDownTimeHandler(this);
        Message message=Message.obtain();
        message.what= COUNTDOWN_TIME_CODE;
        message.arg1= MAX_COUNT;
        handler.sendMessageDelayed(message, DELAY_MILLIS);
    }
    public static class CountDownTimeHandler extends Handler{
        final WeakReference<CountDown> mweakreference;

        public CountDownTimeHandler(CountDown activity) {
            mweakreference =new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            CountDown activity=mweakreference.get();

            switch (msg.what){
                case COUNTDOWN_TIME_CODE:
                    int value =msg.arg1;
                    activity.mtv.setText(String.valueOf(value--));

                    if (value>0) {
                        Message message = Message.obtain();
                        message.what = COUNTDOWN_TIME_CODE;
                        message.arg1 = value;
                        sendMessageDelayed(message,DELAY_MILLIS);
                    }
                    break;
            }
        }
    }
}
