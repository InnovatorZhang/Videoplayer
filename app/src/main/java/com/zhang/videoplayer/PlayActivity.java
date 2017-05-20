package com.zhang.videoplayer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.videoplayer.Adapter.RecyclerViewAdapter;
import com.zhang.videoplayer.util.Utility;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class PlayActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String MEMORY_POSITION = "PlayActivity.video.memory";


    Handler mHandler = new Handler();
    Timer mTimer;
    //控制视频播放的控件
    ImageButton mPauseButton,mStopButton,mDownloadButton,mPreButton,mNextButton;
    SurfaceView mSurfaceView;
    TextView mTotalTime,mCurrentTime;
    //进度条
    SeekBar mSeekBar;
    MediaPlayer mMediaPlayer;
    //记录传过来的是那一条数据
    int mPosition  =0;
    //用来决定使用哪一张图片
    private boolean picture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //得到Intent传过来的数据
        mPosition = getIntent().getIntExtra(RecyclerViewAdapter.EXTRA_POSITION,0);
        //初始化View
        initView();

        mMediaPlayer = new MediaPlayer();

       //给mMedialPayer设置监听
        setMediaListener();

        //得到surfaceViewHolder
        SurfaceHolder surfaceholder = mSurfaceView.getHolder();

        //设置播放时打开屏幕
        surfaceholder.setKeepScreenOn(true);

        //设置回调监听
        surfaceholder.addCallback(new SurfaceListener());

    }


    private void setMediaListener() {
        //设置准备监听，当mediaPlayer准备好之后就播放视频
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //看是否有观看记录
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PlayActivity.this);
                int memory = prefs.getInt(MEMORY_POSITION + Utility.sVideos.get(mPosition).getTitle(),0);
                mMediaPlayer.start();

                //如果不等于0，也就是说原来看过的话，就直接从原来看过的地方开始观看
                //并将seekBar移动到对应位置
                if(memory != 0){
                    mMediaPlayer.seekTo(memory);
                    mSeekBar.setProgress(mMediaPlayer.getCurrentPosition()*100/mMediaPlayer.getDuration());
                }

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
                                //设置时间的同时也要移动seekBar
                                mCurrentTime.setText(change(currentTime/1000));
                                mSeekBar.setProgress(currentTime*100/mMediaPlayer.getDuration());
                            }
                        });
                    }
                },0,1000);
            }
        });
        //当播放完成时，结束这个活动
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //在已经看完的情况下，将记忆的播放的位置清除
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PlayActivity.this).edit();
               // editor.putInt(MEMORY_POSITION + Utility.sVideos.get(mPosition).getTitle(),ALREADY_LOOKED);
                editor.remove(MEMORY_POSITION + Utility.sVideos.get(mPosition).getTitle());
                Log.e("shifou huidiao ","1234582131");
                editor.apply();
                finish();
            }
        });
    }

    private void initView() {
        mSeekBar = (SeekBar)findViewById(R.id.play_seekBar);
        mTotalTime =(TextView)findViewById(R.id.play_totalTime);//显示总共时间
        mCurrentTime = (TextView)findViewById(R.id.play_currentTime);//显示当前时间
        mStopButton = (ImageButton)findViewById(R.id.player_stop);
        mPauseButton = (ImageButton)findViewById(R.id.player_pause);
        mDownloadButton = (ImageButton)findViewById(R.id.player_download);
        mPreButton = (ImageButton)findViewById(R.id.pre_video);
        mNextButton = (ImageButton)findViewById(R.id.next_video);
        mSurfaceView = (SurfaceView)findViewById(R.id.play_surfaceView);
        //注册点击事件
        mPauseButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mDownloadButton.setOnClickListener(this);
        mPreButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        //注册监听事件，监听seekBar的改变
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 应该在这个方法中去进行进度与时间的关联
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = mSeekBar.getProgress()
                        * mMediaPlayer.getDuration() // 计算进度条需要前进的位置数据大小
                        / mSeekBar.getMax();
                mCurrentTime.setText(change(value / 1000));
                mMediaPlayer.seekTo(value);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.player_pause:

                //切换图片
                picture = !picture;
                if(picture) {
                    mPauseButton.setImageResource(R.drawable.cideo_play);
                }else {
                    mPauseButton.setImageResource(R.drawable.pause_video);
                }

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
                finish();
                break;
            case R.id.player_download:
                //检查是否有权限，没有就申请
                if(ContextCompat.checkSelfPermission(PlayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PlayActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    startDownload(Utility.sVideos.get(mPosition).getPlayUri());
                }
                break;
            case R.id.pre_video:
                if(mPosition > 0) {
                    //启动另一个PlayVideoActicity
                    Intent preIntent = new Intent(PlayActivity.this, PlayActivity.class);
                    preIntent.putExtra(RecyclerViewAdapter.EXTRA_POSITION, mPosition - 1);
                    startActivity(preIntent);
                    //结束当前的活动
                    finish();
                }
                break;

            case R.id.next_video:
                if(mPosition < Utility.sVideos.size()) {
                    //启动另一个PlayVideoActicity
                    Intent nextIntent = new Intent(PlayActivity.this, PlayActivity.class);
                    nextIntent.putExtra(RecyclerViewAdapter.EXTRA_POSITION, mPosition + 1);
                    startActivity(nextIntent);
                    //结束当前的活动
                    finish();
                }
                break;
        }
    }

    /**
     * 现在是一个空方法
     * @param address
     */
    private void startDownload(String address) {
    }

    /**
     * SurfaceHolder的回调方法
     */
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
            //开始试过将cancel方法防在onDestroy中，结果会爆炸，先将其放入surfaceDedtroyed中，可以正常运行
            if(mTimer != null){
                mTimer.cancel();
            }

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
        //存储当前的位置，以便下一次直接观看
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PlayActivity.this).edit();
        editor.putInt(MEMORY_POSITION + Utility.sVideos.get(mPosition).getTitle(),mMediaPlayer.getCurrentPosition());
        editor.apply();
        //当活动被摧毁时，释放资源
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }

        mMediaPlayer.release();


        super.onDestroy();
    }

    /**
     * 权限申请的回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownload(Utility.sVideos.get(mPosition).getPlayUri());
                }else{
                    Toast.makeText(this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
