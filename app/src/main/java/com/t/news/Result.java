package com.t.news;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Result {
	public String stat;
	@SerializedName("data")
	public List<Data> dataList;
}
