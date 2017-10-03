package com.atic.multiget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    EditText url;
    private DownloadListener downloadListener=new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
            text.setText("已完成："+progress+"%");
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
    };
    private DownloadTask downloadTask;
    ProgressBar progressBar;
    TextView text;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_start:
                downloadTask=new DownloadTask(downloadListener);
                downloadTask.execute(url.getText().toString().trim());
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
    }
}
