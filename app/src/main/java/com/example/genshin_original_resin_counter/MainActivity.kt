package com.example.genshin_original_resin_counter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.genshin_original_resin_counter.ui.theme.GenshinOriginalResinCounterTheme
import com.example.genshin_original_resin_counter.ui.theme.blue
import com.example.genshin_original_resin_counter.util.convertResinInTimeLeftMillis
import com.example.genshin_original_resin_counter.util.formatTimeToString
import com.example.genshin_original_resin_counter.util.validateInput
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TODO Add a background
// TODO Make number o resin more in the sight on the page

val Context.dataStore by preferencesDataStore(name = "counters")

val RESIN = stringPreferencesKey("RESIN")

suspend fun saveResin(context: Context, value: String) {
    context.dataStore.edit { preferences ->
        preferences[RESIN] = value
    }
}

suspend fun readResin(context: Context): Flow<String> =
    context.dataStore.data.map { preferences -> preferences[RESIN] ?: "0" }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(LocalContext.current)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(current: Context) {
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
        mutableStateOf("")
    }
    var scope = rememberCoroutineScope()



    LaunchedEffect(key1 = totalMillis) {

        while (millisCounter > 0) {
            delay(1000L)
            millisCounter -= 1000L
//            millisCounter -= 8L * 60L * 1000L
            if (millisCounter % (8L * 60L * 1000L) == 0L) {
                try {
//                    Log.d("validateInput", validateInput(input, (input.toInt() + 1).toString()))
                    input = validateInput(input, (input.toInt() + 1).toString())
                    scope.launch { saveResin(current, input) }

                } catch (e: Error) {
                    input = "0"
                    Log.d("Err", e.toString())
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        readResin(current).collect {
            input = it
        }
        totalMillis = convertResinInTimeLeftMillis(
            resin = mutableStateOf(
                value = input
            )
        )

        millisCounter = totalMillis
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
                                        scope.launch {
                                            saveResin(current, input)
                                        }

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

            },
        ) { e ->
            Column(
                Modifier
                    .padding(paddingValues = e)
                    .fillMaxSize()
                    .clickable(interactionSource = null, indication = null, onClick = {
                        focusManager.clearFocus()
                    }), verticalArrangement = Arrangement.Center
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

