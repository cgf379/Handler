package com.example.handlerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class DownloadActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Handler handler;
    public static final int DOWNLOAD_MESSAGE_CODE = 10001;
    public static final int DOWNLOAD_MESSAGE_FAIL_CODE1 = 10002;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }
    @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_PERMISSION_CODE) {
                for (int i = 0; i < permissions.length; i++) {
                    Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
                }
            }


        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String appurl = "http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
                        download(appurl);
                    }
                }).start();

            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DOWNLOAD_MESSAGE_CODE:
                        progressBar.setProgress((Integer) msg.obj);
                        break;
                }
            }
        };
    }

    private void download(String appurl) {
        try {
            URL url = new URL(appurl);
            try {
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                //获取文件的总长度
                int contentlength = urlConnection.getContentLength();
                String downloadFolderName = Environment.getExternalStorageDirectory() + File.separator + "imooc" + File.separator;

                File file = new File(downloadFolderName);
                if (!file.exists()) {
                    file.mkdir();
                }
                String filename = downloadFolderName + "imooc.apk";
                File apkFile = new File(filename);
                if(apkFile.exists()){
                    apkFile.delete();
                }

                int downloadSize = 0;
                byte[] bytes = new byte[1024];
                int length = 0;
                OutputStream outputStream = new FileOutputStream(filename);
                while ((length = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, length);
                    downloadSize += length;

                    Message message = Message.obtain();
                    message.obj = downloadSize * 100 / contentlength;
                    message.what = DOWNLOAD_MESSAGE_CODE;
                    handler.sendMessage(message);
                }
                inputStream.close();
                outputStream.close();

            } catch (IOException e) {
                notifyDownloadFaild();
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            notifyDownloadFaild();
            e.printStackTrace();
        }
    }

    private void notifyDownloadFaild() {
        Message message = Message.obtain();
        message.what = DOWNLOAD_MESSAGE_FAIL_CODE1;
        handler.sendMessage(message);
    }
}
