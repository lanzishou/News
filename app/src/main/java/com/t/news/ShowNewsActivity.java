package com.t.news;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class ShowNewsActivity extends AppCompatActivity {
	private Button backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_news);
		backButton = (Button) findViewById(R.id.back_button);
		Intent intent = getIntent();
		WebView webView = (WebView) findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(intent.getStringExtra("url"));
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
