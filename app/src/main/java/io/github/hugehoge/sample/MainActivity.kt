package io.github.hugehoge.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.webkit.WebViewClientCompat
import io.github.hugehoge.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding.webView.webViewClient = object : WebViewClientCompat() {

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
