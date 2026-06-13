package com.example.ui.shared

import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    textSize: Float = 14f,
    maxLines: Int? = null,
    isSelectable: Boolean = false,
    quoteBackgroundColor: Color? = null,
    quoteTextColor: Color? = null,
    onLinkClick: ((String) -> Unit)? = null
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            TextView(context).apply {
                setTextColor(textColor.toArgb())
                this.textSize = textSize
                this.setTextIsSelectable(isSelectable)
                linksClickable = true
                movementMethod = android.text.method.LinkMovementMethod.getInstance()
                if (maxLines != null) {
                    this.maxLines = maxLines
                    this.ellipsize = android.text.TextUtils.TruncateAt.END
                }
            }
        },
        update = { view ->
            // Replace 2ch quote spans with blockquote
            val processedHtml = html.replace(
                Regex("<span class=\"unkfunc\">(.*?)</span>", RegexOption.DOT_MATCHES_ALL),
                "<blockquote>$1</blockquote>"
            )

            val spanned = HtmlCompat.fromHtml(processedHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)
            
            val spannable = android.text.SpannableStringBuilder(spanned)
            
            if (quoteBackgroundColor != null || quoteTextColor != null) {
                val quoteSpans = spannable.getSpans(0, spannable.length, android.text.style.QuoteSpan::class.java)
                for (span in quoteSpans) {
                    val start = spannable.getSpanStart(span)
                    val end = spannable.getSpanEnd(span)
                    val flags = spannable.getSpanFlags(span)
                    spannable.removeSpan(span)
                    
                    // Add background color
                    if (quoteBackgroundColor != null) {
                        spannable.setSpan(
                            android.text.style.BackgroundColorSpan(quoteBackgroundColor.toArgb()),
                            start,
                            end,
                            flags
                        )
                    }
                    // Add text color
                    if (quoteTextColor != null) {
                       spannable.setSpan(
                           android.text.style.ForegroundColorSpan(quoteTextColor.toArgb()),
                           start,
                           end,
                           flags
                       )
                    }
                }
            }

            if (onLinkClick != null) {
                val urls = spannable.getSpans(0, spannable.length, android.text.style.URLSpan::class.java)
                for (url in urls) {
                    val start = spannable.getSpanStart(url)
                    val end = spannable.getSpanEnd(url)
                    val flags = spannable.getSpanFlags(url)
                    spannable.removeSpan(url)
                    spannable.setSpan(object : android.text.style.ClickableSpan() {
                        override fun onClick(widget: android.view.View) {
                            onLinkClick(url.url)
                        }
                    }, start, end, flags)
                }
            }
            view.text = spannable
        }
    )
}
