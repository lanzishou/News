package com.t.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/10.
 */

public class NewsPreview extends Fragment {

	private List<Data> newsList= new ArrayList<>();
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
				final News news = Utility.handleNewsResponse(responseText);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (news != null && "成功的返回".equals(news.reason)) {
							LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());
							LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
							RecyclerView.setLayoutManager(LayoutManager);
							RecyclerView.Adapter Adapter =new PreviewAdapter(getContext(), newsList);
							RecyclerView.setAdapter(Adapter);
							showNewsInfo(news);
						} else {
							Toast.makeText(activity, "获取新闻信息失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

	}
}
	private void showNewsInfo(News news){
		for (int i = 0; i < news.result.dataList.size(); i++){
			newsList.add(news.result.dataList.get(i));
		}
	}
}
