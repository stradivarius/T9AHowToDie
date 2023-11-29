package com.example.t9ahowtodie.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.t9ahowtodie.ui.D6
import com.example.t9ahowtodie.ui.theme.T9AHowToDieTheme
import java.lang.Integer.min
import java.util.ArrayList

const val MAX_ATTACKS_DIGITS = 3
const val MAX_TEST_DIGITS = 1
const val MAX_DICE_ROWS = 8

@Composable
fun TextComponent(
    text: String,
    size: TextUnit,
    weight: FontWeight = FontWeight.Bold,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Text (
        text = text,
        color = color,
        style = TextStyle(
            fontSize = size,
            fontWeight = weight),
        modifier = modifier)
}

@Composable
fun NavigationTextComponent(
    text: String,
) {
    TextComponent(
        text = text,
        size = 12.sp,
        color = MaterialTheme.colorScheme.secondary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldNumber(
    maxDigits: Int,
    textChangedCallback: (text: String) -> Unit,
    maxVal: Int = Int.MAX_VALUE
) {
    var currentValue by remember{
        mutableStateOf("")
    }
    TextField(
        value = currentValue,
        onValueChange = {// Otherwise just use "it"
            if (it.isEmpty()) currentValue = "1"
            else if (it.length <= maxDigits) currentValue = it
            else if (it.length == maxDigits + 1 && maxDigits == 1) // This is a workaround
                currentValue = it[maxDigits].toString()
            else if (it.toInt() > maxVal) currentValue = maxVal.toString()
            textChangedCallback(currentValue)
        },
        textStyle = TextStyle.Default.copy(fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary),
        placeholder = {
            Text(text = "1", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        },
        singleLine = true,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(40.dp))
            .width(120.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            disabledLabelColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun StatRadioButton(
    text: String,
    checked: Boolean,
    size: Dp = 50.dp,
    textsize: TextUnit = 20.sp,
    clickedColor: Color = MaterialTheme.colorScheme.secondary,
    callback: () -> Unit
) {
    Card (
        shape = CircleShape,
        modifier = Modifier
            .clickable { callback() }
            .padding(5.dp)
            .size(size)
            .border(
                width = 1.dp,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (checked)
                        clickedColor
                    else
                        MaterialTheme.colorScheme.primary
                ),
            contentAlignment = Alignment.Center,
        ) {
            TextComponent(
                text = text,
                size = textsize,
                color = if (checked)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun SmallRadioButton(
    text: String,
    checked: Boolean,
    callback: () -> Unit
) {
    StatRadioButton(
        text = text,
        checked = checked,
        size = 30.dp,
        textsize = 20.sp,
        clickedColor = MaterialTheme.colorScheme.tertiary
    ) {
        callback()
    }
}

@Composable
fun SixRadioButtons(diceFaces: ArrayList<String>, checkedIndex: Int,
                    callback: (selected: Int) -> Unit) {
    if (diceFaces.size != D6)
        throw IllegalStateException("Trying to roll a dice with illegal number of faces!")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
       for (i in diceFaces.indices) {
           StatRadioButton(text = diceFaces[i], i == checkedIndex) {
               callback(i)
               Log.d("hey","callback $checkedIndex")
           }
       }
    }
}

@Composable
fun RadioGrid(diceValues: List<String>, checkedIndex: Int,
                    callback: (selected: Int) -> Unit) {
    if (diceValues.size !in 0 .. D6 * MAX_DICE_ROWS)
        throw IllegalStateException("Trying to roll a dice with illegal number of faces!")

    for (row in 0 .. ((diceValues.size - 1).div(D6))) // Place 6 numbers each row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            for (i in (row * D6) until min((row + 1) * D6, diceValues.size)) {
                StatRadioButton(text = diceValues[i], i == checkedIndex) {
                    callback(i)
                    Log.d("hey","callback $checkedIndex")
                }
            }
        }
}


@Composable
fun StatModifierToggleButton(
    statuses: ArrayList<String>,
    currentStatus: Int,
    callback: () -> Unit
) {
    Card (
        shape = CircleShape,
        modifier = Modifier
            .clickable {
                callback()
            }
            .wrapContentHeight()
            .width(180.dp)
            .border(
                width = 1.dp,
                shape = CircleShape,
                color = if (currentStatus == 0)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.tertiary
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (currentStatus == 0)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.tertiary
                ),
            contentAlignment = Alignment.Center,
        ) {
            TextComponent(
                text = statuses[currentStatus],
                size = 20.sp,
                color = MaterialTheme.colorScheme.primary)
        }
    }

}

@Composable
fun OnOffToggleButton(
    text: String,
    checked: Boolean,
    callback: () -> Unit
) {
    Card (
        shape = CircleShape,
        modifier = Modifier
            .clickable {
                callback()
            }
            .wrapContentHeight()
            .width(180.dp)
            .border(
                width = 1.dp,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (!checked)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                ),
            contentAlignment = Alignment.Center,
        ) {
            TextComponent(
                text = text,
                size = 20.sp,
                color = if (!checked)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.primary)
        }
    }

}


@Preview
@Composable
fun previewWidget() {
    T9AHowToDieTheme {
        SixRadioButtons(arrayListOf("A", "2+", "3+", "4+", "5+", "6"), 3) {}
    }
}

@Preview
@Composable
fun previewToggle() {
    T9AHowToDieTheme {
        StatModifierToggleButton(
            arrayListOf("Reroll Success", "2+", "3+", "4+", "5+", "6"), 0
        ) {}
    }
}