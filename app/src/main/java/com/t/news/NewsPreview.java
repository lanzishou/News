package com.t.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class NewsPreview extends Fragment {

	private List<Data> newsList = new ArrayList<>();
	private News news;
	private int mPagePosition = 0;
	private int mPageSize = 10;
	private boolean hasMoreData = true;
	private PreviewAdapter adapter;
	private String path[] = new String[] {"http://v.juhe.cn/toutiao/index?type=top&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=shehui&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=guonei&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=guoji&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=yule&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=tiyu&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=junshi&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=keji&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=caijing&key=073b888449d480372f360d7bf4c37ecf",
			"http://v.juhe.cn/toutiao/index?type=shishang&key=073b888449d480372f360d7bf4c37ecf",
	};
	RecyclerView RecyclerView;



	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_preview,container, false);
		RecyclerView = (RecyclerView)view.findViewById(R.id.news_preview);
		return view;
	}
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		int tabIndex = bundle.getInt("tabIndex");
		switch (tabIndex) {
			case 0:
				new MyTask().requestNews(getActivity(),path[0]);
				break;
			case 1:
				new MyTask().requestNews(getActivity(),path[1]);
				break;
			case 2:
				new MyTask().requestNews(getActivity(),path[2]);
				break;
			case 3:
				new MyTask().requestNews(getActivity(),path[3]);
				break;
			case 4:
				new MyTask().requestNews(getActivity(),path[4]);
				break;
			case 5:
				new MyTask().requestNews(getActivity(),path[5]);
				break;
			case 6:
				new MyTask().requestNews(getActivity(),path[6]);
				break;
			case 7:
				new MyTask().requestNews(getActivity(),path[7]);
				break;
			case 8:
				new MyTask().requestNews(getActivity(),path[8]);
				break;
			case 9:
				new MyTask().requestNews(getActivity(),path[9]);
				break;

			default:
				break;
		}
	}

	private class MyTask {
		private void requestNews(final Activity activity, String url) {

			Utility.sendOkHttpRequest(url, new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "获取新闻信息失败", Toast.LENGTH_SHORT).show();
						}
					});
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					final String responseText = response.body().string();
					news = Utility.handleNewsResponse(responseText);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (news != null && "成功的返回".equals(news.reason)) {
								LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());
								LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
								RecyclerView.setLayoutManager(LayoutManager);
								adapter =new PreviewAdapter(getContext(), newsList);
								RecyclerView.setAdapter(adapter);
								for (int i = 0; i < 10; i++){
									newsList.add(news.result.dataList.get(i));
								}
								mPagePosition = (mPagePosition + 1) * mPageSize;
							} else {
								Toast.makeText(activity, "获取新闻信息失败", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});

		}
	}

	class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
		private List<Data> dataList;
		private Context mContext;
		class ViewHolder extends RecyclerView.ViewHolder{
			ImageView newsImage;
			TextView newsTitle;
			TextView authorName;
			View newsView;
			ViewHolder(View view){
				super(view);
				newsView = view;
				newsImage = (ImageView) view.findViewById(R.id.news_image);
				newsTitle = (TextView) view.findViewById(R.id.news_title);
				authorName = (TextView) view.findViewById(R.id.author_name);
			}
		}


		void appendData() {
			for(int i = 0; i < mPageSize; i++) {
				dataList.add(news.result.dataList.get(mPagePosition + i));
			}
			mPagePosition = mPagePosition + mPageSize;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (RecyclerView.getScrollState() == android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE) {
						notifyDataSetChanged();
					}
				}
			});
		}

		PreviewAdapter(Context mContext, List<Data> dataList){
			this.mContext = mContext;
			this.dataList = dataList;
		}
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			if (viewType == R.layout.list_item_no_more) {
				View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
				return new NoMoreItemVH(view);
			} else if (viewType == R.layout.list_item_loading) {
				View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
				return new LoadingItemVH(view);
			} else {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_preview_item, parent, false);
				return new ViewHolder(view);
			}


		}

		@Override
		public void onBindViewHolder(PreviewAdapter.ViewHolder holder, int position) {
			if (holder instanceof LoadingItemVH) {
				if (mPagePosition < news.result.dataList.size()) {
					appendData();
				}else{
					hasMoreData = false;
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (RecyclerView.getScrollState() == android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE) {
									notifyDataSetChanged();
								}
							}
						});
					}
			} else if (holder instanceof NoMoreItemVH) {

			} else {
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

		}

		public int getItemViewType(int position) {
			if (position == getItemCount() - 1) {
				if (hasMoreData) {
					return R.layout.list_item_loading;
				} else {
					return R.layout.list_item_no_more;
				}
			} else {
				return R.layout.news_preview_item;
			}
		}

		@Override
		public int getItemCount() {
			return dataList.size() + 1;
		}
		class LoadingItemVH extends ViewHolder {

			LoadingItemVH(View itemView) {
				super(itemView);
			}

		}

		class NoMoreItemVH extends ViewHolder {

			NoMoreItemVH(View itemView) {
				super(itemView);
			}
		}
	}
}