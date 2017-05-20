package com.zhang.videoplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhang.videoplayer.PlayActivity;
import com.zhang.videoplayer.R;
import com.zhang.videoplayer.bean.Video;
import com.zhang.videoplayer.util.HttpUtil;
import com.zhang.videoplayer.util.SetBitmapListener;
import com.zhang.videoplayer.util.Utility;

import java.util.List;

/**
 * Created by 张 on 2017/5/19.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static final String EXTRA_POSITION = "this is position";

    private List<Video> mVideoList;
    private Context mContext;
    private Handler mHandler;//以便在子线程中更新UI

    public RecyclerViewAdapter(List<Video> videoList, Context context, Handler handler){
        mVideoList = videoList;
        mContext = context;
        mHandler = handler;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nameTextView.setText( mVideoList.get(position).getAuthorName());
        holder.titleTextView.setText(mVideoList.get(position).getTitle());
        holder.loveTextView.setText(mVideoList.get(position).getLoveNumber());
        holder.hateTextView.setText(mVideoList.get(position).getHateNumber());
        holder.timeTextView.setText(mVideoList.get(position).getTime());
        //异步加载图片，利用回调和handler更新图片
        HttpUtil.loadBitmap(mVideoList.get(position).getImageUri(), new SetBitmapListener() {//"http://pic3.zhimg.com/1a1ad1682cc17b7058633f2e87368976.jpg"
            @Override
            public void setBitmap(final Bitmap bitmap) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.imageView.setImageBitmap(bitmap);

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView nameTextView,titleTextView,timeTextView,hateTextView,loveTextView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.item_title);
            nameTextView = (TextView)itemView.findViewById(R.id.item_name);
            timeTextView = (TextView)itemView.findViewById(R.id.item_time);
            hateTextView = (TextView)itemView.findViewById(R.id.item_hate_number);
            loveTextView = (TextView)itemView.findViewById(R.id.item_love_number);
            imageView = (ImageView)itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeTextView.setTextColor(mContext.getResources().getColor(R.color.gray));
                    Intent intent = new Intent(mContext, PlayActivity.class);
                    intent.putExtra(EXTRA_POSITION,getLayoutPosition());
                    mContext.startActivity(intent);
                }
            });


        }
    }

}
