package com.zhang.videoplayer.util;

import android.util.Log;

import com.zhang.videoplayer.bean.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张 on 2017/5/19.
 */

public class Utility {

    public static List<Video> sVideos = new ArrayList<>();
    /**
     * 解析数据的类
     * @param response
     * @return
     */
    public static boolean handleInformation(String response){

        try {

            JSONObject jsonObject = new JSONObject(response);

            int  showapi_res_code = jsonObject.getInt("showapi_res_code");

            if(showapi_res_code != 0){
                return false;
            }else{

                JSONObject jsonObject01 = jsonObject.getJSONObject("showapi_res_body");
                JSONObject jsonObject02 = jsonObject01.getJSONObject("pagebean");

                //解析的方法
                parse(jsonObject02);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return true;
    }

    private static void parse(JSONObject jsonObject02) {

        try {
            JSONArray jsonArray = jsonObject02.getJSONArray("contentlist");

            //利用循环读取Array中的数据
            for(int i = 0; i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //判断是否含有视频的uri，有的话才加入集合中
                if (jsonObject.has("video_uri")) {
                    Video video = new Video();
                    video.setAuthorName(jsonObject.getString("name"));

                    video.setPlayUri(jsonObject.getString("video_uri"));
                    video.setImageUri(jsonObject.getString("profile_image"));
                    video.setTitle(jsonObject.getString("text"));
                    //有时间的话 调整为加入数据库中
                    sVideos.add(video);
                }
            }
            Log.e("12345","sdssdsdsdsdsdsds");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
