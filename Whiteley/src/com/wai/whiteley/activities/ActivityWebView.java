package com.wai.whiteley.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wai.whiteley.R;
import com.wai.whiteley.base.BaseActivity;
import com.wai.whiteley.config.Constants;
import com.wai.whiteley.util.CommonUtil;
import com.wai.whiteley.util.FontUtils;

public class ActivityWebView extends BaseActivity {

	public static final String EXTRA_URL = "extra_url";
	public static final String EXTRA_TITLE = "extra_title";
	
	public static ActivityWebView instance = null;

	private TextView txt_headertitle;
	private LinearLayout layer_main;
	private WebView mWebView;

	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		instance = this;

		txt_headertitle = (TextView) findViewById(R.id.txt_headertitle);
		
		String strTitle = "";
		if(getIntent().hasExtra(EXTRA_TITLE)) {
			strTitle = getIntent().getStringExtra(EXTRA_TITLE);
		}
		txt_headertitle.setText(strTitle);

		layer_main = (LinearLayout) findViewById(R.id.layer_main);
		mWebView = (WebView) findViewById(R.id.webview_detail);
		
		// set fonts
		FontUtils.setTypeface(txt_headertitle, FontUtils.font_HelveticaNeueUltraLight, false);
		
		webView(getIntent().getStringExtra(EXTRA_URL));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right, R.anim.out_right);
	}

	public void onBackActivity(View paramView) {
		onBackPressed();
	}

	public void onMore(View paramView) {
		CommonUtil.makeBlurAndStartActivity(this, layer_main);
	}

	private void webView(String url) {
		//mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/20 Safari/537.31");
	      mWebView.getSettings().setJavaScriptEnabled(true);
	      mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	      mWebView.getSettings().setLoadWithOverviewMode(true);
	      mWebView.getSettings().setUseWideViewPort(true);
	      mWebView.setWebChromeClient(new WebChromeClient() {
	      	@Override
	      	public void onReceivedIcon(WebView view, Bitmap icon) {
	      		super.onReceivedIcon(view, icon);
	      	}
	      	
	      	@Override
	      	public void onReceivedTitle(WebView view, String title) {
	      		super.onReceivedTitle(view, title);
	      	}
	      });
	      mWebView.setWebViewClient(new WebViewClient() {
	      	
	          @Override
	          public void onPageStarted(WebView view, String url, Bitmap favicon) {
	              super.onPageStarted(view, url, favicon);
	              showValidationDialog();
	          }
	
	          @Override
	          public boolean shouldOverrideUrlLoading(WebView view, String url) {
	              //view.loadUrl(url);
	              //return shouldOverrideUrlLoading(view, url);
	              return super.shouldOverrideUrlLoading(view, url);
	              //return false;
	          }
	
	          @Override
	          public void onPageFinished(WebView view, String url) {
	        	  dismissValidatingDialog();
	              //urlGlobal = url;
	              super.onPageFinished(view, url);
	              
	          }
	
	          @Override
	          public void onReceivedError(WebView view, int errorCode,
	          		String description, String failingUrl) {
	        	  dismissValidatingDialog();
	          	super.onReceivedError(view, errorCode, description, failingUrl);
	          }
	
	      });
	
	//		_webSearch.getSettings().setJavaScriptEnabled(true);
	      mWebView.loadUrl(url);
	  }
	
	void showValidationDialog() {
        try {
        	if(pd == null) {
        		pd = new ProgressDialog(ActivityWebView.this);
        		pd.setMessage("Please wait...");
        	}
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void dismissValidatingDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.ACTIVITY_SELECT_EXPAND_MENU && resultCode == RESULT_OK) {
    		
			if (ActivityMain.instance != null)
				ActivityMain.instance.selectMainMenu();
    		
    	}
    }
}
