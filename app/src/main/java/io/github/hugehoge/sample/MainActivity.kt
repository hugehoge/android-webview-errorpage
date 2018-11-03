package io.github.hugehoge.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import androidx.databinding.DataBindingUtil
import io.github.hugehoge.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
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
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                view?.evaluateJavascript(DOM_REPLACE_JS, null)
                view?.stopLoading()
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

        private const val ERROR_PAGE_HTML = ""
        private const val DOM_REPLACE_JS = "document.documentElement.innerHTML = '$ERROR_PAGE_HTML'"
    }
}
