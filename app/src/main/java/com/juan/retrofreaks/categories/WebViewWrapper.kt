package com.juan.retrofreaks.categories

import android.view.View
import android.webkit.WebView

/*
This class creates and instantianes WebView's
 */
class WebViewWrapper(webView:WebView?,root: View) {
    private val webViewL:WebView = webView as WebView
    private val rootL = root

    fun getWebView() = webViewL
    fun getRoot() = rootL
}