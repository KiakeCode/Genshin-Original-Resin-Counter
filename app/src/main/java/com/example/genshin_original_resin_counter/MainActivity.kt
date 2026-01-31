package com.example.genshin_original_resin_counter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.genshin_original_resin_counter.ui.theme.blue
import com.example.genshin_original_resin_counter.util.convertResinInTimeLeftMillis
import com.example.genshin_original_resin_counter.util.formatTimeToString
import kotlinx.coroutines.delay


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
    var totalMillis by remember {
        mutableLongStateOf(0L)
    }
    var millisCounter by remember {
        mutableLongStateOf(totalMillis)
    }

    var input by remember {
        mutableStateOf("0")
    }
    val textsize = 17f

    var timerText by remember { mutableStateOf(formatTimeToString(0L)) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = totalMillis) {
        while (millisCounter > 0) {
            delay(1000L)
            // check if focusmanager is trying to change value before updating this
            millisCounter -= 1000L
//            millisCounter -= 8L * 3000L
            if (millisCounter % (8L * 60L * 1000L) == 0L) {
                Log.d("RESIN UPDATE", "+8 min passed")
                try {
                    input = (input.toInt() + 1).toString()
                } catch (e: Error) {
                    input = "0"
                    Log.d("erorrr", e.toString())
                }

            }
            timerText = formatTimeToString(millisCounter)
        }
        timerText = formatTimeToString(0L)
    }



    @Composable
    fun UniversalTextSize(textSize: Float = textsize): TextUnit = TextUnit(
        value = textSize, type = TextUnitType.Sp
    )

    @Composable
    fun UniversalTextStyleBold(): TextStyle = TextStyle(
        fontWeight = FontWeight.W900,
        fontSize = UniversalTextSize(),
        color = blue,
        fontFamily = FontFamily(Font(R.font.zhcn))
    )

//    val borderprop = BorderStroke(1.dp, Color.Red)

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
                                        input =
                                            if (input.contains("^0".toRegex())) {
                                                it.replace(regex = "^0".toRegex(), replacement = "")
                                            } else if (input.length == 3 && it.length > 3) input
                                            else if (it.isNotEmpty() && it.toInt() > 200) "200"
                                            else it

                                        if (input.isEmpty()) input = "0"

                                        // The timer updates
                                        totalMillis = convertResinInTimeLeftMillis(
                                            resin = mutableStateOf(
                                                value = input
                                            )
                                        )
                                        millisCounter = totalMillis

                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Next
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier.width(IntrinsicSize.Min),
                                    keyboardActions = KeyboardActions(onNext = {
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
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    text = timerText.value,
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