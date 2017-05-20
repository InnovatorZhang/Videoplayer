package com.zhang.videoplayer;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhang.videoplayer.Adapter.RecyclerViewAdapter;
import com.zhang.videoplayer.util.HttpCallbackListener;
import com.zhang.videoplayer.util.HttpUtil;
import com.zhang.videoplayer.util.Utility;


public class MainActivity extends AppCompatActivity {

    Handler mHandler;
    private final int firstPage = 0;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPage = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //给RecyclerView设置适配器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAdapter = new RecyclerViewAdapter(Utility.sVideos,MainActivity.this,mHandler);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        sendRequestion(firstPage);
        //给SwipeRefreshLayout添加监听事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //将页数加一
                mPage++;
                //将之前的数据清空
                Utility.sVideos.clear();
                sendRequestion(mPage);
            }
        });
    }
    /**
     * 初始化View
     */
    private void initView() {
        mHandler = new Handler();
        mRecyclerView = (RecyclerView)findViewById(R.id.main_recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.main_swipeRefreshLayout);
    }

    /**
     * 请求数据
     */
    private void sendRequestion(int page) {
        HttpUtil.sendHttpRequest("http://route.showapi.com/255-1?showapi_appid=38534&showapi_sign=37a6da60d1f64755a1318e83c30a2ef8&page=" + page, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                //处理返回的json数据
                boolean result =  Utility.handleInformation(response);
                if(result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerViewAdapter.notifyDataSetChanged();
                            mSwipeRefreshLayout.setRefreshing(false);
                            Log.e("12345", response);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }



}
