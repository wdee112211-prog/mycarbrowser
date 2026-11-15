
package com.example.mycarbrowser

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout

class MainActivity : Activity() {

    private lateinit var web: WebView
    private lateinit var address: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(20, 20, 20)
        window.navigationBarColor = Color.BLACK

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(25, 25, 25))
            setPadding(8, 8, 8, 8)
        }

        address = EditText(this).apply {
            hint = "Search or enter website"
            setSingleLine(true)
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setBackgroundColor(Color.rgb(55, 55, 55))
            imeOptions = EditorInfo.IME_ACTION_GO
        }

        val searchButton = Button(this).apply {
            text = "GO"
            setOnClickListener { openAddress() }
        }

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            addView(address, LinearLayout.LayoutParams(
                0, 55, 1f
            ))
            addView(searchButton)
        }

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        fun addControl(label: String, action: () -> Unit) {
            val button = Button(this).apply {
                text = label
                setOnClickListener { action() }
            }
            controls.addView(button, LinearLayout.LayoutParams(
                0, 50, 1f
            ))
        }

        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }

        addControl("Back") {
            if (web.canGoBack()) web.goBack()
        }

        addControl("Forward") {
            if (web.canGoForward()) web.goForward()
        }

        addControl("Refresh") {
            web.reload()
        }

        address.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                openAddress()
                true
            } else false
        }

        root.addView(bar)
        root.addView(controls)
        root.addView(web, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0, 1f
        ))

        setContentView(root)

        web.loadUrl("https://www.google.com")
    }

    private fun openAddress() {
        var text = address.text.toString().trim()
        if (text.isEmpty()) return

        val url = if (text.contains(" ") ||
            (!text.contains(".") && !text.startsWith("http"))) {
            "https://www.google.com/search?q=" +
                java.net.URLEncoder.encode(text, "UTF-8")
        } else if (!text.startsWith("http://") &&
            !text.startsWith("https://")) {
            "https://$text"
        } else text

        web.loadUrl(url)
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) {
            web.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }
}
