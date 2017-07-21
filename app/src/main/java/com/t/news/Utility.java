package com.t.news;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/5.
 */

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
