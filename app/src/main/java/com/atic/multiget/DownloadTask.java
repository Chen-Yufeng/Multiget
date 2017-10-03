package com.atic.multiget;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by If Chan on 2017/10/3.
 */

public class DownloadTask extends AsyncTask<String,Integer,Integer> {

    //private Handler handler=new Handler();

    public static final int TYPE_SUCCESS =0;
    public static final int TYPE_FAILED=1;
    public static final int TYPE_PAUSED=2;
    public static final int TYPE_CANCELED=3;

    private DownloadListener listener;
    private boolean isCanceled=false;
    private boolean isPaused=false;
    private int lastProgress;

    public DownloadTask(DownloadListener listener){
        this.listener=listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is = null;
        RandomAccessFile savedFile=null;
        File file=null;
        try{
            long downloadedLength=0;
            String downloadUrl=strings[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file =new File(directory+fileName);
            if(file.exists()){
                downloadedLength=file.length();
            }
            long contentLength=getContentLength(downloadUrl);
            if(contentLength==0)
                return TYPE_FAILED;
            else if(contentLength==downloadedLength)
                return TYPE_SUCCESS;
            HttpURLConnection httpURLConnection=(HttpURLConnection)new URL(downloadUrl).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Range","bytes="+downloadedLength+"-");
            is=httpURLConnection.getInputStream();
            savedFile=new RandomAccessFile(file,"rw");
            savedFile.seek(downloadedLength);
            byte[] b=new byte[1024];
            int total=0;
            int len;
            while((len=is.read(b))!=-1){
                if(isCanceled){
                    return TYPE_CANCELED;

                }else if(isPaused){
                    return TYPE_PAUSED;
                }else{
                    total+=len;
                }
                savedFile.write(b,0,len);
                int progress=(int)((total+downloadedLength)*100/contentLength);
                publishProgress(progress);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (is != null)
                    is.close();
                if (savedFile != null)
                    savedFile.close();
                if (isCanceled && file != null)
                    file.delete();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return TYPE_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress=values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            default:
                break;
        }
    }

    public void pauseDownload()
    {
        isPaused=true;
    }

    public void cancelDownload(){
        isCanceled=true;
    }

    private long getContentLength(String downloadUrl)throws IOException{
        URL url=new URL(downloadUrl);
        URLConnection urlConnection=url.openConnection();
        HttpURLConnection httpURLConnection=(HttpURLConnection)urlConnection;
        long fileLength=httpURLConnection.getContentLength();
        return fileLength;
    }
}
