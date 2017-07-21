package com.t.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;



public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
	private List<Data> dataList;
	private Context mContext;
	static class ViewHolder extends RecyclerView.ViewHolder{
		ImageView newsImage;
		TextView newsTitle;
		TextView authorName;
		View newsView;
		public ViewHolder(View view){
			super(view);
			newsView = view;
			newsImage = (ImageView) view.findViewById(R.id.news_image);
			newsTitle = (TextView) view.findViewById(R.id.news_title);
			authorName = (TextView) view.findViewById(R.id.author_name);
		}
	}
	public PreviewAdapter(Context mContext, List<Data> dataList){
		this.mContext = mContext;
		this.dataList = dataList;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_preview_item, parent, false);
		ViewHolder holder = new ViewHolder(view);
		return holder;

	}

	@Override
	public void onBindViewHolder(PreviewAdapter.ViewHolder holder, int position) {
		final Data data = dataList.get(position);
		String pic =data.thumbnail_pic_s;
		Glide.with(mContext).load(pic).into(holder.newsImage);
		holder.newsTitle.setText(data.title);
		holder.authorName.setText(data.author_name);
		holder.newsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(mContext,ShowNewsActivity.class);
				intent.putExtra("url",data.url);
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}
}
