package com.infusiblecoder.allinonevideodownloader.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.infusiblecoder.allinonevideodownloader.databinding.ActivityLoginInstagramBinding;
import com.infusiblecoder.allinonevideodownloader.models.instawithlogin.ModelInstagramPref;
import com.infusiblecoder.allinonevideodownloader.utils.SharedPrefsForInstagram;
import com.infusiblecoder.allinonevideodownloader.utils.iUtils;

import java.util.Random;


public class InstagramLoginActivity extends AppCompatActivity {

    private ActivityLoginInstagramBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginInstagramBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        LoadPage();
        binding.swipeRefreshLayout.setOnRefreshListener(this::LoadPage);

    }

    @SuppressLint("SetJavaScriptEnabled")
    public void LoadPage() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.clearCache(true);


        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true);
        binding.webView.setWebViewClient(new MyWebviewClient());
        CookieSyncManager.createInstance(InstagramLoginActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        binding.webView.loadUrl("https://www.instagram.com/accounts/login/");


        try {
            Random random = new Random();
            int a = random.nextInt(iUtils.UserAgentsListLogin.length);

            binding.webView.getSettings().setUserAgentString(iUtils.UserAgentsListLogin[a] + "");

        } catch (Exception e) {
            System.out.println("dsakdjasdjasd " + e.getMessage());

            binding.webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");

        }


        binding.webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                binding.swipeRefreshLayout.setRefreshing(progress != 100);
            }
        });
    }


    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies != null && !cookies.isEmpty()) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    private class MyWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView1, String url) {
            webView1.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);

            String cookies = CookieManager.getInstance().getCookie(str);

            try {
                String session_id = getCookie(str, "sessionid");
                String csrftoken = getCookie(str, "csrftoken");
                String userid = getCookie(str, "ds_user_id");
                if (session_id != null && csrftoken != null && userid != null) {


                    ModelInstagramPref instagramPref = new ModelInstagramPref(session_id, userid, cookies, csrftoken, "true");
                    SharedPrefsForInstagram sharedPrefsForInstagram = new SharedPrefsForInstagram(InstagramLoginActivity.this);

                    sharedPrefsForInstagram.setPreference(instagramPref);


                    webView.destroy();
                    Intent intent = new Intent();
                    intent.putExtra("result", "result");
                    setResult(RESULT_OK, intent);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onReceivedError(WebView webView, int i, String str, String str2) {
            super.onReceivedError(webView, i, str, str2);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            return super.shouldInterceptRequest(webView, webResourceRequest);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
            return super.shouldOverrideUrlLoading(webView, webResourceRequest);
        }
    }
}