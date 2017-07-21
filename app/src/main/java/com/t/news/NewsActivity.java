package com.t.news;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity {
	private List<String> types = new ArrayList<>();
	private NewsPreview fragment;
	private List<Fragment> fragmentList;
	private ViewPager viewPager;
	private List<Boolean> isClicks;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initTypes();
		initFragment();
		isClicks = new ArrayList<>();
		isClicks.add(true);
		for(int i = 1;i<types.size();i++){
			isClicks.add(false);
		}
		FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragmentList);
		viewPager = (ViewPager)findViewById(R.id.view_pager);
		viewPager.setAdapter(adapter);
		final RecyclerView typeRecyclerView = (RecyclerView) findViewById(R.id.type_choose);
		LinearLayoutManager typeLayoutManager = new LinearLayoutManager(this);
		typeLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		typeRecyclerView.setLayoutManager(typeLayoutManager);
		final RecyclerView.Adapter typeAdapter =new TypeAdapter(types);
		typeRecyclerView.setAdapter(typeAdapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
				typeRecyclerView.smoothScrollToPosition(position);
				for(int i = 0;i<types.size();i++){
					isClicks.set(i,false);
				}
				isClicks.set(position,true);
				typeAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		viewPager.setOffscreenPageLimit(0);

	}
	private void initFragment() {
		fragmentList = new ArrayList<Fragment>();
		for (int i = 0; i < 10; i++) {
			fragment = new NewsPreview();
			Bundle bundle = new Bundle();
			bundle.putSerializable("tabIndex", i);
			fragment.setArguments(bundle);
			fragmentList.add(fragment);
		}
	}



	private void initTypes(){
		types.add("头条");
		types.add("社会");
		types.add("国内");
		types.add("国际");
		types.add("娱乐");
		types.add("体育");
		types.add("军事");
		types.add("科技");
		types.add("财经");
		types.add("时尚");
	}

	private class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
		private List<String> typeList;

		class ViewHolder extends RecyclerView.ViewHolder{
			TextView typeView;
			public ViewHolder(View view){
				super(view);
				typeView = (TextView) view.findViewById(R.id.type_view);
			}
		}
		public TypeAdapter(List<String> typeList){
			this.typeList = typeList;

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_item, parent,false);
			final ViewHolder holder = new ViewHolder(view);
			return holder;
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder,final int position) {
			String type = typeList.get(position);
			holder.typeView.setText(type);
			holder.itemView.setTag(holder.typeView);
			if(isClicks.get(position)){
				holder.typeView.setTextColor(Color.parseColor("#3366CC"));
			}else {
				holder.typeView.setTextColor(Color.GRAY);
			}
			holder.itemView.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view) {
					viewPager.setCurrentItem(position);
					for(int i = 0;i<typeList.size();i++){
						isClicks.set(i,false);
					}
					isClicks.set(position,true);
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public int getItemCount() {
			return typeList.size();
		}

	}
}
