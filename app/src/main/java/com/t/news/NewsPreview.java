package com.t.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
	public SwipeRefreshLayout swipeRefresh;
	private List<Data> newsList = new ArrayList<>();
	private List<Data> moreData = new ArrayList<>();
	private List<Data> reData = new ArrayList<>();
	private List<Data> allData = new ArrayList<>();
	private News news;
	private LinearLayoutManager LayoutManager;
	private RecyclerView RecyclerView;
	private PreviewAdapter adapter;
	private int mPagePosition = 10;
	private int tabIndex;
	private SharedPreferences prefs;
	private String newsString;
	private Handler handler = new Handler();
	private String path[] = new String[]{"http://v.juhe.cn/toutiao/index?type=top&key=073b888449d480372f360d7bf4c37ecf",
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

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_preview, container, false);
		RecyclerView = (RecyclerView) view.findViewById(R.id.news_preview);
		swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sfl);
		swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
		LayoutManager = new LinearLayoutManager(getContext());
		LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		RecyclerView.setLayoutManager(LayoutManager);
		adapter = new PreviewAdapter(getContext(), RecyclerView);
		RecyclerView.setAdapter(adapter);
		Bundle bundle = getArguments();
		tabIndex = bundle.getInt("tabIndex");
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		newsString = prefs.getString(path[tabIndex],null);
		if (newsString != null){
			news = Utility.handleNewsResponse(newsString);
			allData.addAll(news.result.dataList);
			showNews();
		}else {
			new MyTask().requestNews(getActivity(), path[tabIndex]);
			showNews();
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Utility.sendOkHttpRequest(path[tabIndex], new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						e.printStackTrace();
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getActivity(), "获取新闻信息失败", Toast.LENGTH_SHORT).show();
								swipeRefresh.setRefreshing(false);
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String responseText = response.body().string();
						news = Utility.handleNewsResponse(responseText);
						reData.clear();
						for (int i = 0; i < news.result.dataList.size(); i ++){
							if(!allData.contains(news.result.dataList.get(i))){
								reData.add(news.result.dataList.get(i));
							}
						}
						if (reData.isEmpty()){
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getContext(), "没有新数据", Toast.LENGTH_SHORT).show();
									swipeRefresh.setRefreshing(false);
								}
							});
						}else {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									newsList.addAll(0, reData);
									allData.addAll(0, reData);
									mPagePosition = mPagePosition + reData.size();
									swipeRefresh.setRefreshing(false);
									adapter.notifyDataSetChanged();
								}
							});
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
							editor.putString(path[tabIndex],responseText);
							editor.apply();
						}
					}
				});
			}
		});
		adapter.setOnMoreDataLoadListener(new LoadMoreDataListener() {
			@Override
			public void loadMoreData() {
				//加入null值此时adapter会判断item的type
				if (mPagePosition < allData.size()) {
					newsList.add(null);
					adapter.notifyDataSetChanged();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							//移除刷新的progressBar
							newsList.remove(newsList.size() - 1);
							adapter.notifyDataSetChanged();
							initMoreData();
							newsList.addAll(moreData);
							adapter.notifyDataSetChanged();
							adapter.setLoaded();
						}
					}, 2000);
				}
			}
		});

	}

	private class MyTask {
		private void requestNews(final Activity activity,final String url) {

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
					allData.addAll(news.result.dataList);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (news != null && "成功的返回".equals(news.reason)) {
								SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
								editor.putString(url,responseText);
								editor.apply();
							} else {
								Toast.makeText(activity, "获取新闻信息失败", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});

		}
	}

	private void showNews(){
		newsList.clear();
		for (int i = 0; i < 10; i++) {
			newsList.add(allData.get(i));
		}
		adapter.setData(newsList);
		adapter.notifyDataSetChanged();
	}

	private void initMoreData() {
		moreData.clear();
		for (int i = 0; i < 10; i++) {
			moreData.add(allData.get(mPagePosition + i));
		}
		mPagePosition = mPagePosition + 10;
	}

	class PreviewAdapter extends RecyclerView.Adapter {
		private Context mContext;
		private LayoutInflater inflater;
		private RecyclerView mRecyclerView;
		private int totalItemCount;
		private int lastVisibleItemPosition;
		private boolean isLoading;
		private int visibleThreshold = 1;
		private static final int VIEW_ITEM = 0;
		private static final int VIEW_PROG = 1;
		private List<Data> dataList;
		private LoadMoreDataListener mMoreDataListener;

		PreviewAdapter(Context context, RecyclerView recyclerView) {
			mContext = context;
			inflater = LayoutInflater.from(context);
			mRecyclerView = recyclerView;
			if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
				final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				//mRecyclerView添加滑动事件监听
				mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
					@Override
					public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
						super.onScrolled(recyclerView, dx, dy);
						totalItemCount = linearLayoutManager.getItemCount();
						lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
						if (!isLoading && totalItemCount <= (lastVisibleItemPosition + visibleThreshold)) {
							//此时是刷新状态
							if (mMoreDataListener != null)
								mMoreDataListener.loadMoreData();
							isLoading = true;
						}
					}
				});
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			RecyclerView.ViewHolder holder;
			if (viewType == VIEW_ITEM) {
				holder = new MyViewHolder(inflater.inflate(R.layout.news_preview_item, parent, false));
			} else {
				holder = new MyProgressViewHolder(inflater.inflate(R.layout.list_item_loading, parent, false));
			}
			return holder;

		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			if (holder instanceof MyViewHolder) {
				final Data data = dataList.get(position);
				String pic = data.thumbnail_pic_s;
				Glide.with(mContext).load(pic).into(((MyViewHolder) holder).newsImage);
				((MyViewHolder) holder).newsTitle.setText(data.title);
				((MyViewHolder) holder).authorName.setText(data.author_name);
				((MyViewHolder) holder).newsView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(mContext, ShowNewsActivity.class);
						intent.putExtra("url", data.url);
						mContext.startActivity(intent);
					}
				});
			} else if (holder instanceof MyProgressViewHolder) {
				if (((MyProgressViewHolder) holder).pb != null)
					((MyProgressViewHolder) holder).pb.setIndeterminate(true);
			}
		}

		@Override
		public int getItemCount() {
			return dataList == null ? 0 : dataList.size();
		}

		public void setLoaded() {
			isLoading = false;
		}

		class MyViewHolder extends RecyclerView.ViewHolder {
			ImageView newsImage;
			TextView newsTitle;
			TextView authorName;
			View newsView;

			MyViewHolder(View view) {
				super(view);
				newsView = view;
				newsImage = (ImageView) view.findViewById(R.id.news_image);
				newsTitle = (TextView) view.findViewById(R.id.news_title);
				authorName = (TextView) view.findViewById(R.id.author_name);
			}
		}

		public class MyProgressViewHolder extends RecyclerView.ViewHolder {
			private final ProgressBar pb;

			public MyProgressViewHolder(View itemView) {
				super(itemView);
				pb = (ProgressBar) itemView.findViewById(R.id.pb);
			}
		}

		public void setData(List<Data> data) {
			dataList = data;
		}

		public void setOnMoreDataLoadListener(LoadMoreDataListener onMoreDataLoadListener) {
			mMoreDataListener = onMoreDataLoadListener;
		}

		public int getItemViewType(int position) {
			return dataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;

		}
	}
}