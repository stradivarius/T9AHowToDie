package it.stradivarius.t9ahowtodie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import it.stradivarius.t9ahowtodie.R
import it.stradivarius.t9ahowtodie.ui.theme.T9AHowToDieTheme

@Composable
fun BackgroundImg(blur: Boolean) {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.splashscreen),
            contentDescription = "Splashscreen",
            modifier = Modifier
                .fillMaxWidth())
    }

    if (blur) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)))
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun BackgroundPreview() {
    T9AHowToDieTheme {
        BackgroundImg(true)
    }
}