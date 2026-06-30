package com.nothinglondon.sdkdemo

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nothinglondon.sdkdemo.tesla.StockAsset
import com.nothinglondon.sdkdemo.tesla.StockFetcher
import com.nothinglondon.sdkdemo.tesla.StockQuote
import com.nothinglondon.sdkdemo.ui.theme.NothingAndroidSDKDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NothingAndroidSDKDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TeslaStockScreen(
                        modifier = Modifier.padding(innerPadding),
                        onActivateToy = ::openGlyphToyManager,
                    )
                }
            }
        }
    }

    private fun openGlyphToyManager() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.nothing.thirdparty",
                "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity",
            )
        }
        startActivity(intent)
    }
}

@Composable
fun TeslaStockScreen(
    modifier: Modifier = Modifier,
    onActivateToy: () -> Unit,
) {
    var quote by remember { mutableStateOf<StockQuote?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            quote = StockFetcher.fetch(StockAsset.TESLA)
        } catch (error: Exception) {
            errorMessage = error.localizedMessage ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Tesla Glyph Stock",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        when {
            isLoading -> {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.loading_price))
            }

            quote != null -> {
                val currentQuote = quote!!
                Text(
                    text = currentQuote.symbol,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "$${StockFetcher.formatPrice(currentQuote.price)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = StockFetcher.formatChangePercent(currentQuote.changePercent),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            else -> {
                Text(
                    text = stringResource(R.string.price_error),
                    color = MaterialTheme.colorScheme.error,
                )
                errorMessage?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.instructions),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.long_press_hint),
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            onClick = onActivateToy,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.activate_toy))
        }
    }
}

