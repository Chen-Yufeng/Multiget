package com.atic.multiget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText url;
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private String webfilepath;
    private DownloadListener downloadListener=new DownloadListener() {
        @Override
        public void onProgress(double progress,double speed) {
            /**try {
                URL url = new URL(webfilepath);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                long fileLength = httpURLConnection.getContentLength();
            }catch (Exception e){
                e.printStackTrace();
            }*/
            progressBar.setProgress((int)progress);
            text.setText("已完成："+progress+"%"+"  速度："+speed);
            builder.setContentText("已完成"+progress+"%");
            builder.setProgress(100,(int)progress,false);
        }

        @Override
        public void onSuccess() {
            text.setText("下载完成");
            builder.setContentText("已完成");
            builder.setAutoCancel(true);
        }

        @Override
        public void onFailed() {
            text.setText("下载失败");
            builder.setContentText("下载失败");
            builder.setAutoCancel(true);
        }

        @Override
        public void onPaused() {
            text.setText("已暂停");
        }

        @Override
        public void onCanceled() {
            text.setText("取消下载");
            builder.setContentText("取消下载");
            builder.setAutoCancel(true);
        }
    };
    private DownloadTask downloadTask;
    ProgressBar progressBar;
    TextView text;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_start:
                downloadTask=new DownloadTask(downloadListener);
                downloadTask.execute(webfilepath);
                builder.setContentTitle("正在下载");
                builder.setWhen(System.currentTimeMillis());
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                builder.setProgress(100,0,false);
                Intent notice_intent=new Intent(this,MainActivity.class);
                PendingIntent pi=PendingIntent.getActivity(this,0,notice_intent,0);
                builder.setContentIntent(pi);
                manager.notify(1,builder.build());
                break;
            case R.id.button_stop:
                if(downloadTask!=null)
                    downloadTask.pauseDownload();
                break;
            case R.id.button_cancel:
                if(downloadTask!=null) {
                    downloadTask.cancelDownload();
                    //还要删除文件
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        Button start=(Button)findViewById(R.id.button_start);
        Button stop=(Button)findViewById(R.id.button_stop);
        Button cancel=(Button)findViewById(R.id.button_cancel);
        text=(TextView)findViewById(R.id.text);
        url=(EditText)findViewById(R.id.edit_text_url);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        cancel.setOnClickListener(this);
        webfilepath=url.getText().toString().trim();


        //Notice
        manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder=new NotificationCompat.Builder(this);
    }
}
