package com.example.genshin_original_resin_counter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.genshin_original_resin_counter.ui.theme.GenshinOriginalResinCounterTheme
import com.example.genshin_original_resin_counter.util.convertResinInTimeLeftMillis
import com.example.genshin_original_resin_counter.util.formatTimeToString
import com.example.genshin_original_resin_counter.util.validateInput
import kotlinx.coroutines.delay
import java.util.Objects
import kotlin.math.absoluteValue

// TODO Add a background
// TODO Make number o resin more in the sight on the page
// TODO Make so that resin gets updated when timer goes off by 8 minutes.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
//    val borderprop = BorderStroke(1.dp, Color.Red)
    var textsize = 17f
    var focusManager = LocalFocusManager.current

    var totalMillis by remember {
        mutableLongStateOf(0L)
    }
    var millisCounter by remember {
        mutableLongStateOf(totalMillis)
    }
    var input by remember {
        mutableStateOf("0")
    }


    LaunchedEffect(key1 = totalMillis) {
        while (millisCounter > 0) {
            delay(1000L)
            millisCounter -= 1000L
            if (millisCounter % (8L * 60L * 1000L) == 0L) {
                try {
                    input = validateInput((input.toInt() + 1).toString(), input)
                } catch (e: Error) {
                    input = "0"
                    Log.d("Err", "${e.printStackTrace()}")
                }
            }
        }
    }



    @Composable
    fun UniversalTextSize(textSize: Float = textsize): TextUnit = TextUnit(
        value = textSize, type = TextUnitType.Sp
    )

    @Composable
    fun UniversalTextStyleBold(): TextStyle = TextStyle(
        fontWeight = FontWeight.W900,
        fontSize = UniversalTextSize(),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontFamily = FontFamily(Font(R.font.zhcn))
    )


    GenshinOriginalResinCounterTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,

                            ) {

                            GenshinOriginalResinCounterTheme {

                                Text(
                                    text = "Original Resin Numbers: ",
                                    fontSize = UniversalTextSize(),
                                    fontWeight = FontWeight.W600
                                )
                                BasicTextField(
                                    value = input,
                                    onValueChange = {
                                        input = validateInput(input, it)
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Next
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier.width(IntrinsicSize.Min),
                                    keyboardActions = KeyboardActions(onNext = {
                                        if (input.isEmpty()) input = "0"
                                        // The timer updates
                                        totalMillis = convertResinInTimeLeftMillis(
                                            resin = mutableStateOf(
                                                value = input
                                            )
                                        )
                                        millisCounter = totalMillis

                                        focusManager.clearFocus(
                                            force = true
                                        )
                                    }),
                                    textStyle = UniversalTextStyleBold()
                                )
                                Text(
                                    text = "/200",
                                    fontSize = UniversalTextSize(),
                                    fontWeight = FontWeight.W600
                                )
                            }

                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                )

            }) { e ->
            Column(
                Modifier
                    .padding(paddingValues = e)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatTimeToString(millisCounter).value,
                    fontSize = UniversalTextSize(textsize + 4f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.size(120.dp))
                Text(
//                    text = "Click the top number in WHITE to update the timer!",
                    text = buildAnnotatedString {
                        append("Tap the ")

                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.W900,
                                fontFamily = FontFamily(Font(R.font.zhcn)),
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("resin number")
                        }

                        append(" to update the timer!")
                    },
                    fontSize = UniversalTextSize(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()

                )

            }
        }
    }
}

