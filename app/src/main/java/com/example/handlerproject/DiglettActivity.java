package com.example.handlerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Random;

public class DiglettActivity extends AppCompatActivity implements View.OnClickListener , View.OnTouchListener {


    public static final int CODE = 123;
    public static final int RANDOM_NUMBER = 500;
    private TextView mtvContext;
    private Button mbtnStart;
    private ImageView mivMoon;
    private int[][] mposition=new int[][]{
            {350,462},{458,123},
            {856,154},{754,315},
            {249,834},{246,164},
            {761,367},{218,684}
    };
    private int mTotalCount;
    private int successCount;
    public static final int MAX_COUNT = 10;
    private DiglettHandler mhandler=new DiglettHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diglett);
        mtvContext = findViewById(R.id.tv_context);
        mbtnStart = findViewById(R.id.btn_start);
        mivMoon = findViewById(R.id.iv_moon);

        mbtnStart.setOnClickListener(this);
        mivMoon.setOnTouchListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                start();
                break;
        }
    }

    private void start() {
        //发送handler
        mtvContext.setText("开始啦");
        mbtnStart.setText("游戏中...");
        mbtnStart.setEnabled(false);
        next(0);

    }

    private void next(int delayTime){
        int position = new Random().nextInt(mposition.length);
        Message message=Message.obtain();
        message.what=CODE;
        message.arg1=position;

        mhandler.sendMessageDelayed(message,delayTime);
        mTotalCount++;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.setVisibility(View.GONE);
        successCount++;
        mtvContext.setText("打到了"+successCount+"只，共"+MAX_COUNT+"只。");
        return false;
    }

    public static class DiglettHandler extends Handler{
        public final WeakReference<DiglettActivity> mweakReference;

        public DiglettHandler(DiglettActivity activity) {
            this.mweakReference = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DiglettActivity activity=mweakReference.get();
            switch (msg.what){
                case CODE:
                    if (activity.mTotalCount>MAX_COUNT){
                        activity.clear();
                        Toast.makeText(activity,"地鼠打完了！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    int position=msg.arg1;
                    activity.mivMoon.setX(activity.mposition[position][0]);
                    activity.mivMoon.setY(activity.mposition[position][1]);
                    activity.mivMoon.setVisibility(View.VISIBLE);

                    int randomTime=new Random().nextInt(RANDOM_NUMBER)+ RANDOM_NUMBER;
                    activity.next(randomTime);
                    break;

            }
        }
    }

    private void clear() {
        mTotalCount=0;
        successCount=0;
        mivMoon.setVisibility(View.GONE);
        mbtnStart.setText("开始");
        mbtnStart.setEnabled(true);
    }
}
