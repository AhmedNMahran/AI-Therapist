package com.github.ahmednmahran.aitherapist.ui.a2ui

import androidx.compose.material3.a2ui.A2uiSurface
import androidx.compose.material3.a2ui.catalog.MaterialA2uiBasicCatalogV1Defaults
import androidx.compose.material3.a2ui.catalog.materialA2uiBasicCatalogV1
import androidx.a2ui.compose.runtime.A2uiMessageParser
import androidx.a2ui.compose.ui.A2uiMessageProcessor
import androidx.a2ui.model.catalog.functions.A2uiLocaleProvider
import androidx.a2ui.model.processor.A2uiMessageParser
import androidx.a2ui.model.processor.A2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.processor.processInput
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class A2uiHostState {
    val catalog = materialA2uiBasicCatalogV1(
        image = MaterialA2uiBasicCatalogV1Defaults.image { url, description, scale, modifier, onError ->
            Box(modifier = modifier)
        },
        video = MaterialA2uiBasicCatalogV1Defaults.video { url, modifier, onError ->
            Box(modifier = modifier)
        },
        audioPlayer = MaterialA2uiBasicCatalogV1Defaults.audioPlayer { url, description, modifier, onError ->
            Box(modifier = modifier)
        },
        urlOpener = { /* open url */ },
        messageFormatter = { pattern, _, _ -> pattern },
        localeProvider = A2uiLocaleProvider.Default
    )

    val processor: A2uiMessageProcessor = A2uiMessageProcessor(catalogs = listOf(catalog))
    val parser: A2uiMessageParser<String> = A2uiMessageParser()

    fun processMessage(json: String) {
        processor.processInput(parser, json)
    }
}

@Composable
fun RenderA2uiSurface(surface: A2uiSurfaceModel, modifier: Modifier = Modifier) {
    A2uiSurface(
        surfaceModel = surface,
        modifier = modifier
    )
}
