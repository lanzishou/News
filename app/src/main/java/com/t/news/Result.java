package com.t.news;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/7/6.
 */

public class Result {
	public String stat;
	@SerializedName("data")
	public List<Data> dataList;
}
