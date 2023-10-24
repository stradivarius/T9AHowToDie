package com.example.t9ahowtodie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.t9ahowtodie.R
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme

@Composable
fun SplashScreen(navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        /* Background */
        Image(
            painter = painterResource(id = R.drawable.splashscreen),
            contentDescription = "Splashscreen",
            modifier = Modifier
                .align(alignment = Alignment.BottomStart)
                .fillMaxWidth())

        /* Labels */
        Text(
            text = "Welcome to",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(
                    x = 0.dp,
                    y = 28.dp
                )
                .requiredWidth(width = 272.dp)
                .requiredHeight(height = 76.dp))
        Text(
            text = "T9A \nHow to DIE!",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(
                    x = 0.dp,
                    y = 79.dp
                )
                .requiredWidth(width = 272.dp)
                .requiredHeight(height = 100.dp))

        Button(
            onClick = { navHostController.navigate(Routes.ATTACKS_STATS) },
            colors = ButtonDefaults
                .buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .wrapContentSize()
                .align(alignment = Alignment.Center)
                .offset(y = (-40).dp)
                .clip(shape = RoundedCornerShape(40.dp))
        ) {
            Text(
                text = "Get Started",
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 1.9.em,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .requiredWidth(width = 171.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = R.drawable.rightarrow),
                contentDescription = "image 1",
                modifier = Modifier
                    .requiredWidth(width = 30.dp)
                    .requiredHeight(height = 30.dp))
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun SplashScreenPreview() {
    T9AHowToDieTheme {
       SplashScreen(rememberNavController())
    }
}
