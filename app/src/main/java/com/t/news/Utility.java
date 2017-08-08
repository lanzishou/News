package com.t.news;

import com.google.gson.Gson;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Utility {

	public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(address).build();
		client.newCall(request).enqueue(callback);
	}

	public static News handleNewsResponse(String response) {
		try{
			JSONObject jsonObject = new JSONObject(response);
			String newsContent = jsonObject.toString();
			return new Gson().fromJson(newsContent, News.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
