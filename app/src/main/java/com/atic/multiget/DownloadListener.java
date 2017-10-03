package com.atic.multiget;


public interface DownloadListener {
    void onProgress (double progress,double speed);
    void onSuccess ();
    void onFailed ();
    void onPaused ();
    void onCanceled ();
}
