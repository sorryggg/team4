package com.example.cse.tue_sol;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by erunn on 2017-06-22.
 */

public class SeasonTicketFragment extends Fragment {
    private WebView webView;
    final static String url = "http://team4team4.esy.es/passdata.php";
    String myUrl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_season_ticket, container, false);
        webView = (WebView)view.findViewById(R.id.scene_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        if(myUrl == null)
            myUrl = url;
        webView.loadUrl(myUrl);

        return view;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            myUrl = url;
            view.loadUrl(url);
            return true;
        }
    }
}
