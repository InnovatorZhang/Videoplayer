package com.zhang.videoplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 张 on 2017/3/15.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        //开启线程发起请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    bufferedReader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        response.append(line);
                    }

                    if(listener != null){
                        //回调onFinish方法
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if(listener != null) {
                        //回调onError方法
                        listener.onError(e);
                    }
                    e.printStackTrace();
                }finally {
                    if(bufferedReader != null){
                        try{
                            bufferedReader.close();
                        }catch (IOException e1){
                            e1.printStackTrace();
                        }
                    }
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 加载图片的方法
     * @param address
     * @param listener
     */
    public static void loadBitmap(final String address, final SetBitmapListener listener){
        //开启线程加载图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                HttpURLConnection connection = null;
                try {
                    URL uri = new URL(address);
                    connection = (HttpURLConnection) uri.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8 * 1000);
                    connection.setConnectTimeout(8 * 1000);
                    connection.setDoInput(true);
                    //connection.setDoOutput(true);

                    InputStream in = connection.getInputStream();
                    byte[] data = readStream(in);
                    if(data != null){
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        listener.setBitmap(bm);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }


    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }


}
