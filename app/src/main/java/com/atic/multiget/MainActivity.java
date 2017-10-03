package com.atic.multiget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
    private Button start=(Button)findViewById(R.id.button_start);
    private Button stop=(Button)findViewById(R.id.button_stop);
    private Button cancel=(Button)findViewById(R.id.button_cancel);
    private TextView text=(TextView)findViewById(R.id.text);

    private DownloadTask downloadTask=new DownloadTask(downloadListener);

    private DownloadListener downloadListener=new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
            text.setText("已完成："+progress*100+"%");
        }

        @Override
        public void onSuccess() {
            text.setText("下载完成");
        }

        @Override
        public void onFailed() {
            text.setText("下载失败");
        }

        @Override
        public void onPaused() {
            text.setText("已暂停");
        }

        @Override
        public void onCanceled() {
            text.setText("取消下载");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
