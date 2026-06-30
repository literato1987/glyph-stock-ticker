package com.nothinglondon.sdkdemo.tesla

import android.content.Context
import android.util.Log
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixObject
import com.nothinglondon.sdkdemo.demos.GlyphMatrixService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeslaStockService : GlyphMatrixService("Tesla-Stock") {

    private lateinit var bgScope: CoroutineScope
    private var refreshJob: Job? = null
    private var latestQuote: StockQuote? = null
    private var currentAsset: StockAsset = StockAsset.TESLA

    override fun performOnServiceConnected(
        context: Context,
        glyphMatrixManager: GlyphMatrixManager,
    ) {
        bgScope = CoroutineScope(Dispatchers.Default)
        currentAsset = StockAsset.TESLA
        showLoading(glyphMatrixManager)
        refreshJob = bgScope.launch {
            while (isActive) {
                refreshQuote(glyphMatrixManager)
                delay(REFRESH_INTERVAL_MS)
            }
        }
    }

    override fun performOnServiceDisconnected(context: Context) {
        refreshJob?.cancel()
        refreshJob = null
        bgScope.cancel()
    }

    override fun onTouchPointLongPress() {
        glyphMatrixManager?.let { manager ->
            currentAsset = currentAsset.next()
            bgScope.launch {
                refreshQuote(manager, forceLoading = true)
            }
        }
    }

    override fun onAodTick() {
        glyphMatrixManager?.let { manager ->
            bgScope.launch {
                refreshQuote(manager)
            }
        }
    }

    private suspend fun refreshQuote(
        glyphMatrixManager: GlyphMatrixManager,
        forceLoading: Boolean = false,
    ) {
        if (forceLoading) {
            withContext(Dispatchers.Main) {
                showLoading(glyphMatrixManager)
            }
        }

        try {
            val quote = StockFetcher.fetch(currentAsset)
            latestQuote = quote
            withContext(Dispatchers.Main) {
                showQuote(glyphMatrixManager, quote)
            }
        } catch (error: Exception) {
            Log.e(TAG, "Failed to fetch ${currentAsset.yahooSymbol} quote", error)
            withContext(Dispatchers.Main) {
                showError(glyphMatrixManager)
            }
        }
    }

    private fun showLoading(glyphMatrixManager: GlyphMatrixManager) {
        renderDisplay(glyphMatrixManager, currentAsset.displaySymbol, "---", "...")
    }

    private fun showError(glyphMatrixManager: GlyphMatrixManager) {
        val priceLine = latestQuote?.let { StockFetcher.formatPrice(it.price) } ?: "ERR"
        renderDisplay(glyphMatrixManager, currentAsset.displaySymbol, priceLine, "retry")
    }

    private fun showQuote(glyphMatrixManager: GlyphMatrixManager, quote: StockQuote) {
        renderDisplay(
            glyphMatrixManager = glyphMatrixManager,
            symbolLine = quote.symbol,
            priceLine = StockFetcher.formatPrice(quote.price),
            changeLine = StockFetcher.formatChangePercent(quote.changePercent),
        )
    }

    private fun renderDisplay(
        glyphMatrixManager: GlyphMatrixManager,
        symbolLine: String,
        priceLine: String,
        changeLine: String,
    ) {
        val symbolObject = GlyphMatrixObject.Builder()
            .setImageSource(
                MatrixPixelFont.toDrawableBitmap(
                    applicationContext,
                    symbolLine,
                    MatrixCircle.LAYER_TOP_Y,
                ),
            )
            .setScale(100)
            .setPosition(0, 0)
            .build()

        val priceObject = if (needsPixelFont(priceLine)) {
            GlyphMatrixObject.Builder()
                .setImageSource(
                    MatrixPixelFont.toDrawableBitmap(
                        applicationContext,
                        priceLine,
                        MatrixCircle.LAYER_MID_Y,
                    ),
                )
                .setScale(100)
                .setPosition(0, 0)
                .build()
        } else {
            GlyphMatrixObject.Builder()
                .setText(priceLine)
                .setPosition(MatrixCircle.centerX(priceLine.length), MatrixCircle.LAYER_MID_Y)
                .build()
        }

        val changeObject = if (needsPixelFont(changeLine)) {
            GlyphMatrixObject.Builder()
                .setImageSource(
                    MatrixPixelFont.toDrawableBitmap(
                        applicationContext,
                        changeLine,
                        MatrixCircle.LAYER_LOW_Y,
                    ),
                )
                .setScale(100)
                .setPosition(0, 0)
                .build()
        } else {
            GlyphMatrixObject.Builder()
                .setText(changeLine)
                .setPosition(MatrixCircle.centerX(changeLine.length), MatrixCircle.LAYER_LOW_Y)
                .build()
        }

        val frame = GlyphMatrixFrame.Builder()
            .addTop(symbolObject)
            .addMid(priceObject)
            .addLow(changeObject)
            .build(applicationContext)

        glyphMatrixManager.setMatrixFrame(frame.render())
    }

    private fun needsPixelFont(text: String): Boolean {
        return text.any { it in ".+%" }
    }

    private companion object {
        private const val TAG = "TeslaStockService"
        private const val REFRESH_INTERVAL_MS = 5 * 60 * 1000L
    }
}