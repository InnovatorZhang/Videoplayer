package com.zhang.videoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhang.videoplayer.Adapter.RecyclerViewAdapter;
import com.zhang.videoplayer.util.Utility;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class PlayActivity extends AppCompatActivity implements View.OnClickListener{

    Handler mHandler = new Handler();
    Timer mTimer;
    //控制视频播放的控件
    Button mPauseButton,mStopButton;
    SurfaceView mSurfaceView;
    TextView mTotalTime,mCurrentTime;
    //进度条
    SeekBar mSeekBar;
    MediaPlayer mMediaPlayer;
    //记录传过来的是那一条数据
    int mPosition  =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //得到Intent传过来的数据
        mPosition = getIntent().getIntExtra(RecyclerViewAdapter.EXTRA_POSITION,0);
        //初始化View
        initView();

        mMediaPlayer = new MediaPlayer();
        //设置准备监听，当mediaPlayer准备好之后就播放视频
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();

                mTotalTime.setText(change(mMediaPlayer.getDuration()/1000));

                //通过handler刷新当前时间
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        final int currentTime = mMediaPlayer.getCurrentPosition();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                           mCurrentTime.setText(change(currentTime/1000));
                            }
                        });
                    }
                },0,1000);
            }
        });
        //得到surfaceViewHolder
        SurfaceHolder surfaceholder = mSurfaceView.getHolder();
        //设置播放时打开屏幕
        surfaceholder.setKeepScreenOn(true);
        //设置回调监听
        surfaceholder.addCallback(new SurfaceListener());

    }

    private void initView() {
        mSeekBar = (SeekBar)findViewById(R.id.play_seekBar);
        mTotalTime =(TextView)findViewById(R.id.play_totalTime);//显示总共时间
        mCurrentTime = (TextView)findViewById(R.id.play_currentTime);//显示当前时间
        mStopButton = (Button)findViewById(R.id.player_stop);
        mPauseButton = (Button)findViewById(R.id.player_pause);
        mSurfaceView = (SurfaceView)findViewById(R.id.play_surfaceView);
        //注册点击事件
        mPauseButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        //监听seekBar的改变
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.player_pause:
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                }else{
                    mMediaPlayer.start();
                }
                break;
            case R.id.player_stop:
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                break;
        }
    }




    private class SurfaceListener implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //指定输出对象
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            //设置播放地址
            try {
                mMediaPlayer.setDataSource(PlayActivity.this,Uri.parse(Utility.sVideos.get(mPosition).getPlayUri()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //异步准备，这里采用preperAsync()方法来异步准备，如果用preper()方法的话，课能回因为网络的不行而出现ANR
            mMediaPlayer.prepareAsync();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    @Override
    protected void onPause() {
        if(mMediaPlayer.isPlaying()){

            mMediaPlayer.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //当活动被摧毁时，释放资源
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }

        mMediaPlayer.release();

        if(mTimer != null){
            mTimer.cancel();
        }
        super.onDestroy();
    }

    /**
     * 将返回的毫秒数转为十二小时格式的
     * @param second
     * @return
     */
    public static String change(int second) {
        int hh = second/3600;
        int mm = second%3600/60;
        int ss = second%60;
        String str = null;
        if(hh != 0){
            str=String.format("%02d:%02d:%02d",hh,mm,ss);
        }else{
            str=String.format("%02d:%02d",mm,ss);
        }

        return str;
    }
}
