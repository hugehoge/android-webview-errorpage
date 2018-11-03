package io.github.hugehoge.sample

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import androidx.databinding.DataBindingUtil
import io.github.hugehoge.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    // TODO: Move to companion object
    private val displayErrorContentCode by lazy {
        val html = assets.open("error.html")
                .bufferedReader()
                .readText()

        """document.documentElement.innerHTML = `$html`"""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for debug
        WebView.setWebContentsDebuggingEnabled(true)

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.webView.reload()
        }

        setupWebView()

        if (savedInstanceState == null) {
            binding.webView.loadUrl(INITIAL_PAGE_URL)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()

        binding.webView.onPause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        binding.webView.saveState(outState)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }


    private fun setupWebView() {
        // This setting is required
        binding.webView.settings.javaScriptEnabled = true
        // for debug
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        binding.webView.webViewClient =
                if (Build.VERSION.SDK_INT < 23) {
                    object : WebViewClient() {
                        override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)

                            view?.evaluateJavascript(displayErrorContentCode, null)
                            view?.stopLoading()
                        }
                    }
                } else {
                    object : WebViewClient() {
                        override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)

                            if (request?.isForMainFrame == true) {
                                view?.evaluateJavascript(displayErrorContentCode, null)
                                view?.stopLoading()
                            }
                        }
                    }
                }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                binding.swipeRefreshLayout.isRefreshing = newProgress in 1..99
            }
        }
    }


    companion object {
        private const val INITIAL_PAGE_URL = "https://www.example.com/"
    }
}
